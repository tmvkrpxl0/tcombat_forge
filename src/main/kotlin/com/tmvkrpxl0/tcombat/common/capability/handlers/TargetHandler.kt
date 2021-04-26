package com.tmvkrpxl0.tcombat.common.capability.handlers

import com.tmvkrpxl0.tcombat.common.capability.factories.ITargetHolder
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability

class TargetHandler: Capability.IStorage<ITargetHolder> {
    //This is un-serializable capability
    override fun writeNBT(capability: Capability<ITargetHolder>?, instance: ITargetHolder?, side: Direction?): INBT? = null

    //This is un-serializable capability
    override fun readNBT(capability: Capability<ITargetHolder>?, instance: ITargetHolder?, side: Direction?, nbt: INBT?){

    }

}