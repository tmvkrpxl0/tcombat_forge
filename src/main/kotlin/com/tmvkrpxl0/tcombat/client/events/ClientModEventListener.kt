package com.tmvkrpxl0.tcombat.client.events

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.client.key.KeyHandler
import com.tmvkrpxl0.tcombat.client.renderers.*
import com.tmvkrpxl0.tcombat.common.capability.capabilities.WorldAxeCapability
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import com.tmvkrpxl0.tcombat.common.items.weapon.WorldAxeItem
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.item.ItemModelsProperties
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(modid = TCombatMain.MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientModEventListener {
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent){
        KeyHandler.INSTANCE.register()
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.CUSTOMIZABLE_BLOCK_ENTITY.get()) { renderManager: EntityRendererManager ->
            BlockEntityRenderer(renderManager)
        }
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.TNT_ARROW.get()) { renderManagerIn: EntityRendererManager ->
            TNTArrowRenderer(renderManagerIn)
        }
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.SNIPE_ARROW.get()) { renderManager: EntityRendererManager ->
            SnipeArrowRenderer(renderManager)
        }
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.CUSTOMIZABLE_FLUID_ENTITY.get()) { renderManager: EntityRendererManager ->
            FluidEntityRenderer(renderManager)
        }
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.WORLD_AXE.get()) { renderManager: EntityRendererManager ->
            WorldAxeRenderer(renderManager)
        }
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.REFLECTIVE_ARROW.get()){ renderManager: EntityRendererManager ->
            ReflectiveArrowRenderer(renderManager)
        }
        event.enqueueWork {
            ItemModelsProperties.register(TCombatItems.WORLD_AXE.get(), ResourceLocation("cast")) { itemStack, _, _ ->
                val cap = itemStack.getCapability(WorldAxeCapability.itemEntityConnectionHandler)
                if(cap.isPresent){
                    val holder = cap.resolve().get()
                    if(holder.getPlayer()!=null){
                        if(holder.getEntity()!=null){
                            return@register 1.0f
                        } else {
                            return@register 0.0f
                        }
                    }else return@register 0.0f
                }else 0.0f
            }
        }
    }

    @SubscribeEvent
    fun onModelRegister(event: ModelRegistryEvent){
        ModelLoader.addSpecialModel(ResourceLocation(TCombatMain.MODID, "item/world_axe_axe"))
    }
}