package com.tmvkrpxl0.tcombat.common.util

import com.google.common.primitives.Doubles
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FlowingFluid
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.BucketItem
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.IDataSerializer
import net.minecraft.particles.ParticleTypes
import net.minecraft.state.Property
import net.minecraft.tags.FluidTags
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.shapes.ISelectionContext
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.IWorld
import net.minecraft.world.World
import net.minecraftforge.common.util.BlockSnapshot
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.fml.server.ServerLifecycleHooks
import java.util.*
import java.util.stream.Collectors
import javax.annotation.Nonnull
import kotlin.math.acos

object TCombatUtil {
    private val targets = HashMap<PlayerEntity, List<LivingEntity>>()
    fun getEntityVectorAngle(@Nonnull from: Entity, @Nonnull to: Entity, @Nonnull sourceToDestVelocity: Vector3d): Double {
        val sourcePosition = from.positionVec
        val targetPosition = to.positionVec
        val difference = targetPosition.subtract(sourcePosition)
        return Math.toDegrees(angle(sourceToDestVelocity, difference).toDouble())
    }

    @JvmOverloads
    fun emptyBucket(@Nonnull item: BucketItem, player: PlayerEntity, worldIn: World, posIn: BlockPos, rayTrace: BlockRayTraceResult?, fluid: Fluid? = null): Boolean {
        var content = item.fluid
        content = fluid ?: content
        return if (content !is FlowingFluid) {
            false
        } else {
            val blockstate = worldIn.getBlockState(posIn)
            val block = blockstate.block
            val material = blockstate.material
            val flag = blockstate.isReplaceable(content)
            val flag1 = blockstate.isAir || flag || block is ILiquidContainer && (block as ILiquidContainer).canContainFluid(
                worldIn, posIn, blockstate, content
            )
            if (!flag1) {
                rayTrace != null && emptyBucket(item, player, worldIn, rayTrace.pos.offset(rayTrace.face), null)
            } else if (worldIn.dimensionType.isUltrawarm && content.isIn(FluidTags.WATER)) {
                val i = posIn.x
                val j = posIn.y
                val k = posIn.z
                worldIn.playSound(
                    player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8f
                )
                for (l in 0..7) {
                    worldIn.addParticle(
                        ParticleTypes.LARGE_SMOKE, i.toDouble() + Math.random(), j.toDouble() + Math.random(), k.toDouble() + Math.random(), 0.0, 0.0, 0.0
                    )
                }
                true
            } else if (block is ILiquidContainer && (block as ILiquidContainer).canContainFluid(
                        worldIn, posIn, blockstate, content
                    )) {
                (block as ILiquidContainer).receiveFluid(
                    worldIn, posIn, blockstate, content.getStillFluidState(false)
                )
                playEmptySound(item, player, worldIn, posIn)
                true
            } else {
                if (!worldIn.isRemote && flag && !material.isLiquid) {
                    worldIn.destroyBlock(posIn, true)
                }
                if (!worldIn.setBlockState(
                            posIn, fluid!!.defaultState.blockState, 11
                        ) && !blockstate.fluidState.isSource) {
                    false
                } else {
                    playEmptySound(item, player, worldIn, posIn)
                    true
                }
            }
        }
    }

