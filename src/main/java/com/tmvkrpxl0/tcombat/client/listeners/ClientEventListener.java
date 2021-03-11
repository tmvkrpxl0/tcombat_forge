package com.tmvkrpxl0.tcombat.client.listeners;

import com.tmvkrpxl0.tcombat.client.key.KeyHandler;
import com.tmvkrpxl0.tcombat.client.renderers.BlockEntityRenderer;
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes;
import com.tmvkrpxl0.tcombat.TCombatMain;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

@Mod.EventBusSubscriber(modid=MODID,bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventListener {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        TCombatMain.LOGGER.info("Rendering!");
        new KeyHandler();
        RenderingRegistry.registerEntityRenderingHandler(TCombatEntityTypes.CUSTOMIZEABLE_BLOCK_ENTITY.get(), BlockEntityRenderer::new);
    }
}
