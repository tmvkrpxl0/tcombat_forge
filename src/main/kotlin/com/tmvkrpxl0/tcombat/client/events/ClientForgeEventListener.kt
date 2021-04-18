package com.tmvkrpxl0.tcombat.client.events

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.capability.capabilities.ItemEntityConnectionCapability
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = TCombatMain.MODID, bus = EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ClientForgeEventListener {
    @SubscribeEvent
    fun onEntitySpawn(event: EntityJoinWorldEvent){
        if(event.entity is WorldAxeEntity){
            val worldAxeEntity = event.entity as WorldAxeEntity
            val cap = worldAxeEntity.getBaseAxe().getCapability(ItemEntityConnectionCapability.ITEM_ENTITY_CONNECTION_HANDLER)
            if(cap.resolve().isPresent){
                val entityHolder = cap.resolve().get()
                entityHolder.setEntity(worldAxeEntity)
            }
        }
    }
}