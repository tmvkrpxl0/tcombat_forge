package com.tmvkrpxl0.tcombat.client.key

import com.tmvkrpxl0.tcombat.common.network.packets.SkillRequestPacket
import com.tmvkrpxl0.tcombat.common.network.packets.TCombatPacketHandler
import com.tmvkrpxl0.tcombat.common.network.packets.TargetSetPacket
import com.tmvkrpxl0.tcombat.common.skills.ReflectArrow
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.util.InputMappings
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.event.InputEvent.KeyInputEvent
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import org.lwjgl.glfw.GLFW
import java.util.*

class KeyHandler : AbstractKeyHandler(BINDINGS) {
    companion object {
        private const val CATEGORY = "com.tmvkrpxl0 combat"
        val KB_REFLECT_ARROW = KeyBinding(
            "Reflect Arrow", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY
        )
        val KB_SET_TARGETS = KeyBinding("Set targets", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY)
        val BINDINGS = Builder(10).addBinding(KB_REFLECT_ARROW, false).addBinding(KB_SET_TARGETS, false)
    }

    init {
        ClientRegistry.registerKeyBinding(KB_REFLECT_ARROW)
        ClientRegistry.registerKeyBinding(KB_SET_TARGETS)
        MinecraftForge.EVENT_BUS.addListener { _: KeyInputEvent -> onKeyInput() }
    }

    private fun onKeyInput() = keyTick()


    override fun keyDown(kb: KeyBinding, isRepeat: Boolean) {
        val player = Minecraft.getInstance().player ?: return
        if (kb === KB_REFLECT_ARROW) {
            TCombatPacketHandler.INSTANCE.sendToServer(SkillRequestPacket(ReflectArrow))
        } else if (kb === KB_SET_TARGETS) {
            val world = player.world
            val axisAlignedBB = AxisAlignedBB.withSizeAtOrigin(100.0, 100.0, 100.0).offset(player.position)
            val livingEntities: MutableList<LivingEntity> = LinkedList()
            val entities = world.getEntitiesWithinAABBExcludingEntity(player, axisAlignedBB)
            for (e in entities) {
                if (e is LivingEntity && e.isAlive() && player.canEntityBeSeen(e) && player.getDistanceSq(e) < 2500) livingEntities.add(
                    e
                )
            }
            val entityIds = livingEntities.stream().mapToInt { obj: LivingEntity -> obj.entityId }.toArray()
            TCombatPacketHandler.INSTANCE.sendToServer(
                TargetSetPacket(
                    player.uniqueID, entityIds
                )
            )
        }
    }

    override fun keyUp(kb: KeyBinding) {} //This class is based on MekanismKeyHandler.java from mekanism
}