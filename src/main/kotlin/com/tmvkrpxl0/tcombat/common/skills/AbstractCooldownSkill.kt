package com.tmvkrpxl0.tcombat.common.skills

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.registries.IForgeRegistryEntry

abstract class AbstractCooldownSkill : AbstractActiveSkill(), IForgeRegistryEntry<AbstractSkill> {
    protected var currentCooldownTicks = 0
    abstract val maxCooldownTicks: Int
    val isAvailable: Boolean
        get() = currentCooldownTicks == 0

    protected fun tick(event: ServerTickEvent) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            if (currentCooldownTicks > 0) {
                currentCooldownTicks--
            }
        }
    }

    override fun execute(player: ServerPlayerEntity): Boolean {
        if (!player.isCreative && !isAvailable) return false
        currentCooldownTicks = maxCooldownTicks
        return executeCooldown(player)
    }

    abstract fun executeCooldown(player: PlayerEntity): Boolean

    init {
        MinecraftForge.EVENT_BUS.addListener { event: ServerTickEvent -> tick(event) }
    }
}