package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.common.skills.AbstractActiveSkill
import javax.annotation.Nonnull

class SkillRequestPacket(
    @field:Nonnull @get:Nonnull
    @param:Nonnull val skill: AbstractActiveSkill
)