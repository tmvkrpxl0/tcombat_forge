package com.tmvkrpxl0.tcombat.client.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.client.models.TNTArrowModel
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.entity.ArrowRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.entity.TippedArrowRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Vector3f
import javax.annotation.Nonnull

class TNTArrowRenderer(renderManagerIn: EntityRendererManager) : ArrowRenderer<TNTArrowEntity>(renderManagerIn) {
    private val model: TNTArrowModel = TNTArrowModel()

    @Nonnull
    override fun getEntityTexture(@Nonnull entity: TNTArrowEntity): ResourceLocation {
        return TippedArrowRenderer.RES_ARROW
    }

    override fun render(@Nonnull entityIn: TNTArrowEntity, entityYaw: Float, partialTicks: Float, @Nonnull matrixStackIn: MatrixStack, @Nonnull bufferIn: IRenderTypeBuffer, packedLightIn: Int) {
        matrixStackIn.push()
        //Rotate box that this will render
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90f))
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)))
        //render the box
        model.render(
            matrixStackIn, bufferIn.getBuffer(model.getRenderType(BOX)), packedLightIn, OverlayTexture.getPackedUV(0f, false), 1f, 1f, 1f, 1f
        )
        matrixStackIn.pop()
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
    }

    companion object {
        private val BOX = ResourceLocation(TCombatMain.MODID, "textures/entity/projectiles/tnt_arrow.png")
    }

}