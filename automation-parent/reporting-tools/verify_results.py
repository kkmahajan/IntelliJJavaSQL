#!/usr/bin/env python3
"""Fail the build when a test category silently ran zero tests.

Integration suites are gated behind -D flags and use JUnit assumptions, so a
missing or mistyped flag makes the whole suite SKIP instead of fail. Skipped
tests are hidden from the reports, so without this check CI stays green while a
category never ran.

Run after classify_results.py (it reads the test-type tags that script adds).

Usage: verify_results.py <allure-results-dir> [Category ...]
Default categories: Unit API Selenium Playwright
"""
import json
import pathlib
import sys

CATEGORIES = ("Unit", "API", "Selenium", "Playwright")

root = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "target/combined-allure-results")
required = tuple(sys.argv[2:]) or CATEGORIES
if not root.is_dir():
    raise SystemExit(f"Missing Allure results: {root}")

executed = {name: 0 for name in CATEGORIES}
skipped = {name: 0 for name in CATEGORIES}
for path in root.glob("*-result.json"):
    data = json.loads(path.read_text(encoding="utf-8"))
    tags = {x.get("value") for x in data.get("labels", []) if x.get("name") == "tag"}
    kind = next((c for c in ("Playwright", "Selenium", "API", "Unit") if c in tags), "Unit")
    bucket = skipped if data.get("status") == "skipped" else executed
    bucket[kind] += 1

print("Executed tests by category:", executed)
print("Skipped tests by category: ", skipped)
missing = [c for c in required if executed.get(c, 0) == 0]
if missing:
    raise SystemExit(
        "FAIL: no executed tests for: " + ", ".join(missing)
        + ". A suite was probably skipped (check the -Dintegration.tests / -Dui.tests / "
        "-Dplaywright.tests flags and the cucumber tag filter).")
print("OK: every required category executed at least one test.")
