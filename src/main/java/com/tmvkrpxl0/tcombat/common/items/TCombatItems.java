package com.tmvkrpxl0.tcombat.common.items;

import net.minecraft.item.*;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

public class TCombatItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> WORLD_AXE = ITEMS.register("world_axe",
            () -> new AxeItem(ItemTier.NETHERITE, 5.0F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS).isImmuneToFire()));
}
