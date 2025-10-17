# Deployment to Raspberry Pi

This guide shows how to deploy Digital Sun from your Mac to Raspberry Pi.

## Prerequisites

1. **SSH access to Raspberry Pi**
   ```bash
   # Test SSH connection
   ssh simonas@raspberrypi.local
   ```

2. **Java installed on Raspberry Pi** (see target-rpi/README.md)
   ```bash
   ssh simonas@raspberrypi.local "java --version"
   ```

3. **rpi_ws281x library installed** (see target-rpi/README.md)

## Deployment Options

### Option 1: Quick Deploy (Build on Mac, Deploy JAR only)

**Fastest** - Only copies the compiled JAR file.

```bash
./deploy-rpi.sh
```

**Customize with environment variables:**
```bash
# Deploy to different host
RPI_HOST=192.168.1.100 ./deploy-rpi.sh

# Deploy to different user
RPI_USER=pi ./deploy-rpi.sh

# Deploy to different directory
RPI_DIR=/opt/digital-sun ./deploy-rpi.sh

# Combine all
RPI_HOST=192.168.1.100 RPI_USER=pi RPI_DIR=/opt/digital-sun ./deploy-rpi.sh
```

### Option 2: Full Sync (Sync source code and build on Pi)

**Best for development** - Syncs all source code and builds on the Pi.

```bash
./sync-rpi.sh
```

**Customize with environment variables:**
```bash
RPI_HOST=192.168.1.100 RPI_USER=pi RPI_DIR=/opt/digital-sun ./sync-rpi.sh
```

## Running on Raspberry Pi

### From your Mac (recommended):

The `deploy-rpi.sh` script automatically runs the application after deployment:

```bash
./deploy-rpi.sh
# Press Ctrl+C to stop the remote process
```

### Stop a running instance:

If the process keeps running after disconnect:

```bash
./stop-rpi.sh
```

### Manually on Raspberry Pi:

```bash
# SSH into Pi
ssh simonas@raspberrypi.local

# Navigate to project directory
cd ~/digital-sun

# Run with optimized settings (recommended)
sudo ./run.sh

# Or run directly
sudo java -jar target-rpi-1.0.0.jar

# To stop, press Ctrl+C
```

## Persistent Configuration

To avoid typing environment variables every time, create a `.env` file:

```bash
# .env (add to .gitignore)
export RPI_HOST=raspberrypi.local
export RPI_USER=simonas
export RPI_DIR=~/digital-sun
```

Then source it before deploying:

```bash
source .env && ./deploy-rpi.sh
```

## Troubleshooting

### SSH Key Setup

If you get password prompts, set up SSH keys:

```bash
# Generate SSH key if you don't have one
ssh-keygen -t ed25519

# Copy key to Raspberry Pi
ssh-copy-id simonas@raspberrypi.local
```

### Host Not Found

If `raspberrypi.local` doesn't work, find the Pi's IP address:

```bash
# On Raspberry Pi
hostname -I

# Then use the IP address
RPI_HOST=192.168.1.100 ./deploy-rpi.sh
```

### Permission Denied on Raspberry Pi

Make sure the gradlew script is executable:

```bash
ssh simonas@raspberrypi.local "cd ~/digital-sun && chmod +x gradlew"
```

## Quick Start

1. **First time setup:**
   ```bash
   # Sync everything and build on Pi
   ./sync-rpi.sh
   ```

2. **Subsequent deploys:**
   ```bash
   # Quick JAR-only deploy
   ./deploy-rpi.sh

   # Or full sync if you changed dependencies/configs
   ./sync-rpi.sh
   ```

3. **Run on Pi:**
   ```bash
   ssh simonas@raspberrypi.local "cd ~/digital-sun && sudo java -jar target-rpi/build/libs/target-rpi-1.0.0.jar"
   ```
