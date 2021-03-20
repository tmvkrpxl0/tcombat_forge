package com.tmvkrpxl0.tcombat.client.key;

import com.tmvkrpxl0.tcombat.common.network.packets.SkillRequestPacket;
import com.tmvkrpxl0.tcombat.common.network.packets.TCombatPacketHandler;
import com.tmvkrpxl0.tcombat.common.network.packets.TargetSetPacket;
import com.tmvkrpxl0.tcombat.common.skills.Skills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;

import com.tmvkrpxl0.tcombat.client.key.AbstractKeyHandler.Builder;

public class KeyHandler extends AbstractKeyHandler{
    //This class is based on MekanismKeyHandler.java from mekanism
    private static final String CATEGORY = "tmvkrpxl0 combat";
    public static final KeyBinding KB_REFLECT_ARROW = new KeyBinding("Reflect Arrow", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyBinding KB_SET_TARGETS = new KeyBinding("Set targets", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);


    public static final Builder BINDINGS = new Builder(10)
            .addBinding(KB_REFLECT_ARROW, false)
            .addBinding(KB_SET_TARGETS, false);

    public KeyHandler() {
        super(BINDINGS);
        ClientRegistry.registerKeyBinding(KB_REFLECT_ARROW);
        ClientRegistry.registerKeyBinding(KB_SET_TARGETS);
        MinecraftForge.EVENT_BUS.addListener(this::onKeyInput);
    }

    private void onKeyInput(InputEvent.KeyInputEvent event){
        keyTick();
    }

    @Override
    public void keyDown(KeyBinding kb, boolean isRepeat) {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player==null){
            return;
        }

        if(kb==KB_REFLECT_ARROW){
            TCombatPacketHandler.INSTANCE.sendToServer(new SkillRequestPacket(Skills.REFLECT_ARROW));
        }else if(kb==KB_SET_TARGETS){
            World world = player.level;
            AxisAlignedBB axisAlignedBB = AxisAlignedBB.ofSize(100, 100, 100).move(player.blockPosition());
            List<LivingEntity> livingEntities = new LinkedList<>();
            List<Entity> entities = world.getEntities(player, axisAlignedBB);
            for(Entity e : entities){
                if(e instanceof LivingEntity && e.isAlive() && player.canSee(e) && player.distanceToSqr(e) < (2500))livingEntities.add((LivingEntity) e);
            }

            int[] entityIds = livingEntities.stream().mapToInt(Entity::getId).toArray();
            TCombatPacketHandler.INSTANCE.sendToServer(new TargetSetPacket(player.getUUID(), entityIds));
        }
    }

    @Override
    public void keyUp(KeyBinding kb) {

    }
}
