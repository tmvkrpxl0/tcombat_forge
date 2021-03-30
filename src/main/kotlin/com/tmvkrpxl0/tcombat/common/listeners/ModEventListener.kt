package com.tmvkrpxl0.tcombat.common.listeners

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.skills.AbstractSkill
import com.tmvkrpxl0.tcombat.common.skills.Skills
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.RegistryEvent.NewRegistry
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.registries.RegistryBuilder

@EventBusSubscriber(
    modid = TCombatMain.Companion.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object ModEventListener {
    @SubscribeEvent
    fun onRegistryCreation(event: NewRegistry) {
        val builder = RegistryBuilder<AbstractSkill>()
        builder.setName(ResourceLocation(TCombatMain.MODID, "skill_registry"))
        builder.type = AbstractSkill::class.java
        val registry = builder.create()
        registry.registerAll(Skills.ARROW_SENSE, Skills.REFLECT_ARROW, Skills.AUTO_BRIDGE)
        TCombatMain.LOGGER.info("Registry Created!")
    }
}