package com.tmvkrpxl0.tcombat.common.entities;

import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity;
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity;
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
            () -> EntityType.Builder.<CustomizableBlockEntity>of(CustomizableBlockEntity::new, EntityClassification.MISC).sized(1,1).noSummon().build("customizeable_block_entity"));
    public static final RegistryObject<EntityType<TNTArrowEntity>> TNT_ARROW = ENTITIES.register("tnt_arrow",
            () -> EntityType.Builder.<TNTArrowEntity>of(TNTArrowEntity::new, EntityClassification.MISC).sized(0.5f,0.5f).build("tnt_arrow"));
}
