package com.tmvkrpxl0.tcombat.common.skills;

import com.tmvkrpxl0.tcombat.TCombatMain;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;

public class AutoBridge extends AbstractPassiveSkill{
    private static final ResourceLocation NAME = new ResourceLocation(TCombatMain.MODID, "auto_bridge");

    @Override
    protected boolean onTick(TickEvent.ServerTickEvent event, PlayerEntity player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        if(mainHand.getItem() instanceof BucketItem && offHand.getItem() instanceof BucketItem){
            BucketItem mainBucket = (BucketItem) mainHand.getItem();
            BucketItem offBucket = (BucketItem) offHand.getItem();
            int i1 = mainBucket.getFluid() instanceof WaterFluid?1:(mainBucket.getFluid() instanceof LavaFluid?2:0);
            int i2 = offBucket.getFluid() instanceof WaterFluid?1:(offBucket.getFluid() instanceof LavaFluid?2:0);
            if(i1+i2==3){
                Vector3d lookVec = player.getLookAngle();
                lookVec = lookVec.subtract(0,lookVec.y,0);
                BlockPos pos = new BlockPos(player.position().add(lookVec));
                BlockPos below = pos.below();
                World world = player.level;
                if(player.isShiftKeyDown()){
                    pos = below;
                    below = pos.below();
                }
                BlockState belowState = world.getBlockState(below);
                BlockState state = world.getBlockState(pos);
                if((belowState.isAir() || belowState.canBeReplaced(Fluids.FLOWING_WATER)) &&
                        (state.isAir() || state.canBeReplaced(Fluids.FLOWING_WATER))){
                    world.setBlock(below, Fluids.FLOWING_LAVA.defaultFluidState().createLegacyBlock(), 11);
                    world.setBlock(pos, Fluids.FLOWING_WATER.defaultFluidState().createLegacyBlock(), 11);
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 1);
                }
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }
}
