package loqor.ait.data.schema.exterior.variant.classic.client;

import org.joml.Vector3f;

import loqor.ait.core.tardis.handler.BiomeHandler;
import loqor.ait.data.datapack.exterior.BiomeOverrides;

public class ClientClassicBoxMintVariant extends ClientClassicBoxVariant {

    private final BiomeOverrides OVERRIDES = BiomeOverrides.builder(ClientClassicBoxVariant.OVERRIDES)
            .with(type -> type.getTexture(this.texture()), BiomeHandler.BiomeType.CHORUS, BiomeHandler.BiomeType.SNOWY,
                    BiomeHandler.BiomeType.SCULK)
            .build();

    public ClientClassicBoxMintVariant() {
        super("mint");
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.55f, 1.125f, 1.165f);
    }

    @Override
    public BiomeOverrides overrides() {
        return OVERRIDES;
    }
}
