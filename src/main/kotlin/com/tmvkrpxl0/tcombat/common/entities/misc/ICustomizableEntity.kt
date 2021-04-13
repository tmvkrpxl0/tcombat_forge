package com.tmvkrpxl0.tcombat.common.entities.misc

import net.minecraft.entity.player.PlayerEntity

interface ICustomizableEntity {
    fun getOwner(): PlayerEntity
    fun toWorld()
}