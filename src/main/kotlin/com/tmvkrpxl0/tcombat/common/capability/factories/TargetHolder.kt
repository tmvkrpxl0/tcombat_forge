package com.tmvkrpxl0.tcombat.common.capability.factories

import com.google.common.collect.ImmutableList
import net.minecraft.entity.LivingEntity
import java.util.*

class TargetHolder: ITargetHolder{
    private val targets: LinkedList<LivingEntity> = LinkedList()
    private var focused:LivingEntity? = null

    override fun getTargets(): ImmutableList<LivingEntity> = ImmutableList.copyOf(this.targets)

    override fun getFocused(): LivingEntity? = this.focused

    override fun setTargets(targets: List<LivingEntity>) {
        this.targets.clear()
        this.targets.addAll(targets)
        this.focused = null
    }

    override fun setFocused(focused: LivingEntity) {
        if(this.targets.contains(focused))this.focused = focused//If targets does not contains "focused", just ignore it
    }

    override fun clearTargets(){
        this.targets.clear()
        this.focused = null
    }
}