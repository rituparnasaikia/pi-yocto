<h1>pi-yocto</h1>

<p><strong>Yocto workspace to build a minimal Raspberry Pi 4 (64-bit) Linux image with Python, Ethernet, and Wi-Fi.</strong></p>

<p>
This repository is a complete Yocto workspace that uses upstream layers as
<em>git submodules</em> and a project-specific layer (<code>meta-my</code>)
containing the custom image recipe.
</p>

<hr>

<h2>Table of Contents</h2>
<ul>
  <li>1. Scope</li>
  <li>2. Requirements</li>
  <li>3. Repository Layout</li>
  <li>4. Quickstart</li>
  <li>5. Detailed Build Procedure</li>
  <li>6. Image Design</li>
  <li>7. Flash and Boot</li>
  <li>8. Post-Boot Validation</li>
  <li>9. Exceptions & Fixes</li>
  <li>10. Troubleshooting Appendix</li>
</ul>

<hr>

<h2>1. Scope</h2>
<p>
This project provides a reproducible Yocto build environment for generating a
minimal Linux image for <strong>Raspberry Pi 4 (64-bit)</strong> with:
</p>
<ul>
  <li>Python 3 runtime</li>
  <li>Ethernet networking (DHCP)</li>
  <li>Wi-Fi networking</li>
  <li>Minimal footprint for headless embedded systems</li>
</ul>

<h2>2. Requirements</h2>

<h3>Hardware</h3>
<ul>
  <li>Raspberry Pi 4</li>
  <li>microSD card (8 GB or larger)</li>
</ul>

<h3>Host OS</h3>
<ul>
  <li>Linux host (Ubuntu / Debian recommended)</li>
  <li>Ubuntu 24.04 may require AppArmor configuration for BitBake</li>
</ul>

<h3>Tools</h3>
<ul>
  <li>git</li>
  <li>Yocto build prerequisites (compiler, Python, etc.)</li>
</ul>

<h2>3. Repository Layout</h2>

<pre><code>pi-yocto/
├── meta-my/                # project-specific Yocto layer
├── poky/                   # Yocto Project (submodule)
├── meta-raspberrypi/       # Raspberry Pi BSP (submodule)
├── meta-openembedded/      # OE layers (submodule)
├── .gitmodules
├── .gitignore
└── README.*</code></pre>

<h2>4. Quickstart</h2>

<pre><code>git clone https://github.com/rituparnasaikia/pi-yocto.git
cd pi-yocto
git submodule update --init --recursive</code></pre>

<pre><code>source poky/oe-init-build-env build</code></pre>

<pre><code>bitbake-layers add-layer ../meta-raspberrypi
bitbake-layers add-layer ../meta-openembedded/meta-oe
bitbake-layers add-layer ../meta-openembedded/meta-networking
bitbake-layers add-layer ../meta-openembedded/meta-python
bitbake-layers add-layer ../meta-my

bitbake rpi4-minpy-net</code></pre>

<h2>5. Detailed Build Procedure</h2>

<pre><code>MACHINE = "raspberrypi4-64"

DISTRO_FEATURES:append = " wifi systemd"
VIRTUAL-RUNTIME_init_manager = "systemd"

IMAGE_FSTYPES = "wic.bz2"

KERNEL_MODULE_AUTOLOAD:append = " brcmfmac brcmutil cfg80211 "</code></pre>

<h2>6. Image Design</h2>

<ul>
  <li>ConnMan for lightweight network management</li>
  <li>wpa_supplicant and iw for Wi-Fi</li>
  <li>linux-firmware for Broadcom Wi-Fi chips</li>
  <li>python3-core for minimal Python runtime</li>
</ul>

<pre><code>SUMMARY = "Tiny RPi4 image with Python + Ethernet + Wi-Fi"
LICENSE = "MIT"

inherit core-image

IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_INSTALL:append = " \
  connman connman-client \
  wpa-supplicant iw \
  iproute2 ethtool \
  linux-firmware \
  python3-core \
"</code></pre>

<h2>7. Flash and Boot</h2>

<pre><code>bunzip2 -c build/tmp/deploy/images/raspberrypi4-64/*rpi4-minpy-net*.wic.bz2 \
 | sudo dd of=/dev/sdX bs=4M conv=fsync status=progress</code></pre>

<p><strong>Warning:</strong> Ensure <code>/dev/sdX</code> is the SD card, not your system disk.</p>

<h2>8. Post-Boot Validation</h2>

<pre><code>python3 --version
ip a
connmanctl technologies</code></pre>

<h2>9. Exceptions Encountered & Fixes</h2>

<h3>Ubuntu AppArmor user namespace restriction</h3>

<pre><code>sudo sysctl -w kernel.apparmor_restrict_unprivileged_userns=0</code></pre>

<h3>systemd-networkd RPROVIDES failure</h3>

<p>Remove from <code>local.conf</code>:</p>

<pre><code>IMAGE_INSTALL:append = " systemd-networkd systemd-resolved "
SYSTEMD_AUTO_ENABLE:append = " systemd-networkd systemd-resolved "</code></pre>

<p>ConnMan is used instead.</p>

<h2>10. Troubleshooting Appendix</h2>

<h3>GitHub unreachable on host</h3>
<pre><code>ip addr
ip route</code></pre>

<h3>Recipe not visible</h3>
<pre><code>bitbake-layers show-recipes | grep rpi4-minpy-net</code></pre>

<h3>Wi-Fi not working</h3>
<pre><code>lsmod | egrep "brcmfmac|cfg80211"</code></pre>

<h3>Git push rejected</h3>
<pre><code>git fetch origin
git rebase origin/main
git push -u origin main</code></pre>

<hr>

<p><strong>Maintainer:</strong> rituparnasaikia</p>
<p><strong>Project:</strong> Minimal Yocto image for Raspberry Pi 4</p>
