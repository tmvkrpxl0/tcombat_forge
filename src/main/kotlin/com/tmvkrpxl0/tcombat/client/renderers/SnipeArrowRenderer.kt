package com.tmvkrpxl0.tcombat.client.renderers

import com.tmvkrpxl0.tcombat.common.entities.projectile.SnipeArrowEntity
import net.minecraft.client.renderer.entity.ArrowRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.entity.TippedArrowRenderer
import net.minecraft.util.ResourceLocation

class SnipeArrowRenderer(p_i46193_1_: EntityRendererManager) : ArrowRenderer<SnipeArrowEntity>(p_i46193_1_) {
    override fun getTextureLocation(p_110775_1_: SnipeArrowEntity): ResourceLocation = TippedArrowRenderer.NORMAL_ARROW_LOCATION

}