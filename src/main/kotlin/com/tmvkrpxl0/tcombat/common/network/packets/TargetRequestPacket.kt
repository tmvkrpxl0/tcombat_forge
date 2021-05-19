package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.common.capability.capabilities.TargetCapability
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class TargetRequestPacket(val type: TargetCapability.RequestType): ITCombatPacket {

    companion object{
        fun decode(buffer: PacketBuffer): TargetRequestPacket = TargetRequestPacket(TargetCapability.RequestType.values()[buffer.readVarInt()])
    }

    override fun encode(buffer: PacketBuffer) {
        buffer.writeVarInt(type.ordinal)
    }

    override fun handle(supplier: Supplier<NetworkEvent.Context>) {
        val player = supplier.get().sender!!
        supplier.get().enqueueWork {
            when(type){
                TargetCapability.RequestType.SET-> TCombatUtil.setTargets(player)
                TargetCapability.RequestType.UNSET-> TCombatUtil.setTargets(player, emptyList())
                TargetCapability.RequestType.NEXT_PICK_MODE->{
                    val mode = TCombatUtil.getPickMode(player)
                    val next = TargetCapability.PickMode.values()[(mode.ordinal + 1) % TargetCapability.PickMode.values().size]
                    TCombatUtil.setPickMode(player, next)
                }
                TargetCapability.RequestType.PICK_FOCUS-> TCombatUtil.pickTarget(player)
            }
        }
    }
}

