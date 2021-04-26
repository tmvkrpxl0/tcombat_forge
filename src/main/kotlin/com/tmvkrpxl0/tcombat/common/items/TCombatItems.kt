package com.tmvkrpxl0.tcombat.common.items

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.items.misc.MobileDispenser
import com.tmvkrpxl0.tcombat.common.items.projectile.ReflectiveArrowItem
import com.tmvkrpxl0.tcombat.common.items.projectile.SnipeArrowItem
import com.tmvkrpxl0.tcombat.common.items.projectile.TNTArrowItem
import com.tmvkrpxl0.tcombat.common.items.weapon.WorldAxeItem
import net.minecraft.item.AxeItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemTier
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object TCombatItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, TCombatMain.MODID)
    val WORLD_AXE: RegistryObject<Item> = ITEMS.register("world_axe") { WorldAxeItem(Item.Properties().tab(ItemGroup.TAB_COMBAT).fireResistant()) }
    val MOBILE_DISPENSER = ITEMS.register<Item>("mobile_dispenser") { MobileDispenser(Item.Properties().tab(ItemGroup.TAB_TOOLS).stacksTo(1)) }
    val TNT_ARROW = ITEMS.register<Item>("tnt_arrow") { TNTArrowItem(Item.Properties().tab(ItemGroup.TAB_COMBAT)) }
    val SNIPE_ARROW = ITEMS.register<Item>("snipe_arrow") { SnipeArrowItem(Item.Properties().tab(ItemGroup.TAB_COMBAT)) }
    val REFLECTIVE_ARROW: RegistryObject<Item> = ITEMS.register("reflective_arrow"){ ReflectiveArrowItem(Item.Properties().tab(ItemGroup.TAB_COMBAT)) }
}