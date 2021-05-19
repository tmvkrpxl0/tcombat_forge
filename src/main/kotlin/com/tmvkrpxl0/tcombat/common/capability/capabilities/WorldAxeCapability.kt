package com.tmvkrpxl0.tcombat.common.capability.capabilities

import com.tmvkrpxl0.tcombat.common.capability.factories.AxeConnector
import com.tmvkrpxl0.tcombat.common.capability.factories.IAxeConnector
import com.tmvkrpxl0.tcombat.common.capability.handlers.ItemEntityConnectionHandler
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager

object WorldAxeCapability: IHasCapability {
    @JvmStatic
    @CapabilityInject(IAxeConnector::class)
    lateinit var itemEntityConnectionHandler: Capability<IAxeConnector>

    override fun register(){
        CapabilityManager.INSTANCE.register(IAxeConnector::class.java, ItemEntityConnectionHandler()) { AxeConnector() }
    }
}