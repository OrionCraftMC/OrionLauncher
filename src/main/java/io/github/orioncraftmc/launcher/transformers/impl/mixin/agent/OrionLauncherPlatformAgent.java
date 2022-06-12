package io.github.orioncraftmc.launcher.transformers.impl.mixin.agent;

import io.github.orioncraftmc.launcher.util.ResourceUtil;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.mixin.Mixins;

public class OrionLauncherPlatformAgent extends MixinPlatformAgentAbstract {
    @Override
    public void prepare() {
        registerMixinConfigs("**/mixins.*.json");
        registerMixinConfigs("**/*.mixins.json");
    }

    private static void registerMixinConfigs(String pattern) {
        ResourceUtil.findResources(pattern).forEach(Mixins::addConfiguration);
    }
}