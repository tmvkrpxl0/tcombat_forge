package com.tmvkrpxl0.tcombat.common.capability.providers

import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import javax.annotation.Nonnull

class ItemInventoryCapabilityProvider : ICapabilitySerializable<INBT> {
    private val itemStackHandler = ItemStackHandler(9)

    @Nonnull
    override fun <T : Any> getCapability(@Nonnull cap: Capability<T>, unused: Direction?): LazyOptional<T> {
        return if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY === cap) {
            LazyOptional.of { return@of itemStackHandler as T }
        } else LazyOptional.empty()
    }

    override fun serializeNBT(): INBT? {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemStackHandler, null)
    }

    override fun deserializeNBT(nbt: INBT) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemStackHandler, null, nbt)
    }
}