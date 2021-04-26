package com.tmvkrpxl0.tcombat.common.enchants

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentType
import net.minecraft.inventory.EquipmentSlotType

class FocusEnchantment(rarityIn: Rarity, vararg slots: EquipmentSlotType) : Enchantment(rarityIn, EnchantmentType.CROSSBOW, slots) {
    override fun getMinCost(enchantmentLevel: Int): Int {
        return 1 + 10 * (enchantmentLevel - 1)
    }

    override fun getMaxCost(enchantmentLevel: Int): Int {
        return super.getMinCost(enchantmentLevel) + 50
    }

    override fun getMaxLevel(): Int {
        return 1
    }
}