package com.tmvkrpxl0.tcombat.common.network.packets

import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

interface ITCombatPacket {
    fun encode(buffer: PacketBuffer)
    fun handle(supplier: Supplier<NetworkEvent.Context>)
}