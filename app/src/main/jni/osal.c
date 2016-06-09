#include "osal.h"

// These are only stubs, the implementations are available when dynamically linking on the camera
int osal_free_msg(uint32_t type, void *addr) {return 0;};
int osal_snd_msg(uint32_t type, void *addr) {return 0;};
int osal_snd_sync_msg(uint32_t type, void *addr) {return 0;};
int osal_valloc_msg_wait(uint32_t type, void **addr, uint32_t len, uint32_t flag) {return 0;};
