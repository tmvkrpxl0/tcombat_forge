package com.tmvkrpxl0.tcombat.common.skills

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.DamageSource
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.AxisAlignedBB
import kotlin.math.cos
import kotlin.math.sin

object ReflectArrow : AbstractActiveSkill() {
    private val sizeBig = AxisAlignedBB.withSizeAtOrigin(30.0, 30.0, 30.0)
    private val sizeSmall = AxisAlignedBB.withSizeAtOrigin(0.2, 0.2, 0.2)
    private val NAME = ResourceLocation(TCombatMain.MODID, "reflect_arrow")
    override fun execute(player: ServerPlayerEntity): Boolean {
        if (!player.entityWorld.isRemote) {
            val axisAlignedBB = sizeBig.offset(player.positionVec)
            val list = player.world.getEntitiesInAABBexcluding(
                player, axisAlignedBB
            ) { o: Entity ->
                o is ProjectileEntity && o.shooter != player && o.getDistanceSq(player) < 15 * 15 && player.canEntityBeSeen(
                    o
                ) && TCombatUtil.getEntityToEntityAngle(
                    o,
                    player
                ) < 30 && (o !is ArrowEntity ||
                        (o as AbstractArrowEntity).inGround)
            }
            if (list.isEmpty()) return false
            val lookVec = player.getLook(1f)
            val eyeVector = player.getEyePosition(1f)
            for (e in list) {
                val projectile = e as ProjectileEntity
                val entityVector = projectile.positionVec
                val difference = entityVector.subtract(eyeVector)
                val flyToLeft = -lookVec.getX() * difference.getZ() + lookVec.getZ() * difference.getX() > 0
                val radian = Math.toRadians(if (flyToLeft) 90.0 else -90.0)
                val velocity = projectile.motion
                val x = velocity.getX()
                val z = velocity.getZ()
                val sin = sin(radian)
                val cos = cos(radian)
                projectile.setMotion(cos * x - sin * z, projectile.motion.y, sin * x + cos * z)
                projectile.velocityChanged = true
                e.world.addParticle(
                    ParticleTypes.SWEEP_ATTACK,
                    entityVector.x,
                    entityVector.y,
                    entityVector.z,
                    0.0,
                    0.0,
                    0.0
                )
                player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 0.2f, 1f)
                for (temp in e.world.getEntitiesWithinAABBExcludingEntity(e, sizeSmall.offset(entityVector))) {
                    temp.attackEntityFrom(DamageSource.causePlayerDamage(player), 1f)
                }
            }
            TCombatMain.LOGGER.info("REFLECTED!!!")
        }
        return true
    }

    override fun getRegistryName(): ResourceLocation = NAME

}