package com.tmvkrpxl0.tcombat.common.items.weapon

import com.tmvkrpxl0.tcombat.common.capability.capabilities.WorldAxeCapability
import com.tmvkrpxl0.tcombat.common.capability.providers.ItemEntityConnectionProvider
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemTier
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.SoundEvents
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider

class WorldAxeItem(properties:Properties): AxeItem(ItemTier.NETHERITE, 5.0f, -3.0f, properties) {
    var renderAxe = false
    override fun use(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
        val itemStack = player.getItemInHand(hand)
        val cap = itemStack.getCapability(WorldAxeCapability.itemEntityConnectionHandler)
        return if(cap.resolve().isPresent){
            val entityHolder = cap.resolve().get()
                if(entityHolder.getEntity()==null){
                    entityHolder.setPlayer(player)
                    if(!world.isClientSide){
                        val worldAxeEntity = WorldAxeEntity(player, itemStack)
                        entityHolder.setEntity(worldAxeEntity)
                        worldAxeEntity.isNoGravity = true
                        world.addFreshEntity(worldAxeEntity)
                        worldAxeEntity.deltaMovement = player.lookAngle.scale(0.3)
                        worldAxeEntity.hurtMarked = true
                    }
                }else{
                    entityHolder.getEntity()!!.remove()
                    entityHolder.setEntity(null)
                }
                player.playSound(SoundEvents.FISHING_BOBBER_RETRIEVE, 1f, 1f)
            ActionResult.success(itemStack)
        }else{
            ActionResult.fail(itemStack)
        }
    }

    override fun initCapabilities(stack: ItemStack, nbt: CompoundNBT?): ICapabilityProvider {
        return ItemEntityConnectionProvider(stack)
    }
}