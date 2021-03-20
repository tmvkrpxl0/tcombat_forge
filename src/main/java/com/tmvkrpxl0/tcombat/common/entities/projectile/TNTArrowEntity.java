package com.tmvkrpxl0.tcombat.common.entities.projectile;

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes;
import com.tmvkrpxl0.tcombat.common.items.TCombatItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TNTArrowEntity extends AbstractArrowEntity {
    public LivingEntity shooter;
    public TNTArrowEntity( World p_i48547_8_, double p_i48547_2_, double p_i48547_4_, double p_i48547_6_){
        super(TCombatEntityTypes.TNT_ARROW.get(), p_i48547_2_, p_i48547_4_, p_i48547_6_, p_i48547_8_);
    }

    public TNTArrowEntity(EntityType<TNTArrowEntity> fireChargeArrowEntityType, World world) {
        super(fireChargeArrowEntityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level.isClientSide()){
            //level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, position().x, position().y, position().z, 0, 0, 0);
        }else{
            if(inGround){
                if(this.getOwner()!=null){
                    this.level.explode(this.getOwner(), this.getX(), this.getY(0.0625D), this.getZ(), 4.0F, Explosion.Mode.BREAK);
                }else{
                    this.level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 4.0F, Explosion.Mode.BREAK);
                }
                this.remove();
            }
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(TCombatItems.TNT_ARROW.get());
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
