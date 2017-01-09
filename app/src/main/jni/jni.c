#include <dirent.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include "api/shell.h"
#include "drivers/backup.h"
#include "drivers/backup_senser.h"

static void throw_exception(JNIEnv *env, const char *message)
{
    (*env)->ThrowNew(env, (*env)->FindClass(env, "com/github/ma1co/openmemories/tweak/NativeException"), message);
}

JNIEXPORT jint Java_com_github_ma1co_openmemories_tweak_Backup_nativeGetSize(JNIEnv *env, jclass clazz, jint id)
{
    int size = Backup_get_datasize(id);
    if (size < 0)
        throw_exception(env, "Backup_get_datasize failed");
    return size;
}

JNIEXPORT jint Java_com_github_ma1co_openmemories_tweak_Backup_nativeGetAttribute(JNIEnv *env, jclass clazz, jint id)
{
    int attr = Backup_get_attribute(id);
    if (attr < 0)
        throw_exception(env, "Backup_get_attribute failed");
    return attr;
}

JNIEXPORT jbyteArray Java_com_github_ma1co_openmemories_tweak_Backup_nativeRead(JNIEnv *env, jclass clazz, jint id)
{
    jbyteArray data = (*env)->NewByteArray(env, Backup_get_datasize(id));
    jbyte *data_ptr = (*env)->GetByteArrayElements(env, data, NULL);
    int bytes_read = Backup_read(id, data_ptr);
    (*env)->ReleaseByteArrayElements(env, data, data_ptr, 0);
    if (bytes_read < 0)
        throw_exception(env, "Backup_read failed");
    return data;
}

JNIEXPORT void Java_com_github_ma1co_openmemories_tweak_Backup_nativeWrite(JNIEnv *env, jclass clazz, jint id, jbyteArray data)
{
    if ((*env)->GetArrayLength(env, data) != Backup_get_datasize(id))
        throw_exception(env, "Wrong array size");
    jbyte *data_ptr = (*env)->GetByteArrayElements(env, data, NULL);
    int bytes_written = Backup_write(id >> 16, id, data_ptr);
    (*env)->ReleaseByteArrayElements(env, data, data_ptr, 0);
    if (bytes_written < 0)
        throw_exception(env, "Backup_write failed");
}

JNIEXPORT void Java_com_github_ma1co_openmemories_tweak_Backup_nativeSync(JNIEnv *env, jclass clazz)
{
    Backup_sync_all();
}

JNIEXPORT void Java_com_github_ma1co_openmemories_tweak_Backup_nativeSetId1(JNIEnv *env, jclass clazz, jbyte value)
{
    int err = backup_senser_cmd_ID1(value, NULL);
    if (err)
        throw_exception(env, "backup_senser_cmd_ID1 failed");
}

JNIEXPORT jstring Java_com_github_ma1co_openmemories_tweak_Backup_nativeGetRegion(JNIEnv *env, jclass clazz)
{
    backup_senser_preset_data_status status;
    int err = backup_senser_cmd_preset_data_status(&status);
    if (err)
        throw_exception(env, "backup_senser_cmd_preset_data_status failed");
    return (*env)->NewStringUTF(env, status.region);
}

JNIEXPORT jbyteArray Java_com_github_ma1co_openmemories_tweak_Backup_nativeReadPresetData(JNIEnv *env, jclass clazz)
{
    backup_senser_preset_data_status status;
    int err = backup_senser_cmd_preset_data_status(&status);
    if (err)
        throw_exception(env, "backup_senser_cmd_preset_data_status failed");

    jbyteArray res = (*env)->NewByteArray(env, status.size);
    jbyte *res_ptr = (*env)->GetByteArrayElements(env, res, NULL);

    err = backup_senser_cmd_preset_data_read(1, res_ptr, status.size);
    if (err)
        throw_exception(env, "backup_senser_cmd_preset_data_read failed");

    (*env)->ReleaseByteArrayElements(env, res, res_ptr, 0);

    return res;
}

JNIEXPORT void Java_com_github_ma1co_openmemories_tweak_Shell_nativeExec(JNIEnv *env, jclass clazz, jstring command)
{
    const char *command_ptr = (*env)->GetStringUTFChars(env, command, NULL);
    shell_exec_async(command_ptr);
    (*env)->ReleaseStringUTFChars(env, command, command_ptr);
}

JNIEXPORT jint Java_com_github_ma1co_openmemories_tweak_Procfs_nativeFindProcess(JNIEnv *env, jclass clazz, jbyteArray command)
{
    jsize length = (*env)->GetArrayLength(env, command);
    jbyte *command_ptr = (*env)->GetByteArrayElements(env, command, NULL);

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
                    char buf[length + 1];
                    int num = fread(buf, 1, length + 1, f);
                    fclose(f);
                    if (num == length && !memcmp(buf, command_ptr, length)) {
                        result = pid;
                        break;
                    }
                }
            }
        }
    }
    closedir(dp);

    (*env)->ReleaseByteArrayElements(env, command, command_ptr, 0);

    return result;
}
