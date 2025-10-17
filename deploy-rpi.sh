#!/bin/bash
# Deploy Digital Sun to Raspberry Pi

set -e

# Configuration
RPI_HOST="${RPI_HOST:-raspberrypi.local}"
RPI_USER="${RPI_USER:-simonas}"
RPI_DIR="${RPI_DIR:-~/digital-sun}"

echo "============================================"
echo "Digital Sun - Raspberry Pi Deployment"
echo "============================================"
echo ""
echo "Target: $RPI_USER@$RPI_HOST:$RPI_DIR"
echo ""

# Build the project locally
echo "Building project..."
./gradlew :target-rpi:build

# Check if build succeeded
if [ ! -f "target-rpi/build/libs/target-rpi-1.0.0.jar" ]; then
    echo "Error: Build failed or JAR not found"
    exit 1
fi

echo "Build successful!"
echo ""

# Create remote directory if it doesn't exist
echo "Preparing remote directory..."
ssh "$RPI_USER@$RPI_HOST" "mkdir -p $RPI_DIR"

# Deploy the JAR and run script
echo "Deploying JAR and run script to Raspberry Pi..."
scp target-rpi/build/libs/target-rpi-1.0.0.jar "$RPI_USER@$RPI_HOST:$RPI_DIR/"
scp target-rpi/run.sh "$RPI_USER@$RPI_HOST:$RPI_DIR/"
ssh "$RPI_USER@$RPI_HOST" "chmod +x $RPI_DIR/run.sh"

echo "Running with optimized JVM settings..."
echo "Press Ctrl+C to stop"
echo ""

# Trap Ctrl+C to kill remote process gracefully
cleanup_remote() {
    echo ""
    echo "Stopping remote process..."
    # Try graceful shutdown first (SIGTERM)
    ssh "$RPI_USER@$RPI_HOST" "sudo pkill -TERM -f target-rpi-1.0.0.jar" 2>/dev/null
    sleep 2
    # Force kill if still running (SIGKILL)
    ssh "$RPI_USER@$RPI_HOST" "sudo pkill -9 -f target-rpi-1.0.0.jar" 2>/dev/null || true
    echo "Remote process stopped"
}

trap cleanup_remote INT TERM EXIT

# Run with -t to allocate a pseudo-TTY (ensures signals are propagated)
ssh -t "$RPI_USER@$RPI_HOST" "cd $RPI_DIR && sudo ./run.sh"
