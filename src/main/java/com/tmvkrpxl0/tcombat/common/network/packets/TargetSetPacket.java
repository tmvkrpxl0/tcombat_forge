package com.tmvkrpxl0.tcombat.common.network.packets;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TargetSetPacket {
    private final UUID uuid;
    private final int[] entityIds;
    public TargetSetPacket(@Nonnull UUID uuid, @Nonnull int[] entityIds) {
        this.uuid = uuid;
        this.entityIds = entityIds;
    }

    @Nonnull
    public UUID getUniqueID(){
        return this.uuid;
    }

    @Nonnull
    public int[] entityIds(){
        return this.entityIds;
    }
}
