package com.tmvkrpxl0.tcombat.common.capability.providers

import com.tmvkrpxl0.tcombat.common.capability.capabilities.WorldAxeCapability
import com.tmvkrpxl0.tcombat.common.capability.factories.AxeConnector
import com.tmvkrpxl0.tcombat.common.capability.factories.IAxeConnector
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional

class ItemEntityConnectionProvider(itemStack: ItemStack): ICapabilityProvider {
    private val axeConnector: IAxeConnector = AxeConnector()
    init {
        axeConnector.setItem(itemStack)
    }
    override fun <T : Any> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return if(cap===WorldAxeCapability.itemEntityConnectionHandler){
            LazyOptional.of { return@of axeConnector as T }
        }else LazyOptional.empty()
    }

}