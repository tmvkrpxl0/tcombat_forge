package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class CBSizeAnswerPacket(val x: Float, val y: Float, val uniqueId: UUID): ITCombatPacket{
    companion object{
        fun decode(buffer: PacketBuffer) = CBSizeAnswerPacket(buffer.readFloat(), buffer.readFloat(), buffer.readUUID())
    }

    override fun encode(buffer: PacketBuffer) {
        buffer.writeFloat(x)
        buffer.writeFloat(y)
        buffer.writeUUID(uniqueId)
    }

    override fun handle(supplier: Supplier<NetworkEvent.Context>) {
        supplier.get().enqueueWork { TCombatUtil.handleRequest(uniqueId, x, y) }
    }

}