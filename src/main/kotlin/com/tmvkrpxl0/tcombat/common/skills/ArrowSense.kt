package com.tmvkrpxl0.tcombat.common.skills

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.event.TickEvent.ServerTickEvent

object ArrowSense : AbstractPassiveSkill() {
    private val size = AxisAlignedBB.withSizeAtOrigin(30.0, 30.0, 30.0)
    override fun onTick(event: ServerTickEvent, player: PlayerEntity): Boolean {
        if (!player.entityWorld.isRemote) {
            val axisAlignedBB = size.offset(player.positionVec)
            val list = player.world.getEntitiesInAABBexcluding(
                player, axisAlignedBB
            ) { o: Entity ->
                o is ProjectileEntity && o.shooter != player && o.getDistanceSq(player) < 15 * 15 && player.canEntityBeSeen(
                    o
                ) && (o !is ArrowEntity || !(o as AbstractArrowEntity).inGround)
            }
            if (list.isEmpty()) return false
            player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.HOSTILE, 1f, 1f)
        }
        return true
    }

    override fun getRegistryName(): ResourceLocation = NAME
    private val NAME = ResourceLocation(TCombatMain.MODID, "arrow_sense")
}