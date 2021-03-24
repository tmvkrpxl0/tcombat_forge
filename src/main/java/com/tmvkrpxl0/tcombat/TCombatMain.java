package com.tmvkrpxl0.tcombat;

import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants;
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes;
import com.tmvkrpxl0.tcombat.common.items.TCombatItems;
import com.tmvkrpxl0.tcombat.common.network.packets.TCombatPacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TCombatMain.MODID)
public class TCombatMain
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "tcombat";

    public TCombatMain() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        new TCombatPacketHandler();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TCombatEntityTypes.ENTITIES.register(bus);
        TCombatItems.ITEMS.register(bus);
        TCombatEnchants.ENCHANTMENTS.register(bus);
    }
}
