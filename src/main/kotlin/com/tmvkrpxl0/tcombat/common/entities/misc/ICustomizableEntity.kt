package com.tmvkrpxl0.tcombat.common.entities.misc

import net.minecraft.entity.player.PlayerEntity
import java.util.*

interface ICustomizableEntity {
    fun getOwner(): PlayerEntity?
    fun getOwnerId(): UUID
    fun setOwnerId(uuid:UUID)
    fun setOwner(player:PlayerEntity)
    fun toWorld()
}