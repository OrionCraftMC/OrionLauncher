package io.github.orioncraftmc.launcher.transformers.impl.mixin.service;

import io.github.orioncraftmc.launcher.OrionLauncher;
import io.github.orioncraftmc.launcher.transformers.OrionClassTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.mixin.MixinClassTransformer;
import io.github.orioncraftmc.launcher.transformers.impl.mixin.agent.OrionLauncherPlatformAgent;
import io.github.orioncraftmc.launcher.transformers.impl.mixin.agent.OrionLauncherSideProviderAgent;
import io.github.orioncraftmc.launcher.util.UrlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterConsole;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.*;

public class OrionLauncherMixinService extends MixinServiceAbstract implements IClassProvider, IClassBytecodeProvider, ITransformerProvider {

    private static IMixinTransformer transformer;

    public static IMixinTransformer getTransformer() {
        return transformer;
    }

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        byte[] bytes = OrionLauncher.getInstance().loader().getUnmodifiedClassBytes(name);
        for (OrionClassTransformer transformer : OrionLauncher.getInstance().loader().transformers()) {
            if (transformer instanceof MixinClassTransformer) {
                continue;
            }
            bytes = transformer.transformClass(name, bytes);
        }

        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        return node;
    }

    @Override
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, false);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return OrionLauncher.getInstance().loader().findClass(name, initialize);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return findClass(name, initialize);
    }

    @Override
    public String getName() {
        return "OrionLauncher - Mixin Service";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return this;
    }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return List.of(OrionLauncherSideProviderAgent.class.getName(), OrionLauncherPlatformAgent.class.getName());
    }

    @Override
    public void offer(IMixinInternal internal) {
        if (internal instanceof IMixinTransformerFactory) {
            transformer = ((IMixinTransformerFactory) internal).createTransformer();
        }
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return new ContainerHandleURI(UrlUtil.LOADER_CODE_SOURCE.toUri());
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return OrionLauncher.getInstance().loader().getResourceAsStream(name);
    }

    @Override
    public Collection<ITransformer> getTransformers() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ITransformer> getDelegatedTransformers() {
        return Collections.emptyList();
    }

    @Override
    protected ILogger createLogger(String name) {
        return new LoggerAdapterConsole(name);
    }

    @Override
    public void beginPhase() {

    }

    @Override
    public void addTransformerExclusion(String name) {

    }


}