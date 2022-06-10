package io.github.orioncraftmc.launcher.transformers.impl.mixin;

import io.github.orioncraftmc.launcher.OrionLauncher;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.spongepowered.asm.mixin.extensibility.IRemapper;
import org.spongepowered.asm.util.ObfuscationUtil;

public class OrionLauncherRemapper implements IRemapper, ObfuscationUtil.IClassRemapper {
    @Override
    public String mapMethodName(String owner, String name, String desc) {
        MemoryMappingTree tree = OrionLauncher.getInstance().deobfMappingTree();
        if (tree == null) return name;

        MappingTree.ClassMapping classEntry = tree.getClass(owner);
        if (classEntry == null) return name;

        MappingTree.MethodMapping method = classEntry.getMethod(name, desc, tree.getMaxNamespaceId() - 1);
        if (method == null) return name;

        return method.getSrcName() + (desc == null && method.getSrcDesc() != null ? method.getSrcDesc() : "");
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        MemoryMappingTree tree = OrionLauncher.getInstance().deobfMappingTree();
        if (tree == null) return name;

        MappingTree.ClassMapping classEntry = tree.getClass(owner);
        if (classEntry == null) return name;

        MappingTree.FieldMapping field = classEntry.getField(name, desc, tree.getMaxNamespaceId() - 1);
        if (field == null) return name;

        return field.getSrcName() + (desc == null && field.getSrcDesc() != null ? field.getSrcDesc() : "");
    }

    @Override
    public String map(String typeName) {
        MemoryMappingTree tree = OrionLauncher.getInstance().deobfMappingTree();
        if (tree == null) return typeName;

        MappingTree.ClassMapping classEntry = tree.getClass(typeName);
        if (classEntry == null) return typeName;

        return classEntry.getSrcName();
    }

    @Override
    public String unmap(String typeName) {
        MemoryMappingTree tree = OrionLauncher.getInstance().deobfMappingTree();

        if (tree == null) return typeName;

        int maxNamespaceId = tree.getMaxNamespaceId() - 1;
        MappingTree.ClassMapping classEntry = tree.getClass(typeName, maxNamespaceId);

        if (classEntry == null) return typeName;

        return classEntry.getName(maxNamespaceId);
    }

    @Override
    public String mapDesc(String desc) {
        return ObfuscationUtil.mapDescriptor(desc, this);
    }

    @Override
    public String unmapDesc(String desc) {
        return ObfuscationUtil.unmapDescriptor(desc, this);
    }
}
