package loqor.ait.core.item;

import org.joml.Vector3f;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import loqor.ait.AITMod;
import loqor.ait.core.AITSounds;
import loqor.ait.core.blockentities.ConsoleBlockEntity;
import loqor.ait.core.blocks.PeanutBlock;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.handler.travel.TravelHandler;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;

public class HammerItem extends SwordItem {

    public HammerItem(int attackDamage, float attackSpeed, Settings settings) {
        super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.IRON_BLOCK)) {
            return 15.0F;
        } else {
            return state.isIn(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        if (!(context.getWorld() instanceof ServerWorld world))
            return ActionResult.SUCCESS;

        if (world.getBlockState(pos).getBlock() instanceof PeanutBlock peanut)
            peanut.explode(context.getWorld(), pos);

        if (!(world.getBlockEntity(pos) instanceof ConsoleBlockEntity consoleBlockEntity))
            return ActionResult.PASS;

        Tardis tardis = consoleBlockEntity.tardis().get();

        if (player == null || tardis == null)
            return ActionResult.PASS;

        if (!(tardis.travel().getState() == TravelHandlerBase.State.FLIGHT)) {
            world.playSound(null, consoleBlockEntity.getPos(), AITSounds.GROAN, SoundCategory.BLOCKS,
                    1f, 1.0f);
            world.playSoundFromEntity(null, player, AITSounds.HAMMER_STRIKE, SoundCategory.PLAYERS, 0.5f, 0.2f);

            tardis.loyalty().subLevel((ServerPlayerEntity) player, 50); // safe cast since its on server already
            return ActionResult.SUCCESS;
        }

        TravelHandler travel = tardis.travel();
        int targetTicks = travel.getTargetTicks();
        int currentFlightTicks = travel.getFlightTicks();
        int bonus = 500 * travel.speed();
        int hammerUses = travel.getHammerUses();

        double fuel = tardis.fuel().getCurrentFuel();
        double maxFuel = tardis.fuel().getMaxFuel();

        double fuelCost = bonus / 5.0;

        if (hammerUses > 0) {
            bonus -= (int) Math.round(bonus * 0.1 * hammerUses);
            fuelCost += (150 * travel.speed() * hammerUses) / 7.0;
        }

        if (!world.isClient() && fuel + fuelCost > maxFuel) {
            travel.crash();

            tardis.fuel().setCurrentFuel(0.0);
            return ActionResult.SUCCESS;
        }

        travel.setFlightTicks(Math.min(currentFlightTicks + bonus, targetTicks));
        tardis.fuel().setCurrentFuel(fuel - fuelCost);
        travel.useHammer();

        if (!world.isClient() && shouldCrashTardis(hammerUses)) {
            travel.crash();
        } else {
            world.playSound(null, consoleBlockEntity.getPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,
                    0.25f * hammerUses, 1.0f);
        }

        if (world.isClient())
            return ActionResult.PASS;

        world.spawnParticles(ParticleTypes.SMALL_FLAME, pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f,
                5 * hammerUses, 0, 0, 0, 0.1f * hammerUses);

        world.spawnParticles(
                new DustColorTransitionParticleEffect(new Vector3f(0.75f, 0.75f, 0.75f), new Vector3f(0.1f, 0.1f, 0.1f),
                        1),
                pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, 5 * hammerUses, 0, 0, 0, 0.1f * hammerUses);

        world.playSound(null, consoleBlockEntity.getPos(), SoundEvents.ENTITY_GLOW_ITEM_FRAME_BREAK,
                SoundCategory.BLOCKS, 0.25f * hammerUses, 1.0f);

        return ActionResult.SUCCESS;
    }

    public boolean shouldCrashTardis(int annoyance) {
        if (annoyance <= 3)
            return false;

        for (int i = 0; i < annoyance; i++) {
            if (AITMod.RANDOM.nextInt(0, 10) == 1)
                return true;
        }

        return false;
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        return state.isOf(Blocks.IRON_BLOCK);
    }
}
