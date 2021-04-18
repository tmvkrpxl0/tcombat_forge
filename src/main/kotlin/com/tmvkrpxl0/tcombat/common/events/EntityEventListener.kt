package com.tmvkrpxl0.tcombat.common.events

import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.item.Items
import net.minecraft.util.EntityDamageSource
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.ProjectileImpactEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(
    modid = TCombatMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object EntityEventListener {
    val instaArrows: MutableSet<ArrowEntity> = HashSet()
    val explosionImmune: MutableSet<LivingEntity> = HashSet()

    @SubscribeEvent
    fun onProjectileHit(event: ProjectileImpactEvent.Arrow) {
        if (instaArrows.contains(event.arrow)) {
            val r = event.rayTraceResult
            if (r is EntityRayTraceResult) {
                r.entity.isInvulnerable = false
                r.entity.hurtResistantTime = 0
            }
        }
    }

    @SubscribeEvent
    fun onEntityDamage(event: LivingDamageEvent) {
        if (event.source.damageType == "explosion.player") {
            val entityDamageSource = event.source as EntityDamageSource
            if (entityDamageSource.isExplosion) {
                if (explosionImmune.contains(entityDamageSource.immediateSource)) {
                    event.amount = 0f
                    event.isCanceled = true
                }
            }
        }
    }
}