from __future__ import annotations

import argparse
from pathlib import Path

import tensorflow as tf

from train_event_classifier import LABELS


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--saved_model_dir", required=True, type=Path)
    parser.add_argument("--output_dir", required=True, type=Path)
    args = parser.parse_args()

    output_dir = args.output_dir.resolve()
    output_dir.mkdir(parents=True, exist_ok=True)

    converter = tf.lite.TFLiteConverter.from_saved_model(str(args.saved_model_dir.resolve()))
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_ops = [
        tf.lite.OpsSet.TFLITE_BUILTINS,
        tf.lite.OpsSet.SELECT_TF_OPS,
    ]
    tflite_model = converter.convert()

    model_path = output_dir / "classsync_event_classifier.tflite"
    labels_path = output_dir / "classsync_event_labels.txt"
    model_path.write_bytes(tflite_model)
    labels_path.write_text("\n".join(LABELS), encoding="utf-8")

    print(f"Exported: {model_path}")
    print(f"Exported: {labels_path}")


if __name__ == "__main__":
    main()
