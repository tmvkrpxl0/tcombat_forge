package com.tmvkrpxl0.tcombat.common.capability.providers

import com.tmvkrpxl0.tcombat.common.capability.capabilities.TargetCapability
import com.tmvkrpxl0.tcombat.common.capability.factories.ITargetHolder
import com.tmvkrpxl0.tcombat.common.capability.factories.TargetHolder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional

class TargetCapabilityProvider(player: PlayerEntity): ICapabilityProvider {
    private val targetHolder:ITargetHolder = TargetHolder()
    init {
        targetHolder.setPlayer(player)
    }
    override fun <T : Any> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return if(cap===TargetCapability.TARGET_HANDLER){
            LazyOptional.of{return@of targetHolder as T}
        }else LazyOptional.empty()
    }

}