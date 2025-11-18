#
# Copyright (C) 2024 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit_only.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit some common Lineage stuff.
$(call inherit-product, vendor/lineage/config/common_full_phone.mk)

# Inherit from peridot device
$(call inherit-product, device/xiaomi/peridot/device.mk)

PRODUCT_NAME := lineage_peridot
PRODUCT_DEVICE := peridot
PRODUCT_MANUFACTURER := Xiaomi
PRODUCT_BRAND := POCO
PRODUCT_MODEL := 24069PC21G

PRODUCT_SYSTEM_NAME := peridot_global
PRODUCT_SYSTEM_DEVICE := peridot

# GMS
PRODUCT_GMS_CLIENTID_BASE := android-xiaomi

# Lunaris
WITH_GMS := true
TARGET_OPTIMIZED_DEXOPT := true
TARGET_ENABLE_BLUR := true
TARGET_HAS_UDFPS := true
TARGET_CUSTOM_UDFPS := true
TARGET_BOOT_ANIMATION_RES := 1080
TARGET_SUPPORTS_QUICK_TAP := true
TARGET_FACE_UNLOCK_SUPPORTED := true

# Boost Framework
LUNARIS_CPU_SMALL_CORES := 0,1,2
LUNARIS_CPU_BIG_CORES := 3,4,5,6,7
LUNARIS_CPU_SYS_BG := 0-3
LUNARIS_CPU_BG := 0-2
LUNARIS_CPU_FG := 0-7
LUNARIS_CPU_LIMIT_BG := 0-2
LUNARIS_CPU_UNLIMIT_UI := 0-7
LUNARIS_CPU_LIMIT_UI := 0-5
LUNARIS_CPU_DISPLAY := 6-7
GPU_FREQS_PATH := /sys/class/kgsl/kgsl-3d0/devfreq/available_frequencies
GPU_MIN_FREQ_PATH := /sys/class/kgsl/kgsl-3d0/devfreq/min_freq

PRODUCT_BUILD_PROP_OVERRIDES += \
    BuildDesc="peridot_global-user 15 AQ3A.240912.001 OS2.0.206.0.VNPMIXM release-keys" \
    BuildFingerprint=POCO/peridot_global/peridot:15/AQ3A.240912.001/OS2.0.206.0.VNPMIXM:user/release-keys \
    DeviceName=$(PRODUCT_SYSTEM_DEVICE) \
    DeviceProduct=$(PRODUCT_SYSTEM_NAME)

PRODUCT_GMS_CLIENTID_BASE := android-xiaomi
