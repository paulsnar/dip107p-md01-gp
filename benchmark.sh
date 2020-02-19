#!/bin/sh
ninja
# -Xint disables the JIT compiler which makes for more predictable performance
exec java -Xint Runner
