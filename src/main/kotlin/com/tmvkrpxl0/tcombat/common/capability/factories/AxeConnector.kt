package com.tmvkrpxl0.tcombat.common.capability.factories

import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

class AxeConnector: IAxeConnector {
    private var entity: WorldAxeEntity? = null
    private lateinit var item: ItemStack
    private var player: PlayerEntity? = null

    override fun getEntity(): WorldAxeEntity?{
        if(this.entity != null && !this.entity!!.isAlive)this.entity = null
        return this.entity
    }

    override fun setEntity(entity: WorldAxeEntity?) {
        this.entity = entity
    }

    override fun getItem(): ItemStack = this.item

    override fun setItem(item: ItemStack) {
        this.item = item
    }

    override fun getPlayer(): PlayerEntity? = this.player

    override fun setPlayer(player: PlayerEntity) {
        this.player = player
    }
}