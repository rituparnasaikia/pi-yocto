SUMMARY = "Tiny RPi4 image with Python + Ethernet/Wi-Fi"
LICENSE = "MIT"

inherit core-image

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    "

# Network bits
IMAGE_INSTALL:append = " \
    connman \
    connman-client \
    iproute2 \
    ethtool \
    iw \
    wpa-supplicant \
    "

# Firmware baseline
IMAGE_INSTALL:append = " \
    linux-firmware \
    "

# Python (minimal-ish)
IMAGE_INSTALL:append = " \
    python3-core \
    python3-modules \
    "
