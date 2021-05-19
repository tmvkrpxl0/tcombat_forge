package com.tmvkrpxl0.tcombat.common.skills

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.block.Blocks
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import java.util.*

object TestSkill: AbstractActiveSkill() {
    private val NAME = ResourceLocation(TCombatMain.MODID, "test")
    override fun execute(player: ServerPlayerEntity): Boolean {
        val pos = player.position().add(0.0, 1.0, 0.0).add(player.lookAngle.scale(2.0))
        val block = Blocks.SANDSTONE.defaultBlockState()
        TCombatUtil.requestCB(player, block) { uuid: UUID, x: Float, y: Float ->
            val cb = CustomizableBlockEntity(pos.x, pos.y, pos.z, block, player, true, uuid, x, y)
            player.level.addFreshEntity(cb)
        }
        return true
    }

    override fun getRegistryName(): ResourceLocation = NAME
}