package com.tmvkrpxl0.tcombat.common.network.packets

class TargetRequestPacket(val type:RequestType) {
    enum class RequestType{
        SET,
        UNSET,
        PICK_LOOK,
        PICK_CLOSE,
        PICK_RANDOM
    }
}

