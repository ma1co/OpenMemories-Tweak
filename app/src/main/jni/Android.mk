LOCAL_PATH := $(call my-dir)

MODE = ANDROID
PLATFORMDIR = platform
include $(LOCAL_PATH)/$(PLATFORMDIR)/vars.mk

# Compile stub shared libraries which are needed to link libtweak.so. These
# files are already present on the camera.
$(foreach lib, $(LIBS), \
    $(eval include $(CLEAR_VARS)) \
    $(eval LOCAL_MODULE := $(lib)) \
    $(eval LOCAL_SRC_FILES := $(PLATFORMDIR)/$(DRIVERDIR)/$(lib).c) \
    $(eval LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(PLATFORMDIR)) \
    $(eval LOCAL_CFLAGS += $(DEFS) $(WFLAGS) -std=c11) \
    $(eval LOCAL_LDFLAGS += $(LFLAGS)) \
    $(eval include $(BUILD_SHARED_LIBRARY)) \
)

# Compile libtweak.so
include $(CLEAR_VARS)
LOCAL_MODULE := tweak
LOCAL_SRC_FILES := jni.c $(SOURCES)
LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(PLATFORMDIR)
LOCAL_CFLAGS += $(DEFS) $(WFLAGS) -std=c11
LOCAL_SHARED_LIBRARIES := $(LIBS)
include $(BUILD_SHARED_LIBRARY)

# Compile adbd binary
include $(CLEAR_VARS)
CORE_DIR = android_platform_system_core
LOCAL_MODULE := adbd
LOCAL_SRC_FILES := $(addprefix $(CORE_DIR)/, \
    adb/adb.c \
    adb/fdevent.c \
    adb/transport.c \
    adb/transport_local.c \
    adb/transport_usb.c \
    adb/sockets.c \
    adb/services.c \
    adb/file_sync_service.c \
    adb/jdwp_service.c \
    adb/framebuffer_service.c \
    adb/remount_service.c \
    adb/usb_linux_client.c \
    adb/log_service.c \
    adb/utils.c \
    libcutils/properties.c \
    libcutils/socket_inaddr_any_server.c \
    libcutils/socket_local_client.c \
    libcutils/socket_local_server.c \
    libcutils/socket_loopback_client.c \
    libcutils/socket_loopback_server.c \
)
LOCAL_CFLAGS := -I$(LOCAL_PATH)/$(CORE_DIR)/adb -I$(LOCAL_PATH)/$(CORE_DIR)/include \
                -include arch/linux-arm/AndroidConfig.h -U __linux__ -DADB_HOST=0
include $(BUILD_EXECUTABLE)
