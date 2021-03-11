package com.tmvkrpxl0.tcombat.common.skills;

import net.minecraft.entity.player.ServerPlayerEntity;

public abstract class AbstractActiveSkill extends AbstractSkill{
    public abstract boolean execute(ServerPlayerEntity player);
}
