package com.tmvkrpxl0.tcombat.common.entities

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.SnipeArrowEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.world.World
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

class TCombatEntityTypes(eventBus: IEventBus) {
    companion object {
        val ENTITIES: DeferredRegister<EntityType<*>> =
            DeferredRegister.create(ForgeRegistries.ENTITIES, TCombatMain.MODID)
        val CUSTOMIZABLE_BLOCK_ENTITY: RegistryObject<EntityType<CustomizableBlockEntity>> = ENTITIES.register(
            "customizable_block_entity"
        ) {
            EntityType.Builder.create(
                { type: EntityType<CustomizableBlockEntity>, world: World -> CustomizableBlockEntity(type, world) },
                EntityClassification.MISC
            ).size(1f, 1f).disableSummoning().build("customizable_block_entity")
        }
        val TNT_ARROW: RegistryObject<EntityType<TNTArrowEntity>> = ENTITIES.register(
            "tnt_arrow"
        ) {
            EntityType.Builder.create({ tntArrowType: EntityType<TNTArrowEntity>, world: World ->
                TNTArrowEntity(
                    tntArrowType,
                    world
                )
            }, EntityClassification.MISC).size(0.5f, 0.5f).build("tnt_arrow")
        }
        val SNIPE_ARROW: RegistryObject<EntityType<SnipeArrowEntity>> = ENTITIES.register("snipe_arrow"){
            EntityType.Builder.create({snipeArrowType: EntityType<SnipeArrowEntity>, world: World -> SnipeArrowEntity(snipeArrowType, world)}, EntityClassification.MISC).size(0.5f, 0.5f).build("snipe_arrow")
        }
    }

    init {
        ENTITIES.register(eventBus)
    }
}