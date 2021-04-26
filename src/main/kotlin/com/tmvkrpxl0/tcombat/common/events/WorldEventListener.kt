package com.tmvkrpxl0.tcombat.common.events

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.capability.capabilities.TargetCapability
import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableFluidEntity
import com.tmvkrpxl0.tcombat.common.entities.misc.ICustomizableEntity
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import com.tmvkrpxl0.tcombat.common.util.VanilaCopy
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.fluid.FlowingFluid
import net.minecraft.item.BucketItem
import net.minecraft.item.Items
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
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
        }
    }


}