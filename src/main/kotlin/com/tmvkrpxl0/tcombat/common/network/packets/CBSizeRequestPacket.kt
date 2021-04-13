package com.tmvkrpxl0.tcombat.common.network.packets;

import net.minecraft.block.BlockState
import javax.annotation.Nonnull

class CBSizeRequestPacket(
    @field:Nonnull @get:Nonnull @param:Nonnull val blockState: BlockState,
    @field:Nonnull @get:Nonnull @param:Nonnull val uniqueId:Int
    )
