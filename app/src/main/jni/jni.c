#include <dirent.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include "backup.h"
#include "shell.h"

static void throw_exception(JNIEnv *env, const char *message)
{
    (*env)->ThrowNew(env, (*env)->FindClass(env, "com/github/ma1co/openmemories/tweak/NativeException"), message);
}

jint Java_com_github_ma1co_openmemories_tweak_Backup_nativeGetSize(JNIEnv *env, jclass clazz, jint id)
{
    int32_t size = Backup_get_datasize(id);
    if (size < 0)
        throw_exception(env, "Backup_get_datasize failed");
    return size;
}

jint Java_com_github_ma1co_openmemories_tweak_Backup_nativeGetAttribute(JNIEnv *env, jclass clazz, jint id)
{
    int32_t attr = Backup_get_attribute(id);
    if (attr < 0)
        throw_exception(env, "Backup_get_attribute failed");
    return attr;
}

jbyteArray Java_com_github_ma1co_openmemories_tweak_Backup_nativeRead(JNIEnv *env, jclass clazz, jint id)
{
    jbyteArray data = (*env)->NewByteArray(env, Backup_get_datasize(id));
    jbyte *data_ptr = (*env)->GetByteArrayElements(env, data, NULL);
    int32_t bytes_read = Backup_read(id, data_ptr);
    (*env)->ReleaseByteArrayElements(env, data, data_ptr, 0);
    if (bytes_read < 0)
        throw_exception(env, "Backup_read failed");
    return data;
}

void Java_com_github_ma1co_openmemories_tweak_Backup_nativeWrite(JNIEnv *env, jclass clazz, jint id, jbyteArray data)
{
    if ((*env)->GetArrayLength(env, data) != Backup_get_datasize(id))
        throw_exception(env, "Wrong array size");
    jbyte *data_ptr = (*env)->GetByteArrayElements(env, data, NULL);
    int32_t bytes_written = Backup_write(id, data_ptr);
    (*env)->ReleaseByteArrayElements(env, data, data_ptr, 0);
    if (bytes_written < 0)
        throw_exception(env, "Backup_write failed");
}

void Java_com_github_ma1co_openmemories_tweak_Backup_nativeSync(JNIEnv *env, jclass clazz)
{
    Backup_sync_all();
}

void Java_com_github_ma1co_openmemories_tweak_Backup_nativeSetId1(JNIEnv *env, jclass clazz, jint value)
{
    int32_t err = Backup_senser_cmd_ID1(value);
    if (err)
        throw_exception(env, "Backup_senser_cmd_ID1 failed");
}

jbyteArray Java_com_github_ma1co_openmemories_tweak_Backup_nativeReadPresetData(JNIEnv *env, jclass clazz)
{
    void *data = NULL;
    uint32_t len = 0;
    int32_t err = Backup_senser_cmd_preset_data_read(&data, &len);
    if (err)
        throw_exception(env, "Backup_senser_cmd_preset_data_read (read) failed");

    jbyteArray res = (*env)->NewByteArray(env, len);
    jbyte *res_ptr = (*env)->GetByteArrayElements(env, res, NULL);

    FILE *f = fopen("/dev/mem", "rb");
    fseek(f, (long) data, SEEK_SET);
    fread(res_ptr, 1, len, f);
    fclose(f);

    (*env)->ReleaseByteArrayElements(env, res, res_ptr, 0);

    err = Backup_senser_cmd_preset_data_read(&data, &len);
    if (err)
        throw_exception(env, "Backup_senser_cmd_preset_data_read (free) failed");

    return res;
}

void Java_com_github_ma1co_openmemories_tweak_Shell_nativeExec(JNIEnv *env, jclass clazz, jstring command)
{
    const char *command_ptr = (*env)->GetStringUTFChars(env, command, NULL);
    shell_exec(command_ptr);
    (*env)->ReleaseStringUTFChars(env, command, command_ptr);
}

jint Java_com_github_ma1co_openmemories_tweak_Procfs_nativeFindProcess(JNIEnv *env, jclass clazz, jbyteArray command)
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
