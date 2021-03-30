package com.tmvkrpxl0.tcombat.common.skills

import net.minecraft.entity.player.ServerPlayerEntity

abstract class AbstractActiveSkill : AbstractSkill() {
    abstract fun execute(player: ServerPlayerEntity): Boolean
}