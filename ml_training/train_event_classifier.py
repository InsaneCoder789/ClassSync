from __future__ import annotations

import argparse
import json
import shutil
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix


LABELS = [
    "ACTIONABLE_NO_DATE",
    "ANNOUNCEMENT_ONLY",
    "DUE_DATE_TASK",
    "GRADE_OR_FEEDBACK",
    "INFORMATION_ONLY",
    "MATERIAL_ONLY",
    "SUBMISSION_INSTRUCTION",
    "TASK_REQUIRED",
    "TEST_OR_EXAM_INFO",
    "UNKNOWN",
]

TEXT_FALLBACK_COLUMNS = ["title", "body", "course_name"]
REPORT_DIR_NAME = "reports"
MODEL_DIR_NAME = "saved_model"
VECTORIZER_DIR_NAME = "vectorizer_vocabulary.txt"


@dataclass
class DatasetSplits:
    train_texts: np.ndarray
    train_labels: np.ndarray
    val_texts: np.ndarray
    val_labels: np.ndarray
    test_texts: np.ndarray
    test_labels: np.ndarray
    test_frame: pd.DataFrame


def normalize_text(value: object) -> str:
    if value is None or (isinstance(value, float) and np.isnan(value)):
        return ""
    return " ".join(str(value).replace("\n", " ").split()).strip()


def build_input_text(frame: pd.DataFrame) -> pd.Series:
    combined = frame.get("combined_text", pd.Series(dtype=str)).fillna("").map(normalize_text)
    fallback = (
        frame.get("title", pd.Series(dtype=str)).fillna("").map(normalize_text)
        + " "
        + frame.get("body", pd.Series(dtype=str)).fillna("").map(normalize_text)
        + " "
        + frame.get("course_name", pd.Series(dtype=str)).fillna("").map(normalize_text)
    ).map(normalize_text)
    return combined.where(combined.str.len() > 0, fallback)


def ensure_required_columns(frame: pd.DataFrame) -> None:
    required = {"classification_label"}
    missing = sorted(required - set(frame.columns))
    if missing:
        raise ValueError(f"Dataset missing required columns: {missing}")


def load_dataset(csv_path: Path) -> pd.DataFrame:
    frame = pd.read_csv(csv_path)
    ensure_required_columns(frame)
    frame = frame.copy()
    frame["input_text"] = build_input_text(frame)
    frame["classification_label"] = frame["classification_label"].fillna("UNKNOWN").astype(str).str.strip()
    frame = frame[frame["classification_label"].isin(LABELS)]
    if frame.empty:
        raise ValueError("No rows remain after filtering to supported classification labels.")
    frame["split_hint_normalized"] = frame.get("split_hint", "").fillna("").astype(str).str.lower().str.strip()
    return frame


def split_dataset(frame: pd.DataFrame, random_seed: int = 42) -> DatasetSplits:
    valid_splits = {"train", "validation", "val", "test"}
    if frame["split_hint_normalized"].isin(valid_splits).any():
        train_frame = frame[frame["split_hint_normalized"] == "train"].copy()
        val_frame = frame[frame["split_hint_normalized"].isin({"validation", "val"})].copy()
        test_frame = frame[frame["split_hint_normalized"] == "test"].copy()
        if train_frame.empty or val_frame.empty or test_frame.empty:
            raise ValueError("split_hint exists but does not produce non-empty train/validation/test sets.")
    else:
        shuffled = frame.sample(frac=1.0, random_state=random_seed).reset_index(drop=True)
        total = len(shuffled)
        train_end = int(total * 0.8)
        val_end = train_end + int(total * 0.1)
        train_frame = shuffled.iloc[:train_end].copy()
        val_frame = shuffled.iloc[train_end:val_end].copy()
        test_frame = shuffled.iloc[val_end:].copy()

    label_to_index = {label: idx for idx, label in enumerate(LABELS)}

    def encode_labels(series: pd.Series) -> np.ndarray:
        return series.map(label_to_index).to_numpy(dtype=np.int32)

    return DatasetSplits(
        train_texts=train_frame["input_text"].to_numpy(dtype=str),
        train_labels=encode_labels(train_frame["classification_label"]),
        val_texts=val_frame["input_text"].to_numpy(dtype=str),
        val_labels=encode_labels(val_frame["classification_label"]),
        test_texts=test_frame["input_text"].to_numpy(dtype=str),
        test_labels=encode_labels(test_frame["classification_label"]),
        test_frame=test_frame,
    )


