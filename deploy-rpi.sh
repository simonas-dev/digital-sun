#!/bin/bash
# Deploy Digital Sun to Raspberry Pi

set -e

# Configuration
RPI_HOST="${RPI_HOST:-192.168.0.209}"
RPI_USER="${RPI_USER:-microsun}"
RPI_DIR="${RPI_DIR:-~/digital-sun/target-rpi}"

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

# Deploy the JAR and run script
echo "Deploying JAR and run script to Raspberry Pi..."
scp target-rpi/build/libs/target-rpi-1.0.0.jar "$RPI_USER@$RPI_HOST:$RPI_DIR/"

echo "Running on remote..."
echo "Press Ctrl+C to stop"
echo ""
echo "Options:"
echo "  SHADER=warm ./deploy-rpi.sh  (default - yellow/red/magenta colors)"
echo "  SHADER=red ./deploy-rpi.sh   (original red-only shader)"
echo "  HW=v1 ./deploy-rpi.sh       (50cm display, 292 pixels, GPIO 18)"
echo "  HW=v2 ./deploy-rpi.sh       (50cm display, 228 pixels, GPIO 10)"
echo "  HW=v3 ./deploy-rpi.sh       (21cm display, 604 pixels, GPIO 18 - default)"
echo ""

# Pass environment variables if set
ENV_ARGS=""
if [ ! -z "$SHADER" ]; then
    ENV_ARGS="SHADER=$SHADER"
fi
if [ ! -z "$HW" ]; then
    ENV_ARGS="$ENV_ARGS HW=$HW"
fi

# Run with -t to allocate a pseudo-TTY (ensures Ctrl+C is forwarded to remote process)
# SSH will forward SIGINT when you press Ctrl+C, run.sh will forward it to Java, Main.kt cleanup runs
ssh -t "$RPI_USER@$RPI_HOST" "cd $RPI_DIR && sudo $ENV_ARGS ./run.sh"
