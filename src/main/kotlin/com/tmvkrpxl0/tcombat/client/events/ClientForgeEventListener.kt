package com.tmvkrpxl0.tcombat.client.events

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = TCombatMain.MODID, bus = EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ClientForgeEventListener {

}