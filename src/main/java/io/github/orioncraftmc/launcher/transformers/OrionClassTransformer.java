package io.github.orioncraftmc.launcher.transformers;

import org.jetbrains.annotations.Nullable;

public interface OrionClassTransformer {
    void init();

    byte[] transformClass(String className, byte @Nullable [] classBytes);
}
