package io.github.orioncraftmc.launcher.transformers.impl.remapper;

import io.github.orioncraftmc.launcher.OrionLauncher;
import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import net.fabricmc.mappingio.tree.MappingTree;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;

public class RemapperTransformer implements OrionClassTransformer {
    private MappingTree mappingTree;
    private OrionRemapper remapper;

    @Override
    public void init() {
        mappingTree = OrionLauncher.getInstance().deobfMappingTree();

        if (mappingTree == null) return;

        remapper = new OrionRemapper(mappingTree);
    }

    @Override
    public byte[] transformClass(String className, byte @Nullable [] classBytes) {
        if (mappingTree == null) return classBytes;
        if (classBytes == null) return null;

        ClassReader classReader = new ClassReader(classBytes);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        ClassRemapper classRemapper = new ClassRemapper(classWriter, remapper);

        classReader.accept(classRemapper, 0);

        return classWriter.toByteArray();
    }
}
