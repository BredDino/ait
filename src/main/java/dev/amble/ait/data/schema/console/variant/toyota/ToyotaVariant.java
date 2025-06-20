package dev.amble.ait.data.schema.console.variant.toyota;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.ToyotaType;
import net.minecraft.util.Identifier;

public class ToyotaVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/toyota");

    public ToyotaVariant() {
        super(ToyotaType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.COMPANION));
    }
}
