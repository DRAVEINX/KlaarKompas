#!/usr/bin/env python3
"""Fast, dependency-free sanity checks for the KlaarKompas Android project."""
from __future__ import annotations

import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
errors: list[str] = []
checks: list[str] = []


def require(condition: bool, message: str) -> None:
    if condition:
        checks.append(message)
    else:
        errors.append(message)


# All XML resource files and the manifest must be well-formed.
xml_files = sorted((ROOT / "app" / "src" / "main").rglob("*.xml"))
try:
    for xml_file in xml_files:
        ET.parse(xml_file)
    checks.append(f"XML syntax valid ({len(xml_files)} files)")
except (ET.ParseError, OSError) as exc:
    errors.append(f"XML parsing failed: {exc}")

manifest_path = ROOT / "app/src/main/AndroidManifest.xml"
manifest = manifest_path.read_text(encoding="utf-8")
require("android.permission.INTERNET" not in manifest, "no Internet permission in the manifest")
require('android:resizeableActivity="true"' in manifest, "activity supports resizable layouts")
require('android:exported="true"' in manifest and 'android.intent.action.MAIN' in manifest, "launcher activity is declared")

build_file = (ROOT / "app/build.gradle.kts").read_text(encoding="utf-8")
require("create(\"phone\")" in build_file, "phone product flavor exists")
require("create(\"tablet\")" in build_file, "tablet product flavor exists")
require("compileSdk = 36" in build_file, "compile SDK is pinned")
require('versionCode = 2' in build_file and 'versionName = "0.2.0"' in build_file, "Android package version is 0.2.0")
require('applicationIdSuffix = ".phone"' in build_file, "phone has its own application ID")
require('applicationIdSuffix = ".tablet"' in build_file, "tablet has its own application ID")

repo_path = ROOT / "app/src/main/java/nl/draveinx/klaarkompas/data/GuideRepository.kt"
repo_source = repo_path.read_text(encoding="utf-8")
guide_ids = re.findall(r'\bg\("([^"\n]+)"', repo_source)
require(len(guide_ids) >= 30, f"starter library has at least 30 guides (found {len(guide_ids)})")
require(len(guide_ids) == len(set(guide_ids)), "guide IDs are unique")
require('"wifi-check"' in repo_source and '"cv-start"' in repo_source, "expected starter guides are present")

codec_path = ROOT / "app/src/main/java/nl/draveinx/klaarkompas/data/CustomGuideCodec.kt"
codec_source = codec_path.read_text(encoding="utf-8")
require("fun encode(guides: List<Guide>)" in codec_source and "fun decode(serialized: String?)" in codec_source, "personal guide codec has encode/decode")
require("Base64.getUrlEncoder()" in codec_source and "distinctBy { it.id }" in codec_source, "personal guide storage safely encodes and deduplicates records")
activity_source = (ROOT / "app/src/main/java/nl/draveinx/klaarkompas/MainActivity.kt").read_text(encoding="utf-8")
require("CreateGuideScreen" in activity_source and "Stap toevoegen" in activity_source or "Stappen (maximaal 20)" in activity_source, "users can create multi-step personal guides")
require("customGuides" in activity_source and "guides = guideLibrary" in activity_source, "personal guides are included in search and saved guides")
require("Maak het kleiner" in activity_source and "Vraag gericht om hulp" in activity_source, "stuck helper offers alternative approaches")
for category in ["Digitale vaardigheden", "Thuis", "Praktische zaken", "Werk & studie", "Leren & maken"]:
    require(category in repo_source, f"guide category is present: {category}")

workflow_path = ROOT / ".github/workflows/android.yml"
workflow = workflow_path.read_text(encoding="utf-8")
for task in [
    "testPhoneDebugUnitTest", "testTabletDebugUnitTest", "lintPhoneDebug", "lintTabletDebug",
    "assemblePhoneDebug", "assembleTabletDebug", "assemblePhoneRelease", "assembleTabletRelease",
    "bundlePhoneRelease", "bundleTabletRelease",
]:
    require(task in workflow, f"CI includes {task}")
require("actions/upload-artifact@v4" in workflow, "CI archives build artifacts")

# Ensure developer launchers target the same pinned Gradle version.
wrapper_props = (ROOT / "gradle/wrapper/gradle-wrapper.properties").read_text(encoding="utf-8")
require("gradle-8.13-bin.zip" in wrapper_props, "Gradle distribution is pinned to 8.13")
require('GRADLE_VERSION="8.13"' in (ROOT / "gradlew").read_text(encoding="utf-8"), "Unix bootstrap version matches")
require('GRADLE_VERSION=8.13' in (ROOT / "gradlew.bat").read_text(encoding="utf-8"), "Windows bootstrap version matches")

for message in checks:
    print(f"PASS: {message}")
for message in errors:
    print(f"FAIL: {message}", file=sys.stderr)
print(f"\nPreflight: {len(checks)} passed, {len(errors)} failed")
sys.exit(1 if errors else 0)
