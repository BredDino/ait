package dev.amble.ait.core.item;

import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.UUID;

public class TardisLocatorItem extends Item {
    public TardisLocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.sendMessage(Text.literal("X: Y: Z:"), true);
        for (UUID TARDISId : TardisManager.getInstance(world).ids()) {
            //Tardis tardis = TardisManager.getInstance(world).getTardis(TARDISId, );
            //tardis.
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
