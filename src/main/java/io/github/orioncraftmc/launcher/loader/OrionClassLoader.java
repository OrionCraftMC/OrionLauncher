package io.github.orioncraftmc.launcher.loader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.orioncraftmc.launcher.OrionLauncher;
import io.github.orioncraftmc.launcher.mixin.obfuscation.DeobfuscatingReferenceRemapper;
import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.MixinClassTransformer;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class OrionClassLoader extends ClassLoader {
    private final List<OrionClassTransformer> transformers = List.of(
            new MixinClassTransformer()
    );

    private final String[] excludedPackages = new String[]{
            "java.",
            "org.spongepowered.asm.",
            "io.github.orioncraftmc.launcher."
    };

    public OrionClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void init() {
        initTransformers();
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

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream resource = super.getResourceAsStream(name);
        if (OrionLauncher.getInstance().deobfMappingTree() != null && resource != null && name.endsWith(".json")) {
            System.out.println("Found mixin resource " + name);
            try {
                byte[] bytes = resource.readAllBytes();
                resource.close();

                JsonObject json = new Gson().fromJson(new String(bytes), JsonObject.class);

                json.addProperty("refmapWrapper", DeobfuscatingReferenceRemapper.class.getSimpleName());

                return new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                new RuntimeException("Unable to inject deobfuscation remapper", e).printStackTrace();
            }
        }

        return resource;
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
