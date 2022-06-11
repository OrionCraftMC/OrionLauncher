package io.github.orioncraftmc.launcher.loader;

import com.google.common.collect.Lists;
import io.github.orioncraftmc.launcher.OrionLauncher;
import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.exposer.AccessExposerTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.mixin.MixinClassTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.remapper.RemapperTransformer;
import java.io.InputStream;
import java.util.*;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.jetbrains.annotations.Nullable;

public class OrionClassLoader extends ClassLoader {
    private final List<OrionClassTransformer> transformers = List.of(
            new AccessExposerTransformer(),
            new RemapperTransformer(),
            new MixinClassTransformer()
    );

    public List<OrionClassTransformer> transformers() {
        return transformers;
    }

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

    public byte @Nullable [] getUnmodifiedClassBytes(String name) {
        String finalName = name.replace('.', '/');

        MemoryMappingTree deobfTree = OrionLauncher.getInstance().deobfMappingTree();
        if (deobfTree != null) {
            String obfName = deobfTree.mapClassName(finalName, deobfTree.getMaxNamespaceId() - 1, deobfTree.getMinNamespaceId());
            if (obfName != null) {
                finalName = obfName;
            }
        }

        try (InputStream resourceAsStream = OrionLauncher.getInstance().loader()
                .getResourceAsStream(finalName + ".class")) {
            return Objects.requireNonNull(resourceAsStream).readAllBytes();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (isClassExcludedFromTransform(name)) {
            return super.loadClass(name);
        }

        byte[] classBytes = getUnmodifiedClassBytes(name);
        byte[] bytes = transformClass(name, classBytes);

        if (bytes == null) throw new ClassNotFoundException(name);
        return defineClass(name, bytes, 0, bytes.length);
    }

    private byte[] transformClass(String name, byte @Nullable [] original) {
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
