package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.skills.AbstractActiveSkill
import com.tmvkrpxl0.tcombat.common.skills.AbstractSkill
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.thread.SidedThreadGroups
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import java.util.*
import java.util.function.Supplier

object TCombatPacketHandler {
    private const val PROTOCOL_VERSION = "1"
    private const val CHANNEL_NAME = "tcombat_packet_handler"
    val INSTANCE: SimpleChannel = NetworkRegistry.newSimpleChannel(ResourceLocation(TCombatMain.MODID, CHANNEL_NAME), { PROTOCOL_VERSION }, { o: String -> PROTOCOL_VERSION == o }) { o: String ->
        PROTOCOL_VERSION == o
    }

    fun registerPackets() {
        var id = 0
        INSTANCE.registerMessage(id, SkillRequestPacket::class.java, { skillRequestPacket: SkillRequestPacket, packetBuffer: PacketBuffer ->
            packetBuffer.writeRegistryId(skillRequestPacket.skill)
        }, { packetBuffer: PacketBuffer ->
            val skill = packetBuffer.readRegistryIdSafe(
                AbstractSkill::class.java
            )
            require(skill is AbstractActiveSkill) { "Only Active Skills can be sent!!!" }
            SkillRequestPacket(skill)
        }, { skillRequestPacket: SkillRequestPacket, contextSupplier: Supplier<NetworkEvent.Context> ->
            contextSupplier.get().enqueueWork {
                require(contextSupplier.get().sender is PlayerEntity)
                skillRequestPacket.skill.execute(contextSupplier.get().sender!!)
            }
            contextSupplier.get().packetHandled = true
        })
        id++
        INSTANCE.registerMessage(id, TargetRequestPacket::class.java, { targetRequest: TargetRequestPacket, packetBuffer: PacketBuffer ->
            packetBuffer.writeVarInt(targetRequest.type.ordinal)
        }, { packetBuffer: PacketBuffer ->
            val type = TargetRequestPacket.RequestType.values()[packetBuffer.readVarInt()]
            return@registerMessage TargetRequestPacket(type)
        }) { targetRequest: TargetRequestPacket, contextSupplier: Supplier<NetworkEvent.Context> ->
            val type = targetRequest.type
            val player = contextSupplier.get().sender!!
            contextSupplier.get().enqueueWork{
                when(type){
                    TargetRequestPacket.RequestType.SET-> TCombatUtil.setTargets(player)
                    TargetRequestPacket.RequestType.UNSET-> TCombatUtil.setTargets(player, LinkedList())
                    TargetRequestPacket.RequestType.PICK_CLOSE->{

                    }
                }
            }

            contextSupplier.get().packetHandled = true
        }
        id++
        INSTANCE.registerMessage(id, CBSizeRequestPacket::class.java, { sizeRequestPacket: CBSizeRequestPacket, packetBuffer: PacketBuffer ->
            packetBuffer.writeVarInt(Block.getId(sizeRequestPacket.blockState))
            packetBuffer.writeVarInt(sizeRequestPacket.uniqueId)
        }, { packetBuffer: PacketBuffer ->
            val blockState = Block.stateById(packetBuffer.readVarInt())
            val uniqueId = packetBuffer.readVarInt()
            CBSizeRequestPacket(blockState, uniqueId)
        }) { sizeRequestPacket: CBSizeRequestPacket, contextSupplier: Supplier<NetworkEvent.Context> ->
            if (Thread.currentThread().threadGroup == SidedThreadGroups.CLIENT) throw IllegalStateException("CBSizeRequestPacket should only be sent from client to server!!!")
            contextSupplier.get().enqueueWork {
                TODO("Not Implemented!")
            }
        }
    }

}