package com.tmvkrpxl0.tcombat.common.events

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.capability.capabilities.TargetCapability
import com.tmvkrpxl0.tcombat.common.capability.providers.TargetCapabilityProvider
import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableFluidEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.ICustomizableEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import com.tmvkrpxl0.tcombat.common.util.VanilaCopy
import net.minecraft.client.Minecraft
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.fluid.FlowingFluid
import net.minecraft.item.BucketItem
import net.minecraft.item.Items
import net.minecraft.util.EntityDamageSource
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.ProjectileImpactEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.server.ServerLifecycleHooks

@Mod.EventBusSubscriber(
    modid = TCombatMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object EntityEventListener {
    val instaArrows: MutableSet<ArrowEntity> = HashSet()
    val explosionImmune: MutableSet<LivingEntity> = HashSet()
    val TARGET_HOLDER = ResourceLocation(TCombatMain.MODID, "target_holder")

    @SubscribeEvent
    fun onInteract(event: PlayerInteractEvent.LeftClickBlock) {
        if (event.side == LogicalSide.CLIENT) return
        val stack = event.itemStack
        if (stack.item is BucketItem) {
            if (event.face != null) {
                val world = event.entityLiving.level
                val blockPos = event.pos.offset(event.face!!.normal)
                val blockState = world.getBlockState(blockPos)
                val bucketItem = stack.item as BucketItem
                var fluid = bucketItem.fluid
                if (fluid is FlowingFluid) {
                    fluid = fluid.flowing
                    if (event.entityLiving.isShiftKeyDown) {
                        val fluidEntity = CustomizableFluidEntity(
                            blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), event.player, fluid.defaultFluidState()
                        )
                        world.addFreshEntity(fluidEntity)
                    } else {
                        if (blockState.isAir(world, blockPos) || blockState.canBeReplaced(fluid)) {
                            world.setBlockAndUpdate(blockPos, fluid.defaultFluidState().createLegacyBlock())
                            VanilaCopy.playEmptySound(bucketItem, event.player, world, blockPos)
                        }
                    }
                }
                event.isCanceled = true
            }
        }
    }

    @SubscribeEvent
    fun attachCapability(event: AttachCapabilitiesEvent<Entity>){
        if(event.`object` is PlayerEntity){
            val player = event.`object` as PlayerEntity
            if(player.level.isClientSide){
                if(player == Minecraft.getInstance().player){//Client should only have Target Holder of the player
                    event.addCapability(TARGET_HOLDER, TargetCapabilityProvider(player))
                }
            }else{//Server should have Target Holder for all player
                event.addCapability(TARGET_HOLDER, TargetCapabilityProvider(player))
            }
        }
    }

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
        ServerLifecycleHooks.getCurrentServer().allLevels.forEach { serverWorld ->
            serverWorld.entities.forEach { entity ->
                if (entity is ICustomizableEntity) {
                    if (event.player == entity.getOwner()) {
                        entity.remove()
                    }
                }else if(entity is WorldAxeEntity){
                    if(event.player == entity.owner){
                        entity.remove()
                    }
                }
            }
        }
        val targetCap = event.player.getCapability(TargetCapability.TARGET_HANDLER)
        if(targetCap.resolve().isPresent){
            val targets = targetCap.resolve().get()
            targets.clearTargets()
        }
    }

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent){
        if(event.entityLiving is PlayerEntity){
            val cap = event.entityLiving.getCapability(TargetCapability.TARGET_HANDLER)
            if(cap.resolve().isPresent){
                cap.resolve().get().clearTargets()
            }
        }
    }

    @SubscribeEvent
    fun onPlayerChangeDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        ServerLifecycleHooks.getCurrentServer().allLevels.forEach { serverWorld ->
            serverWorld.entities.forEach { entity ->
                if (entity is CustomizableBlockEntity) {
                    if (event.player == entity.getOwner()) {
                        entity.toWorld()
                    }
                }else if(entity is WorldAxeEntity){
                    entity.remove()
                }
            }
        }
        val targetCap = event.player.getCapability(TargetCapability.TARGET_HANDLER)
        if(targetCap.resolve().isPresent){
            val targets = targetCap.resolve().get()
            targets.clearTargets()
        }
    }

    @SubscribeEvent
    fun onProjectileHit(event: ProjectileImpactEvent.Arrow) {
        if (instaArrows.contains(event.arrow)) {
            val r = event.rayTraceResult
            if (r is EntityRayTraceResult) {
                r.entity.isInvulnerable = false
                r.entity.invulnerableTime = 0
            }
        }
    }

    @SubscribeEvent
    fun onEntityDamage(event: LivingDamageEvent) {
        if (event.source.getMsgId() == "explosion.player") {
            val entityDamageSource = event.source as EntityDamageSource
            if (entityDamageSource.isExplosion) {
                if (explosionImmune.contains(entityDamageSource.directEntity)) {
                    event.amount = 0f
                    event.isCanceled = true
                }
            }
        }
    }

    @SubscribeEvent
    fun onEntitySpawn(event: EntityJoinWorldEvent) {
        if (!event.entity.level.isClientSide) {
            if (event.entity is AbstractArrowEntity) {
                val arrowEntity = event.entity as AbstractArrowEntity
                if (arrowEntity.owner is LivingEntity) {
                    val shooter = arrowEntity.owner as LivingEntity
                    if (arrowEntity is ArrowEntity) {
                        val stack = if (shooter.mainHandItem.item === Items.CROSSBOW) shooter.mainHandItem else shooter.offhandItem
                        if (stack.item === Items.CROSSBOW) {
                            val enchants = EnchantmentHelper.getEnchantments(stack)
                            if (enchants.containsKey(TCombatEnchants.FOCUS.get())) {
                                instaArrows.add(arrowEntity)
                                if (enchants.containsKey(Enchantments.MULTISHOT)) {
                                    if (!WorldEventListener.multiShotTracker.containsKey(shooter)) {
                                        WorldEventListener.multiShotTracker[shooter] = ArrowCounter()
                                    }
                                    val counter = WorldEventListener.multiShotTracker[shooter]
                                    if (counter!!.count == 3) return
                                    counter.count(arrowEntity)
                                    if (counter.count == 3) {
                                        val entities = counter.arrows
                                        val vector3d1 = shooter.getUpVector(1.0f)
                                        val quaternion = Quaternion(Vector3f(vector3d1), 0F, true)
                                        val vector3d = shooter.getViewVector(1.0f)
                                        val vector3f = Vector3f(vector3d)
                                        vector3f.transform(quaternion)
                                        val originalVector = entities[0]!!.deltaMovement
                                        for (i in 1..2) {
                                            entities[i]!!.setDeltaMovement(
                                                vector3f.x() * originalVector.length(), vector3f.y() * originalVector.length(), vector3f.z() * originalVector.length()
                                            )
                                            entities[i]!!.hurtMarked = true
                                        }
                                    }
                                } else {
                                    val vector3d1 = shooter.getUpVector(1.0f)
                                    val quaternion = Quaternion(Vector3f(vector3d1), 0F, true)
                                    val vector3d = shooter.lookAngle
                                    val vector3f = Vector3f(vector3d)
                                    vector3f.transform(quaternion)
                                    val originalVector = arrowEntity.deltaMovement
                                    arrowEntity.setDeltaMovement(
                                        vector3f.x() * originalVector.length(), vector3f.y() * originalVector.length(), vector3f.z() * originalVector.length()
                                    )
                                    arrowEntity.hurtMarked
                                }
                            }

                            if (enchants.containsKey(TCombatEnchants.CROSSBOW_FLAME.get())) {
                                arrowEntity.setSecondsOnFire(100)
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