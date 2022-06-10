package io.github.orioncraftmc.launcher;

import io.github.orioncraftmc.launcher.loader.OrionClassLoader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.List;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.spongepowered.asm.util.Constants;

public class OrionLauncher {
    private static final OrionLauncher instance = new OrionLauncher();

    private OrionLauncher() {
    }

    private OrionClassLoader loader;
    private MemoryMappingTree deobfMappingTree;

    private String mixinSide = Constants.SIDE_CLIENT;

    public MemoryMappingTree deobfMappingTree() {
        return deobfMappingTree;
    }

    public OrionClassLoader loader() {
        return loader;
    }

    public static OrionLauncher getInstance() {
        return instance;
    }

    public void init(ClassLoader parentLoader, String mainClassName, List<String> params) throws Throwable {
        loader = new OrionClassLoader(parentLoader);
        loader.init();

        Class<?> mainClass = loader.loadClass(mainClassName);
        String[] args = params.toArray(String[]::new);

        MethodHandles.lookup()
                .findStatic(mainClass, "main", MethodType.methodType(Void.TYPE, String[].class))
                .invoke((Object) args);
    }

    public String mixinSide() {
        return mixinSide;
    }

    public void setMixinSide(String mixinSide) {
        this.mixinSide = mixinSide;
    }

    public void initDeobfMappings(Path obfMapping) throws IOException {
        deobfMappingTree = new MemoryMappingTree();
        MappingReader.read(obfMapping, deobfMappingTree);
    }
}
