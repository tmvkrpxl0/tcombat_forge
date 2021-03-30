package com.tmvkrpxl0.tcombat.common.network.packets

class RocketJumpPacket(//int, int, int, int, int, boolean, int
    val world: Int,
    val shooterId: Int,
    val x: Double,
    val y: Double,
    val z: Double,
    val radius: Int,
    val fire: Boolean,
    val mode: Int
)