package com.tmvkrpxl0.tcombat.common.capability.factories

import com.google.common.collect.ImmutableList
import com.tmvkrpxl0.tcombat.common.capability.capabilities.TargetCapability
import com.tmvkrpxl0.tcombat.common.network.packets.TargetRequestPacket
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import java.util.*

class TargetHolder : ITargetHolder{
    private val targets: LinkedList<LivingEntity> = LinkedList()
    private var focused:LivingEntity? = null
    private var pickMode: TargetCapability.PickMode = TargetCapability.PickMode.LOOK
    private lateinit var player: PlayerEntity

    override fun getTargets(): ImmutableList<LivingEntity>{
        targets.removeIf { !it.isAlive || it.level != player.level || it.distanceToSqr(player) > 100 * 100 }
        return ImmutableList.copyOf(this.targets)
    }

    override fun getFocused(): LivingEntity?{
        if(this.focused?.isAlive != true || !this.targets.contains(this.focused)){
            this.focused = null
        }
        return this.focused
    }

    override fun setTargets(targets: List<LivingEntity>) {
        this.targets.clear()
        this.targets.addAll(targets)
        this.focused = null
    }

    override fun setFocused(focused: LivingEntity?) {
        if(this.targets.contains(focused))this.focused = focused//If targets does not contains "focused", just ignore it
    }

    override fun clearTargets(){
        this.targets.clear()
        this.focused = null
    }

    override fun getPickMode(): TargetCapability.PickMode = this.pickMode

    override fun setPickMode(mode: TargetCapability.PickMode){
        this.pickMode = mode
    }

    override fun getPlayer(): PlayerEntity = this.player

    override fun setPlayer(player: PlayerEntity) {
        if(this::player.isInitialized)throw IllegalAccessException("Player already initialized! Side:${if(player.level.isClientSide)"Client" else "Server"} Name:${player.name}")
        this.player = player
    }
}