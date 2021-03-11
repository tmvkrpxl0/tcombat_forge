package com.tmvkrpxl0.tcombat.common.skills;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public abstract class AbstractPassiveSkill extends AbstractSkill{
    public AbstractPassiveSkill(){
        MinecraftForge.EVENT_BUS.addListener(this::tick);
    }

    private void tick(TickEvent.ServerTickEvent event){
        if(event.side.equals(LogicalSide.SERVER) && event.phase.equals(TickEvent.Phase.START)){
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            for(PlayerEntity player : server.getPlayerList().getPlayers()){
                onTick(event, player);
            }
        }
    }
    protected abstract boolean onTick(TickEvent.ServerTickEvent event, PlayerEntity player);
}
