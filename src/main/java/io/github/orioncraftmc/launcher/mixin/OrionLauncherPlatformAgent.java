package io.github.orioncraftmc.launcher.mixin;

import io.github.orioncraftmc.launcher.util.ResourceUtil;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class OrionLauncherPlatformAgent extends MixinPlatformAgentAbstract {
    @Override
    public void prepare() {
        registerMixinConfigs("**/mixins.*.json");
        registerMixinConfigs("**/*.mixins.json");
        MixinEnvironment.getDefaultEnvironment().getRemappers().add(new OrionLauncherRemapper());
    }

    private static void registerMixinConfigs(String pattern) {
        ResourceUtil.findResources(pattern).forEach(Mixins::addConfiguration);
    }
}