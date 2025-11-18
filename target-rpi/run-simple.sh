#!/bin/bash
# Simple startup script for Digital Sun on Raspberry Pi
# Runs in foreground - suitable for systemd or direct execution

set -e

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Use absolute path for JAR file based on script location
JAR_FILE="${JAR_FILE:-$SCRIPT_DIR/target-rpi-1.0.0.jar}"

# Check if running with sudo
if [ "$EUID" -ne 0 ]; then
    echo "Error: This script must be run with sudo (GPIO access required)"
    echo "Usage: sudo ./run-simple.sh"
    exit 1
fi

echo "Starting Digital Sun..."

# JVM optimization flags for Raspberry Pi
# Using exec to replace the shell process with Java
exec java \
    -server \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=10 \
    -XX:+UseStringDeduplication \
    -Xms256m \
    -Xmx512m \
    -XX:+TieredCompilation \
    -XX:TieredStopAtLevel=1 \
    -Djava.awt.headless=true \
    -jar "$JAR_FILE"
