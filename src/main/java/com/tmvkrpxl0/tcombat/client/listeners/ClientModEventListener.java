package com.tmvkrpxl0.tcombat.client.listeners;

import com.tmvkrpxl0.tcombat.client.key.KeyHandler;
import com.tmvkrpxl0.tcombat.client.renderers.BlockEntityRenderer;
import com.tmvkrpxl0.tcombat.client.renderers.TNTArrowRenderer;
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.tmvkrpxl0.tcombat.TCombatMain.LOGGER;
import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

@Mod.EventBusSubscriber(modid=MODID,bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventListener {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        LOGGER.info("Stencil: " + Minecraft.getInstance().getMainRenderTarget().isStencilEnabled());
        Minecraft.getInstance().getMainRenderTarget().enableStencil();
        LOGGER.info("Stencil: " + Minecraft.getInstance().getMainRenderTarget().isStencilEnabled());
        new KeyHandler();
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.CUSTOMIZEABLE_BLOCK_ENTITY.get(), BlockEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.TNT_ARROW.get(), TNTArrowRenderer::new);
    }
}
