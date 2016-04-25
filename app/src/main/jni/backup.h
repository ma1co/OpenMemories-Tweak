#pragma once
#include <stdint.h>

int32_t Backup_get_datasize(uint32_t id);
int32_t Backup_get_attribute(uint32_t id);
int32_t Backup_read(uint32_t id, void *addr);
int32_t Backup_write(uint32_t id, void *addr);
void Backup_sync_all();
int32_t Backup_protect(uint32_t mode, void *overwrite_data, uint32_t size);
int32_t Backup_senser_cmd_ID1(uint32_t value);
