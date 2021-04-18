package com.tmvkrpxl0.tcombat.common.capability.factories

import net.minecraft.entity.Entity

class EntityHolder: IEntityHolder {
    private var entity: Entity? = null
    override fun getEntity(): Entity? = this.entity

    override fun setEntity(entity: Entity?) {
        this.entity = entity
    }
}