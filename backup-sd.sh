#!/usr/bin/env bash
set -euo pipefail

# Back up an SD card to a compressed .img.gz file on macOS.
# Only copies up to the end of the last partition, skipping empty trailing space.
# Usage: ./backup-sd.sh [/dev/diskN]

BACKUP_DIR="$(cd "$(dirname "$0")" && pwd)/backups"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
mkdir -p "$BACKUP_DIR"

# --- Select disk ---
if [[ $# -ge 1 ]]; then
    DISK="$1"
else
    echo "Available disks:"
    diskutil list physical | grep -v "^/dev/disk0"
    echo ""
    DEFAULT_DISK=$(diskutil list physical | grep "^/dev/disk" | grep -v "/dev/disk0" | head -1 | awk '{print $1}')
    if [[ -n "$DEFAULT_DISK" ]]; then
        read -rp "Enter source disk [$DEFAULT_DISK]: " DISK
        DISK="${DISK:-$DEFAULT_DISK}"
    else
        read -rp "Enter source disk (e.g. /dev/disk4): " DISK
    fi
fi

if [[ ! "$DISK" =~ ^/dev/disk[0-9]+$ ]]; then
    echo "Error: '$DISK' doesn't look like a valid disk path."
    exit 1
fi

if [[ "$DISK" == "/dev/disk0" ]]; then
    echo "Error: refusing to read /dev/disk0 (boot drive)."
    exit 1
fi

# --- Calculate copy size ---
COPY_COUNT=""
LAST_PARTITION=$(diskutil list "$DISK" | grep "^ " | tail -1 | awk '{print $NF}')
if [[ -n "$LAST_PARTITION" ]]; then
    PART_OFFSET=$(diskutil info "/dev/$LAST_PARTITION" | grep "Partition Offset" | awk -F'[()]' '{print $2}' | awk '{print $1}')
    PART_SIZE=$(diskutil info "/dev/$LAST_PARTITION" | grep "Disk Size" | awk -F'[()]' '{print $2}' | awk '{print $1}')
    if [[ -n "$PART_OFFSET" && -n "$PART_SIZE" ]]; then
        TOTAL_BYTES=$(( PART_OFFSET + PART_SIZE ))
        TOTAL_MB=$(( (TOTAL_BYTES + 1048575) / 1048576 ))
        DISK_BYTES=$(diskutil info "$DISK" | grep "Disk Size" | awk -F'[()]' '{print $2}' | awk '{print $1}')
        DISK_MB=$(( (DISK_BYTES + 1048575) / 1048576 ))
        echo ""
        echo "Partitions end at ${TOTAL_MB} MB (full disk: ${DISK_MB} MB)"
        COPY_COUNT=$TOTAL_MB
    fi
fi

echo ""
diskutil info "$DISK" | grep -E "Device / Media Name|Disk Size|Volume Name" || true
echo ""

OUTFILE="$BACKUP_DIR/sd-backup-$TIMESTAMP.img.gz"
echo "Will save to: $OUTFILE"

# Estimate copy size and duration
SPEED_MB=46
if [[ -n "$COPY_COUNT" ]]; then
    COPY_MB=$COPY_COUNT
    echo "Copying ${COPY_MB} MB (only up to end of last partition)"
else
    DISK_BYTES=$(diskutil info "$DISK" | grep "Disk Size" | awk -F'[()]' '{print $2}' | awk '{print $1}')
    COPY_MB=$(( (DISK_BYTES + 1048575) / 1048576 ))
    echo "Copying entire disk: ${COPY_MB} MB"
fi
COPY_GB=$(awk "BEGIN {printf \"%.1f\", $COPY_MB / 1024}")
EST_SECS=$(( COPY_MB / SPEED_MB ))
EST_MINS=$(( EST_SECS / 60 ))
echo "Estimated time: ~${EST_MINS} minutes (${COPY_GB} GB at ~${SPEED_MB} MB/s)"
read -rp "Continue? [y/N] " CONFIRM
if [[ "$CONFIRM" != "y" ]]; then
    echo "Aborted."
    exit 1
fi

RDISK="${DISK/disk/rdisk}"

echo "Unmounting $DISK..."
diskutil unmountDisk "$DISK"

echo "Reading from $RDISK..."
if [[ -n "$COPY_COUNT" ]]; then
    sudo dd if="$RDISK" bs=1m count="$COPY_COUNT" status=progress | gzip > "$OUTFILE"
else
    sudo dd if="$RDISK" bs=1m status=progress | gzip > "$OUTFILE"
fi

echo "Ejecting $DISK..."
diskutil eject "$DISK"

SIZE=$(du -h "$OUTFILE" | cut -f1)
echo ""
echo "Done! Backup saved to $OUTFILE ($SIZE)"
