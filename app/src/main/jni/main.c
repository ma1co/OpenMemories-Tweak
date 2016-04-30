#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "backup.h"

int patch_region(const char *newRegion, char **oldRegion)
{
    printf("Reading backup\n");

    FILE *f = fopen("/setting/Backup.bin", "rb");
    if (!f) {
        printf("fopen failed\n");
        return -1;
    }

    fseek(f, 0, SEEK_END);
    size_t len = ftell(f);
    fseek(f, 0, SEEK_SET);

    void *data = malloc(len);
    if (!data) {
        printf("malloc failed\n");
        return -1;
    }

    fread(data, 1, len, f);
    fclose(f);

    void *region = data + 0xC0;
    if (oldRegion) {
        *oldRegion = malloc(strlen(region) + 1);
        strcpy(*oldRegion, region);
    }
    strcpy(region, newRegion);

    printf("Calling Backup_protect\n");
    int err = Backup_protect(0, data, len);

    free(data);

    if (err) {
        printf("Backup_protect failed\n");
        return -1;
    }

    return 0;
}

int main(int argc, char *argv[])
{
    if (argc != 2) {
        printf("Usage: %s 1|0\n", argv[0]);
        return -1;
    }
    uint32_t flag = atoi(argv[1]) ? 1 : 0;

    printf("Saving backup\n");
    Backup_sync_all();

    char *region = NULL;
    if (!flag) {
        printf("Patching region\n");
        int err = patch_region("", &region);
        if (err) {
            printf("patch_region failed\n");
            return -1;
        }
    }

    printf("Calling Backup_senser_cmd_ID1(%d)\n", flag);
    int err = Backup_senser_cmd_ID1(flag);

    if (region) {
        printf("Resetting region\n");
        int err = patch_region(region, NULL);
        free(region);
        if (err) {
            printf("patch_region failed\n");
            return -1;
        }
    }

    if (err) {
        printf("Backup_senser_cmd_ID1 failed\n");
        return -1;
    }

    printf("Done\n");

    return 0;
}
