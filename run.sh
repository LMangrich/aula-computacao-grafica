#!/bin/sh
# Linux (Debian) / macOS equivalent of run.bat: compile everything under src/ into bin/ and start the app.
set -e
cd "$(dirname "$0")"
find src -name '*.java' -print0 | xargs -0 javac -d bin
java -cp bin app.MainClass
