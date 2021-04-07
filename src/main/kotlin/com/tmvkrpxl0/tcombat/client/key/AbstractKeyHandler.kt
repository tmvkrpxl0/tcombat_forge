package com.tmvkrpxl0.tcombat.client.key

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.util.InputMappings
import net.minecraftforge.client.settings.KeyModifier
import org.lwjgl.glfw.GLFW
import java.util.*

/**
 * This is copy of MekKeyhandler.java from Mekanism 1.16
 */
abstract class AbstractKeyHandler(bindings: Builder) {
    /**
     * KeyBinding instances
     */
    private val keyBindings: Array<KeyBinding> = bindings.getBindings()

    /**
     * Track which keys have been seen as pressed currently
     */
    private val keyDown: BitSet = BitSet()

    /**
     * Whether keys send repeated KeyDown pseudo-messages
     */
    private val repeatings: BitSet = bindings.repeatFlags

    companion object {
        fun getIsKeyPressed(keyBinding: KeyBinding): Boolean {
            if (keyBinding.isKeyDown) {
                return true
            }
            return if (keyBinding.keyConflictContext.isActive && keyBinding.keyModifier.isActive(keyBinding.keyConflictContext)) {
                //Manually check in case keyBinding#pressed just never got a chance to be updated
                isKeyDown(keyBinding)
            } else KeyModifier.isKeyCodeModifier(keyBinding.key) && isKeyDown(
                keyBinding
            )
            //If we failed, due to us being a key modifier as our key, check the old way
        }

        private fun isKeyDown(keyBinding: KeyBinding): Boolean {
            val key = keyBinding.key
            val keyCode = key.keyCode
            if (keyCode != InputMappings.INPUT_INVALID.keyCode) {
                val windowHandle = Minecraft.getInstance().mainWindow.handle
                try {
                    if (key.type == InputMappings.Type.KEYSYM) {
                        return InputMappings.isKeyDown(windowHandle, keyCode)
                    } else if (key.type == InputMappings.Type.MOUSE) {
                        return GLFW.glfwGetMouseButton(windowHandle, keyCode) == GLFW.GLFW_PRESS
                    }
                } catch (ignored: Exception) {
                }
            }
            return false
        }
    }

    fun keyTick() {
        for (i in keyBindings.indices) {
            val keyBinding = keyBindings[i]
            val state = keyBinding.isKeyDown
            val lastState = keyDown[i]
            if (state != lastState || state && repeatings[i]) {
                if (state) {
                    keyDown(keyBinding, lastState)
                } else {
                    keyUp(keyBinding)
                }
                keyDown[i] = state
            }
        }
    }

    abstract fun keyDown(kb: KeyBinding, isRepeat: Boolean)
    abstract fun keyUp(kb: KeyBinding)
    class Builder(expectedCapacity: Int) {
        private val bindings: MutableList<KeyBinding>
        val repeatFlags = BitSet()

        /**
         * Add a keybinding to the list
         *
         * @param k          the KeyBinding to add
         * @param repeatFlag true if keyDown pseudo-events continue to be sent while key is held
         */
        fun addBinding(k: KeyBinding, repeatFlag: Boolean): Builder {
            repeatFlags[bindings.size] = repeatFlag
            bindings.add(k)
            return this
        }

        fun getBindings(): Array<KeyBinding> {
            return bindings.toTypedArray()
        }

        init {
            bindings = ArrayList(expectedCapacity)
        }
    }

}