#!/usr/bin/env bash
set -euo pipefail

# Flash latest DietPi (RPi ARMv8 64-bit) to an SD card on macOS.
# Usage: ./flash-dietpi.sh [/dev/diskN]

DOWNLOAD_URL="https://dietpi.com/downloads/images/DietPi_RPi234-ARMv8-Bookworm.img.xz"
CACHE_DIR="$(cd "$(dirname "$0")" && pwd)/.cache"
ARCHIVE="$CACHE_DIR/DietPi_RPi234-ARMv8-Bookworm.img.xz"
IMG_DIR="$CACHE_DIR/img"

# --- Dependencies ---
if ! command -v xz &>/dev/null; then
    echo "Installing xz via Homebrew..."
    brew install xz
fi

# --- Download (cached) ---
mkdir -p "$CACHE_DIR"
if [[ -f "$ARCHIVE" ]]; then
    echo "Using cached download: $ARCHIVE"
else
    echo "Downloading DietPi image..."
    curl -L --progress-bar -o "$ARCHIVE" "$DOWNLOAD_URL"
fi

# --- Extract (cached) ---
IMG_FILE=$(find "$IMG_DIR" -name '*.img' 2>/dev/null | head -1 || true)
if [[ -z "$IMG_FILE" ]]; then
    echo "Extracting..."
    mkdir -p "$IMG_DIR"
    xz -dk "$ARCHIVE"
    mv "${ARCHIVE%.xz}" "$IMG_DIR/"
    IMG_FILE=$(find "$IMG_DIR" -name '*.img' | head -1)
fi
if [[ -z "$IMG_FILE" ]]; then
    echo "Error: no .img file found in archive."
    exit 1
fi
echo "Image: $(basename "$IMG_FILE")"

# --- Select disk ---
if [[ $# -ge 1 ]]; then
    DISK="$1"
else
    echo ""
    echo "Available disks:"
    diskutil list physical | grep -v "^/dev/disk0"
    echo ""
    # Default to the first non-disk0 external/removable physical disk
    DEFAULT_DISK=$(diskutil list physical | grep "^/dev/disk" | grep -v "/dev/disk0" | head -1 | awk '{print $1}')
    if [[ -n "$DEFAULT_DISK" ]]; then
        read -rp "Enter target disk [$DEFAULT_DISK]: " DISK
        DISK="${DISK:-$DEFAULT_DISK}"
    else
        read -rp "Enter target disk (e.g. /dev/disk4): " DISK
    fi
fi

if [[ ! "$DISK" =~ ^/dev/disk[0-9]+$ ]]; then
    echo "Error: '$DISK' doesn't look like a valid disk path."
    exit 1
fi

if [[ "$DISK" == "/dev/disk0" ]]; then
    echo "Error: refusing to write to /dev/disk0 (boot drive)."
    exit 1
fi

echo ""
echo "*** WARNING: ALL DATA ON $DISK WILL BE ERASED ***"
diskutil info "$DISK" | grep -E "Device / Media Name|Disk Size|Volume Name" || true
echo ""
read -rp "Continue? [y/N] " CONFIRM
if [[ "$CONFIRM" != "y" ]]; then
    echo "Aborted."
    exit 1
fi

# --- Flash ---
RDISK="${DISK/disk/rdisk}"

echo "Unmounting $DISK..."
diskutil unmountDisk "$DISK"

echo "Flashing to $RDISK (this takes a few minutes)..."
dd if="$IMG_FILE" of="$RDISK" bs=4m status=progress

echo "Flushing..."
sync

# --- WiFi setup ---
echo ""
echo "Mounting boot partition for WiFi configuration..."
sleep 2  # wait for OS to recognise partitions
diskutil mountDisk "$DISK"

# Find the boot partition mount point
BOOT_MOUNT=$(diskutil info "${DISK}s1" 2>/dev/null | grep "Mount Point" | awk -F: '{print $2}' | xargs)
if [[ -z "$BOOT_MOUNT" ]]; then
    echo "Warning: could not find boot partition. Skipping WiFi setup."
else
    WIFI_CONF="$BOOT_MOUNT/dietpi-wifi.txt"
    if [[ -f "$WIFI_CONF" ]]; then
        read -rp "Set up WiFi? [Y/n] " SETUP_WIFI
        SETUP_WIFI="${SETUP_WIFI:-y}"
        if [[ "$SETUP_WIFI" =~ ^[Yy]$ ]]; then
            read -rp "WiFi SSID: " WIFI_SSID
            read -rsp "WiFi password: " WIFI_PASS
            echo ""

            sed -i '' "s|^aWIFI_SSID\[0\]=.*|aWIFI_SSID[0]='${WIFI_SSID}'|" "$WIFI_CONF"
            sed -i '' "s|^aWIFI_KEY\[0\]=.*|aWIFI_KEY[0]='${WIFI_PASS}'|" "$WIFI_CONF"
            sed -i '' "s|^aWIFI_COUNTRY_CODE=.*|aWIFI_COUNTRY_CODE=LT|" "$WIFI_CONF"

            # Enable WiFi in dietpi.txt
            DIETPI_CONF="$BOOT_MOUNT/dietpi.txt"
            if [[ -f "$DIETPI_CONF" ]]; then
                sed -i '' "s|^AUTO_SETUP_NET_WIFI_ENABLED=.*|AUTO_SETUP_NET_WIFI_ENABLED=1|" "$DIETPI_CONF"
                echo "WiFi enabled in dietpi.txt"
            fi

            echo "WiFi configured for SSID: $WIFI_SSID"
        fi
    else
        echo "Warning: dietpi-wifi.txt not found on boot partition."
    fi
fi

echo "Ejecting $DISK..."
diskutil eject "$DISK"

echo ""
echo "Done! SD card is ready. Insert it into the Raspberry Pi."
