package io.github.orioncraftmc.launcher.mixin;

import org.spongepowered.asm.mixin.extensibility.IRemapper;

public class OrionLauncherRemapper implements IRemapper {
    @Override
    public String mapMethodName(String owner, String name, String desc) {
        System.out.println("Remapping method " + owner + "." + name + desc);
        return name;
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        System.out.println("Remapping field " + owner + "." + name + desc);
        return name;
    }

    @Override
    public String map(String typeName) {
        System.out.println("Remapping type " + typeName);
        return typeName;
    }

    @Override
    public String unmap(String typeName) {
        System.out.println("Unmapping type " + typeName);
        return typeName;
    }

    @Override
    public String mapDesc(String desc) {
        System.out.println("Remapping desc " + desc);
        return desc;
    }

    @Override
    public String unmapDesc(String desc) {
        System.out.println("Unmapping desc " + desc);
        return desc;
    }
}
