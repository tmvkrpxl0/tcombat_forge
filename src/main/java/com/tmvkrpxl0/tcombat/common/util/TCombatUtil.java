package com.tmvkrpxl0.tcombat.common.util;

import com.google.common.primitives.Doubles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TCombatUtil {
    private static final HashMap<PlayerEntity, List<LivingEntity>> targets = new HashMap<>();
    private static final Field inGround = FieldUtils.getDeclaredField(AbstractArrowEntity.class, "inGround", true);

    public static double getEntityVectorAngle(@Nonnull Entity from, @Nonnull Entity to, @Nonnull Vector3d sourceToDestVelocity){
        Vector3d sourcePosition = from.position();
        Vector3d targetPosition = to.position();
        Vector3d difference = targetPosition.subtract(sourcePosition);
        return Math.toDegrees(angle(sourceToDestVelocity, difference));
    }

    public static boolean emptyBucket(@Nonnull BucketItem item, @Nullable PlayerEntity p_180616_1_, World p_180616_2_, BlockPos p_180616_3_, @Nullable BlockRayTraceResult p_180616_4_) {
        return emptyBucket(item, p_180616_1_, p_180616_2_, p_180616_3_, p_180616_4_, null);
    }

    public static boolean emptyBucket(@Nonnull BucketItem item, @Nullable PlayerEntity p_180616_1_, World p_180616_2_, BlockPos p_180616_3_, @Nullable BlockRayTraceResult p_180616_4_, @Nullable Fluid fluid) {
        Fluid content = item.getFluid();
        content = fluid==null? content : fluid;
        if (!(content instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = p_180616_2_.getBlockState(p_180616_3_);
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            boolean flag = blockstate.canBeReplaced(content);
            boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(p_180616_2_, p_180616_3_, blockstate, content);
            if (!flag1) {
                return p_180616_4_ != null && emptyBucket(item, p_180616_1_, p_180616_2_, p_180616_4_.getBlockPos().relative(p_180616_4_.getDirection()), null);
            } else if (p_180616_2_.dimensionType().ultraWarm() && content.is(FluidTags.WATER)) {
                int i = p_180616_3_.getX();
                int j = p_180616_3_.getY();
                int k = p_180616_3_.getZ();
                p_180616_2_.playSound(p_180616_1_, p_180616_3_, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_180616_2_.random.nextFloat() - p_180616_2_.random.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    p_180616_2_.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(p_180616_2_,p_180616_3_,blockstate,content)) {
                ((ILiquidContainer)block).placeLiquid(p_180616_2_, p_180616_3_, blockstate, ((FlowingFluid)content).getSource(false));
                playEmptySound(item, p_180616_1_, p_180616_2_, p_180616_3_);
                return true;
            } else {
                if (!p_180616_2_.isClientSide && flag && !material.isLiquid()) {
                    p_180616_2_.destroyBlock(p_180616_3_, true);
                }

                if (!p_180616_2_.setBlock(p_180616_3_, fluid.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    playEmptySound(item, p_180616_1_, p_180616_2_, p_180616_3_);
                    return true;
                }
            }
        }
    }

    protected static void playEmptySound(@Nonnull BucketItem item, @Nullable PlayerEntity p_203791_1_, IWorld p_203791_2_, BlockPos p_203791_3_) {
        Fluid content = item.getFluid();
        SoundEvent soundevent = content.getAttributes().getEmptySound();
        if(soundevent == null) soundevent = content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        p_203791_2_.playSound(p_203791_1_, p_203791_3_, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public static double getEntityToEntityAngle(@Nonnull Entity from, @Nonnull Entity to){
        return getEntityVectorAngle(from, to, from.getDeltaMovement());
    }

    public static float angle(@Nonnull Vector3d a, Vector3d b) {
        double dot = Doubles.constrainToRange(a.dot(b) / (a.length() * b.length()), -1.0D, 1.0D);
        return (float)Math.acos(dot);
    }

    @Nonnull
    public static List<LivingEntity> getTargets(@Nonnull PlayerEntity player) {
        return targets.containsKey(player)?targets.get(player).stream().filter(LivingEntity::isAlive).collect(Collectors.toList()):new LinkedList<>();
    }

    public static void setTargets(@Nonnull PlayerEntity player, @Nonnull List<LivingEntity> list){
        targets.put(player, list);
    }

    public static boolean inGround(@Nonnull AbstractArrowEntity abstractArrowEntity){
        try {
            return inGround.getBoolean(abstractArrowEntity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
