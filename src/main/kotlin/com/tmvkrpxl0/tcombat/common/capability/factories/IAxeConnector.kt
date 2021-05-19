package com.tmvkrpxl0.tcombat.common.capability.factories

import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import com.tmvkrpxl0.tcombat.common.util.ForgeRunnable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

interface IAxeConnector {
    fun getEntity(): WorldAxeEntity?
    fun setEntity(entity: WorldAxeEntity?)
    fun getItem(): ItemStack
    fun setItem(item: ItemStack)
    fun getPlayer(): PlayerEntity?
    fun setPlayer(player: PlayerEntity)
    fun getPuller(): ForgeRunnable?
    fun setPuller(puller: ForgeRunnable?)
    fun pull()
}