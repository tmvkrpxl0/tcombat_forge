package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.network.PacketBuffer
import net.minecraft.util.Direction
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class CBSizeRequestPacket(val blockState: BlockState, val uniqueId: UUID): ITCombatPacket{
    companion object{
        val sides = arrayOf(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null)
        val random = Random()
        fun decode(buffer: PacketBuffer): CBSizeRequestPacket{
            val blockId = buffer.readVarInt()
            val unique = buffer.readUUID()
            val state = Block.stateById(blockId)
            return CBSizeRequestPacket(state, unique)
        }
    }

    override fun encode(buffer: PacketBuffer) {
        buffer.writeVarInt(Block.getId(blockState))
        buffer.writeUUID(uniqueId)
    }

    override fun handle(supplier: Supplier<NetworkEvent.Context>) {
        supplier.get().enqueueWork {
            val size = TCombatUtil.getBlockSize(blockState)
            val answer = CBSizeAnswerPacket(size.x, size.y, uniqueId)
            TCombatPacketHandler.INSTANCE.sendToServer(answer)
        }
    }

}
