package io.github.orioncraftmc.launcher.transformers.impl;

import io.github.orioncraftmc.launcher.mixin.OrionLauncherMixinService;
import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import java.lang.reflect.Method;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class MixinClassTransformer implements OrionClassTransformer {
    @Override
    public void init() {
        MixinBootstrap.init();
        finishMixinBootstrapping();
    }

    protected static void finishMixinBootstrapping() {
        try {
            Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            m.setAccessible(true);
            m.invoke(null, MixinEnvironment.Phase.INIT);
            m.invoke(null, MixinEnvironment.Phase.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] transformClass(String className, byte[] classBytes) {
        if (OrionLauncherMixinService.getTransformer() == null) return classBytes;

        return OrionLauncherMixinService.getTransformer()
                .transformClass(MixinEnvironment.getCurrentEnvironment(), className, classBytes);
    }
}
