package com.tmvkrpxl0.tcombat.common.network.packets

import net.minecraft.block.BlockState
import javax.annotation.Nonnull

class SpawnCBPacket(
    @field:Nonnull @get:Nonnull @param:Nonnull val x: Double,
    @field:Nonnull @get:Nonnull @param:Nonnull val y: Double,
    @field:Nonnull @get:Nonnull @param:Nonnull val z: Double,
    @field:Nonnull @get:Nonnull @param:Nonnull val blockState: BlockState,
    @field:Nonnull @get:Nonnull @param:Nonnull val isSolid: Boolean,
    @field:Nonnull @get:Nonnull @param:Nonnull val sizeX: Double,
    @field:Nonnull @get:Nonnull @param:Nonnull val sizeY: Double
) {
}