package com.tmvkrpxl0.tcombat.common.entities.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import java.util.*

interface ICustomizableEntity: IEntityAdditionalSpawnData {
    fun getOwner(): PlayerEntity?
    fun getOwnerId(): UUID
    fun toWorld()
}