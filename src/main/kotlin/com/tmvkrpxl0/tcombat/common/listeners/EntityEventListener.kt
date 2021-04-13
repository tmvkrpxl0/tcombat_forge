package com.tmvkrpxl0.tcombat.common.listeners

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.item.Items
import net.minecraft.util.EntityDamageSource
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.ProjectileImpactEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(
    modid = TCombatMain.MODID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object EntityEventListener {
    val multishotTracker: MutableMap<LivingEntity, ArrowCounter> = HashMap()
    val instaArrows: MutableSet<ArrowEntity> = HashSet()
    val explosionImmune: MutableSet<LivingEntity> = HashSet()
    @SubscribeEvent
    fun onProjectileHit(event: ProjectileImpactEvent.Arrow) {
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
    fun onEntitySpawn(event: EntityJoinWorldEvent) {
        if (!event.entity.world.isRemote()) {
            if (event.entity is AbstractArrowEntity) {
                val arrowEntity = event.entity as AbstractArrowEntity
                if (arrowEntity.shooter is LivingEntity) {
                    val shooter = arrowEntity.shooter as LivingEntity
                    if (arrowEntity is ArrowEntity) {
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

        fun count(entity: ArrowEntity) {
            arrows[count] = entity
            count++
        }
    }
}