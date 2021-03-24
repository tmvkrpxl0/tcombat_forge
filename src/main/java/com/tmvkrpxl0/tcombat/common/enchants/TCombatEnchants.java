package com.tmvkrpxl0.tcombat.common.enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

public class TCombatEnchants {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    public static final RegistryObject<Enchantment> FOCUS = ENCHANTMENTS.register("focus", () ->
            new FocusEnchantment(Enchantment.Rarity.RARE,EnchantmentType.CROSSBOW));
    public static final RegistryObject<Enchantment> SNIPE = ENCHANTMENTS.register("snipe", () ->
            new SnipeEnchantment(Enchantment.Rarity.RARE, EnchantmentType.CROSSBOW));
    public static final RegistryObject<Enchantment> CROSSBOW_FLAME = ENCHANTMENTS.register("crossbow_flame", () ->
            new CrossbowFlameEnchantment(Enchantment.Rarity.RARE));
}
