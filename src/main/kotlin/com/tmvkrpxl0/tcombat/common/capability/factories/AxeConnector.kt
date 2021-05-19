package com.tmvkrpxl0.tcombat.common.capability.factories

import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import com.tmvkrpxl0.tcombat.common.events.WorldEventListener
import com.tmvkrpxl0.tcombat.common.util.ForgeRunnable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

class AxeConnector: IAxeConnector {
    private var entity: WorldAxeEntity? = null
    private lateinit var item: ItemStack
    private var player: PlayerEntity? = null
    private var puller: ForgeRunnable? = null

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

    override fun getPuller(): ForgeRunnable?{
        if(this.puller!=null && this.puller!!.isCancelled())this.puller = null
        return this.puller
    }

    override fun setPuller(puller: ForgeRunnable?) {
        this.puller?.setCancelled(true)
        this.puller = puller
    }

    override fun pull(){
        if(this.puller==null)return
        if(this.player==null)return
        if(this.player!!.level.isClientSide)return
        if(this.getEntity()==null)return
        WorldEventListener.tasks.add(this.puller!!)
    }
}