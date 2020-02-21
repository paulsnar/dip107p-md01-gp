#!/bin/sh

# -Xint disables the JIT compiler which makes for more predictable performance
# -XX:+UseEpsilonGC functionally disables garbage collection
exec java -Xint -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC Runner $@
