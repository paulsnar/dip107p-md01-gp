#!/bin/sh

# -Xint disables the JIT compiler which makes for more predictable performance
exec java -Xint Runner $@
