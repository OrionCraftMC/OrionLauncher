package io.github.orioncraftmc.launcher.loader;

import com.google.common.collect.Lists;
import io.github.orioncraftmc.launcher.OrionLauncher;
import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.exposer.AccessExposerTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.mixin.MixinClassTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.remapper.RemapperTransformer;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.*;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrionClassLoader extends SecureClassLoader {
    private final List<OrionClassTransformer> transformers = List.of(
            new RemapperTransformer(),
            new AccessExposerTransformer(),
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
        try (InputStream resourceAsStream = OrionLauncher.getInstance().loader()
                .getResourceAsStream(getClassFileName(name))) {
            return Objects.requireNonNull(resourceAsStream).readAllBytes();
        } catch (Exception e) {
            return null;
        }
    }

    @NotNull
    private static String getClassFileName(String name) {
        String finalName = name.replace('.', '/');
        finalName = remapClassName(finalName, true);

        return finalName + ".class";
    }

    private static String remapClassName(String finalName, boolean getObfuscatedName) {
        MemoryMappingTree deobfTree = OrionLauncher.getInstance().deobfMappingTree();
        if (deobfTree == null) return finalName;

        int namedNs = deobfTree.getMaxNamespaceId() - 1;
        int srcNs = deobfTree.getMinNamespaceId();
        String obfName = deobfTree.mapClassName(finalName, getObfuscatedName ? namedNs : srcNs,
                getObfuscatedName ? srcNs : namedNs);
        if (obfName != null) {
            finalName = obfName;
        }
        return finalName;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (isClassExcludedFromTransform(name)) {
            return super.loadClass(name);
        }

        // Check if we have loaded the class already, if so, return it directly
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) return loadedClass;

        byte[] classBytes = getUnmodifiedClassBytes(name);
        name = remapClassName(name, false);

        byte[] bytes = transformClass(name, classBytes);

        if (bytes == null) throw new ClassNotFoundException(name);

        URL url = OrionLauncher.getInstance().loader().getResource(getClassFileName(name));
        CodeSource cs = new CodeSource(url, (Certificate[]) null);

        return defineClass(name, bytes, 0, bytes.length, cs);
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
