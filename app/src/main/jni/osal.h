#pragma once
#include <stdint.h>

int osal_free_msg(uint32_t type, void *addr);
int osal_snd_msg(uint32_t type, void *addr);
int osal_snd_sync_msg(uint32_t type, void *addr);
int osal_valloc_msg_wait(uint32_t type, void **addr, uint32_t len, uint32_t flag);
