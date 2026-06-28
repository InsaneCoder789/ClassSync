from __future__ import annotations

import argparse
import json
from pathlib import Path

from train_event_classifier import (
    LABELS,
    build_prediction_dataset,
    copy_reports_to_project_root,
    load_dataset,
    split_dataset,
    write_reports,
)
import numpy as np
import tensorflow as tf


def load_saved_model_for_inference(model_dir: Path) -> tf.keras.Model:
    inputs = tf.keras.Input(shape=(1,), dtype=tf.string, name="text")
    layer = tf.keras.layers.TFSMLayer(str(model_dir), call_endpoint="serve")
    outputs = layer(inputs)
    return tf.keras.Model(inputs=inputs, outputs=outputs)


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

    predictions = model.predict(
        build_prediction_dataset(splits.test_texts, args.batch_size),
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
