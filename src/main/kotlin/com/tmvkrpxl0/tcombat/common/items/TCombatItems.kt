package com.tmvkrpxl0.tcombat.common.items

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.items.projectile.SnipeArrowItem
import com.tmvkrpxl0.tcombat.common.items.misc.MobileDispenser
import com.tmvkrpxl0.tcombat.common.items.projectile.TNTArrowItem
import net.minecraft.item.AxeItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemTier
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object TCombatItems {
    val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TCombatMain.MODID)
    val WORLD_AXE = ITEMS.register<Item>(
        "world_axe"
    ) {
        AxeItem(
            ItemTier.NETHERITE, 5.0f, -3.0f, Item.Properties()
                .group(ItemGroup.TOOLS).isImmuneToFire
        )
    }
    val MOBILE_DISPENSER = ITEMS.register<Item>("mobile_dispenser") {
        MobileDispenser(
            Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1)
        )
    }
    val TNT_ARROW = ITEMS.register<Item>("tnt_arrow") { TNTArrowItem(Item.Properties().group(ItemGroup.COMBAT)) }
    val SNIPE_ARROW = ITEMS.register<Item>("snipe_arrow") { SnipeArrowItem(Item.Properties().group(ItemGroup.COMBAT)) }
}