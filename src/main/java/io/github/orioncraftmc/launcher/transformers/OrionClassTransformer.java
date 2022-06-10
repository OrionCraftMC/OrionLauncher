package io.github.orioncraftmc.launcher.transformers;

public interface OrionClassTransformer {
    void init();

    byte[] transformClass(String className, byte[] classBytes);
}
