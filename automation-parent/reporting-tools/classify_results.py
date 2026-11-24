#!/usr/bin/env python3
"""Assign stable test-type labels to merged Allure results before HTML generation.

Category filters: UI (Selenium + Playwright), API, Selenium, Playwright, Unit.
No network access or external Python dependencies required.
"""
import json
import pathlib
import sys

root = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "target/combined-allure-results")
if not root.is_dir():
    raise SystemExit(f"Missing Allure results: {root}")
counts = {"API": 0, "Selenium": 0, "Playwright": 0, "Unit": 0}
for path in root.glob("*-result.json"):
    data = json.loads(path.read_text(encoding="utf-8"))
    labels = data.setdefault("labels", [])
    identifier = " ".join(str(data.get(k, "")) for k in ("fullName", "name", "testCaseId")).lower()
    tag_values = [str(x.get("value", "")).lower().lstrip("@") for x in labels if x.get("name") == "tag"]
    if "playwright" in tag_values or any(s in identifier for s in ("playwright", "cucumber", "user management through")):
        kind = "Playwright"
    elif "selenium" in tag_values or any(s in identifier for s in ("uitest", "uitests")):
        kind = "Selenium"
    elif any(s in identifier for s in ("apitest", "schematest", "wiremock")):
        kind = "API"
    else:
        kind = "Unit"
    family = "UI" if kind in ("Selenium", "Playwright") else kind
    labels[:] = [x for x in labels if not (x.get("name") == "tag" and x.get("value") in ("UI", "API", "Selenium", "Playwright", "Unit"))]
    labels.extend([{"name": "tag", "value": family}])
    if family != kind:
        labels.append({"name": "tag", "value": kind})
    counts[kind] += 1
    path.write_text(json.dumps(data, ensure_ascii=False), encoding="utf-8")
print("Allure test categories:", counts)
