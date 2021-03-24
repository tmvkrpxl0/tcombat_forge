package com.tmvkrpxl0.tcombat.common.enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class CrossbowFlameEnchantment extends Enchantment {
    public CrossbowFlameEnchantment(Rarity p_i46737_1_, EquipmentSlotType... p_i46737_2_) {
        super(p_i46737_1_, EnchantmentType.CROSSBOW, p_i46737_2_);
    }

    public int getMinCost(int p_77321_1_) {
        return 20;
    }

    public int getMaxCost(int p_223551_1_) {
        return 50;
    }

    public int getMaxLevel() {
        return 1;
    }
}
