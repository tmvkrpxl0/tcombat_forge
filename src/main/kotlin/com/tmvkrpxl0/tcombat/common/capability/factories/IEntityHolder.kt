package com.tmvkrpxl0.tcombat.common.capability.factories

import net.minecraft.entity.Entity

interface IEntityHolder {
    fun getEntity(): Entity?
    fun setEntity(entity: Entity?)
}