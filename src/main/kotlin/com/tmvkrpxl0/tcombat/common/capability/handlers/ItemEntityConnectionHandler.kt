package com.tmvkrpxl0.tcombat.common.capability.handlers

import com.tmvkrpxl0.tcombat.common.capability.factories.IEntityHolder
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.server.ServerLifecycleHooks

class ItemEntityConnectionHandler: Capability.IStorage<IEntityHolder> {
    override fun writeNBT(capability: Capability<IEntityHolder>, instance: IEntityHolder, side: Direction?): INBT {
        val toReturn = CompoundNBT()
        return if(instance.getEntity()==null){
            toReturn
        }else{
            val entity = instance.getEntity()!!
            toReturn.putString("World", entity.level.dimension().location().toString())
            toReturn.putUUID("EntityId", entity.uuid)
            toReturn
        }
    }

    override fun readNBT(capability: Capability<IEntityHolder>, instance: IEntityHolder, side: Direction?, nbt: INBT) {
        val compoundNBT = nbt as CompoundNBT
        if(compoundNBT.contains("EntityId")){
            val worldKey = ResourceLocation(compoundNBT.getString("World"))
            val uniqueId = compoundNBT.getUUID("EntityId")
            var world: ServerWorld? = null
            ServerLifecycleHooks.getCurrentServer().allLevels.forEach { serverWorld: ServerWorld ->
                if(serverWorld.dimension().equals(worldKey)){
                    world = serverWorld
                }
            }
            if(world==null)return
            val entity = world!!.getEntity(uniqueId)
            if(entity!=null)instance.setEntity(entity)
        }
    }

}