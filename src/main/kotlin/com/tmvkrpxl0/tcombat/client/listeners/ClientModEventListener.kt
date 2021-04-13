package com.tmvkrpxl0.tcombat.client.listeners

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.client.renderers.BlockEntityRenderer
import com.tmvkrpxl0.tcombat.client.renderers.TNTArrowRenderer
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.entity.TippedArrowRenderer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(modid = TCombatMain.MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientModEventListener {
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.CUSTOMIZABLE_BLOCK_ENTITY.get()) { renderManager: EntityRendererManager ->
            BlockEntityRenderer(renderManager)
        }
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.TNT_ARROW.get()) { renderManagerIn: EntityRendererManager ->
            TNTArrowRenderer(renderManagerIn)
        }
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.SNIPE_ARROW.get()) { renderManager: EntityRendererManager ->
            TippedArrowRenderer(
                renderManager
            )
        }
    }
}