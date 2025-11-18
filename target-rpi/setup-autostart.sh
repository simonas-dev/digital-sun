#!/bin/bash
# Setup script to configure Digital Sun to run at Raspberry Pi boot
# This script creates a systemd service that will automatically start the application

set -e

# Check if running with sudo
if [ "$EUID" -ne 0 ]; then
    echo "Error: This script must be run with sudo"
    echo "Usage: sudo ./setup-autostart.sh"
    exit 1
fi

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

SERVICE_FILE="/etc/systemd/system/digital-sun.service"

echo "Creating systemd service for Digital Sun..."

# Create the systemd service file
cat > "$SERVICE_FILE" << EOF
[Unit]
Description=Digital Sun LED Controller
After=multi-user.target

[Service]
Type=simple
User=root
WorkingDirectory=$SCRIPT_DIR
ExecStart=$SCRIPT_DIR/run-simple.sh
Restart=on-failure
RestartSec=10s
TimeoutStartSec=60s
StandardOutput=journal
StandardError=journal
KillMode=mixed

# Run as root because GPIO access requires elevated privileges
# The application uses hardware SPI and GPIO pins

[Install]
WantedBy=multi-user.target
EOF

echo "Service file created at: $SERVICE_FILE"

# Reload systemd to recognize the new service
echo "Reloading systemd daemon..."
systemctl daemon-reload

# Enable the service to start at boot
echo "Enabling Digital Sun service to start at boot..."
systemctl enable digital-sun.service

# Ask if user wants to start the service now
read -p "Do you want to start the service now? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Starting Digital Sun service..."
    systemctl start digital-sun.service
    sleep 2
    echo
    echo "Service started! Checking status..."
    systemctl status digital-sun.service --no-pager || true
else
    echo "Service not started. You can start it manually with:"
    echo "  sudo systemctl start digital-sun.service"
fi

echo
echo "==================================================================="
echo "Setup complete! Digital Sun will now start automatically at boot."
echo
echo "Useful commands:"
echo "  sudo systemctl status digital-sun    # Check service status"
echo "  sudo systemctl start digital-sun     # Start the service"
echo "  sudo systemctl stop digital-sun      # Stop the service"
echo "  sudo systemctl restart digital-sun   # Restart the service"
echo "  sudo systemctl disable digital-sun   # Disable autostart"
echo "  sudo journalctl -u digital-sun -f    # View live logs"
echo "  sudo journalctl -u digital-sun -n 50 # View last 50 log lines"
echo "==================================================================="
