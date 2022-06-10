package io.github.orioncraftmc.launcher.mixin;

import io.github.orioncraftmc.launcher.OrionLauncher;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.spongepowered.asm.mixin.extensibility.IRemapper;
import org.spongepowered.asm.util.ObfuscationUtil;

public class OrionLauncherRemapper implements IRemapper, ObfuscationUtil.IClassRemapper {
    @Override
    public String mapMethodName(String owner, String name, String desc) {
        System.out.println("Remapping method " + owner + "." + name + desc);
        MemoryMappingTree tree = OrionLauncher.getInstance().deobfMappingTree();
        if (tree == null) return name;

        MappingTree.ClassMapping classEntry = tree.getClass(owner);
        if (classEntry == null) return name;

        MappingTree.MethodMapping method = classEntry.getMethod(name, desc, tree.getMaxNamespaceId() - 1);

        return method.getSrcName();
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        System.out.println("Remapping field " + owner + "." + name + desc);

        MemoryMappingTree tree = OrionLauncher.getInstance().deobfMappingTree();
        if (tree == null) return name;

        MappingTree.ClassMapping classEntry = tree.getClass(owner);
        if (classEntry == null) return name;

        MappingTree.FieldMapping field = classEntry.getField(name, desc, tree.getMaxNamespaceId() - 1);

        return field.getSrcName();
    }

    @Override
    public String map(String typeName) {
        System.out.println("Remapping type " + typeName);

        MemoryMappingTree tree = OrionLauncher.getInstance().deobfMappingTree();
        if (tree == null) return typeName;

        MappingTree.ClassMapping classEntry = tree.getClass(typeName);
        if (classEntry == null) return typeName;

        return classEntry.getSrcName();
    }

    @Override
    public String unmap(String typeName) {
        System.out.println("Unmapping type " + typeName);

        MemoryMappingTree tree = OrionLauncher.getInstance().deobfMappingTree();

        if (tree == null) return typeName;

        int maxNamespaceId = tree.getMaxNamespaceId() - 1;
        MappingTree.ClassMapping classEntry = tree.getClass(typeName, maxNamespaceId);

        if (classEntry == null) return typeName;

        return classEntry.getName(maxNamespaceId);
    }

    @Override
    public String mapDesc(String desc) {
        System.out.println("Remapping desc " + desc);
        return ObfuscationUtil.mapDescriptor(desc, this);
    }

    @Override
    public String unmapDesc(String desc) {
        System.out.println("Unmapping desc " + desc);
        return ObfuscationUtil.unmapDescriptor(desc, this);
    }
}
