#include <stdarg.h>
#include <stdlib.h>
#include <string.h>
#include "backup.h"
#include "osal.h"

static int32_t backup_sync_msg(uint32_t function, int arg_count, ...)
{
    const uint32_t type = 0x3E014D;
    const uint32_t len = 0x40;

    uint32_t *msg;
    osal_valloc_msg_wait(type, (void **) &msg, len, 1);
    memset(msg, 0, len);
    msg[0] = function;
    msg[2] = arg_count;
    msg[3] = type;

    va_list ap;
    va_start(ap, arg_count);
    for (int i = 0; i < arg_count; i++)
        msg[6 + i] = va_arg(ap, int);
    va_end(ap);

    osal_snd_sync_msg(type, msg);
    int32_t result = msg[1];

    osal_free_msg(type, msg);
    return result;
}

int32_t Backup_get_datasize(uint32_t id)
{
    return backup_sync_msg(0, 1, id);
}

int32_t Backup_get_attribute(uint32_t id)
{
    return backup_sync_msg(2, 1, id);
}

int32_t Backup_read(uint32_t id, void *addr)
{
    return backup_sync_msg(3, 2, id, addr);
}

int32_t Backup_write(uint32_t id, void *addr)
{
    return backup_sync_msg(8, 3, id >> 16, id, addr);
}

void Backup_sync_all()
{
    backup_sync_msg(15, 0);
}

int32_t Backup_protect(uint32_t mode, void *overwrite_data, uint32_t size)
{
    return backup_sync_msg(25, 3, mode, overwrite_data, size);
}

uint32_t Backup_senser_sync_msg(uint16_t function, uint32_t arg0, uint32_t arg1, uint32_t *ret1, uint32_t *ret2)
{
    const uint32_t type = 0x3E0166;
    const uint32_t len = 0x140;

    uint32_t *msg;
    osal_valloc_msg_wait(type, (void **) &msg, len, 1);
    memset(msg, 0, len);
    msg[0] = arg0;
    msg[6] = (function << 16) | 0x603;
    msg[7] = arg1;

    osal_snd_sync_msg(type, msg);
    uint32_t result = msg[0];
    if (ret1) *ret1 = msg[2];
    if (ret2) *ret2 = msg[3];

    osal_free_msg(type, msg);
    return result;
}

int32_t Backup_senser_cmd_ID1(uint32_t value)
{
    return Backup_senser_sync_msg(0xF, 0, value, NULL, NULL) == 0x108 ? 0 : -1;
}

int32_t Backup_senser_cmd_preset_data_read(void **ptr, uint32_t *len)
{
    if (ptr && !*ptr) {
        // alloc & read
        return Backup_senser_sync_msg(5, 1, 1, (void *) ptr, len) == 0x108 ? 0 : -1;
    } else {
        // free
        return Backup_senser_sync_msg(5, 0x10, 0, (void *) ptr, len) == 0x120 ? 0 : -1;
    }
}
