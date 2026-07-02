# ClassSync Event Classifier Training

This folder contains the local Python training pipeline for the optional ClassSync smart classroom event classifier.

## Dataset format

The expected CSV contains these fields:

- `sample_id`
- `source`
- `course_name`
- `teacher_style`
- `title`
- `body`
- `combined_text`
- `has_due_date`
- `due_text`
- `due_date_iso`
- `event_type`
- `action_type`
- `classification_label`
- `should_create_task`
- `priority`
- `classifier_confidence_target`
- `classifier_reason`
- `detected_keywords`
- `noise_level`
- `split_hint`

## Required training columns

The model trains on:

- input text: `combined_text`
- fallback input: `title + body + course_name`
- target label: `classification_label`

If `split_hint` is present, the scripts can use it, but the safer default is a group-aware split by normalized message text so duplicate templates do not leak across train / validation / test.

## Install dependencies

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r ml_training/requirements.txt
```

## Train

```bash
python ml_training/train_event_classifier.py \
  --csv /Users/rohanc/Downloads/classsync_synthetic_event_training_50000.csv \
  --output_dir ml_training/output \
  --split_strategy group_by_text \
  --epochs 10 \
  --batch_size 32
```

## Evaluate

```bash
python ml_training/evaluate_event_classifier.py \
  --csv /Users/rohanc/Downloads/classsync_synthetic_event_training_50000.csv \
  --model_dir ml_training/output/saved_model \
  --output_dir ml_training/output \
  --split_strategy group_by_text
```

## Export TFLite only

```bash
python ml_training/export_tflite.py \
  --saved_model_dir ml_training/output/saved_model \
  --output_dir ml_training/output
```

## Output artifacts

Training produces:

- `ml_training/output/classsync_event_classifier.tflite`
- `ml_training/output/classsync_event_labels.txt`
- `ml_training/output/training_metrics.json`
- `ml_training/output/reports/classification_report.txt`
- `ml_training/output/reports/confusion_matrix.csv`
- `ml_training/output/reports/label_metrics.csv`

The training/evaluation scripts also mirror the latest reports to:

- `ml_training/reports/classification_report.txt`
- `ml_training/reports/confusion_matrix.csv`
- `ml_training/reports/label_metrics.csv`

The training script also copies:

- `app/src/main/assets/classsync_event_classifier.tflite`
- `app/src/main/assets/classsync_event_labels.txt`

## Split strategy

- `group_by_text`: recommended default. Keeps normalized duplicate texts in only one split.
- `auto`: uses `split_hint` only when there is no cross-split duplicate-text overlap.
- `split_hint`: trusts the dataset-provided split assignments as-is.
- `random_rows`: plain random 80 / 10 / 10 row split. Fastest, but most leakage-prone on synthetic/template-heavy data.

## Android fallback behavior

The Android classifier must treat the model as optional.

- If the model is missing, the app falls back to rule-based classification.
- If label loading fails, the app falls back to rule-based classification.
- If inference fails or confidence is too low, the hybrid classifier falls back to rule-based classification.

## Retraining with a larger dataset

Use the same script with a different CSV path and output directory. As long as the CSV preserves the required schema, the training/export flow stays the same.
