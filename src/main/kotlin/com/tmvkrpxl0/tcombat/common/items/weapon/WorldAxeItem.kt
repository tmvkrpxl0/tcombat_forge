package com.tmvkrpxl0.tcombat.common.items.weapon

import com.tmvkrpxl0.tcombat.common.capability.capabilities.WorldAxeCapability
import com.tmvkrpxl0.tcombat.common.capability.providers.ItemEntityConnectionProvider
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import com.tmvkrpxl0.tcombat.common.util.ForgeRunnable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemTier
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider

class WorldAxeItem(properties:Properties): AxeItem(ItemTier.NETHERITE, 5.0f, -3.0f, properties) {
    override fun use(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
        val itemStack = player.getItemInHand(hand)
        val cap = itemStack.getCapability(WorldAxeCapability.itemEntityConnectionHandler)
        return if(cap.resolve().isPresent){
            val axeConnector = cap.resolve().get()
                if(axeConnector.getEntity()==null){
                    axeConnector.setPlayer(player)
                    if(!world.isClientSide){
                        val worldAxeEntity = WorldAxeEntity(player, itemStack)
                        axeConnector.setEntity(worldAxeEntity)
                        worldAxeEntity.isNoGravity = true
                        world.addFreshEntity(worldAxeEntity)
                        worldAxeEntity.deltaMovement = player.lookAngle.scale(5.0)
                        worldAxeEntity.hurtMarked = true
                    }
                }else{
                    val entity = axeConnector.getEntity()!!
                    if(entity.inGround){
                        if(axeConnector.getPuller()==null){
                            val puller = object: ForgeRunnable() {
                                var arrived = false
                                override fun run() {
                                    if(!entity.isAlive)this.setCancelled(true)
                                    if(entity.distanceToSqr(player) <= 2.25 && !arrived){
                                        arrived = true
                                        player.deltaMovement = Vector3d.ZERO
                                    }
                                    var v = entity.position().subtract(player.position()).normalize().scale(0.4)
                                    if(arrived){
                                        v = Vector3d(0.0, -player.deltaMovement.y, 0.0)
                                    }
                                    val after = player.deltaMovement.add(v)
                                    player.deltaMovement = after
                                    player.hurtMarked = true
                                }

                                override fun setCancelled(cancel: Boolean){
                                    entity.remove()
                                    axeConnector.setEntity(null)
                                    super.setCancelled(cancel)
                                }
                            }
                            axeConnector.setPuller(puller)
                            axeConnector.pull()
                        }else{
                            axeConnector.getPuller()?.setCancelled(true)
                        }
                    }else{
                        entity.remove()
                        axeConnector.setEntity(null)
                    }
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