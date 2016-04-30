#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include "osal.h"
#include "shell.h"

void shell_exec(const char *cmd)
{
    uint32_t type = 0x94020A;

    char *msg;
    osal_valloc_msg_wait(type, (void **) &msg, 4 + strlen(cmd) + 1, 1);

    ((uint32_t *) msg)[0] = 0x940005;
    strcpy(msg + 4, cmd);

    osal_snd_msg(type, msg);
    osal_free_msg(type, msg);
}
