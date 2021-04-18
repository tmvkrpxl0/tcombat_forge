package com.tmvkrpxl0.tcombat.common.events

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableFluidEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.ICustomizableEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.fluid.FlowingFluid
import net.minecraft.item.BucketItem
import net.minecraft.item.Items
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.server.ServerLifecycleHooks

@EventBusSubscriber(
    modid = TCombatMain.MODID, bus = EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object WorldEventListener {
    val multiShotTracker: MutableMap<LivingEntity, ArrowCounter> = HashMap()
    @SubscribeEvent
    fun serverTickEvent(event: ServerTickEvent) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            multiShotTracker.clear()
            EntityEventListener.instaArrows.removeIf { e: ArrowEntity -> e.isOnGround || !e.isAlive }
            EntityEventListener.explosionImmune.clear()
            for (world in ServerLifecycleHooks.getCurrentServer().worlds) {
                for (entity in world.entitiesIteratable) {
                    if (entity is LivingEntity) {
                        if (!entity.isOnGround) {
                            val vehicle = entity.getLowestRidingEntity()
                            if (vehicle !== entity && vehicle.isOnGround) {
                                val i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, entity)
                                if (i > 0) {
                                    val ground = entity.isOnGround()
                                    entity.isOnGround = true
                                    TCombatUtil.freezeGround(entity, world, vehicle.position, i)
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
        if (event.side == LogicalSide.CLIENT) return
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
                        val fluidEntity = CustomizableFluidEntity(
                            blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), event.player, fluid.defaultState
                        )
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
    fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
        ServerLifecycleHooks.getCurrentServer().worlds.forEach { serverWorld ->
            serverWorld.entitiesIteratable.forEach { entity ->
                if (entity is ICustomizableEntity) {
                    if (event.player == entity.getOwner()) {
                        entity.remove()
                    }
                }else if(entity is WorldAxeEntity){
                    if(event.player == entity.shooter){
                        entity.remove()
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onPlayerChangeDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        ServerLifecycleHooks.getCurrentServer().worlds.forEach { serverWorld ->
            serverWorld.entitiesIteratable.forEach { entity ->
                if (entity is CustomizableBlockEntity) {
                    if (event.player == entity.getOwner()) {
                        entity.toWorld()
                    }
                }else if(entity is WorldAxeEntity){
                    entity.remove()
                }
            }
        }
    }

    @SubscribeEvent
    fun onChat(event: ClientChatEvent) {
        if (event.message.contains("test")) {

        }
    }

    @SubscribeEvent
    fun onEntitySpawn(event: EntityJoinWorldEvent) {
        if (!event.entity.world.isRemote()) {
            if (event.entity is AbstractArrowEntity) {
                val arrowEntity = event.entity as AbstractArrowEntity
                if (arrowEntity.shooter is LivingEntity) {
                    val shooter = arrowEntity.shooter as LivingEntity
                    if (arrowEntity is ArrowEntity) {
                        val stack = if (shooter.heldItemMainhand.item === Items.CROSSBOW) shooter.heldItemMainhand else shooter.heldItemOffhand
                        if (stack.item === Items.CROSSBOW) {
                            val enchants = EnchantmentHelper.getEnchantments(stack)
                            if (enchants.containsKey(TCombatEnchants.FOCUS.get())) {
                                EntityEventListener.instaArrows.add(arrowEntity)
                                if (enchants.containsKey(Enchantments.MULTISHOT)) {
                                    if (!multiShotTracker.containsKey(shooter)) {
                                        multiShotTracker[shooter] =
                                            ArrowCounter()
                                    }
                                    val counter = multiShotTracker[shooter]
                                    if (counter!!.count == 3) return
                                    counter.count(arrowEntity)
                                    if (counter.count == 3) {
                                        val entities = counter.arrows
                                        val vector3d1 = shooter.getUpVector(1.0f)
                                        val quaternion = Quaternion(Vector3f(vector3d1), 0F, true)
                                        val vector3d = shooter.getLook(1.0f)
                                        val vector3f = Vector3f(vector3d)
                                        vector3f.transform(quaternion)
                                        val originalVector = entities[0]!!.motion
                                        for (i in 1..2) {
                                            entities[i]!!.setMotion(
                                                vector3f.x * originalVector.length(), vector3f.y * originalVector.length(), vector3f.z * originalVector.length()
                                            )
                                            entities[i]!!.velocityChanged = true
                                        }
                                    }
                                } else {
                                    val vector3d1 = shooter.getUpVector(1.0f)
                                    val quaternion = Quaternion(Vector3f(vector3d1), 0F, true)
                                    val vector3d = shooter.getLook(1.0f)
                                    val vector3f = Vector3f(vector3d)
                                    vector3f.transform(quaternion)
                                    val originalVector = arrowEntity.motion
                                    arrowEntity.setMotion(
                                        vector3f.x * originalVector.length(), vector3f.y * originalVector.length(), vector3f.z * originalVector.length()
                                    )
                                    arrowEntity.velocityChanged = true
                                }
                            }

                            if (enchants.containsKey(TCombatEnchants.CROSSBOW_FLAME.get())) {
                                arrowEntity.setFire(100)
                            }
                        }
                    }
                }
            }
        }
    }

    class ArrowCounter {
        val arrows = arrayOfNulls<ArrowEntity>(3)
        var count = 0

        fun count(entity: ArrowEntity) {
            arrows[count] = entity
            count++
        }
    }
}