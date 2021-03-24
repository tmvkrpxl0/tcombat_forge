package com.tmvkrpxl0.tcombat.common.items.projectile;

import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TNTArrow extends ArrowItem {
    public TNTArrow(Properties p_i48487_1_) {
        super(p_i48487_1_);
        DispenserBlock.registerBehavior(this, new ProjectileDispenseBehavior() {
            @Nonnull
            @Override
            protected ProjectileEntity getProjectile(@Nonnull World p_82499_1_, @Nonnull IPosition p_82499_2_, @Nonnull ItemStack p_82499_3_) {
                return new TNTArrowEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z());
            }
        });
    }

    @Nonnull
    @Override
    public AbstractArrowEntity createArrow(@Nonnull World p_200887_1_, @Nonnull ItemStack p_200887_2_, LivingEntity p_200887_3_) {
        Vector3d pos = p_200887_3_.position();
        TNTArrowEntity entity = new TNTArrowEntity(p_200887_1_, pos.x, pos.y, pos.z);
        entity.setOwner(p_200887_3_);
        return entity;
    }
}
