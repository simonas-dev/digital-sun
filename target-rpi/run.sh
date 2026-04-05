#!/bin/bash
# Startup script for Digital Sun on Raspberry Pi

set -e

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Use absolute path for JAR file based on script location
JAR_FILE="${JAR_FILE:-$SCRIPT_DIR/target-rpi-1.0.0.jar}"
PID_FILE="${PID_FILE:-/tmp/digital-sun.pid}"

# Check if running with sudo
if [ "$EUID" -ne 0 ]; then
    echo "Error: This script must be run with sudo (GPIO access required)"
    echo "Usage: sudo ./run.sh"
    exit 1
fi

# Check if already running
if [ -f "$PID_FILE" ]; then
    OLD_PID=$(cat "$PID_FILE")
    if kill -0 "$OLD_PID" 2>/dev/null; then
        echo "Error: Digital Sun is already running with PID $OLD_PID"
        echo "Use stop-rpi.sh to stop it first"
        exit 1
    else
        echo "Removing stale PID file..."
        rm -f "$PID_FILE"
    fi
fi

echo "Starting Digital Sun..."

# Write PID file (will contain the java PID after exec in run-simple.sh)
echo "$$" > "$PID_FILE"

# Clean up PID file on exit
trap 'rm -f "$PID_FILE"' EXIT

# Exec replaces this shell with run-simple.sh (which execs java),
# so stdin passes straight through to the Java process.
exec "$SCRIPT_DIR/run-simple.sh"
