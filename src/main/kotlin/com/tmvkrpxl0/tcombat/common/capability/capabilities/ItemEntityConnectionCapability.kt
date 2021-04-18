package com.tmvkrpxl0.tcombat.common.capability.capabilities

import com.tmvkrpxl0.tcombat.common.capability.factories.IEntityHolder
import com.tmvkrpxl0.tcombat.common.capability.factories.EntityHolder
import com.tmvkrpxl0.tcombat.common.capability.handlers.ItemEntityConnectionHandler
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager

object ItemEntityConnectionCapability {
    @JvmStatic
    @CapabilityInject(IEntityHolder::class)
    lateinit var ITEM_ENTITY_CONNECTION_HANDLER: Capability<IEntityHolder>

    fun register(){
        CapabilityManager.INSTANCE.register(IEntityHolder::class.java, ItemEntityConnectionHandler()) { EntityHolder() }
    }
}