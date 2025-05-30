package org.tywrapstudios.ctd.platform;

import com.google.common.collect.ImmutableMap;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CTDMixinPlugin implements IMixinConfigPlugin {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CTDMixinPlugin.class);
    private static final Supplier<Boolean> TRUE = () -> true;

    private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of(
            "org.tywrapstudios.ctd.mixin.AsyncWorldInfoProviderMixin", () -> CTDServices.PLATFORM.isModLoaded("spark")
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        LOGGER.debug("Checking if mixin {} should be applied to {} ({})", mixinClassName, targetClassName, CTDServices.PLATFORM.getPlatformName());
        return CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
    }

    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
