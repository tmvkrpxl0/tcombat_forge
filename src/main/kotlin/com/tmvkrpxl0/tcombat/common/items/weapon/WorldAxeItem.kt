package com.tmvkrpxl0.tcombat.common.items.weapon

import com.tmvkrpxl0.tcombat.common.capability.capabilities.ItemEntityConnectionCapability
import com.tmvkrpxl0.tcombat.common.capability.providers.ItemEntityConnectionProvider
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemTier
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider

class WorldAxeItem(properties:Properties): AxeItem(ItemTier.NETHERITE, 5.0f, -3.0f, properties) {
    override fun use(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
        val itemStack = player.getItemInHand(hand)
        if(itemStack.item !== this){
            throw IllegalStateException("Player right clicked item that is not on their hand!!")
        }
        val cap = itemStack.getCapability(ItemEntityConnectionCapability.ITEM_ENTITY_CONNECTION_HANDLER)
        return if(cap.resolve().isPresent){
            val entityHolder = cap.resolve().get()
            if(entityHolder.getEntity()==null || !entityHolder.getEntity()!!.isAlive){
                val worldAxeEntity = WorldAxeEntity(player, itemStack)
                entityHolder.setEntity(worldAxeEntity)
                world.addFreshEntity(worldAxeEntity)
                worldAxeEntity.deltaMovement = player.lookAngle.multiply(5.0,5.0,5.0)
                worldAxeEntity.hurtMarked = true
                ActionResult.success(itemStack)
            }else{
                ActionResult.fail(itemStack)
            }
        }else{
            ActionResult.fail(itemStack)
        }
    }

    override fun initCapabilities(stack: ItemStack, nbt: CompoundNBT?): ICapabilityProvider {
        return ItemEntityConnectionProvider()
    }
}