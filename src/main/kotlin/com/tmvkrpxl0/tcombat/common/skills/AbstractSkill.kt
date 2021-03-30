package com.tmvkrpxl0.tcombat.common.skills

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

abstract class AbstractSkill : IForgeRegistryEntry<AbstractSkill> {
    override fun setRegistryName(name: ResourceLocation): AbstractSkill {
        TCombatMain.LOGGER.warn("Skill names are unchangeable! ignoring setRegistryName")
        return this
    }

    override fun getRegistryType(): Class<AbstractSkill> {
        return AbstractSkill::class.java
    }
}