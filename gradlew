#!/usr/bin/env sh
# Small bootstrap launcher for this repository. Downloads the pinned official Gradle distribution on first use.
set -eu
GRADLE_VERSION="8.13"
GRADLE_HOME_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/dists/klaarkompas/gradle-${GRADLE_VERSION}-bin"
GRADLE_BIN="$GRADLE_HOME_DIR/gradle-${GRADLE_VERSION}/bin/gradle"
if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$GRADLE_HOME_DIR"
  ZIP="$GRADLE_HOME_DIR/gradle-${GRADLE_VERSION}-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl --fail --location --retry 3 "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" --output "$ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ZIP" "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
  else
    echo "Install curl or wget to download Gradle ${GRADLE_VERSION}." >&2
    exit 1
  fi
  unzip -q -o "$ZIP" -d "$GRADLE_HOME_DIR"
  rm -f "$ZIP"
fi
exec "$GRADLE_BIN" "$@"
