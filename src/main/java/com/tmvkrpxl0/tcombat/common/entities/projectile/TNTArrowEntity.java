package com.tmvkrpxl0.tcombat.common.entities.projectile;

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes;
import com.tmvkrpxl0.tcombat.common.items.TCombatItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class TNTArrowEntity extends AbstractArrowEntity {
    private boolean explode = true;
    public TNTArrowEntity( World p_i48547_8_, double p_i48547_2_, double p_i48547_4_, double p_i48547_6_){
        super(TCombatEntityTypes.TNT_ARROW.get(), p_i48547_2_, p_i48547_4_, p_i48547_6_, p_i48547_8_);
    }

    public TNTArrowEntity(EntityType<TNTArrowEntity> fireChargeArrowEntityType, World world) {
        super(fireChargeArrowEntityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(isInWater())explode = false;
        if(isInLava()){
            if(explode){
                if(!level.isClientSide){
                    if(this.getOwner()!=null){
                        this.level.explode(this.getOwner(), getX(), getY(), getZ(), isCritArrow()?8:4, isOnFire(), Explosion.Mode.BREAK);
                    }else{
                        this.level.explode(this, getX(), getY(), getZ(), isCritArrow()?8:4, isOnFire(), Explosion.Mode.BREAK);
                    }
                }
                this.remove();
            }
        }
    }

    public boolean isExplode() {
        return explode;
    }

    public void setExplode(boolean explode){
        this.explode = explode;
    }

    @Override
    protected void onHitEntity(@Nonnull EntityRayTraceResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        if(level.isClientSide())return;
        Vector3d location = p_213868_1_.getLocation();
        if(explode && !isInWater()){
            if(!level.isClientSide){
                if(this.getOwner()!=null){
                    this.level.explode(this.getOwner(), location.x, location.y, location.z, isCritArrow()?8:4, isOnFire(), Explosion.Mode.BREAK);
                }else{
                    this.level.explode(this, location.x, location.y, location.z, isCritArrow()?8:4, isOnFire(), Explosion.Mode.BREAK);
                }
            }
            this.remove();
        }
    }

    @Override
    protected void onHitBlock(@Nonnull BlockRayTraceResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
        BlockPos pos = p_230299_1_.getBlockPos();
        if(explode && !isInWater()){
            if(!level.isClientSide){
                if(this.getOwner()!=null){
                    this.level.explode(this.getOwner(), pos.getX(), pos.getY(), pos.getZ(), isCritArrow()?8:4, isOnFire(), Explosion.Mode.BREAK);
                }else{
                    this.level.explode(this, pos.getX(), pos.getY(), pos.getZ(), isCritArrow()?8:4, isOnFire(), Explosion.Mode.BREAK);
                }
            }
            this.remove();
        }
    }

    @Nonnull
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(TCombatItems.TNT_ARROW.get());
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
