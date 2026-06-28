from __future__ import annotations

import argparse
import json
from pathlib import Path

from train_event_classifier import (
    LABELS,
    KERAS_MODEL_FILE_NAME,
    build_prediction_dataset,
    copy_reports_to_project_root,
    build_vectorizer,
    load_dataset,
    split_dataset,
    vectorize_texts,
    write_reports,
)
import numpy as np
import tensorflow as tf


def load_saved_model_for_inference(model_dir: Path) -> tf.keras.Model:
    resolved_path = model_dir
    if model_dir.is_dir():
        keras_model_path = model_dir / KERAS_MODEL_FILE_NAME
        if keras_model_path.exists():
            resolved_path = keras_model_path
    return tf.keras.models.load_model(resolved_path)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--csv", required=True, type=Path)
    parser.add_argument("--model_dir", required=True, type=Path)
    parser.add_argument("--output_dir", required=True, type=Path)
    parser.add_argument("--batch_size", type=int, default=32)
    args = parser.parse_args()

    output_dir = args.output_dir.resolve()
    output_dir.mkdir(parents=True, exist_ok=True)

    frame = load_dataset(args.csv.resolve())
    splits = split_dataset(frame)
    model = load_saved_model_for_inference(args.model_dir.resolve())
    vectorizer = build_vectorizer(splits.train_texts)
    test_tokens = vectorize_texts(vectorizer, splits.test_texts)

    predictions = model.predict(
        build_prediction_dataset(test_tokens, args.batch_size),
        verbose=0,
    )
    predicted_indices = np.argmax(predictions, axis=1)
    truth_labels = np.array([LABELS[idx] for idx in splits.test_labels])
    predicted_labels = np.array([LABELS[idx] for idx in predicted_indices])
    metrics = write_reports(output_dir, splits.test_frame.reset_index(drop=True), truth_labels, predicted_labels)
    copy_reports_to_project_root(output_dir, Path(__file__).resolve().parents[1])
    (output_dir / "evaluation_metrics.json").write_text(json.dumps(metrics, indent=2), encoding="utf-8")
    print(json.dumps(metrics, indent=2))


if __name__ == "__main__":
    main()
