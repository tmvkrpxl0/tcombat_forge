package com.tmvkrpxl0.tcombat.common.util

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.fluid.FluidState
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.IDataSerializer
import java.util.*

object TCombatDataSerializers {
    val UNIQUE_ID: IDataSerializer<UUID> = object : IDataSerializer<UUID> {
        override fun write(buf: PacketBuffer, value: UUID) {
            buf.writeUUID(value)
        }

        override fun read(buf: PacketBuffer): UUID {
            return buf.readUUID()
        }

        override fun copy(value: UUID): UUID {
            return value
        }
    }

    val BLOCK_STATE: IDataSerializer<BlockState> = object : IDataSerializer<BlockState> {
        override fun write(buf: PacketBuffer, value: BlockState) {
            buf.writeVarInt(Block.getId(value))
        }

        override fun read(buf: PacketBuffer): BlockState {
            val i = buf.readVarInt()
            return Block.stateById(i)
        }

        override fun copy(value: BlockState): BlockState {
            return value
        }
    }

    val FLUID_STATE: IDataSerializer<FluidState> = object : IDataSerializer<FluidState> {
        override fun write(buf: PacketBuffer, value: FluidState) {
            buf.writeVarInt(Block.getId(value.createLegacyBlock()))
        }

        override fun read(buf: PacketBuffer): FluidState {
            val i = buf.readVarInt()
            return Block.stateById(i).fluidState
        }

        override fun copy(value: FluidState): FluidState {
            return value
        }
    }
}