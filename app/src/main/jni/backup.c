#include <stdarg.h>
#include <stdlib.h>
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

int32_t Backup_sync_all()
{
    return backup_sync_msg(15, 0);
}

int32_t Backup_protect(uint32_t mode, void *overwrite_data, uint32_t size)
{
    return backup_sync_msg(25, 3, mode, overwrite_data, size);
}

int32_t Backup_senser_cmd_ID1(uint32_t value)
{
    const uint32_t type = 0x3E0166;
    const uint32_t len = 0x24;

    uint32_t *msg;
    osal_valloc_msg_wait(type, (void **) &msg, len, 1);
    memset(msg, 0, len);
    msg[6] = 0xF0603;
    msg[7] = value;

    osal_snd_sync_msg(type, msg);
    uint32_t result = msg[0];

    osal_free_msg(type, msg);
    return result == 0x108 ? 0 : -1;
}
