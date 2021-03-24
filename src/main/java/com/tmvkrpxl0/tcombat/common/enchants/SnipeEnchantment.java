package com.tmvkrpxl0.tcombat.common.enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

import javax.annotation.Nonnull;

public class SnipeEnchantment extends Enchantment {
    protected SnipeEnchantment(Rarity p_i46731_1_, EnchantmentType p_i46731_2_, EquipmentSlotType... p_i46731_3_) {
        super(p_i46731_1_, EnchantmentType.CROSSBOW, p_i46731_3_);
    }

    @Override
    public int getMinCost(int p_77321_1_) {
        return 1 + 10 * (p_77321_1_ - 1);
    }

    @Override
    public int getMaxCost(int p_223551_1_) {
        return super.getMinCost(p_223551_1_) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean checkCompatibility(@Nonnull Enchantment p_77326_1_) {
        return super.checkCompatibility(p_77326_1_) && p_77326_1_ != TCombatEnchants.FOCUS.get();
    }
}
