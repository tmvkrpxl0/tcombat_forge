package com.tmvkrpxl0.tcombat.client.key

import com.tmvkrpxl0.tcombat.common.network.packets.SkillRequestPacket
import com.tmvkrpxl0.tcombat.common.network.packets.TCombatPacketHandler
import com.tmvkrpxl0.tcombat.common.network.packets.TargetRequestPacket
import com.tmvkrpxl0.tcombat.common.skills.ReflectionBlast
import com.tmvkrpxl0.tcombat.common.skills.RicochetArrow
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.util.InputMappings
import net.minecraftforge.client.event.InputEvent.KeyInputEvent
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import org.lwjgl.glfw.GLFW

class KeyHandler private constructor(): AbstractKeyHandler(BINDINGS) {
    companion object {
        private const val CATEGORY = "com.tmvkrpxl0 combat"
        val KB_REFLECT_ARROW = KeyBinding(
            "Reflect Arrow", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY
        )
        val KB_SET_TARGETS = KeyBinding("Set targets", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY)
        val KB_SET_TARGET_MODE = KeyBinding("Set targetting mode", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY)
        val KB_REFLECTION_BLAST = KeyBinding("Reflection blast", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY)
        val BINDINGS = Builder(10)
            .addBinding(KB_REFLECT_ARROW, false)
            .addBinding(KB_SET_TARGETS, false)
            .addBinding(KB_SET_TARGET_MODE, false)
            .addBinding(KB_REFLECTION_BLAST, false)
        val INSTANCE = KeyHandler()
        var requestType = TargetRequestPacket.RequestType.PICK_CLOSE
    }

    fun register() {
        ClientRegistry.registerKeyBinding(KB_REFLECT_ARROW)
        ClientRegistry.registerKeyBinding(KB_SET_TARGETS)
        ClientRegistry.registerKeyBinding(KB_SET_TARGET_MODE)
        ClientRegistry.registerKeyBinding(KB_REFLECTION_BLAST)
        MinecraftForge.EVENT_BUS.addListener { _: KeyInputEvent -> onKeyInput() }
    }

    private fun onKeyInput() = keyTick()


    override fun keyDown(kb: KeyBinding, isRepeat: Boolean) {
        val player = Minecraft.getInstance().player ?: return
        when {
            kb === KB_REFLECT_ARROW -> {
                TCombatPacketHandler.INSTANCE.sendToServer(SkillRequestPacket(RicochetArrow))
            }
            kb === KB_SET_TARGETS -> {
                TCombatPacketHandler.INSTANCE.sendToServer(TargetRequestPacket(requestType))
            }
            kb === KB_SET_TARGET_MODE -> {
                var ordinal = requestType.ordinal + 1
                ordinal %= (TargetRequestPacket.RequestType.values().size - 1)
            }
            kb === KB_REFLECTION_BLAST -> {
                TCombatPacketHandler.INSTANCE.sendToServer(SkillRequestPacket(ReflectionBlast))
            }
        }
    }

    override fun keyUp(kb: KeyBinding) {} //This class is based on MekanismKeyHandler.java from mekanism
}