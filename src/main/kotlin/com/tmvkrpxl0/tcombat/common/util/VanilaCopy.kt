package com.tmvkrpxl0.tcombat.common.util

import net.minecraft.block.Blocks
import net.minecraft.block.FlowingFluidBlock
import net.minecraft.block.material.Material
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.BucketItem
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.state.Property
import net.minecraft.tags.FluidTags
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.shapes.ISelectionContext
import net.minecraft.util.registry.Registry
import net.minecraft.world.IWorld
import net.minecraft.world.World
import net.minecraftforge.common.util.BlockSnapshot
import net.minecraftforge.event.ForgeEventFactory
import javax.annotation.Nonnull

object VanilaCopy {
    fun playEmptySound(@Nonnull item: BucketItem, player: PlayerEntity, worldIn: IWorld, pos: BlockPos) {
        val content = item.fluid
        var soundevent = content.attributes.emptySound
        if (soundevent == null) soundevent = if (content.`is`(FluidTags.LAVA)) SoundEvents.BUCKET_EMPTY_LAVA else SoundEvents.BUCKET_EMPTY
        worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0f, 1.0f)
    }

    fun freezeGround(living: Entity, worldIn: World, pos: BlockPos, level: Int) {
        val blockState = Blocks.FROSTED_ICE.defaultBlockState()
        val f = 16.coerceAtMost(2 + level).toFloat()
        val mutablePos = BlockPos.Mutable()
        for (blockPos in BlockPos.betweenClosed(
            pos.offset(-f.toDouble(), -1.0, -f.toDouble()), pos.offset(f.toDouble(), -1.0, f.toDouble())
        )) {
            if (blockPos.closerThan(living.position(), f.toDouble())) {
                Enchantments.FROST_WALKER
                mutablePos.set(blockPos.x, blockPos.y + 1, blockPos.z)
                val blockState1 = worldIn.getBlockState(mutablePos)
                if (blockState1.isAir(worldIn, mutablePos)) {
                    val blockState2 = worldIn.getBlockState(blockPos)
                    val isFull = blockState2.block === Blocks.WATER && blockState2.getValue(FlowingFluidBlock.LEVEL) == 0
                    if (blockState2.material == Material.WATER && isFull && blockState.canSurvive(
                            worldIn, blockPos
                        ) && worldIn.isUnobstructed(
                            blockState, blockPos, ISelectionContext.empty()
                        ) && !ForgeEventFactory.onBlockPlace(
                            living, BlockSnapshot.create(worldIn.dimension(), worldIn, blockPos), Direction.UP
                        )) {
                        worldIn.setBlockAndUpdate(blockPos, blockState)
                        worldIn.blockTicks.scheduleTick(
                            blockPos, Blocks.FROSTED_ICE, MathHelper.nextInt(worldIn.random, 60, 120)
                        )
                    }
                }
            }
        }
    }

    //Modified copy of readFluidState from NBTUtil
    fun readFluidState(tag: CompoundNBT): FluidState {
        return if (!tag.contains("Name", 8)) {
            Fluids.EMPTY.defaultFluidState()
        } else {
            val fluid = Registry.FLUID.get(ResourceLocation(tag.getString("Name")))
            var fluidState = fluid.defaultFluidState()
            if (tag.contains("Properties", 10)) {
                val compoundNBT = tag.getCompound("Properties")
                val statecontainer = fluid.stateDefinition
                for (s in compoundNBT.allKeys) {
                    val property = statecontainer.getProperty(s)
                    if (property != null) {
                        fluidState = NBTUtil.setValueHelper(fluidState, property, s, compoundNBT, tag)
                    }
                }
            }
            fluidState
        }
    }

    //Modified copy of writeFluidState from NBTUtil
    fun writeFluidState(tag: FluidState): CompoundNBT {
        val compoundNBT = CompoundNBT()
        compoundNBT.putString("Name", Registry.FLUID.getKey(tag.type).toString())
        val immutableMap = tag.values
        if (!immutableMap.isEmpty()) {
            val compoundNBT1 = CompoundNBT()
            for ((property, value) in immutableMap) {
                compoundNBT1.putString(property.name, getName(property, value))
            }
            compoundNBT.put("Properties", compoundNBT1)
        }
        return compoundNBT
    }

    fun <T : Comparable<T>?> getName(p_190010_0_: Property<T>, p_190010_1_: Comparable<*>): String {
        return p_190010_0_.getName(p_190010_1_ as T)
    }
}