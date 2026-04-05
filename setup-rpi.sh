#!/bin/bash
# Setup script for Digital Sun on a clean Raspberry Pi (Debian/DietPi)
# Installs all dependencies and builds the rpi_ws281x native library.

set -e

echo "============================================"
echo "Digital Sun - Raspberry Pi Setup"
echo "============================================"
echo ""

# Must run as root for library installation
if [ "$EUID" -ne 0 ]; then
    echo "Error: This script must be run with sudo"
    echo "Usage: sudo ./setup-rpi.sh"
    exit 1
fi

# Preserve the real user for non-root operations later
REAL_USER="${SUDO_USER:-$USER}"
REAL_HOME=$(eval echo "~$REAL_USER")

# 1. System packages
echo "[1/4] Installing system packages..."
apt-get update
apt-get install -y \
    openjdk-21-jdk \
    cmake \
    build-essential \
    git \
    scons

# Verify Java
java -version 2>&1 | head -1
echo ""

# 2. Build and install rpi_ws281x
echo "[2/4] Building rpi_ws281x native library..."
WS_DIR="/tmp/rpi_ws281x"
rm -rf "$WS_DIR"
git clone https://github.com/jgarff/rpi_ws281x.git "$WS_DIR"
cd "$WS_DIR"
mkdir build && cd build
cmake -D BUILD_SHARED=ON ..
cmake --build .
make install
ldconfig

# Verify
echo ""
echo "Verifying libws2811..."
if ldconfig -p | grep -q ws2811; then
    ldconfig -p | grep ws2811
    echo "OK"
else
    echo "WARNING: libws2811.so not found in ldconfig. The LED strip will not work."
fi
echo ""

# Cleanup build dir
rm -rf "$WS_DIR"

# 3. Enable SPI (needed for GPIO 10 / SPI MOSI)
echo "[3/4] Enabling SPI..."
if ! grep -q "^dtparam=spi=on" /boot/config.txt 2>/dev/null && \
   ! grep -q "^dtparam=spi=on" /boot/dietpi/config.txt 2>/dev/null; then
    # Try DietPi location first, then standard
    if [ -f /boot/dietpi/config.txt ]; then
        echo "dtparam=spi=on" >> /boot/dietpi/config.txt
    elif [ -f /boot/config.txt ]; then
        echo "dtparam=spi=on" >> /boot/config.txt
    elif [ -f /boot/firmware/config.txt ]; then
        echo "dtparam=spi=on" >> /boot/firmware/config.txt
    fi
    echo "SPI enabled (reboot required to take effect)"
else
    echo "SPI already enabled"
fi
echo ""

# 4. Disable audio (conflicts with PWM for LED control)
echo "[4/4] Disabling onboard audio (conflicts with PWM)..."
for cfg in /boot/config.txt /boot/dietpi/config.txt /boot/firmware/config.txt; do
    if [ -f "$cfg" ]; then
        if grep -q "^dtparam=audio=on" "$cfg"; then
            sed -i 's/^dtparam=audio=on/dtparam=audio=off/' "$cfg"
            echo "Audio disabled in $cfg (reboot required)"
        fi
    fi
done
echo ""

echo "============================================"
echo "Setup complete!"
echo "============================================"
echo ""
echo "Next steps:"
echo "  1. Reboot if SPI/audio changes were made:  sudo reboot"
echo "  2. Build the project:  ./gradlew :target-rpi:build"
echo "  3. Run:  sudo ./target-rpi/run.sh"
echo ""
echo "Or deploy from your Mac:  ./deploy-rpi.sh"
