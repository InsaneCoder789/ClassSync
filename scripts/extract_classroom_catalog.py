#!/usr/bin/env python3
"""Generate the classroom timetable catalog JSON from the semester XLS source.

Requires:
    pip install xlrd

Usage:
    python scripts/extract_classroom_catalog.py
"""

from __future__ import annotations

import json
import re
from collections import defaultdict
from pathlib import Path

import xlrd


ROOT = Path(__file__).resolve().parents[1]
SOURCE_XLS = ROOT / "app/src/main/assets/classroom/source/4th_semester_tt_and_section_detail.xls"
OUTPUT_JSON = ROOT / "app/src/main/assets/classroom/classroom_catalog.json"


def normalize_section(value: str) -> str:
    match = re.search(r"(\d+)", value or "")
    return f"CSE-{int(match.group(1)):02d}" if match else value.strip()


def day_key(raw: str) -> str:
    return raw.split("(")[0].strip().upper()[:3]


def day_variant(raw: str) -> str | None:
    match = re.search(r"\((\d+)\)", raw or "")
    return match.group(1) if match else None


def add_entry(day_map, day, variant, slot_index, time_label, subject, room):
    subject = str(subject).strip()
    room = str(room).strip()
    if not subject or subject.upper() == "X":
        return
    day_map[day].append(
        {
            "slotIndex": slot_index,
            "time": time_label,
            "subject": subject,
            "room": room if room and room != "---" else "TBA",
            "variant": variant,
        }
    )


def main() -> None:
    workbook = xlrd.open_workbook(SOURCE_XLS.as_posix())
    timetable_sheet = workbook.sheet_by_name("Time-Table")
    section_sheet = workbook.sheet_by_name("Section Detail")

    student_sets = defaultdict(set)
    for row_index in range(1, section_sheet.nrows):
        roll_number = str(section_sheet.cell_value(row_index, 0)).strip().replace(".0", "")
        section = normalize_section(str(section_sheet.cell_value(row_index, 1)).strip())
        if roll_number and section:
            student_sets[section].add(roll_number)

    section_days = defaultdict(lambda: defaultdict(list))
    for row_index in range(timetable_sheet.nrows):
        raw_day = str(timetable_sheet.cell_value(row_index, 0)).strip()
        raw_section = str(timetable_sheet.cell_value(row_index, 1)).strip()
        if not raw_day or not re.fullmatch(r"CSE-\d+", raw_section):
            continue

        row = [str(timetable_sheet.cell_value(row_index, column)).strip() for column in range(timetable_sheet.ncols)]
        section = normalize_section(raw_section)
        day = day_key(raw_day)
        variant = day_variant(raw_day)
        slot_map = [
            (0, "08:00 AM - 09:00 AM", row[3], row[2]),
            (1, "09:00 AM - 10:00 AM", row[5], row[4]),
            (2, "10:00 AM - 11:00 AM", row[6], row[4]),
            (3, "11:00 AM - 12:00 PM", row[8], row[7]),
            (4, "12:00 PM - 01:00 PM", row[10], row[9]),
            (5, "01:00 PM - 02:00 PM", row[11], row[9]),
            (6, "02:00 PM - 03:00 PM", row[13], row[12]),
            (7, "03:00 PM - 04:00 PM", row[15], row[14]),
            (8, "04:00 PM - 05:00 PM", row[17], row[16]),
            (9, "05:00 PM - 06:00 PM", row[18], row[16]),
        ]

        for slot_index, time_label, subject, room in slot_map:
            add_entry(section_days[section], day, variant, slot_index, time_label, subject, room)

    day_order = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"]
    sections = []
    for section in sorted(section_days.keys(), key=lambda value: int(re.search(r"(\d+)", value).group(1))):
        days = []
        for day in day_order:
            entries = section_days[section].get(day, [])
            entries.sort(key=lambda item: item["slotIndex"])
            days.append({"dayKey": day, "label": day, "entries": entries})
        sections.append(
            {
                "sectionId": section,
                "studentCount": len(student_sets.get(section, set())),
                "days": days,
            }
        )

    catalog = {
        "version": 1,
        "sourceFile": SOURCE_XLS.name,
        "generatedSemester": 4,
        "semesters": [
            {"semester": semester, "sections": sections if semester == 4 else []}
            for semester in range(1, 9)
        ],
    }

    OUTPUT_JSON.write_text(json.dumps(catalog, indent=2))
    print(f"Wrote {OUTPUT_JSON}")


if __name__ == "__main__":
    main()
