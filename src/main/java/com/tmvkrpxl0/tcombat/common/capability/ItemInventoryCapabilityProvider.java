package com.tmvkrpxl0.tcombat.common.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemInventoryCapabilityProvider implements ICapabilitySerializable<INBT> {
    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY==cap){
            return LazyOptional.of(() -> (T) itemStackHandler);
        }
        return LazyOptional.empty();
    }

    private ItemStackHandler itemStackHandler = new ItemStackHandler(9);

    @Override
    public INBT serializeNBT() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemStackHandler, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemStackHandler, null, nbt);
    }
}
