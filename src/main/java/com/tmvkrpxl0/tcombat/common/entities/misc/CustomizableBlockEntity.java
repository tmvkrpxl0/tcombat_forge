package com.tmvkrpxl0.tcombat.common.entities.misc;

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;


public class CustomizableBlockEntity extends Entity {
    private boolean isSolid = false;
    protected static final DataParameter<Integer> OWNER_ID = EntityDataManager.defineId(IronGolemEntity.class, DataSerializers.INT);
    private PlayerEntity player;
    private BlockState blockState = Blocks.SAND.defaultBlockState();
    public CustomizableBlockEntity(World worldIn, double x, double y, double z, BlockState blockState, PlayerEntity player, boolean isSolid) {
        super(TCombatEntityTypes.CUSTOMIZEABLE_BLOCK_ENTITY.get(), worldIn);
        this.setPos(x, y + (double)((1.0F - this.getBbHeight()) / 2.0F), z);
        this.player = player;
        this.blockState = blockState;
        this.isSolid = isSolid;
        this.entityData.set(OWNER_ID, this.player.getId());
    }


    public CustomizableBlockEntity(EntityType<? extends CustomizableBlockEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide){
            if(player==null || !player.isAlive() || !level.players().contains(player))this.remove();
        }
        blockState = Blocks.DARK_OAK_SAPLING.defaultBlockState();
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_ID, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        this.blockState = NBTUtil.readBlockState(compound.getCompound("BlockState"));
        if(this.blockState.getBlock().isAir(this.blockState, this.getCommandSenderWorld(), this.blockPosition())){
            this.blockState = Blocks.SAND.defaultBlockState();
        }
        this.isSolid = compound.getBoolean("Solid");
        this.player = level.getPlayerByUUID(compound.getUUID("Owner"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        compound.putBoolean("Solid", isSolid);
        compound.put("BlockState", NBTUtil.writeBlockState(blockState));
        compound.putUUID("Owner", player.getUUID());
    }


    public BlockState getBlockState(){
        return this.blockState;
    }

    @Override
    public boolean canBeCollidedWith() {
        return isSolid;
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
