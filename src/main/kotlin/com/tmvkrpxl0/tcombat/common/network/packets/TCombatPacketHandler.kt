package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import java.util.function.Function
import java.util.function.Supplier
import kotlin.reflect.KClass

object TCombatPacketHandler {
    private const val PROTOCOL_VERSION = "1"
    private const val CHANNEL_NAME = "tcombat_packet_handler"
    private var id = 0
    val INSTANCE: SimpleChannel = NetworkRegistry.newSimpleChannel(ResourceLocation(TCombatMain.MODID, CHANNEL_NAME), { PROTOCOL_VERSION }, { o: String -> PROTOCOL_VERSION == o }) { o: String ->
        PROTOCOL_VERSION == o
    }

    private fun <T: ITCombatPacket>registerPacket(packet: KClass<T>, decode: Function<PacketBuffer, T>, receiveSide: LogicalSide) {
        INSTANCE.registerMessage(id, packet.java, {p: T, buffer: PacketBuffer-> p.encode(buffer)}, decode, {p: T, supplier: Supplier<NetworkEvent.Context> ->
            if (supplier.get().direction.receptionSide != receiveSide)throw IllegalThreadStateException("${packet.simpleName} can't be received on ${supplier.get().direction.receptionSide}")
            p.handle(supplier)
            supplier.get().packetHandled = true
        })
        id++
    }

    fun register() {
        registerPacket(SkillRequestPacket::class, SkillRequestPacket::decode, LogicalSide.SERVER)
        registerPacket(TargetRequestPacket::class, TargetRequestPacket::decode, LogicalSide.SERVER)
        registerPacket(TargetModifyNotifyPacket.Mode::class, TargetModifyNotifyPacket.Mode::decode, LogicalSide.CLIENT)
        registerPacket(TargetModifyNotifyPacket.Targets::class, TargetModifyNotifyPacket.Targets::decode, LogicalSide.CLIENT)
        registerPacket(TargetModifyNotifyPacket.Focus::class, TargetModifyNotifyPacket.Focus::decode, LogicalSide.CLIENT)
        registerPacket(CBSizeRequestPacket::class, CBSizeRequestPacket::decode, LogicalSide.CLIENT)
        registerPacket(CBSizeAnswerPacket::class, CBSizeAnswerPacket::decode, LogicalSide.SERVER)
    }

}