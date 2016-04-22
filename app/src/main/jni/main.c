#include <jni.h>
#include <stdlib.h>
#include "backup.h"

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
    int32_t err = Backup_sync_all();
    if (err)
        throw_exception(env, "Backup_sync_all failed");
}

void Java_com_github_ma1co_openmemories_tweak_Backup_nativeProtect(JNIEnv *env, jclass clazz, jint mode, jbyteArray data)
{
    jbyte *data_ptr = (*env)->GetByteArrayElements(env, data, NULL);
    int32_t err = Backup_protect(mode, data_ptr, (*env)->GetArrayLength(env, data));
    (*env)->ReleaseByteArrayElements(env, data, data_ptr, 0);
    if (err)
        throw_exception(env, "Backup_protect failed");
}

void Java_com_github_ma1co_openmemories_tweak_Backup_nativeSetId1(JNIEnv *env, jclass clazz, jint value)
{
    int32_t err = Backup_senser_cmd_ID1(value);
    if (err)
        throw_exception(env, "Backup_senser_cmd_ID1 failed");
}
