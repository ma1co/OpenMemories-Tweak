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
