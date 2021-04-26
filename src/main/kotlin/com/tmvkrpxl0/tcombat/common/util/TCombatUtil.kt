package com.tmvkrpxl0.tcombat.common.util

import com.google.common.primitives.Doubles
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.vector.Vector3d
import java.util.*
import java.util.stream.Collectors
import javax.annotation.Nonnull
import kotlin.math.acos

object TCombatUtil {
    private val targets = HashMap<PlayerEntity, List<LivingEntity>>()
    fun getEntityVectorAngle(@Nonnull from: Entity, @Nonnull to: Entity, @Nonnull sourceToDestVelocity: Vector3d): Double {
        val sourcePosition = from.position()
        val targetPosition = to.position()
        val difference = targetPosition.subtract(sourcePosition)
        return Math.toDegrees(angle(sourceToDestVelocity, difference).toDouble())
    }

    fun getEntityToEntityAngle(@Nonnull from: Entity, @Nonnull to: Entity): Double {
        return getEntityVectorAngle(from, to, from.deltaMovement)
    }

    fun angle(@Nonnull a: Vector3d, b: Vector3d): Float {
        val dot = Doubles.constrainToRange(a.dot(b) / (a.length() * b.length()), -1.0, 1.0)
        return acos(dot).toFloat()
    }

    @Nonnull
    fun getTargets(@Nonnull player: PlayerEntity): List<LivingEntity> {
        return if (targets.containsKey(player)) targets[player]!!.stream().filter { obj: LivingEntity -> obj.isAlive }.collect(Collectors.toList()) else LinkedList()
    }

    fun setTargets(@Nonnull player: PlayerEntity, @Nonnull list: List<LivingEntity>) {
        targets[player] = list
    }

    fun setTargets(@Nonnull player: PlayerEntity){
            val targets = LinkedList<LivingEntity>()
            val degree = 60
            val playerDirection: Vector3d = player.lookAngle
            val playerEyeVector: Vector3d = player.getEyePosition(1f)
            var size = 0
            for(e in player.level.getEntities(player, AxisAlignedBB.ofSize(100.0, 100.0, 100.0).move(player.position()))) {
                if(e !is LivingEntity) continue
                if(!player.canSee(e)) continue
                val entityVector: Vector3d = e.position()
                val difference: Vector3d = entityVector.subtract(playerEyeVector)
                val angleDifference =
                    Math.toDegrees(angle(playerDirection, difference).toDouble())
                if(angleDifference <= degree) {
                    targets.add(e)
                    size++
                    if(size == 100) break
                }
            }
            setTargets(player, targets)
    }

    fun getReflect(@Nonnull v1: Vector3d, @Nonnull surface: Vector3d): Vector3d {
        val normal = surface.normalize()
        val dot2 = v1.dot(normal) * 2
        return v1.subtract(normal.scale(dot2))
    }

    //v1.x*v2.x + v1.y*v2.y + v1.z*v2.z = 0
    //v1.z*v2.z = - v1.x*v2.x - v1.y*v2.y
    //v2.z = (- v1.x*v2.x - v1.y*v2.y) / v1.z
    fun getPerpendicularRandom(@Nonnull v1: Vector3d, @Nonnull random: Random): Vector3d{
        val v2x = random.nextDouble() * v1.x
        val v2y = random.nextDouble() * v1.y
        val v2z = (-v2x -v2y) / v1.z
        return Vector3d(v2x, v2y, v2z).normalize()
    }
}