def build_model(train_texts: Iterable[str], label_count: int) -> tuple[tf.keras.Model, tf.keras.layers.TextVectorization]:
    vectorizer = tf.keras.layers.TextVectorization(
        max_tokens=15000,
        standardize="lower_and_strip_punctuation",
        split="whitespace",
        output_mode="int",
        output_sequence_length=120,
    )
    vectorizer.adapt(tf.data.Dataset.from_tensor_slices(list(train_texts)).batch(256))

    inputs = tf.keras.Input(shape=(1,), dtype=tf.string, name="text")
    x = vectorizer(inputs)
    x = tf.keras.layers.Embedding(input_dim=15000, output_dim=64, name="embedding")(x)
    x = tf.keras.layers.GlobalAveragePooling1D()(x)
    x = tf.keras.layers.Dense(96, activation="relu")(x)
    x = tf.keras.layers.Dropout(0.25)(x)
    outputs = tf.keras.layers.Dense(label_count, activation="softmax", name="classification")(x)

    model = tf.keras.Model(inputs=inputs, outputs=outputs, name="classsync_event_classifier")
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=1e-3),
        loss=tf.keras.losses.SparseCategoricalCrossentropy(),
        metrics=["accuracy"],
    )
    return model, vectorizer


def build_tf_dataset(texts: np.ndarray, labels: np.ndarray, batch_size: int, shuffle: bool) -> tf.data.Dataset:
    dataset = tf.data.Dataset.from_tensor_slices((texts, labels))
    if shuffle:
        dataset = dataset.shuffle(buffer_size=len(texts), reshuffle_each_iteration=True)
    return dataset.batch(batch_size).prefetch(tf.data.AUTOTUNE)


def build_prediction_dataset(texts: np.ndarray, batch_size: int) -> tf.data.Dataset:
    return tf.data.Dataset.from_tensor_slices(texts.astype(object)).batch(batch_size).prefetch(tf.data.AUTOTUNE)


def accuracy_by_column(frame: pd.DataFrame, predictions: np.ndarray, column: str) -> dict[str, float]:
    results: dict[str, float] = {}
    for value, group in frame.groupby(column, dropna=False):
        idx = group.index.to_numpy()
        truth = group["classification_label"].to_numpy(dtype=str)
        preds = predictions[idx]
        results[str(value)] = float((truth == preds).mean())
    return results


def write_reports(output_dir: Path, test_frame: pd.DataFrame, truth: np.ndarray, predictions: np.ndarray) -> dict:
    reports_dir = output_dir / REPORT_DIR_NAME
    reports_dir.mkdir(parents=True, exist_ok=True)

    report_dict = classification_report(truth, predictions, labels=LABELS, output_dict=True, zero_division=0)
    report_text = classification_report(truth, predictions, labels=LABELS, zero_division=0)
    confusion = confusion_matrix(truth, predictions, labels=LABELS)

    label_metrics = pd.DataFrame(report_dict).transpose()
    label_metrics.to_csv(reports_dir / "label_metrics.csv", index=True)
    pd.DataFrame(confusion, index=LABELS, columns=LABELS).to_csv(reports_dir / "confusion_matrix.csv", index=True)
    (reports_dir / "classification_report.txt").write_text(report_text, encoding="utf-8")

    confused_pairs = []
    for i, actual in enumerate(LABELS):
        for j, predicted in enumerate(LABELS):
            if i == j:
                continue
            count = int(confusion[i, j])
            if count > 0:
                confused_pairs.append({"actual": actual, "predicted": predicted, "count": count})
    confused_pairs.sort(key=lambda item: item["count"], reverse=True)

    metrics = {
        "accuracy": float(accuracy_score(truth, predictions)),
        "classification_report_text": report_text,
        "confusion_matrix": confusion.tolist(),
        "accuracy_by_source": accuracy_by_column(test_frame, predictions, "source"),
        "accuracy_by_noise_level": accuracy_by_column(test_frame, predictions, "noise_level"),
        "accuracy_by_has_due_date": accuracy_by_column(
            test_frame.assign(has_due_date=test_frame["has_due_date"].astype(str)),
            predictions,
            "has_due_date",
        ),
        "most_confused_label_pairs": confused_pairs[:15],
        "per_label_metrics": report_dict,
    }
    return metrics


def copy_reports_to_project_root(output_dir: Path, project_root: Path) -> None:
    source_dir = output_dir / REPORT_DIR_NAME
    target_dir = project_root / "ml_training" / REPORT_DIR_NAME
    target_dir.mkdir(parents=True, exist_ok=True)
    for file_name in ("classification_report.txt", "confusion_matrix.csv", "label_metrics.csv"):
        shutil.copy2(source_dir / file_name, target_dir / file_name)


def save_vectorizer_vocabulary(output_dir: Path, vectorizer: tf.keras.layers.TextVectorization) -> None:
    vocabulary = vectorizer.get_vocabulary()
    (output_dir / VECTORIZER_DIR_NAME).write_text("\n".join(vocabulary), encoding="utf-8")


