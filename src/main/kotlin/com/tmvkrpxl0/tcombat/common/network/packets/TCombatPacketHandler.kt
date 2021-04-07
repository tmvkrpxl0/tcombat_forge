package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.skills.AbstractActiveSkill
import com.tmvkrpxl0.tcombat.common.skills.AbstractSkill
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import java.util.*
import java.util.function.Supplier

object TCombatPacketHandler {
    private const val PROTOCOL_VERSION = "1"
    private const val CHANNEL_NAME = "tcombat_packet_handler"
    val INSTANCE: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(TCombatMain.MODID, CHANNEL_NAME),
        { PROTOCOL_VERSION }, { o: String -> PROTOCOL_VERSION == o }) { o: String ->
        PROTOCOL_VERSION == o
    }

    fun registerPackets(){
        var id = 0
        INSTANCE.registerMessage(id, SkillRequestPacket::class.java,
            { skillRequestPacket: SkillRequestPacket, packetBuffer: PacketBuffer ->
                packetBuffer.writeRegistryId(skillRequestPacket.skill)
            },
            { packetBuffer: PacketBuffer ->
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
        INSTANCE.registerMessage(id,
            TargetSetPacket::class.java,
            { targetSetPacket: TargetSetPacket, packetBuffer: PacketBuffer ->
                packetBuffer.writeUniqueId(targetSetPacket.uniqueID)
                packetBuffer.writeVarIntArray(targetSetPacket.entityIds())
            },
            { packetBuffer: PacketBuffer ->
                val uuid = packetBuffer.readUniqueId()
                val entityIds = packetBuffer.readVarIntArray(100)
                TargetSetPacket(uuid, entityIds)
            }) { targetSetPacket: TargetSetPacket, contextSupplier: Supplier<NetworkEvent.Context> ->
            val list = LinkedList<LivingEntity>()
            val playerEntity = contextSupplier.get().sender
            val world = playerEntity!!.world
            contextSupplier.get().enqueueWork {
                for (i in targetSetPacket.entityIds()) {
                    val entity = world.getEntityByID(i)
                    if (entity is LivingEntity) list.add(entity)
                }
                TCombatUtil.setTargets(playerEntity, list)
                playerEntity.sendMessage(
                    StringTextComponent("Succeed! Count: " + TCombatUtil.getTargets(playerEntity).size),
                    Util.DUMMY_UUID
                )
            }
            contextSupplier.get().packetHandled = true
        }
        id++
    }

}