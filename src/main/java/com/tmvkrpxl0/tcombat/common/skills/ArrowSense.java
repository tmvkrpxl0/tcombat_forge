package com.tmvkrpxl0.tcombat.common.skills;

import com.tmvkrpxl0.tcombat.common.util.TCombatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class ArrowSense extends AbstractPassiveSkill {
    private final Field inGroundField = ObfuscationReflectionHelper.findField(AbstractArrowEntity.class, "inGround");
    private final AxisAlignedBB size = AxisAlignedBB.withSizeAtOrigin(30, 30, 30);

    protected boolean onTick(TickEvent.ServerTickEvent event, PlayerEntity player) {
        if(!player.getEntityWorld().isRemote){
            AxisAlignedBB axisAlignedBB = size.offset(player.getPositionVec());
            List<Entity> list = player.world.getEntitiesInAABBexcluding(player, axisAlignedBB,
                    o -> {
                        try {
                            return o instanceof ProjectileEntity && o.getDistanceSq(player) < (15*15) && player.canEntityBeSeen(o) &&
                                    TCombatUtil.getEntityToEntityAngle(o, player) < 30 && (!(o instanceof ArrowEntity) || !inGroundField.getBoolean(o));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            return false;
                        }
                    });
            if(list.isEmpty())return false;
            player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.HOSTILE, 1, 1);
        }
        return true;
    }
}
