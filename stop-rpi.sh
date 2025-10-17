#!/bin/bash
# Stop Digital Sun running on Raspberry Pi

set -e

# Configuration
RPI_HOST="${RPI_HOST:-raspberrypi.local}"
RPI_USER="${RPI_USER:-simonas}"
PID_FILE="${PID_FILE:-/tmp/digital-sun.pid}"

echo "Stopping Digital Sun on $RPI_USER@$RPI_HOST..."

# Send SIGTERM and trust Main.kt shutdown hook to handle cleanup
ssh "$RPI_USER@$RPI_HOST" "
    if [ -f '$PID_FILE' ]; then
        PID=\$(cat '$PID_FILE')
        if kill -0 \"\$PID\" 2>/dev/null; then
            echo \"Sending SIGTERM to PID \$PID...\"
            sudo kill -TERM \"\$PID\"
            echo \"Shutdown signal sent, application will clean up\"
        else
            echo \"PID file exists but process not running, removing stale file\"
            sudo rm -f '$PID_FILE'
        fi
    else
        echo \"No PID file found\"
    fi
"

echo "Done"
