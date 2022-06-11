package io.github.orioncraftmc.launcher.transformers.impl.remapper;

import net.fabricmc.mappingio.tree.MappingTree;
import org.objectweb.asm.commons.Remapper;

public class OrionRemapper extends Remapper {
    private final MappingTree mappingTree;
    private final int dest;
    private final int src;

    // Remap obfuscated to deobfuscated names
    public OrionRemapper(MappingTree mappingTree) {
        this.mappingTree = mappingTree;
        this.src = mappingTree.getMinNamespaceId();
        this.dest = mappingTree.getMaxNamespaceId() - 1;
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        MappingTree.ClassMapping clazz = mappingTree.getClass(owner, src);
        if (clazz == null) return name;

        MappingTree.MethodMapping method = clazz.getMethod(name, descriptor, src);
        if (method == null) return name;

        return method.getName(dest);
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        MappingTree.ClassMapping clazz = mappingTree.getClass(owner, src);
        if (clazz == null) return name;

        MappingTree.FieldMapping field = clazz.getField(name, descriptor, src);
        if (field == null) return name;

        return field.getName(dest);
    }

    @Override
    public String map(String internalName) {
        return mappingTree.mapClassName(internalName, src, dest);
    }
}
