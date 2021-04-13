package com.tmvkrpxl0.tcombat.common.network.packets

import java.util.*
import javax.annotation.Nonnull

class TargetSetPacket(
    @get:Nonnull @param:Nonnull val uniqueID: UUID,
    @param:Nonnull private val entityIds: IntArray
) {
    @Nonnull
    fun entityIds(): IntArray {
        return entityIds
    }
}