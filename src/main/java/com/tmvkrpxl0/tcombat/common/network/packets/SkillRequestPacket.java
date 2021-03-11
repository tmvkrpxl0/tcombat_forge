package com.tmvkrpxl0.tcombat.common.network.packets;

import com.tmvkrpxl0.tcombat.common.skills.AbstractActiveSkill;

import javax.annotation.Nonnull;

public class SkillRequestPacket{
    @Nonnull
    private final AbstractActiveSkill skill;
    @Nonnull
    public AbstractActiveSkill getSkill(){
        return skill;
    }

    public SkillRequestPacket(@Nonnull AbstractActiveSkill skill){
        this.skill = skill;
    }
}
