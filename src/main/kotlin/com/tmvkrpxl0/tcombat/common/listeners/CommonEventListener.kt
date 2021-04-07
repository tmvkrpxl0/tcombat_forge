package com.tmvkrpxl0.tcombat.common.listeners

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.network.packets.SpawnCBPacket
import com.tmvkrpxl0.tcombat.common.network.packets.TCombatPacketHandler
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.block.Blocks
import net.minecraft.block.FlowingFluidBlock
import net.minecraft.block.material.Material
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.attributes.Attributes
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.fluid.FlowingFluid
import net.minecraft.item.BucketItem
import net.minecraft.item.Items
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import net.minecraft.util.Direction
import net.minecraft.util.EntityDamageSource
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.shapes.ISelectionContext
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.util.BlockSnapshot
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.ProjectileImpactEvent.Arrow
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Tick
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.server.ServerLifecycleHooks
import java.util.*

@EventBusSubscriber(
    modid = TCombatMain.MODID,
    bus = EventBusSubscriber.Bus.FORGE,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object CommonEventListener {
    private val multishotTracker: MutableMap<LivingEntity, ArrowCounter> = HashMap()
    private val instaArrows: MutableSet<ArrowEntity> = HashSet()
    val explosionImmune: MutableSet<LivingEntity> = HashSet()

    @SubscribeEvent
    fun serverTickEvent(event: ServerTickEvent) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            multishotTracker.clear()
            instaArrows.removeIf { e: ArrowEntity -> e.isOnGround || !e.isAlive }
            explosionImmune.clear()
            for (world in ServerLifecycleHooks.getCurrentServer().worlds) {
                for (entity in world.entitiesIteratable) {
                    if (entity is LivingEntity) {
                        if (!entity.isOnGround()) {
                            val vehicle = entity.getLowestRidingEntity()
                            if (vehicle !== entity && vehicle.isOnGround) {
                                val i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, entity)
                                if (i > 0) {
                                    val ground = entity.isOnGround()
                                    entity.setOnGround(true)
                                    freezeGround(entity, world, vehicle.position, i)
                                    entity.setOnGround(ground)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun freezeGround(living: Entity, worldIn: World, pos: BlockPos, level: Int) {
        val blockstate = Blocks.FROSTED_ICE.defaultState
        val f = 16.coerceAtMost(2 + level).toFloat()
        val mutablePos = BlockPos.Mutable()
        for (blockPos in BlockPos.getAllInBoxMutable(
            pos.add(-f.toDouble(), -1.0, -f.toDouble()),
            pos.add(f.toDouble(), -1.0, f.toDouble())
        )) {
            if (blockPos.withinDistance(living.positionVec, f.toDouble())) {
                Enchantments.FROST_WALKER
                mutablePos.setPos(blockPos.x, blockPos.y + 1, blockPos.z)
                val blockstate1 = worldIn.getBlockState(mutablePos)
                if (blockstate1.isAir(worldIn, mutablePos)) {
                    val blockstate2 = worldIn.getBlockState(blockPos)
                    val isFull = blockstate2.block === Blocks.WATER && blockstate2.get(FlowingFluidBlock.LEVEL) == 0
                    if (blockstate2.material == Material.WATER && isFull && blockstate.isValidPosition(
                            worldIn,
                            blockPos
                        ) && worldIn.placedBlockCollides(
                            blockstate,
                            blockPos,
                            ISelectionContext.dummy()
                        ) && !ForgeEventFactory.onBlockPlace(
                            living,
                            BlockSnapshot.create(worldIn.dimensionKey, worldIn, blockPos),
                            Direction.UP
                        )
                    ) {
                        worldIn.setBlockState(blockPos, blockstate)
                        worldIn.pendingBlockTicks.scheduleTick(
                            blockPos,
                            Blocks.FROSTED_ICE,
                            MathHelper.nextInt(worldIn.rand, 60, 120)
                        )
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onProjectileHit(event: Arrow) {
        if (instaArrows.contains(event.arrow)) {
            val r = event.rayTraceResult
            if (r is EntityRayTraceResult) {
                r.entity.isInvulnerable = false
                r.entity.hurtResistantTime = 0
            }
        }
    }

    @SubscribeEvent
    fun onEntityDamage(event: LivingDamageEvent) {
        if (event.source.damageType == "explosion.player") {
            val entityDamageSource = event.source as EntityDamageSource
            if (entityDamageSource.isExplosion) {
                if (explosionImmune.contains(entityDamageSource.immediateSource)) {
                    event.amount = 0f
                    event.isCanceled = true
                }
            }
        }
    }

    @SubscribeEvent
    fun onUseTick(event: LivingEntityUseItemEvent.Start) {
        TCombatMain.LOGGER.info(event.duration)
        if (event.item.item === Items.CROSSBOW || event.item.item === Items.BOW) {
            if (event.entityLiving.isPotionActive(Effects.STRENGTH)) {
                val effectInstance = event.entityLiving.getActivePotionEffect(Effects.STRENGTH)
                val amplifier = effectInstance!!.amplifier
                event.duration = event.duration - 3 * amplifier
            }
            if (event.entityLiving.isPotionActive(Effects.HASTE)) {
                val effectInstance = event.entityLiving.getActivePotionEffect(Effects.HASTE)
                val amplifier = effectInstance!!.amplifier
                event.duration = event.duration - 3 * amplifier
            }
        }
    }

    @SubscribeEvent
    fun onUse(event: Tick) {
        if (event.item.item === Items.CROSSBOW || event.item.item === Items.BOW) {
            if (event.duration <= 0) {
                if (event.entityLiving.isSneaking) {
                    event.isCanceled = true
                    event.item.onPlayerStoppedUsing(event.entityLiving.world, event.entityLiving, 0)
                    event.entityLiving.resetActiveHand()
                }
            }
        }
    }

    @SubscribeEvent
    fun onInteract(event: LeftClickBlock) {
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
                        val packet = SpawnCBPacket(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), blockState, false, 1.0, 1.0)
                        TCombatPacketHandler.INSTANCE.sendToServer(packet)
                    } else {
                        if (blockState.isReplaceable(fluid) || blockState.isAir(world, blockPos)) {
                            TCombatUtil.emptyBucket(bucketItem, event.player, world, blockPos, null, fluid)
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onEntitySpawn(event: EntityJoinWorldEvent) {
        if(!event.entity.world.isRemote()){
            if(event.entity is AbstractArrowEntity) {
                val arrowEntity = event.entity as AbstractArrowEntity
                if (arrowEntity.shooter is LivingEntity) {
                    val shooter = arrowEntity.shooter as LivingEntity
                    if(arrowEntity is ArrowEntity){
                        val stack =
                            if (shooter.heldItemMainhand.item === Items.CROSSBOW) shooter.heldItemMainhand else shooter.heldItemOffhand
                        if (stack.item === Items.CROSSBOW) {
                            val enchants = EnchantmentHelper.getEnchantments(stack)
                            if (enchants.containsKey(TCombatEnchants.FOCUS.get())) {
                                instaArrows.add(arrowEntity)
                                if (enchants.containsKey(Enchantments.MULTISHOT)) {
                                    if (!multishotTracker.containsKey(shooter)) {
                                        multishotTracker[shooter] = ArrowCounter()
                                    }
                                    val counter = multishotTracker[shooter]
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
                                                vector3f.x * originalVector.length(),
                                                vector3f.y * originalVector.length(),
                                                vector3f.z * originalVector.length()
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
                                        vector3f.x * originalVector.length(),
                                        vector3f.y * originalVector.length(),
                                        vector3f.z * originalVector.length()
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
            private set

        fun count(entity: ArrowEntity) {
            arrows[count] = entity
            count++
        }
    }
}