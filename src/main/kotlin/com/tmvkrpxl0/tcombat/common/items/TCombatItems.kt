package com.tmvkrpxl0.tcombat.common.items

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.items.misc.MobileDispenser
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
    val WORLD_AXE: RegistryObject<Item> = ITEMS.register<Item>("world_axe") { WorldAxeItem(Item.Properties().group(ItemGroup.COMBAT).isImmuneToFire) }
    val MOBILE_DISPENSER: RegistryObject<Item> = ITEMS.register<Item>("mobile_dispenser") { MobileDispenser(Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1)) }
    val TNT_ARROW: RegistryObject<Item> = ITEMS.register<Item>("tnt_arrow") { TNTArrowItem(Item.Properties().group(ItemGroup.COMBAT)) }
    val SNIPE_ARROW: RegistryObject<Item> = ITEMS.register<Item>("snipe_arrow") { SnipeArrowItem(Item.Properties().group(ItemGroup.COMBAT)) }
}