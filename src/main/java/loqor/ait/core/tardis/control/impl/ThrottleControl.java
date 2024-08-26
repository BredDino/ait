package loqor.ait.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import loqor.ait.core.AITSounds;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.control.Control;
import loqor.ait.tardis.handler.travel.TravelHandler;

public class ThrottleControl extends Control {
    public ThrottleControl() {
        super("throttle");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console,
            boolean leftClick) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        if (tardis.isInDanger())
            return false;

        TravelHandler travel = tardis.travel();

        if (!leftClick) {
            if (player.isSneaking()) {
                travel.speed(travel.maxSpeed().get());
            } else {
                travel.increaseSpeed();
            }
        } else {
            if (player.isSneaking()) {
                travel.speed(0);
            } else {
                travel.decreaseSpeed();
            }
        }

        if (travel.getState() == TravelHandler.State.DEMAT)
            tardis.sequence().setActivePlayer(player);

        return true;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.DEMAT_LEVER_PULL;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }
}
