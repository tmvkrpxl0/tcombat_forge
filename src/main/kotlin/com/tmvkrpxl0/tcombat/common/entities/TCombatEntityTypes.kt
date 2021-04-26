package com.tmvkrpxl0.tcombat.common.entities

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableFluidEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.ReflectiveArrowEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.SnipeArrowEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.world.World
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

class TCombatEntityTypes(eventBus: IEventBus) {
    companion object {
        val ENTITIES: DeferredRegister<EntityType<*>> = DeferredRegister.create(ForgeRegistries.ENTITIES, TCombatMain.MODID)
        val CUSTOMIZABLE_BLOCK_ENTITY: RegistryObject<EntityType<CustomizableBlockEntity>> = ENTITIES.register("customizable_block_entity") {
            EntityType.Builder.of({ type: EntityType<CustomizableBlockEntity>, world: World ->
                CustomizableBlockEntity(type, world)
            }, EntityClassification.MISC).sized(1f, 1f).build("customizable_block_entity")
        }
        val CUSTOMIZABLE_FLUID_ENTITY: RegistryObject<EntityType<CustomizableFluidEntity>> = ENTITIES.register("customizable_fluid_entity") {
            EntityType.Builder.of({ type: EntityType<CustomizableFluidEntity>, world: World ->
                CustomizableFluidEntity(type, world)
            }, EntityClassification.MISC).sized(1f, 1f).build("customizable_fluid_entity")
        }
        val TNT_ARROW: RegistryObject<EntityType<TNTArrowEntity>> = ENTITIES.register("tnt_arrow"){
            EntityType.Builder.of({ tntArrowType: EntityType<TNTArrowEntity>, world: World ->
                TNTArrowEntity(tntArrowType, world)
            }, EntityClassification.MISC).sized(0.5f, 0.5f).build("tnt_arrow")
        }
        val SNIPE_ARROW: RegistryObject<EntityType<SnipeArrowEntity>> = ENTITIES.register("snipe_arrow") {
            EntityType.Builder.of({ snipeArrowType: EntityType<SnipeArrowEntity>, world: World ->
                SnipeArrowEntity(snipeArrowType, world)
            }, EntityClassification.MISC).sized(0.5f, 0.5f).build("snipe_arrow")
        }

        val WORLD_AXE: RegistryObject<EntityType<WorldAxeEntity>> = ENTITIES.register("world_axe"){
            EntityType.Builder.of({ worldAxeType: EntityType<WorldAxeEntity>, world: World->
                WorldAxeEntity(worldAxeType, world)
            }, EntityClassification.MISC).sized(0.5f, 0.5f).noSummon().fireImmune().updateInterval(1).build("world_axe")
        }

        val REFLECTIVE_ARROW: RegistryObject<EntityType<ReflectiveArrowEntity>> = ENTITIES.register("reflective_arrow"){
            EntityType.Builder.of({reflectiveType: EntityType<ReflectiveArrowEntity>, world: World->
                ReflectiveArrowEntity(reflectiveType, world)
            }, EntityClassification.MISC).sized(0.5f, 0.5f).build("reflective_arrow")
        }
    }

    init {
        ENTITIES.register(eventBus)
    }
}