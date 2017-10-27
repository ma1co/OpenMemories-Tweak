#include <cstdlib>

#include "api/tweaks.hpp"

int main(int argc, char *argv[])
{
    if (argc != 2)
        return EXIT_FAILURE;
    bool enabled = atoi(argv[1]);

    try {
        Tweak &tweak = tweak_protection_advanced();
        if (tweak.is_available())
            tweak.set_enabled(enabled);
    } catch (...) {
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}
