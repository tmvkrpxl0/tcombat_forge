package com.tmvkrpxl0.tcombat.common.events

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.ICustomizableEntity
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import com.tmvkrpxl0.tcombat.common.util.VanilaCopy
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.server.ServerLifecycleHooks

@EventBusSubscriber(
    modid = TCombatMain.MODID, bus = EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object WorldEventListener {
    val multiShotTracker: MutableMap<LivingEntity, EntityEventListener.ArrowCounter> = HashMap()
    @SubscribeEvent
    fun serverTickEvent(event: ServerTickEvent) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            multiShotTracker.clear()
            EntityEventListener.instaArrows.removeIf { e: ArrowEntity -> e.isOnGround || !e.isAlive }
            EntityEventListener.explosionImmune.clear()

            for (world in ServerLifecycleHooks.getCurrentServer().allLevels) {
                for (entity in world.entities) {
                    if (entity is LivingEntity) {
                        if (!entity.isOnGround) {
                            val vehicle = entity.rootVehicle
                            if (vehicle !== entity && vehicle.isOnGround) {
                                val i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, entity)
                                if (i > 0) {
                                    val ground = entity.isOnGround()
                                    entity.isOnGround = true
                                    VanilaCopy.freezeGround(entity, world, vehicle.blockPosition(), i)
                                    entity.isOnGround = ground
                                }
                            }
                        }
                    }
                }
            }

            for(player: ServerPlayerEntity in ServerLifecycleHooks.getCurrentServer().playerList.players) TCombatUtil.updateTargets(player);
        }
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload){
        if(event.world is ServerWorld){
            for(e in (event.world as ServerWorld).entities){
                if(e is ICustomizableEntity)e.remove()
            }
        }
    }
}