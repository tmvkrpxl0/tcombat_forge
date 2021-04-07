package com.tmvkrpxl0.tcombat.common.enchants

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraft.enchantment.Enchantment
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object TCombatEnchants {
    val ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, TCombatMain.MODID)
    val FOCUS = ENCHANTMENTS.register<Enchantment>("focus") {
        FocusEnchantment(
            Enchantment.Rarity.RARE
        )
    }

    val CROSSBOW_FLAME =
        ENCHANTMENTS.register<Enchantment>("crossbow_flame") { CrossbowFlameEnchantment(Enchantment.Rarity.RARE) }
}