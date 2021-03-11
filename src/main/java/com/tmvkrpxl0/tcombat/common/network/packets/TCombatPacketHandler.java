package com.tmvkrpxl0.tcombat.common.network.packets;

import com.tmvkrpxl0.tcombat.TCombatMain;
import com.tmvkrpxl0.tcombat.common.skills.AbstractActiveSkill;
import com.tmvkrpxl0.tcombat.common.skills.AbstractSkill;
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TCombatPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final String CHANNEL_NAME = "tcombat_packet_handler";
    private int id = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TCombatMain.MODID, CHANNEL_NAME),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public TCombatPacketHandler(){
        INSTANCE.registerMessage(id, SkillRequestPacket.class,
                (skillRequestPacket, packetBuffer) ->
                        packetBuffer.writeRegistryId(Objects.requireNonNull(skillRequestPacket).getSkill()),
                packetBuffer -> {
                    AbstractSkill skill = packetBuffer.readRegistryIdSafe(AbstractSkill.class);
                    if(!(skill instanceof AbstractActiveSkill)){
                        throw new IllegalArgumentException("Only Active Skills can be sent!!!");
                    }
                    return new SkillRequestPacket((AbstractActiveSkill) skill);
                }, (skillRequestPacket, contextSupplier) -> {
                    contextSupplier.get().enqueueWork(() -> skillRequestPacket.getSkill().execute(contextSupplier.get().getSender()));
                    contextSupplier.get().setPacketHandled(true);
                });
        id++;
        INSTANCE.registerMessage(id, TargetSetPacket.class, (targetSetPacket, packetBuffer) -> {
            packetBuffer.writeUniqueId(targetSetPacket.getUniqueID());
            packetBuffer.writeVarIntArray(targetSetPacket.entityIds());
        },
                packetBuffer -> {
                    UUID uuid = packetBuffer.readUniqueId();
                    int[] entityIds = packetBuffer.readVarIntArray(100);
                    ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(uuid)
                            .sendMessage(new StringTextComponent("Sending.. Count: " + entityIds.length), Util.DUMMY_UUID);
                    return new TargetSetPacket(uuid, entityIds);
                }, (targetSetPacket, contextSupplier) -> {
                    LinkedList<LivingEntity> list = new LinkedList<>();
                    ServerPlayerEntity playerEntity = contextSupplier.get().getSender();
                    World world = playerEntity.world;
                    contextSupplier.get().enqueueWork(() -> {
                        for(int i : targetSetPacket.entityIds()){
                            Entity entity = world.getEntityByID(i);
                            if(entity instanceof LivingEntity)list.add((LivingEntity)entity);
                        }
                        TCombatUtil.setTargets(playerEntity, list);
                        playerEntity.sendMessage(new StringTextComponent("Succeed! Count: " + TCombatUtil.getTargets(playerEntity).size()), Util.DUMMY_UUID);
                    });
                    contextSupplier.get().setPacketHandled(true);
                });
    }
}
