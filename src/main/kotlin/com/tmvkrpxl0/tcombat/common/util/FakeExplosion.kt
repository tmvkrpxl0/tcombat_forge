package com.tmvkrpxl0.tcombat.common.util

import com.google.common.collect.Sets
import com.mojang.datafixers.util.Pair
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.block.AbstractFireBlock
import net.minecraft.block.Block
import net.minecraft.enchantment.ProtectionEnchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.item.TNTEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootContext
import net.minecraft.loot.LootParameters
import net.minecraft.network.play.server.SExplosionPacket
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.Explosion
import net.minecraft.world.ExplosionContext
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.event.ForgeEventFactory
import java.util.function.Consumer
import kotlin.math.sqrt

class FakeExplosion(
    world: World,
    exploder: Entity?,
    source: DamageSource?,
    context: ExplosionContext?,
    x: Double,
    y: Double,
    z: Double,
    size: Float,
    causesFire: Boolean,
    mode: Mode
) : Explosion(world, exploder, source, context, x, y, z, size, causesFire, mode) {
    constructor(
        worldIn: World,
        exploderIn: Entity?,
        xIn: Double,
        yIn: Double,
        zIn: Double,
        sizeIn: Float,
        causesFireIn: Boolean,
        modeIn: Mode,
        affectedBlockPositionsIn: List<BlockPos>
    ) : this(worldIn, exploderIn, xIn, yIn, zIn, sizeIn, causesFireIn, modeIn) {
        affectedBlockPositions.addAll(affectedBlockPositionsIn)
    }

    constructor(
        worldIn: World,
        exploderIn: Entity?,
        xIn: Double,
        yIn: Double,
        zIn: Double,
        sizeIn: Float,
        causesFireIn: Boolean,
        modeIn: Mode
    ) : this(worldIn, exploderIn, null, null, xIn, yIn, zIn, sizeIn, causesFireIn, modeIn)

    constructor(
        worldIn: World,
        entityIn: Entity?,
        x: Double,
        y: Double,
        z: Double,
        size: Float,
        affectedPositions: List<BlockPos>
    ) : this(worldIn, entityIn, x, y, z, size, false, Mode.DESTROY, affectedPositions)

    override fun doExplosionA() {
        if (world.isRemote) return
        val set: MutableSet<BlockPos> = Sets.newHashSet()
        for (j in 0..15) {
            for (k in 0..15) {
                for (l in 0..15) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        var d0 = (j.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                        var d1 = (k.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                        var d2 = (l.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                        val d3 = sqrt(d0 * d0 + d1 * d1 + d2 * d2)
                        d0 /= d3
                        d1 /= d3
                        d2 /= d3
                        var f = size * (0.7f + world.rand.nextFloat() * 0.6f)
                        var d4 = x
                        var d6 = y
                        var d8 = z
                        while (f > 0.0f) {
                            val blockpos = BlockPos(d4, d6, d8)
                            val blockstate = world.getBlockState(blockpos)
                            val fluidstate = world.getFluidState(blockpos)
                            val optional = context.getExplosionResistance(this, world, blockpos, blockstate, fluidstate)
                            if (optional.isPresent) {
                                f -= (optional.get() + 0.3f) * 0.3f
                            }
                            if (f > 0.0f && context.canExplosionDestroyBlock(this, world, blockpos, blockstate, f)) {
                                set.add(blockpos)
                            }
                            d4 += d0 * 0.3f.toDouble()
                            d6 += d1 * 0.3f.toDouble()
                            d8 += d2 * 0.3f.toDouble()
                            f -= 0.22500001f
                        }
                    }
                }
            }
        }
        affectedBlockPositions.addAll(set)
        val f2 = size * 2.0f
        val k1 = MathHelper.floor(x - f2.toDouble() - 1.0)
        val l1 = MathHelper.floor(x + f2.toDouble() + 1.0)
        val i2 = MathHelper.floor(y - f2.toDouble() - 1.0)
        val i1 = MathHelper.floor(y + f2.toDouble() + 1.0)
        val j2 = MathHelper.floor(z - f2.toDouble() - 1.0)
        val j1 = MathHelper.floor(z + f2.toDouble() + 1.0)
        val list = world.getEntitiesWithinAABBExcludingEntity(
            exploder, AxisAlignedBB(
                k1.toDouble(), i2.toDouble(), j2.toDouble(), l1.toDouble(), i1.toDouble(), j1.toDouble()
            )
        )
        ForgeEventFactory.onExplosionDetonate(world, this, list, f2.toDouble())
        val vector3d = Vector3d(x, y, z)
        for (entity in list) {
            if (!entity.isImmuneToExplosions) {
                val d12 = (MathHelper.sqrt(entity.getDistanceSq(vector3d)) / f2).toDouble()
                if (d12 <= 1.0) {
                    var d5 = entity.posX - x
                    var d7 = if(entity is TNTEntity)entity.posY else entity.posYEye - y
                    var d9 = entity.posZ - z
                    val d13 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9).toDouble()
                    if (d13 != 0.0) {
                        d5 /= d13
                        d7 /= d13
                        d9 /= d13
                        val d14 = getBlockDensity(vector3d, entity).toDouble()
                        val d10 = (1.0 - d12) * d14
                        var d11 = d10
                        if (entity is LivingEntity) {
                            d11 = ProtectionEnchantment.getBlastDamageReduction(entity, d10)
                        }
                        entity.motion = entity.motion.add(d5 * d11, d7 * d11, d9 * d11)
                        if (entity is PlayerEntity) {
                            if (!entity.isSpectator && (!entity.isCreative || !entity.abilities.isFlying)) {
                                playerKnockbackMap[entity] = Vector3d(d5 * d10, d7 * d10, d9 * d10)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun doExplosionB(spawnParticles: Boolean) {
        world.playSound(
            x,
            y,
            z,
            SoundEvents.ENTITY_GENERIC_EXPLODE,
            SoundCategory.BLOCKS,
            4.0f,
            (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f,
            false
        )
        val flag = mode != Mode.NONE
        if (spawnParticles) {
            if (size >= 2.0f && flag) {
                world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1.0, 0.0, 0.0)
            } else {
                world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1.0, 0.0, 0.0)
            }
        }
        if (flag) {
            val objectarraylist = ObjectArrayList<Pair<ItemStack, BlockPos>>()
            affectedBlockPositions.shuffle(world.rand)
            for (blockpos in affectedBlockPositions) {
                val blockState = world.getBlockState(blockpos)
                if (!blockState.isAir(world, blockpos)) {
                    val blockpos1 = blockpos.toImmutable()
                    world.profiler.startSection("explosion_blocks")
                    if (blockState.canDropFromExplosion(world, blockpos, this) && world is ServerWorld) {
                        val tileEntity = if (blockState.hasTileEntity()) world.getTileEntity(blockpos) else null
                        val builder = LootContext.Builder(world).withRandom(world.rand)
                            .withParameter(LootParameters.ORIGIN, Vector3d.copyCentered(blockpos))
                            .withParameter(LootParameters.TOOL, ItemStack.EMPTY)
                            .withNullableParameter(LootParameters.BLOCK_ENTITY, tileEntity)
                            .withNullableParameter(LootParameters.THIS_ENTITY, exploder)
                        if (mode == Mode.DESTROY) {
                            builder.withParameter(LootParameters.EXPLOSION_RADIUS, size)
                        }
                        blockState.getDrops(builder).forEach(Consumer { stack: ItemStack ->
                            handleExplosionDrops(
                                objectarraylist,
                                stack,
                                blockpos1
                            )
                        })
                    }
                    blockState.onBlockExploded(world, blockpos, this)
                    world.profiler.endSection()
                }
            }
            for (pair in objectarraylist) {
                Block.spawnAsEntity(world, pair.second, pair.first)
            }
        }
        if (causesFire) {
            for (blockpos2 in affectedBlockPositions) {
                if (random.nextInt(3) == 0 && world.getBlockState(blockpos2).isAir && world.getBlockState(blockpos2.down())
                        .isOpaqueCube(
                            world, blockpos2.down()
                        )
                ) {
                    world.setBlockState(blockpos2, AbstractFireBlock.getFireForPlacement(world, blockpos2))
                }
            }
        }
    }

    init {
        doExplosionA()
        doExplosionB(true)
        if (mode == Mode.NONE) {
            clearAffectedBlockPositions()
        }
        if (!world.isRemote) {
            for (p in exploder!!.world.players) {
                val serverplayerentity = p as ServerPlayerEntity
                if (serverplayerentity.getDistanceSq(x, y, z) < 4096.0) {
                    serverplayerentity.connection.sendPacket(
                        SExplosionPacket(
                            x,
                            y,
                            z,
                            size,
                            getAffectedBlockPositions(),
                            getPlayerKnockbackMap()[serverplayerentity]!!
                        )
                    )
                }
            }
        }
    }
}