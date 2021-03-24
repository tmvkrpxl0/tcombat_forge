package com.tmvkrpxl0.tcombat.common.skills;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

import static com.tmvkrpxl0.tcombat.TCombatMain.LOGGER;
import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

public abstract class AbstractSkill implements IForgeRegistryEntry<AbstractSkill> {
    private static final ResourceLocation NAME = new ResourceLocation(MODID, "skill_registry");

    @Override
    public AbstractSkill setRegistryName(ResourceLocation name) {
        LOGGER.warn("Skill names are unchangeable! ignoring setRegistryName");
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    public Class<AbstractSkill> getRegistryType() {
        return AbstractSkill.class;
    }
}
