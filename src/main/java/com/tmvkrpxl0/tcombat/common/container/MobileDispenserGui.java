package com.tmvkrpxl0.tcombat.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.DispenserContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class MobileDispenserGui implements INamedContainerProvider {
    private final ItemStack mobileDispenser;

    public MobileDispenserGui(ItemStack mobileDispenser) {
        this.mobileDispenser = mobileDispenser;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return mobileDispenser.getHoverName();
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
        Optional<IItemHandler> optional = mobileDispenser.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
        DispenserContainer container = new DispenserContainer(windowId, inventory){
            @Override
            public void removed(@Nonnull PlayerEntity playerIn) {
                if(optional.isPresent() && player.isEffectiveAi()){
                    ItemStackHandler handler = (ItemStackHandler) optional.get();
                    for(int i = 0;i<9;i++){
                        Slot slot = this.getSlot(i);
                        ItemStack stack = slot.getItem();
                        handler.setStackInSlot(i, stack);
                    }
                }
                super.removed(playerIn);
            }
        };
        if(optional.isPresent() && player.isEffectiveAi()){
            IItemHandler handler = optional.get();
            for(int i = 0;i<9;i++){
                container.setItem(i, handler.getStackInSlot(i));
            }
        }
        return container;
    }

}
