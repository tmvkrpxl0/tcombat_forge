package com.tmvkrpxl0.tcombat.common.network.packets

import java.util.*

class TargetModifyNotifyPacket(val type: TargetRequestPacket.RequestType, val targets: Array<UUID>, val focused: UUID)