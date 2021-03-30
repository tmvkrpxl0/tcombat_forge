package com.tmvkrpxl0.tcombat.common.util

import com.google.common.primitives.Doubles
import net.minecraft.block.ILiquidContainer
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FlowingFluid
import net.minecraft.fluid.Fluid
import net.minecraft.item.BucketItem
import net.minecraft.particles.ParticleTypes
import net.minecraft.tags.FluidTags
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.IWorld
import net.minecraft.world.World
import net.minecraftforge.fml.server.ServerLifecycleHooks
import java.util.*
import java.util.stream.Collectors
import javax.annotation.Nonnull

object TCombatUtil {
    private val targets = HashMap<PlayerEntity, List<LivingEntity>>()
    fun getEntityVectorAngle(
        @Nonnull from: Entity,
        @Nonnull to: Entity,
        @Nonnull sourceToDestVelocity: Vector3d
    ): Double {
        val sourcePosition = from.positionVec
        val targetPosition = to.positionVec
        val difference = targetPosition.subtract(sourcePosition)
        return Math.toDegrees(angle(sourceToDestVelocity, difference).toDouble())
    }

    @JvmOverloads
    fun emptyBucket(
        @Nonnull item: BucketItem,
        player: PlayerEntity,
        worldIn: World,
        posIn: BlockPos,
        rayTrace: BlockRayTraceResult?,
        fluid: Fluid? = null
    ): Boolean {
        var content = item.fluid
        content = fluid ?: content
        return if (content !is FlowingFluid) {
            false
        } else {
            val blockstate = worldIn.getBlockState(posIn)
            val block = blockstate.block
            val material = blockstate.material
            val flag = blockstate.isReplaceable(content)
            val flag1 =
                blockstate.isAir || flag || block is ILiquidContainer && (block as ILiquidContainer).canContainFluid(
                    worldIn,
                    posIn,
                    blockstate,
                    content
                )
            if (!flag1) {
                rayTrace != null && emptyBucket(item, player, worldIn, rayTrace.pos.offset(rayTrace.face), null)
            } else if (worldIn.dimensionType.isUltrawarm && content.isIn(FluidTags.WATER)) {
                val i = posIn.x
                val j = posIn.y
                val k = posIn.z
                worldIn.playSound(
                    player,
                    posIn,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS,
                    0.5f,
                    2.6f + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8f
                )
                for (l in 0..7) {
                    worldIn.addParticle(
                        ParticleTypes.LARGE_SMOKE,
                        i.toDouble() + Math.random(),
                        j.toDouble() + Math.random(),
                        k.toDouble() + Math.random(),
                        0.0,
                        0.0,
                        0.0
                    )
                }
                true
            } else if (block is ILiquidContainer && (block as ILiquidContainer).canContainFluid(
                    worldIn,
                    posIn,
                    blockstate,
                    content
                )
            ) {
                (block as ILiquidContainer).receiveFluid(
                    worldIn,
                    posIn,
                    blockstate,
                    content.getStillFluidState(false)
                )
                playEmptySound(item, player, worldIn, posIn)
                true
            } else {
                if (!worldIn.isRemote && flag && !material.isLiquid) {
                    worldIn.destroyBlock(posIn, true)
                }
                if (!worldIn.setBlockState(
                        posIn,
                        fluid!!.defaultState.blockState,
                        11
                    ) && !blockstate.fluidState.isSource
                ) {
                    false
                } else {
                    playEmptySound(item, player, worldIn, posIn)
                    true
                }
            }
        }
    }

    internal fun playEmptySound(@Nonnull item: BucketItem, player: PlayerEntity, worldIn: IWorld, pos: BlockPos) {
        val content = item.fluid
        var soundevent = content.attributes.emptySound
        if (soundevent == null) soundevent =
            if (content.isIn(FluidTags.LAVA)) SoundEvents.ITEM_BUCKET_EMPTY_LAVA else SoundEvents.ITEM_BUCKET_EMPTY
        worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0f, 1.0f)
    }

    fun getEntityToEntityAngle(@Nonnull from: Entity, @Nonnull to: Entity): Double {
        return getEntityVectorAngle(from, to, from.motion)
    }

    fun angle(@Nonnull a: Vector3d, b: Vector3d): Float {
        val dot = Doubles.constrainToRange(a.dotProduct(b) / (a.length() * b.length()), -1.0, 1.0)
        return Math.acos(dot).toFloat()
    }

    @Nonnull
    fun getTargets(@Nonnull player: PlayerEntity): List<LivingEntity> {
        return if (targets.containsKey(player)) targets[player]!!
            .stream().filter { obj: LivingEntity -> obj.isAlive }.collect(Collectors.toList()) else LinkedList()
    }

    fun setTargets(@Nonnull player: PlayerEntity, @Nonnull list: List<LivingEntity>) {
        targets[player] = list
    }

    fun getWorldId(@Nonnull world: World): Int {
        val id = 0
        for (w in ServerLifecycleHooks.getCurrentServer().worlds) {
            if (w === world) return id
        }
        throw NullPointerException("Cannot find world!")
    }

    fun getWorldById(id: Int): World? {
        var idx = 0
        for (w in ServerLifecycleHooks.getCurrentServer().worlds) {
            if (idx == id) return w
            idx++
        }
        return null
    }
}