package com.tmvkrpxl0.tcombat.common.skills

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.event.TickEvent.ServerTickEvent

object ArrowSense : AbstractPassiveSkill() {
    private val size = AxisAlignedBB.ofSize(30.0, 30.0, 30.0)
    override fun onTick(event: ServerTickEvent, player: PlayerEntity): Boolean {
        if (!player.level.isClientSide) {
            val axisAlignedBB = size.move(player.position())
            val list = player.level.getEntities(
                player, axisAlignedBB
            ) { o: Entity ->
                var b1 = o is ProjectileEntity && o.owner != player && o.distanceToSqr(player) < 15 * 15 && player.canSee(o)
                if(o is AbstractArrowEntity){
                    if(o.inGround)b1 = false
                }
                return@getEntities b1
            }
            if (list.isEmpty()) return false
            player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.HOSTILE, 1f, 1f)
        }
        return true
    }

    override fun getRegistryName(): ResourceLocation = NAME
    private val NAME = ResourceLocation(TCombatMain.MODID, "arrow_sense")
}