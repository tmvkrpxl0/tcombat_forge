package com.tmvkrpxl0.tcombat.common.entities;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

public class TCombatEntityTypes {

    public TCombatEntityTypes(IEventBus eventBus){
        ENTITIES.register(eventBus);
    }

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<EntityType<CustomizableBlockEntity>> CUSTOMIZEABLE_BLOCK_ENTITY = ENTITIES.register("customizeable_block_entity",
            () -> EntityType.Builder.<CustomizableBlockEntity>create(CustomizableBlockEntity::new, EntityClassification.MISC).size(1,1).disableSummoning().build("customizeable_block_entity"));
}
