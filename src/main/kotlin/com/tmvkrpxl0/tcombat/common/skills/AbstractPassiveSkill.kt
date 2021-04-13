package com.tmvkrpxl0.tcombat.common.skills

import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.server.ServerLifecycleHooks

abstract class AbstractPassiveSkill : AbstractSkill() {
    private var enabled: Boolean = true
    private fun tick(event: ServerTickEvent) {
        if (enabled && event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            val server = ServerLifecycleHooks.getCurrentServer()
            for (player in server.playerList.players) {
                onTick(event, player)
            }
        }
    }

    protected abstract fun onTick(event: ServerTickEvent, player: PlayerEntity): Boolean

    init {
        MinecraftForge.EVENT_BUS.addListener { event: ServerTickEvent -> tick(event) }
    }
}