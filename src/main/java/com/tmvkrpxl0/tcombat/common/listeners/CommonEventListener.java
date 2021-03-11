package com.tmvkrpxl0.tcombat.common.listeners;

import com.tmvkrpxl0.tcombat.common.skills.AbstractSkill;
import com.tmvkrpxl0.tcombat.common.skills.Skills;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import static com.tmvkrpxl0.tcombat.TCombatMain.LOGGER;
import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

@Mod.EventBusSubscriber(modid=MODID,bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT,Dist.DEDICATED_SERVER})
public class CommonEventListener {
    @SubscribeEvent
    public static void onRegistryCreation(RegistryEvent.NewRegistry event){
        RegistryBuilder<AbstractSkill> builder = new RegistryBuilder<>();
        builder.setName(new ResourceLocation(MODID, "skill_registry"));
        builder.setType(AbstractSkill.class);
        IForgeRegistry<AbstractSkill> registry = builder.create();
        registry.registerAll(Skills.ARROW_SENSE, Skills.REFLECT_ARROW);
        LOGGER.info("Registry Created!");
    }
}
