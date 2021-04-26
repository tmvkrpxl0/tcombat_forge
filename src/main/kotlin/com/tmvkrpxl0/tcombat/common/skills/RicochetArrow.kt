package com.tmvkrpxl0.tcombat.common.skills

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraft.entity.Entity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.DamageSource
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.AxisAlignedBB
import kotlin.math.cos
import kotlin.math.sin

object RicochetArrow : AbstractActiveSkill() {
    private val sizeBig = AxisAlignedBB.ofSize(30.0, 30.0, 30.0)
    private val sizeSmall = AxisAlignedBB.ofSize(0.2, 0.2, 0.2)
    private val NAME = ResourceLocation(TCombatMain.MODID, "ricochet_arrow")
    override fun execute(player: ServerPlayerEntity): Boolean {
        if (!player.level.isClientSide) {
            val axisAlignedBB = sizeBig.move(player.position())
            val list = player.level.getEntities(
                player, axisAlignedBB
            ) { o: Entity ->
                var b1 = o is ProjectileEntity && o.owner != player && o.distanceToSqr(player) < 15 * 15 && player.canSee(o)
                if(o is AbstractArrowEntity){
                    if(o.inGround)b1 = false
                }
                return@getEntities b1
            }
            if (list.isEmpty()) return false
            val lookVec = player.lookAngle
            val eyeVector = player.getEyePosition(1f)
            for (e in list) {
                val projectile = e as ProjectileEntity
                val entityVector = projectile.position()
                val difference = entityVector.subtract(eyeVector)
                val flyToLeft = -lookVec.x * difference.z + lookVec.z * difference.x > 0
                val radian = Math.toRadians(if (flyToLeft) 90.0 else -90.0)
                val velocity = projectile.deltaMovement
                val x = velocity.x
                val z = velocity.z
                val sin = sin(radian)
                val cos = cos(radian)
                projectile.setDeltaMovement(cos * x - sin * z, projectile.deltaMovement.y, sin * x + cos * z)
                projectile.hurtMarked = true
                e.level.addParticle(
                    ParticleTypes.SWEEP_ATTACK, entityVector.x, entityVector.y, entityVector.z, 0.0, 0.0, 0.0
                )
                player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.2f, 1f)
                for (temp in e.level.getEntities(e, sizeSmall.move(entityVector))) {
                    temp.hurt(DamageSource.playerAttack(player), 1f)
                }
            }
            TCombatMain.LOGGER.info("REFLECTED!!!")
        }
        return true
    }

    override fun getRegistryName(): ResourceLocation = NAME

}