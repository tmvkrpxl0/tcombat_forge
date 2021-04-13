package com.tmvkrpxl0.tcombat.common.listeners

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableFluidEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.ICustomizableEntity
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.block.BlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.enchantment.FrostWalkerEnchantment
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.fluid.FlowingFluid
import net.minecraft.item.BucketItem
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.server.ServerLifecycleHooks

@EventBusSubscriber(
    modid = TCombatMain.MODID,
    bus = EventBusSubscriber.Bus.FORGE,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object WorldEventListener {
    @SubscribeEvent
    fun serverTickEvent(event: ServerTickEvent) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            EntityEventListener.multishotTracker.clear()
            EntityEventListener.instaArrows.removeIf { e: ArrowEntity -> e.isOnGround || !e.isAlive }
            EntityEventListener.explosionImmune.clear()
            for (world in ServerLifecycleHooks.getCurrentServer().worlds) {
                for (entity in world.entitiesIteratable) {
                    if (entity is LivingEntity) {
                        if (!entity.isOnGround()) {
                            val vehicle = entity.getLowestRidingEntity()
                            if (vehicle !== entity && vehicle.isOnGround) {
                                val i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, entity)
                                if (i > 0) {
                                    val ground = entity.isOnGround()
                                    entity.isOnGround = true
                                    FrostWalkerEnchantment.freezeNearby(entity, world, vehicle.position.add(0,1,0), i)
                                    //TCombatUtil.freezeGround(entity, world, vehicle.position, i)
                                    entity.isOnGround = ground
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onInteract(event: LeftClickBlock) {
        if(event.side == LogicalSide.CLIENT)return
        val stack = event.itemStack
        if (stack.item is BucketItem) {
            if (event.face != null) {
                val world = event.entityLiving.world
                val blockPos = event.pos.add(event.face!!.directionVec)
                val blockState = world.getBlockState(blockPos)
                val bucketItem = stack.item as BucketItem
                var fluid = bucketItem.fluid
                if (fluid is FlowingFluid) {
                    fluid = fluid.flowingFluid
                    if (event.entityLiving.isSneaking) {
                        val fluidEntity = CustomizableFluidEntity(TCombatEntityTypes.CUSTOMIZABLE_FLUID_ENTITY.get(), world)
                        fluidEntity.initialize(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(),
                            event.player, fluid.defaultState)
                        world.addEntity(fluidEntity)
                    } else {
                        if (blockState.isReplaceable(fluid) || blockState.isAir(world, blockPos)) {
                            TCombatUtil.emptyBucket(bucketItem, event.player, world, blockPos, null, fluid)
                        }
                    }
                }
                event.isCanceled = true
            }
        }
    }

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent){
        ServerLifecycleHooks.getCurrentServer().worlds.forEach { serverWorld ->
            serverWorld.entitiesIteratable.forEach { entity ->
                if(entity is ICustomizableEntity){
                    if(event.player == entity.getOwner()){
                        entity.remove()
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onPlayerChangeDimension(event: PlayerEvent.PlayerChangedDimensionEvent){
        ServerLifecycleHooks.getCurrentServer().worlds.forEach { serverWorld ->
            serverWorld.entitiesIteratable.forEach { entity ->
                if(entity is CustomizableBlockEntity){
                    if(event.player == entity.getOwner()){
                        entity.toWorld()
                    }
                }
            }
        }
    }
}