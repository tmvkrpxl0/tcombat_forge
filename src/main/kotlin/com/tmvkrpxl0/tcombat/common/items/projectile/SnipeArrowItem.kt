package com.tmvkrpxl0.tcombat.common.items.projectile

import com.tmvkrpxl0.tcombat.common.entities.projectile.SnipeArrowEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.item.ArrowItem
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class SnipeArrowItem(properties:Properties) : ArrowItem(properties) {
    override fun createArrow(worldIn: World, stack: ItemStack, shooter: LivingEntity): AbstractArrowEntity = SnipeArrowEntity( worldIn, shooter)
}
