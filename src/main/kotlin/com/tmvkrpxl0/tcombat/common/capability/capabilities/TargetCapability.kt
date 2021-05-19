package com.tmvkrpxl0.tcombat.common.capability.capabilities

import com.tmvkrpxl0.tcombat.common.capability.factories.ITargetHolder
import com.tmvkrpxl0.tcombat.common.capability.factories.TargetHolder
import com.tmvkrpxl0.tcombat.common.capability.handlers.TargetHandler
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager

object TargetCapability: IHasCapability {
    @JvmStatic
    @CapabilityInject(ITargetHolder::class)
    lateinit var TARGET_HANDLER: Capability<ITargetHolder>

    override fun register(){
        CapabilityManager.INSTANCE.register(ITargetHolder::class.java, TargetHandler()){TargetHolder()}
    }

    enum class RequestType{
        SET,
        UNSET,
        PICK_FOCUS,
        NEXT_PICK_MODE
    }

    enum class PickMode{
        LOOK,
        CLOSE,
        RANDOM
    }
}