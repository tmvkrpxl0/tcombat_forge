package com.tmvkrpxl0.tcombat.common.network.packets

import com.tmvkrpxl0.tcombat.common.capability.capabilities.TargetCapability
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.client.Minecraft
import net.minecraft.entity.LivingEntity
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

object TargetModifyNotifyPacket{
    class Mode(val pickMode: TargetCapability.PickMode): ITCombatPacket{
        companion object{
            fun decode(buffer: PacketBuffer):Mode = Mode(TargetCapability.PickMode.values()[buffer.readVarInt()])
        }
        override fun encode(buffer: PacketBuffer) {
            buffer.writeVarInt(pickMode.ordinal)
        }

        override fun handle(supplier: Supplier<NetworkEvent.Context>) {
            supplier.get().enqueueWork { TCombatUtil.setPickMode(Minecraft.getInstance().player!!, pickMode) }
        }
    }

    class Targets(private val targets: List<LivingEntity>): ITCombatPacket{
        companion object{
            fun decode(buffer: PacketBuffer):Targets{
                val entityIds = buffer.readVarIntArray()
                val player = Minecraft.getInstance().player!!
                val livingEntities = LinkedList<LivingEntity>()
                for(id in entityIds){
                    val entity = player.level.getEntity(id)
                    if(entity is LivingEntity){
                        livingEntities.add(entity)
                    }
                }
                return Targets(livingEntities);
            }
        }
        override fun encode(buffer: PacketBuffer) {
            val entityIds = LinkedList<Int>()
            val player = Minecraft.getInstance().player!!
            for(livingEntity in targets){
                if(livingEntity.isAlive && livingEntity.level == player.level){
                    entityIds.add(livingEntity.id)
                }
            }
            buffer.writeVarIntArray(entityIds.toIntArray())
        }

        override fun handle(supplier: Supplier<NetworkEvent.Context>) {
            supplier.get().enqueueWork { TCombatUtil.setTargets(Minecraft.getInstance().player!!, targets) }
        }
    }

    class Focus(private val focus: LivingEntity?): ITCombatPacket{
        companion object{
            fun decode(buffer: PacketBuffer): Focus{
                val entityId = buffer.readVarInt();
                val player = Minecraft.getInstance().player!!
                val entity = player.level.getEntity(entityId)
                return if(entity != null && entity is LivingEntity && entity.level == player.level){
                    Focus(entity)
                }else{
                    Focus(null)
                }
            }
        }

        override fun encode(buffer: PacketBuffer) {
            buffer.writeVarInt(focus?.id ?: 0)
        }

        override fun handle(supplier: Supplier<NetworkEvent.Context>) {
            supplier.get().enqueueWork { TCombatUtil.setFocus(Minecraft.getInstance().player!!, focus) }
        }
    }
}