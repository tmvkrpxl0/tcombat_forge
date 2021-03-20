package com.tmvkrpxl0.tcombat.common.items;

import com.tmvkrpxl0.tcombat.common.items.misc.MobileDispenser;
import com.tmvkrpxl0.tcombat.common.items.projectile.TNTArrow;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

public class TCombatItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> WORLD_AXE = ITEMS.register("world_axe",
            () -> new AxeItem(ItemTier.NETHERITE, 5.0F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
    public static final RegistryObject<Item> MOBILE_DISPENSER = ITEMS.register("mobile_dispenser", () ->
            new MobileDispenser(new Item.Properties().tab(ItemGroup.TAB_TOOLS).stacksTo(1)));
    public static final RegistryObject<Item> TNT_ARROW = ITEMS.register("tnt_arrow", () ->
            new TNTArrow(new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
}
