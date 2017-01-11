# Expects the following variables to be set:
#  BUILDDIR: Path to the build dir
#  MAIN: Name of the executable

# This executable will only be called on gen3 devices
MODE = MAIN
GEN = 3
PLATFORMDIR = platform
CSOURCES = main.c

.PHONY: all
all: $(BUILDDIR)/$(MAIN)

include $(PLATFORMDIR)/build.mk

$(BUILDDIR)/$(MAIN): $(OBJS) $(LIBOBJS)
	$(CXX) $(LDFLAGS) -L$(BUILDDIR)/$(LIBDIR) $(OBJS) $(LIBS:%=-l%) -o $@
