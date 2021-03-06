package com.tmvkrpxl0.tcombat.common.items.projectile

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.entities.projectile.SnipeArrowEntity
import net.minecraft.block.DispenserBlock
import net.minecraft.dispenser.IPosition
import net.minecraft.dispenser.ProjectileDispenseBehavior
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ArrowItem
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class SnipeArrowItem(properties: Properties) : ArrowItem(properties) {
    override fun createArrow(worldIn: World, stack: ItemStack, shooter: LivingEntity): AbstractArrowEntity =
        SnipeArrowEntity(TCombatEntityTypes.SNIPE_ARROW.get(), shooter, worldIn)
    init{
        DispenserBlock.registerBehavior(this, object: ProjectileDispenseBehavior(){
            override fun getProjectile(world: World, iPosition: IPosition, itemStack: ItemStack): ProjectileEntity{
                val snipe = SnipeArrowEntity(TCombatEntityTypes.SNIPE_ARROW.get(), world)
                snipe.setPos(iPosition.x(), iPosition.y(), iPosition.z())
                return snipe
            }
        })
    }
}
