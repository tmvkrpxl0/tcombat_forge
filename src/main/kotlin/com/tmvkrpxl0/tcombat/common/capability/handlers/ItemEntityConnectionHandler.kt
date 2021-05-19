package com.tmvkrpxl0.tcombat.common.capability.handlers

import com.tmvkrpxl0.tcombat.common.capability.factories.IAxeConnector
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability

class ItemEntityConnectionHandler: Capability.IStorage<IAxeConnector> {
    //This is unserializable Capability
    override fun writeNBT(capability: Capability<IAxeConnector>, instance: IAxeConnector, side: Direction?): INBT? = null

    //This is unserializable Capability
    override fun readNBT(capability: Capability<IAxeConnector>, instance: IAxeConnector, side: Direction?, nbt: INBT?){

    }
}