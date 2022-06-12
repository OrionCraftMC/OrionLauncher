package io.github.orioncraftmc.launcher.transformers.impl.remapper;

import io.github.orioncraftmc.launcher.OrionLauncher;
import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import io.github.orioncraftmc.launcher.util.ResourceUtil;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;

public class RemapperTransformer implements OrionClassTransformer {
    private MappingTree mappingTree;

    private final Map<String, byte[]> deobfuscatedClasses = new HashMap<>();

    @Override
    public void init() {
        mappingTree = OrionLauncher.getInstance().deobfMappingTree();

        if (mappingTree == null) return;

        String deobfNs = mappingTree.getNamespaceName(mappingTree.getMaxNamespaceId() - 1);
        String obfNs = mappingTree.getNamespaceName(mappingTree.getMinNamespaceId());

        TinyRemapper remapper = TinyRemapper.newRemapper()
                .withMappings(TinyRemapperHelper.create(mappingTree, obfNs, deobfNs, true))
                .checkPackageAccess(true)
                .fixPackageAccess(true)
                .renameInvalidLocals(true)
                .rebuildSourceFilenames(true)
                .build();

        remapper.readInputs(ResourceUtil.getClassPath());

        remapper.apply((s, bytes) -> {
            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);

            if (Annotations.getInvisible(cn, Mixin.class) == null) {
                deobfuscatedClasses.put(s, bytes);
            }
        });

        remapper.finish();
    }

    @Override
    public byte[] transformClass(String className, byte @Nullable [] classBytes) {
        if (mappingTree == null) return classBytes;
        if (classBytes == null) return null;

        byte[] clazz = deobfuscatedClasses.getOrDefault(className.replace('.', '/'), null);
        return clazz == null ? classBytes : clazz;
    }
}