    fun playEmptySound(@Nonnull item: BucketItem, player: PlayerEntity, worldIn: IWorld, pos: BlockPos) {
        val content = item.fluid
        var soundevent = content.attributes.emptySound
        if (soundevent == null) soundevent = if (content.isIn(FluidTags.LAVA)) SoundEvents.ITEM_BUCKET_EMPTY_LAVA else SoundEvents.ITEM_BUCKET_EMPTY
        worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0f, 1.0f)
    }

    fun getEntityToEntityAngle(@Nonnull from: Entity, @Nonnull to: Entity): Double {
        return getEntityVectorAngle(from, to, from.motion)
    }

    fun angle(@Nonnull a: Vector3d, b: Vector3d): Float {
        val dot = Doubles.constrainToRange(a.dotProduct(b) / (a.length() * b.length()), -1.0, 1.0)
        return acos(dot).toFloat()
    }

    @Nonnull
    fun getTargets(@Nonnull player: PlayerEntity): List<LivingEntity> {
        return if (targets.containsKey(player)) targets[player]!!.stream().filter { obj: LivingEntity -> obj.isAlive }.collect(Collectors.toList()) else LinkedList()
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

    fun freezeGround(living: Entity, worldIn: World, pos: BlockPos, level: Int) {
        val blockstate = Blocks.FROSTED_ICE.defaultState
        val f = 16.coerceAtMost(2 + level).toFloat()
        val mutablePos = BlockPos.Mutable()
        for (blockPos in BlockPos.getAllInBoxMutable(
            pos.add(-f.toDouble(), -1.0, -f.toDouble()), pos.add(f.toDouble(), -1.0, f.toDouble())
        )) {
            if (blockPos.withinDistance(living.positionVec, f.toDouble())) {
                Enchantments.FROST_WALKER
                mutablePos.setPos(blockPos.x, blockPos.y + 1, blockPos.z)
                val blockstate1 = worldIn.getBlockState(mutablePos)
                if (blockstate1.isAir(worldIn, mutablePos)) {
                    val blockstate2 = worldIn.getBlockState(blockPos)
                    val isFull = blockstate2.block === Blocks.WATER && blockstate2.get(FlowingFluidBlock.LEVEL) == 0
                    if (blockstate2.material == Material.WATER && isFull && blockstate.isValidPosition(
                                worldIn, blockPos
                            ) && worldIn.placedBlockCollides(
                                blockstate, blockPos, ISelectionContext.dummy()
                            ) && !ForgeEventFactory.onBlockPlace(
                                living, BlockSnapshot.create(worldIn.dimensionKey, worldIn, blockPos), Direction.UP
                            )) {
                        worldIn.setBlockState(blockPos, blockstate)
                        worldIn.pendingBlockTicks.scheduleTick(
                            blockPos, Blocks.FROSTED_ICE, MathHelper.nextInt(worldIn.rand, 60, 120)
                        )
                    }
                }
            }
        }
    }

    //Modified copy of readFluidState from NBTUtil
    fun readFluidState(tag: CompoundNBT): FluidState {
        return if (!tag.contains("Name", 8)) {
            Fluids.EMPTY.defaultState
        } else {
            val fluid = Registry.FLUID.getOrDefault(ResourceLocation(tag.getString("Name")))
            var fluidState = fluid.defaultState
            if (tag.contains("Properties", 10)) {
                val compoundnbt = tag.getCompound("Properties")
                val statecontainer = fluid.stateContainer
                for (s in compoundnbt.keySet()) {
                    val property = statecontainer.getProperty(s)
                    if (property != null) {
                        fluidState = NBTUtil.setValueHelper(fluidState, property, s, compoundnbt, tag)
                    }
                }
            }
            fluidState
        }
    }

    //Modified copy of writeFluidState from NBTUtil
    fun writeFluidState(tag: FluidState): CompoundNBT {
        val compoundnbt = CompoundNBT()
        compoundnbt.putString("Name", Registry.FLUID.getKey(tag.fluid).toString())
        val immutablemap = tag.values
        if (!immutablemap.isEmpty()) {
            val compoundnbt1 = CompoundNBT()
            for ((property, value) in immutablemap) {
                compoundnbt1.putString(property.name, getName(property, value))
            }
            compoundnbt.put("Properties", compoundnbt1)
        }
        return compoundnbt
    }

    fun <T : Comparable<T>?> getName(p_190010_0_: Property<T>, p_190010_1_: Comparable<*>): String {
        return p_190010_0_.getName(p_190010_1_ as T)
    }

    val UNIQUE_ID: IDataSerializer<UUID> = object : IDataSerializer<UUID> {
        override fun write(buf: PacketBuffer, value: UUID) {
            buf.writeUniqueId(value)
        }

        override fun read(buf: PacketBuffer): UUID {
            return buf.readUniqueId()
        }

        override fun copyValue(value: UUID): UUID {
            return value
        }
    }

    val BLOCK_STATE: IDataSerializer<BlockState> = object : IDataSerializer<BlockState> {
        override fun write(buf: PacketBuffer, value: BlockState) {
            buf.writeVarInt(Block.getStateId(value))
        }

        override fun read(buf: PacketBuffer): BlockState {
            val i = buf.readVarInt()
            return Block.getStateById(i)
        }

        override fun copyValue(value: BlockState): BlockState {
            return value
        }
    }

    val FLUID_STATE: IDataSerializer<FluidState> = object : IDataSerializer<FluidState> {
        override fun write(buf: PacketBuffer, value: FluidState) {
            buf.writeVarInt(Block.getStateId(value.blockState))
        }

        override fun read(buf: PacketBuffer): FluidState {
            val i = buf.readVarInt()
            return Block.getStateById(i).fluidState
        }

        override fun copyValue(value: FluidState): FluidState {
            return value
        }
    }
}