#!/bin/bash
# Stop Digital Sun running on Raspberry Pi

set -e

# Configuration
RPI_HOST="${RPI_HOST:-raspberrypi.local}"
RPI_USER="${RPI_USER:-simonas}"
PID_FILE="${PID_FILE:-/tmp/digital-sun.pid}"

echo "Stopping Digital Sun on $RPI_USER@$RPI_HOST..."

# Stop using PID file for graceful shutdown
ssh "$RPI_USER@$RPI_HOST" "
    if [ -f '$PID_FILE' ]; then
        PID=\$(cat '$PID_FILE')
        if kill -0 \"\$PID\" 2>/dev/null; then
            echo \"Sending SIGTERM to process \$PID...\"
            sudo kill -TERM \"\$PID\"

            # Wait for graceful shutdown (up to 10 seconds)
            for i in {1..10}; do
                if ! kill -0 \"\$PID\" 2>/dev/null; then
                    echo \"Process stopped gracefully\"
                    sudo rm -f '$PID_FILE'
                    exit 0
                fi
                sleep 1
            done

            # Force kill if still running
            if kill -0 \"\$PID\" 2>/dev/null; then
                echo \"Process didn't stop gracefully, forcing...\"
                sudo kill -KILL \"\$PID\"
                sudo rm -f '$PID_FILE'
                echo \"Process forcefully stopped\"
            fi
        else
            echo \"PID file exists but process not running\"
            sudo rm -f '$PID_FILE'
        fi
    else
        echo \"No PID file found, trying pkill as fallback...\"
        sudo pkill -TERM -f 'target-rpi-1.0.0.jar' && echo 'Process stopped' || echo 'No process found'
    fi
"

echo "Done!"
