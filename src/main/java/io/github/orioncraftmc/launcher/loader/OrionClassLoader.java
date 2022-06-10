package io.github.orioncraftmc.launcher.loader;

import com.google.common.collect.Lists;
import io.github.orioncraftmc.launcher.OrionLauncher;
import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.MixinClassTransformer;
import java.io.InputStream;
import java.util.*;

public class OrionClassLoader extends ClassLoader {
    private final List<OrionClassTransformer> transformers = List.of(
            new MixinClassTransformer()
    );

    private final ArrayList<String> excludedPackages = Lists.newArrayList(
            "java.",
            "jdk.",
            "javax.",

            "sun.",
            "com.sun.",

            "org.spongepowered.asm.",
            "io.github.orioncraftmc.launcher."
    );

    public OrionClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void init() {
        initTransformers();
    }

    public void addExcludedPackage(String packageName) {
        excludedPackages.add(packageName);
    }

    public void addExcludedPackages(List<String> packageNames) {
        excludedPackages.addAll(packageNames);
    }

    private void initTransformers() {
        for (OrionClassTransformer transformer : transformers) {
            transformer.init();
        }
    }

    public byte[] getUnmodifiedClassBytes(String name) throws ClassNotFoundException {
        try (InputStream resourceAsStream = OrionLauncher.getInstance().loader()
                .getResourceAsStream(name.replace('.', '/') + ".class")) {
            return Objects.requireNonNull(resourceAsStream).readAllBytes();
        } catch (Exception e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (isClassExcludedFromTransform(name)) {
            return super.loadClass(name);
        }

        byte[] bytes = transformClass(name, getUnmodifiedClassBytes(name));
        return defineClass(name, bytes, 0, bytes.length);
    }

    private byte[] transformClass(String name, byte[] original) {
        byte[] finalBytes = original;
        for (OrionClassTransformer transformer : transformers) {
            finalBytes = transformer.transformClass(name, finalBytes);
        }

        return finalBytes;
    }

    private boolean isClassExcludedFromTransform(String name) {
        boolean isExcluded = false;
        for (String excludedPackage : excludedPackages) {
            if (name.startsWith(excludedPackage)) {
                isExcluded = true;
                break;
            }
        }
        return isExcluded;
    }

    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return loadClass(name);
    }
}
