package com.tmvkrpxl0.tcombat.common.skills

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.entities.projectile.ReflectiveArrowEntity
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

object ReflectionBlast: AbstractCooldownSkill() {
    private val NAME = ResourceLocation(TCombatMain.MODID, "reflection_blast")
    override val maxCooldownTicks: Int
        get() = 200

    override fun executeCooldown(player: PlayerEntity): Boolean {
        val random = player.level.random
        val base = player.position().add(0.0, player.eyeHeight.toDouble(), 0.0).add(player.lookAngle)
        val delta = player.lookAngle.scale(1.5)
        val check = ItemStack(TCombatItems.REFLECTIVE_ARROW.get())
        for(i in 0..15){
            if(player.inventory.contains(check)){
                val arrow = ReflectiveArrowEntity(TCombatEntityTypes.REFLECTIVE_ARROW.get(), player, player.level)
                arrow.owner = player
                val perpendicular = TCombatUtil.getPerpendicularRandom(delta, random).scale(random.nextDouble()*10 - 5)
                val combined = base.add(perpendicular)
                arrow.setPos(combined.x, combined.y, combined.z)
                arrow.deltaMovement = delta
                player.level.addFreshEntity(arrow)
                for(item in player.inventory.items){
                    if(player.isCreative)break
                    if(item.item == TCombatItems.REFLECTIVE_ARROW.get()){
                        item.count--
                        break
                    }
                }
            }
        }
        return true
    }

    override fun getRegistryName(): ResourceLocation = NAME
}