def export_tflite_model(model: tf.keras.Model, output_dir: Path) -> Path:
    saved_model_dir = output_dir / MODEL_DIR_NAME
    model.export(str(saved_model_dir))
    converter = tf.lite.TFLiteConverter.from_saved_model(str(saved_model_dir))
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_ops = [
        tf.lite.OpsSet.TFLITE_BUILTINS,
        tf.lite.OpsSet.SELECT_TF_OPS,
    ]
    tflite_model = converter.convert()
    tflite_path = output_dir / "classsync_event_classifier.tflite"
    tflite_path.write_bytes(tflite_model)
    return tflite_path


def copy_assets(output_dir: Path, project_root: Path) -> None:
    assets_dir = project_root / "app" / "src" / "main" / "assets"
    assets_dir.mkdir(parents=True, exist_ok=True)
    (assets_dir / "classsync_event_classifier.tflite").write_bytes(
        (output_dir / "classsync_event_classifier.tflite").read_bytes()
    )
    (assets_dir / "classsync_event_labels.txt").write_text("\n".join(LABELS), encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--csv", required=True, type=Path)
    parser.add_argument("--output_dir", required=True, type=Path)
    parser.add_argument("--epochs", type=int, default=10)
    parser.add_argument("--batch_size", type=int, default=32)
    args = parser.parse_args()

    output_dir = args.output_dir.resolve()
    output_dir.mkdir(parents=True, exist_ok=True)
    project_root = Path(__file__).resolve().parents[1]

    frame = load_dataset(args.csv.resolve())
    splits = split_dataset(frame)

    model, vectorizer = build_model(splits.train_texts, len(LABELS))
    train_ds = build_tf_dataset(splits.train_texts, splits.train_labels, args.batch_size, shuffle=True)
    val_ds = build_tf_dataset(splits.val_texts, splits.val_labels, args.batch_size, shuffle=False)

    history = model.fit(
        train_ds,
        validation_data=val_ds,
        epochs=args.epochs,
        callbacks=[
            tf.keras.callbacks.EarlyStopping(
                monitor="val_accuracy",
                patience=2,
                restore_best_weights=True,
            )
        ],
        verbose=2,
    )

    test_predictions = model.predict(
        build_prediction_dataset(splits.test_texts, args.batch_size),
        verbose=0,
    )
    predicted_indices = np.argmax(test_predictions, axis=1)
    truth_labels = np.array([LABELS[idx] for idx in splits.test_labels])
    predicted_labels = np.array([LABELS[idx] for idx in predicted_indices])
    test_frame = splits.test_frame.reset_index(drop=True)

    report_metrics = write_reports(output_dir, test_frame, truth_labels, predicted_labels)
    copy_reports_to_project_root(output_dir, project_root)
    save_vectorizer_vocabulary(output_dir, vectorizer)
    export_tflite_model(model, output_dir)
    (output_dir / "classsync_event_labels.txt").write_text("\n".join(LABELS), encoding="utf-8")
    copy_assets(output_dir, project_root)

    metrics_payload = {
        "dataset_rows": int(len(frame)),
        "splits": {
            "train": int(len(splits.train_texts)),
            "validation": int(len(splits.val_texts)),
            "test": int(len(splits.test_texts)),
        },
        "labels": LABELS,
        "history": {k: [float(v) for v in values] for k, values in history.history.items()},
        **report_metrics,
    }

    metrics_path = output_dir / "training_metrics.json"
    metrics_path.write_text(json.dumps(metrics_payload, indent=2), encoding="utf-8")

    print(f"Accuracy: {metrics_payload['accuracy']:.4f}")
    print("Per-label precision / recall / F1:")
    print(metrics_payload["classification_report_text"])
    print("Confusion matrix:")
    for row in metrics_payload["confusion_matrix"]:
        print(row)
    print("Accuracy by source:", json.dumps(metrics_payload["accuracy_by_source"], indent=2))
    print("Accuracy by noise_level:", json.dumps(metrics_payload["accuracy_by_noise_level"], indent=2))
    print("Accuracy by has_due_date:", json.dumps(metrics_payload["accuracy_by_has_due_date"], indent=2))
    print("Most confused label pairs:", json.dumps(metrics_payload["most_confused_label_pairs"][:10], indent=2))
    print(f"TFLite model exported to: {output_dir / 'classsync_event_classifier.tflite'}")
    print(f"Label file exported to: {output_dir / 'classsync_event_labels.txt'}")
    print(f"Reports copied to: {project_root / 'ml_training' / REPORT_DIR_NAME}")


if __name__ == "__main__":
    main()
