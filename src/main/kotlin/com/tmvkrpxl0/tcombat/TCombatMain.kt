package com.tmvkrpxl0.tcombat

import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import com.tmvkrpxl0.tcombat.common.network.packets.TCombatPacketHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TCombatMain.MODID)
class TCombatMain {
    companion object {
        // Directly reference a log4j logger.
        val LOGGER: Logger = LogManager.getLogger()
        const val MODID = "tcombat"
    }

    init {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
        TCombatPacketHandler()
        val bus = FMLJavaModLoadingContext.get().modEventBus
        TCombatEntityTypes.ENTITIES.register(bus)
        TCombatItems.ITEMS.register(bus)
        TCombatEnchants.ENCHANTMENTS.register(bus)
    }
}