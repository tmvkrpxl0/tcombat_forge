package com.tmvkrpxl0.tcombat.common.container

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.DispenserContainer
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import javax.annotation.Nonnull

class MobileDispenserGui(private val mobileDispenser: ItemStack) : INamedContainerProvider {
    @Nonnull
    override fun getDisplayName(): ITextComponent {
        return mobileDispenser.textComponent
    }

    override fun createMenu(windowId: Int, @Nonnull inventory: PlayerInventory, @Nonnull player: PlayerEntity): Container {
        val optional = mobileDispenser.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve()
        val container: DispenserContainer = object : DispenserContainer(windowId, inventory) {
            override fun onContainerClosed(@Nonnull playerIn: PlayerEntity) {
                if (optional.isPresent && player.isServerWorld) {
                    val handler = optional.get() as ItemStackHandler
                    for (i in 0..8) {
                        val slot = getSlot(i)
                        val stack = slot.stack
                        handler.setStackInSlot(i, stack)
                    }
                }
                super.onContainerClosed(playerIn)
            }
        }
        if (optional.isPresent && player.isServerWorld) {
            val handler = optional.get()
            for (i in 0..8) {
                container.putStackInSlot(i, handler.getStackInSlot(i))
            }
        }
        return container
    }
}