package com.tmvkrpxl0.tcombat.common.items;

import com.tmvkrpxl0.tcombat.common.capability.ItemInventoryCapabilityProvider;
import com.tmvkrpxl0.tcombat.common.container.MobileDispenserGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MobileDispenser extends Item {
    public MobileDispenser(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {
        if(!worldIn.isRemote){
            ServerPlayerEntity playerEntity = (ServerPlayerEntity) playerIn;
            ItemStack stack = playerIn.getHeldItem(handIn);
            playerEntity.openContainer(new MobileDispenserGui(stack));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
//Override initCapabilities and return an ICapabilitySerializable which exposes IItemHandler as a capability. Use ItemStackHandler as your IItemHandler implementation

    @Nullable
    @Override
    public ICapabilitySerializable<? extends INBT> initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().isPresent()){
            return stack;
        }else{
            return new ItemInventoryCapabilityProvider();
        }
    }
}
