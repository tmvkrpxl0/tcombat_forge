package com.tmvkrpxl0.tcombat.common.skills

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.fluid.LavaFluid
import net.minecraft.fluid.WaterFluid
import net.minecraft.item.BucketItem
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.event.TickEvent.ServerTickEvent

object AutoBridge : AbstractPassiveSkill() {
    private val NAME = ResourceLocation(TCombatMain.MODID, "auto_bridge")
    override fun onTick(event: ServerTickEvent, player: PlayerEntity): Boolean {
        val mainHand = player.mainHandItem
        val offHand = player.offhandItem
        if (mainHand.item is BucketItem && offHand.item is BucketItem) {
            val mainBucket = mainHand.item as BucketItem
            val offBucket = offHand.item as BucketItem
            val i1 = if (mainBucket.fluid is WaterFluid) 1 else if (mainBucket.fluid is LavaFluid) 2 else 0
            val i2 = if (offBucket.fluid is WaterFluid) 1 else if (offBucket.fluid is LavaFluid) 2 else 0
            if (i1 + i2 == 3) {
                var lookVec = player.lookAngle
                lookVec = lookVec.subtract(0.0, lookVec.y, 0.0)
                var pos = BlockPos(player.position().add(lookVec))
                var below = pos.below()
                val world = player.level
                if (player.isShiftKeyDown) {
                    pos = below
                    below = pos.below()
                }
                val belowState = world.getBlockState(below)
                val state = world.getBlockState(pos)
                if ((belowState.isAir || belowState.canBeReplaced(Fluids.FLOWING_WATER)) && (state.isAir || state.canBeReplaced(Fluids.FLOWING_WATER))) {
                    world.setBlock(below, Fluids.FLOWING_LAVA.defaultFluidState().createLegacyBlock(), 11)
                    world.setBlock(pos, Fluids.FLOWING_WATER.defaultFluidState().createLegacyBlock(), 11)
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 1)
                }
                return true
            }
        }
        return false
    }

    override fun getRegistryName(): ResourceLocation = NAME
}