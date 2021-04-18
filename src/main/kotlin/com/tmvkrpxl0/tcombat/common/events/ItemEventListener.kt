package com.tmvkrpxl0.tcombat.common.events

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraft.item.Items
import net.minecraft.potion.Effects
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(
    modid = TCombatMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object ItemEventListener {
    @SubscribeEvent
    fun onUseTick(event: LivingEntityUseItemEvent.Start) {
        TCombatMain.LOGGER.info(event.duration)
        if (event.item.item === Items.CROSSBOW || event.item.item === Items.BOW) {
            if (event.entityLiving.isPotionActive(Effects.STRENGTH)) {
                val effectInstance = event.entityLiving.getActivePotionEffect(Effects.STRENGTH)
                val amplifier = effectInstance!!.amplifier
                event.duration = event.duration - 3 * amplifier
            }
            if (event.entityLiving.isPotionActive(Effects.HASTE)) {
                val effectInstance = event.entityLiving.getActivePotionEffect(Effects.HASTE)
                val amplifier = effectInstance!!.amplifier
                event.duration = event.duration - 3 * amplifier
            }
        }
    }

    @SubscribeEvent
    fun onUse(event: LivingEntityUseItemEvent.Tick) {
        if (event.item.item === Items.CROSSBOW || event.item.item === Items.BOW) {
            if (event.duration <= 0) {
                if (event.entityLiving.isSneaking) {
                    event.isCanceled = true
                    event.item.onPlayerStoppedUsing(event.entityLiving.world, event.entityLiving, 0)
                    event.entityLiving.resetActiveHand()
                }
            }
        }
    }
}