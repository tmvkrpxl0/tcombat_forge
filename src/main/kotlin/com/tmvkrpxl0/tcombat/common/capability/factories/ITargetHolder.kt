package com.tmvkrpxl0.tcombat.common.capability.factories

import net.minecraft.entity.LivingEntity

interface ITargetHolder {
    fun getTargets():List<LivingEntity>
    fun getFocused():LivingEntity?
    fun setTargets(targets: List<LivingEntity>)
    fun setFocused(focused: LivingEntity)
    fun clearTargets()
}