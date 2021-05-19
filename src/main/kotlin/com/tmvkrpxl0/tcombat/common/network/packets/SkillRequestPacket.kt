package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.common.skills.AbstractActiveSkill
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier
import javax.annotation.Nonnull

class SkillRequestPacket(val skill: AbstractActiveSkill): ITCombatPacket {

    companion object{
        fun decode(buffer: PacketBuffer): SkillRequestPacket = SkillRequestPacket(buffer.readRegistryId())
    }

    override fun encode(buffer: PacketBuffer) {
        buffer.writeRegistryId(skill)
    }

    override fun handle(supplier: Supplier<NetworkEvent.Context>) {
        supplier.get().enqueueWork {
            require(supplier.get().sender is PlayerEntity)
            skill.execute(supplier.get().sender!!)
        }
    }
}