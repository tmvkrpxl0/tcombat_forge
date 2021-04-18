package com.tmvkrpxl0.tcombat.common.capability.providers

import com.tmvkrpxl0.tcombat.common.capability.capabilities.ItemEntityConnectionCapability
import com.tmvkrpxl0.tcombat.common.capability.factories.EntityHolder
import com.tmvkrpxl0.tcombat.common.capability.factories.IEntityHolder
import com.tmvkrpxl0.tcombat.common.capability.handlers.ItemEntityConnectionHandler
import net.minecraft.entity.Entity
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional

class ItemEntityConnectionProvider: ICapabilitySerializable<INBT> {
    private val entityHolder: IEntityHolder = EntityHolder()
    override fun <T : Any> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return if(cap===ItemEntityConnectionCapability.ITEM_ENTITY_CONNECTION_HANDLER){
            LazyOptional.of { return@of entityHolder as T }
        }else LazyOptional.empty()
    }

    override fun serializeNBT(): INBT {
        return ItemEntityConnectionCapability.ITEM_ENTITY_CONNECTION_HANDLER.writeNBT(entityHolder, null)!!
    }

    override fun deserializeNBT(nbt: INBT) {
        ItemEntityConnectionCapability.ITEM_ENTITY_CONNECTION_HANDLER.readNBT(entityHolder, null, nbt)
    }

}