package com.tmvkrpxl0.tcombat.client.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import com.tmvkrpxl0.tcombat.common.entities.projectile.ReflectiveArrowEntity
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.entity.ArrowRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.entity.TippedArrowRenderer
import net.minecraft.util.ResourceLocation

class ReflectiveArrowRenderer(p_i46193_1_: EntityRendererManager) : ArrowRenderer<ReflectiveArrowEntity>(p_i46193_1_) {
    override fun getTextureLocation(p_110775_1_: ReflectiveArrowEntity): ResourceLocation = TippedArrowRenderer.NORMAL_ARROW_LOCATION

    override fun render(
        p_225623_1_: ReflectiveArrowEntity,
        p_225623_2_: Float,
        p_225623_3_: Float,
        p_225623_4_: MatrixStack,
        p_225623_5_: IRenderTypeBuffer,
        p_225623_6_: Int
    ) {
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_)
    }
}