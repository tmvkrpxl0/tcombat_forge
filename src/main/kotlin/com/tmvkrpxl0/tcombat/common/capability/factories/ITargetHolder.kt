package com.tmvkrpxl0.tcombat.common.capability.factories

import com.tmvkrpxl0.tcombat.common.capability.capabilities.TargetCapability
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity

//On server side, Every player instance should have capability that gets removed when player dies
//On client side, only `Minecraft.getInstance().getPlayer()` instance should have capability that gets removed when the player dies
interface ITargetHolder {
    fun getTargets():List<LivingEntity>
    fun getFocused():LivingEntity?
    fun setTargets(targets: List<LivingEntity>)
    fun setFocused(focused: LivingEntity?)
    fun clearTargets()
    fun getPickMode(): TargetCapability.PickMode
    fun setPickMode(mode: TargetCapability.PickMode)
    fun getPlayer(): PlayerEntity
    fun setPlayer(player: PlayerEntity)
}