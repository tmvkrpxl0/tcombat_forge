package com.tmvkrpxl0.tcombat.common.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;


public class CustomizableBlockEntity extends Entity {
    private boolean isSolid = false;
    protected static final DataParameter<Integer> OWNER_ID = EntityDataManager.createKey(IronGolemEntity.class, DataSerializers.VARINT);
    private PlayerEntity player;
    private BlockState blockState = Blocks.SAND.getDefaultState();
    public CustomizableBlockEntity(World worldIn, double x, double y, double z, BlockState blockState, PlayerEntity player, boolean isSolid) {
        this(TCombatEntityTypes.CUSTOMIZEABLE_BLOCK_ENTITY.get(), worldIn);
        this.setPosition(x, y + (double)((1.0F - this.getHeight()) / 2.0F), z);
        this.player = player;
        this.blockState = blockState;
        this.isSolid = isSolid;
    }


    public CustomizableBlockEntity(EntityType<? extends CustomizableBlockEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isRemote){
            if(player==null || !player.isAlive() || !world.getPlayers().contains(player))this.remove();
        }
    }

    @Override
    protected void registerData() {
        this.dataManager.register(OWNER_ID, player.getEntityId());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.blockState = NBTUtil.readBlockState(compound.getCompound("BlockState"));
        if(this.blockState.getBlock().isAir(this.blockState, this.getEntityWorld(), this.getPosition())){
            this.blockState = Blocks.SAND.getDefaultState();
        }
        this.isSolid = compound.getBoolean("Solid");
        this.player = world.getPlayerByUuid(compound.getUniqueId("Owner"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putBoolean("Solid", isSolid);
        compound.put("BlockState", NBTUtil.writeBlockState(blockState));
        compound.putUniqueId("Owner", player.getUniqueID());
    }


    public BlockState getBlockState(){
        return this.blockState;
    }

    @Override
    public boolean func_241845_aY() {
        return true;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
