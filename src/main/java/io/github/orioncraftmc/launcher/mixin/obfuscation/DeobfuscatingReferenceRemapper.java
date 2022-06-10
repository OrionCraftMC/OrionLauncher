package io.github.orioncraftmc.launcher.mixin.obfuscation;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.refmap.IReferenceMapper;

public class DeobfuscatingReferenceRemapper implements IReferenceMapper {
    private final MixinEnvironment environment;
    private final IReferenceMapper wrapped;

    public DeobfuscatingReferenceRemapper(MixinEnvironment environment, IReferenceMapper wrapped) {
        this.environment = environment;
        this.wrapped = wrapped;
    }

    @Override
    public boolean isDefault() {
        return wrapped.isDefault();
    }

    @Override
    public String getResourceName() {
        return wrapped.getResourceName();
    }

    @Override
    public String getStatus() {
        return wrapped.getStatus();
    }

    @Override
    public String getContext() {
        return wrapped.getContext();
    }

    @Override
    public void setContext(String context) {
        wrapped.setContext(context);
    }

    @Override
    public String remap(String className, String reference) {
        return wrapped.remap(className, reference);
    }

    @Override
    public String remapWithContext(String context, String className, String reference) {
        return wrapped.remapWithContext(context, className, reference);
    }
}
