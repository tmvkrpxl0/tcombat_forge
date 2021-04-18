package com.tmvkrpxl0.tcombat.common.events

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.capability.capabilities.ItemEntityConnectionCapability
import com.tmvkrpxl0.tcombat.common.skills.AbstractSkill
import com.tmvkrpxl0.tcombat.common.skills.ArrowSense
import com.tmvkrpxl0.tcombat.common.skills.AutoBridge
import com.tmvkrpxl0.tcombat.common.skills.ReflectArrow
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.RegistryEvent.NewRegistry
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.RegistryBuilder

@EventBusSubscriber(modid = TCombatMain.MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT, Dist.DEDICATED_SERVER])
object ModEventListener {
    @SubscribeEvent
    fun onRegistryCreation(event: NewRegistry) {
        val builder = RegistryBuilder<AbstractSkill>()
        builder.setName(ResourceLocation(TCombatMain.MODID, "skill_registry"))
        builder.type = AbstractSkill::class.java
        val registry = builder.create()
        registry.registerAll(ArrowSense, ReflectArrow, AutoBridge)
        TCombatMain.LOGGER.info("Registry Created!")
    }

    @SubscribeEvent
    fun fmlCommonSetupEvent(event: FMLCommonSetupEvent){
        ItemEntityConnectionCapability.register()
    }
}