package io.github.orioncraftmc.launcher.transformers.impl.mixin.agent;

import io.github.orioncraftmc.launcher.OrionLauncher;
import java.util.Collection;
import java.util.Collections;
import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.util.IConsumer;

public class OrionLauncherSideProviderAgent implements IMixinPlatformServiceAgent {
    @Override
    public void init() {
    }

    @Override
    public String getSideName() {
        return OrionLauncher.getInstance().mixinSide();
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return Collections.emptyList();
    }

    @Override
    public void wire(MixinEnvironment.Phase phase, IConsumer<MixinEnvironment.Phase> phaseConsumer) {
    }

    @Override
    public void unwire() {
    }

    @Override
    public AcceptResult accept(MixinPlatformManager manager, IContainerHandle handle) {
        return AcceptResult.ACCEPTED;
    }

    @Override
    public String getPhaseProvider() {
        return null;
    }

    @Override
    public void prepare() {
    }

    @Override
    public void initPrimaryContainer() {
    }

    @Override
    public void inject() {
    }
}
