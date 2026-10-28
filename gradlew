#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GRADLE_VERSION="8.7"
INSTALL_DIR="$ROOT_DIR/.gradle-install"
GRADLE_HOME="$INSTALL_DIR/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"
ARCHIVE="$INSTALL_DIR/gradle-$GRADLE_VERSION-bin.zip"

mkdir -p "$INSTALL_DIR"

if [ ! -x "$GRADLE_BIN" ]; then
  if [ ! -f "$ARCHIVE" ]; then
    echo "Downloading Gradle $GRADLE_VERSION..."
    curl -L "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$ARCHIVE"
  fi
  if [ ! -d "$GRADLE_HOME" ]; then
    unzip -q "$ARCHIVE" -d "$INSTALL_DIR"
  fi
fi

exec "$GRADLE_BIN" "$@"
