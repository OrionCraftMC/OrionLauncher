package io.github.orioncraftmc.launcher.transformers.impl.exposer;

import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class AccessExposerTransformer implements OrionClassTransformer {
    @Override
    public void init() {

    }

    @Override
    public byte[] transformClass(String className, byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, 0);
        cr.accept(new AccessExposerClassVisitor(cw), 0);

        return cw.toByteArray();
    }
}
