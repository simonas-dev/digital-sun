#!/bin/bash
# Optimized startup script for Digital Sun on Raspberry Pi

set -e

JAR_FILE="${JAR_FILE:-target-rpi-1.0.0.jar}"
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

echo "Starting Digital Sun with optimized JVM settings..."
echo ""

# Trap signals and forward to Java process
JAVA_PID=""
cleanup() {
    echo ""
    echo "Caught signal, stopping Java process..."
    if [ ! -z "$JAVA_PID" ]; then
        kill -TERM "$JAVA_PID" 2>/dev/null || true
        wait "$JAVA_PID" 2>/dev/null || true
    fi
    rm -f "$PID_FILE"
    exit 0
}

trap cleanup SIGINT SIGTERM

# JVM optimization flags for Raspberry Pi
java \
    -server \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=10 \
    -XX:+UseStringDeduplication \
    -Xms256m \
    -Xmx512m \
    -XX:+TieredCompilation \
    -XX:TieredStopAtLevel=1 \
    -Djava.awt.headless=true \
    -jar "$JAR_FILE" &

JAVA_PID=$!
echo "$JAVA_PID" > "$PID_FILE"
echo "Java process started with PID: $JAVA_PID"

# Wait for Java process to finish
wait "$JAVA_PID"
rm -f "$PID_FILE"
