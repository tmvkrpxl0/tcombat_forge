package com.tmvkrpxl0.tcombat.common.skills;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class AbstractCooldownSkill extends AbstractActiveSkill implements IForgeRegistryEntry<AbstractSkill> {
    protected int currentCooldownTicks = 0;
    public abstract int getMaxCooldownTicks();
    public boolean isAvailable(){
        return currentCooldownTicks==0;
    }

    public AbstractCooldownSkill(){
        MinecraftForge.EVENT_BUS.addListener(this::tick);
    }

    protected void tick(TickEvent.ServerTickEvent event) {
        if(event.side.equals(LogicalSide.SERVER) && event.phase.equals(TickEvent.Phase.START)){
            if(this.currentCooldownTicks>0){
                this.currentCooldownTicks--;
            }
        }
    }

    @Override
    public boolean execute(ServerPlayerEntity player) {
        if(!isAvailable())return false;
        currentCooldownTicks = getMaxCooldownTicks();
        return executeCooldown(player);
    }

    public abstract boolean executeCooldown(PlayerEntity player);
}
