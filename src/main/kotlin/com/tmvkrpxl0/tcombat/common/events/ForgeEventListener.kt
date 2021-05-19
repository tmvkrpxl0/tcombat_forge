package com.tmvkrpxl0.tcombat.common.events

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = TCombatMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT, Dist.DEDICATED_SERVER])
object ForgeEventListener {

}