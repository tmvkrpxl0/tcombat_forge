package com.tmvkrpxl0.tcombat.common.enchants

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentType
import net.minecraft.inventory.EquipmentSlotType

class CrossbowFlameEnchantment(rarityIn: Rarity, vararg slots: EquipmentSlotType) :
    Enchantment(rarityIn, EnchantmentType.CROSSBOW, slots) {
    override fun getMinEnchantability(enchantmentLevel: Int): Int {
        return 20
    }

    override fun getMaxEnchantability(enchantmentLevel: Int): Int {
        return 50
    }

    override fun getMaxLevel(): Int {
        return 1
    }
}