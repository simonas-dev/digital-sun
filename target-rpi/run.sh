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

# Forward signals to Java process - Main.kt shutdown hook handles cleanup
trap 'kill -TERM "$JAVA_PID" 2>/dev/null; wait "$JAVA_PID" 2>/dev/null; rm -f "$PID_FILE"' SIGINT SIGTERM

# Run the simple script in background for PID management
"$SCRIPT_DIR/run-simple.sh" &

JAVA_PID=$!
echo "$JAVA_PID" > "$PID_FILE"
echo "Started with PID: $JAVA_PID"

# Wait for process, then clean up PID file
wait "$JAVA_PID"
rm -f "$PID_FILE"
