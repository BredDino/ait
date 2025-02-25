package dev.amble.ait.core.tardis.control.impl;

import java.util.Random;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class EngineOverload extends Control {

    private static final Random RANDOM = new Random();
    private static final String[] SPINNER = {"/", "-", "\\", "|"};

    public EngineOverload() {
        super("engine_overload");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        if (tardis.fuel().getCurrentFuel() < 25000) {
            player.sendMessage(Text.literal("§cERROR, TARDIS REQUIRES AT LEAST 25K ARTRON TO EXECUTE THIS ACTION."), true);
            world.playSound(null, player.getBlockPos(), AITSounds.CLOISTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return false;
        }

        boolean isInFlight = tardis.travel().getState() == TravelHandlerBase.State.FLIGHT;

        if (!isInFlight) {
            tardis.travel().finishDemat();
        }

        runDumpingArtronSequence(player, () -> {
            world.getServer().execute(() -> {
                tardis.travel().decreaseFlightTime(999999999);
                tardis.removeFuel(5000000);

                if (!isInFlight) {
                    tardis.travel().finishDemat();
                    tardis.travel().decreaseFlightTime(999999999);
                } else {
                    tardis.travel().decreaseFlightTime(999999999);
                }

                Scheduler.get().runTaskLater(() -> triggerExplosion(world, console, tardis, 4), TimeUnit.SECONDS, 0);
            });
        });

        return true;
    }

    private void triggerExplosion(ServerWorld world, BlockPos console, Tardis tardis, int stage) {
        if (stage <= 0) return;

        tardis.alarm().enable();
        tardis.subsystems().demat().removeDurability(500);
        tardis.subsystems().chameleon().removeDurability(75);
        tardis.subsystems().shields().removeDurability(325);
        tardis.subsystems().lifeSupport().removeDurability(100);
        tardis.subsystems().engine().removeDurability(750);
        tardis.crash().addRepairTicks(999999999);

        spawnParticles(world, console);
        Scheduler.get().runTaskLater(() -> spawnExteriorParticles(tardis), TimeUnit.SECONDS, 3);

        int nextDelay = (stage == 4) ? 2 : 3;
        Scheduler.get().runTaskLater(() -> triggerExplosion(world, console, tardis, stage - 1), TimeUnit.SECONDS, nextDelay);
    }

    private void runDumpingArtronSequence(ServerPlayerEntity player, Runnable onFinish) {
        for (int i = 0; i < 6; i++) {
            int delay = i + 1;
            Scheduler.get().runTaskLater(() -> {
                String frame = SPINNER[delay % SPINNER.length];
                player.sendMessage(Text.literal("§6DUMPING ARTRON " + frame), true);
            }, TimeUnit.SECONDS, delay);
        }

        Scheduler.get().runTaskLater(() -> runFlashingFinalMessage(player, onFinish), TimeUnit.SECONDS, 3);
    }

    private void runFlashingFinalMessage(ServerPlayerEntity player, Runnable onFinish) {
        for (int i = 0; i < 6; i++) {
            int delay = i + 1;
            Scheduler.get().runTaskLater(() -> {
                String flashColor = (delay % 2 == 0) ? "§c" : "§f";
                player.sendMessage(Text.literal(flashColor + "ARTRON DUMPED, ENGINES OVERLOADED, TRIGGERING EMERGENCY ARTRON RELEASE"), true);
            }, TimeUnit.SECONDS, delay);
        }

        Scheduler.get().runTaskLater(onFinish, TimeUnit.SECONDS, 3);
    }

    private void spawnParticles(ServerWorld world, BlockPos position) {
        for (int i = 0; i < 50; i++) {
            double offsetX = (RANDOM.nextDouble() - 0.5) * 2.0;
            double offsetY = RANDOM.nextDouble() * 1.5;
            double offsetZ = (RANDOM.nextDouble() - 0.5) * 2.0;

            world.spawnParticles(ParticleTypes.SNEEZE, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.ASH, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.EXPLOSION, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.LAVA, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.SMALL_FLAME, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
            world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, position.getX() + 0.5 + offsetX, position.getY() + 1.5 + offsetY, position.getZ() + 0.5 + offsetZ, 2, 0, 0.05, 0, 0.1);
        }
    }

    private void spawnExteriorParticles(Tardis tardis) {
        CachedDirectedGlobalPos exteriorPos = tardis.travel().position();

        if (exteriorPos == null) return;
        ServerWorld exteriorWorld = exteriorPos.getWorld();
        BlockPos exteriorBlockPos = exteriorPos.getPos();

        spawnParticles(exteriorWorld, exteriorBlockPos);
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.ENGINE;
    }

    @Override
    public long getDelayLength() {
        return 360000;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.ENGINE_OVERLOAD;
    }
}
