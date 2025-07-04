package dev.amble.ait.data.schema.console.variant.steam.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.client.models.consoles.SteamConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.steam.SteamVariant;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ClientSteamVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/steam_console.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/steam_console_emission.png"));

    public ClientSteamVariant() {
        super(SteamVariant.REFERENCE, SteamVariant.REFERENCE);
    }

    @Override
    public Identifier texture() {
        return TEXTURE;
    }

    @Override
    public Identifier emission() {
        return EMISSION;
    }

    @Override
    public ConsoleModel model() {
        return new SteamConsoleModel(SteamConsoleModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.9f, 1.125f, -0.19f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{30f, 120f};
    }

    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(0.5f, 1.25f, 0.5f);
    }

    @Override
    public float[] handlesRotations() {
        return new float[]{210f, 120f};
    }
}
