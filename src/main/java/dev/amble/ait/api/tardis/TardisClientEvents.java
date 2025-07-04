package dev.amble.ait.api.tardis;

import dev.amble.ait.client.screens.interior.InteriorSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public class TardisClientEvents {

    public static final Event<SettingsSetup> SETTINGS_SETUP = EventFactory.createArrayBacked(SettingsSetup.class,
            callbacks -> screen -> {
                for (SettingsSetup callback : callbacks) {
                    callback.onSetup(screen);
                }
            });

    @FunctionalInterface
    public interface SettingsSetup {
        void onSetup(InteriorSettingsScreen screen);
    }
}
