#include <cstdio>
#include <cstdlib>
#include <dirent.h>
#include <jni.h>

#include "api/backup.hpp"
#include "api/properties.hpp"
#include "api/tweaks.hpp"
#include "api/util.hpp"

extern "C"
{
    #include "api/shell.h"
}

using namespace std;

static static_map_entry property_map[] = {
    // Keys have to be sorted alphabetically!
    {"android_platform_version", &prop_android_platform_version()},
    {"backup_region", &prop_backup_region()},
    {"model_name", &prop_model_name()},
    {"serial_number", &prop_serial_number()},
};

static static_map_entry tweak_map[] = {
    // Keys have to be sorted alphabetically!
    {"language", &tweak_language()},
    {"pal_ntsc_selector", &tweak_pal_ntsc_selector()},
    {"protection", &tweak_protection()},
    {"rec_limit", &tweak_rec_limit()},
    {"rec_limit_4k", &tweak_rec_limit_4k()},
};

static Property *property_by_key(JNIEnv *env, jstring key)
{
    const char *key_ptr = env->GetStringUTFChars(key, 0);
    Property *prop = (Property *) static_map_find(key_ptr, property_map, sizeof(property_map));
    env->ReleaseStringUTFChars(key, key_ptr);
    return prop;
}

static Tweak *tweak_by_key(JNIEnv *env, jstring key)
{
    const char *key_ptr = env->GetStringUTFChars(key, 0);
    Tweak *tweak = (Tweak *) static_map_find(key_ptr, tweak_map, sizeof(tweak_map));
    env->ReleaseStringUTFChars(key, key_ptr);
    return tweak;
}

static void throw_exception(JNIEnv *env, const char *clazz, const char *message)
{
    env->ThrowNew(env->FindClass(clazz), message);
}

static void throw_native_exception(JNIEnv *env, const char *message)
{
    throw_exception(env, "com/github/ma1co/openmemories/tweak/NativeException", message);
}

extern "C" JNIEXPORT void Java_com_github_ma1co_openmemories_tweak_Shell_nativeExec(JNIEnv *env, jclass clazz, jstring command)
{
    const char *command_ptr = env->GetStringUTFChars(command, NULL);
    int err = shell_exec_async(command_ptr);
    if (err)
        throw_native_exception(env, "shell_exec_async failed");
    env->ReleaseStringUTFChars(command, command_ptr);
}

extern "C" JNIEXPORT jint Java_com_github_ma1co_openmemories_tweak_Procfs_nativeFindProcess(JNIEnv *env, jclass clazz, jbyteArray command)
{
    jsize length = env->GetArrayLength(command);
    jbyte *command_ptr = env->GetByteArrayElements(command, NULL);

    DIR *dp = opendir("/proc");
    int result = -1;
    struct dirent *ep;
    while ((ep = readdir(dp))) {
        if (ep->d_type == DT_DIR) {
            int pid = atoi(ep->d_name);
            if (pid) {
                char fn[255];
                snprintf(fn, 255, "/proc/%s/cmdline", ep->d_name);
                FILE *f = fopen(fn, "rb");
                if (f) {
                    char buf[length + 1u];
                    int num = fread(buf, 1, length + 1u, f);
                    fclose(f);
                    if (num == length && !memcmp(buf, command_ptr, (size_t) length)) {
                        result = pid;
                        break;
                    }
                }
            }
        }
    }
    closedir(dp);

    env->ReleaseByteArrayElements(command, command_ptr, 0);

    return result;
}

extern "C" JNIEXPORT jboolean Java_com_github_ma1co_openmemories_tweak_NativeTweak_nativeIsAvailable(JNIEnv *env, jclass clazz, jstring key)
{
    return (jboolean) tweak_by_key(env, key)->is_available();
}

extern "C" JNIEXPORT jboolean Java_com_github_ma1co_openmemories_tweak_NativeTweak_nativeIsEnabled(JNIEnv *env, jclass clazz, jstring key)
{
    return (jboolean) tweak_by_key(env, key)->is_enabled();
}

extern "C" JNIEXPORT void Java_com_github_ma1co_openmemories_tweak_NativeTweak_nativeSetEnabled(JNIEnv *env, jclass clazz, jstring key, jboolean enabled)
{
    try {
        tweak_by_key(env, key)->set_enabled(enabled);
    } catch (const backup_protected_error &e) {
        throw_exception(env, "com/github/ma1co/openmemories/tweak/NativeProtectionException", "");
    } catch (const runtime_error &e) {
        throw_native_exception(env, e.what());
    }
}

extern "C" JNIEXPORT jstring Java_com_github_ma1co_openmemories_tweak_NativeTweak_nativeGetStringValue(JNIEnv *env, jclass clazz, jstring key)
{
    return env->NewStringUTF(tweak_by_key(env, key)->get_string_value().c_str());
}

extern "C" JNIEXPORT jboolean Java_com_github_ma1co_openmemories_tweak_NativeProperty_nativeIsAvailable(JNIEnv *env, jclass clazz, jstring key)
{
    return (jboolean) property_by_key(env, key)->is_available();
}

extern "C" JNIEXPORT jstring Java_com_github_ma1co_openmemories_tweak_NativeProperty_nativeGetStringValue(JNIEnv *env, jclass clazz, jstring key)
{
    return env->NewStringUTF(property_by_key(env, key)->get_string_value().c_str());
}
