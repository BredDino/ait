package dev.amble.ait.core.item;

import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class TardisLocatorItem extends LinkableItem implements ArtronHolderItem {


    public TardisLocatorItem(Settings settings, boolean showTooltip) {
        super(settings, showTooltip);

    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putDouble(FUEL_KEY, 1000);

        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld)) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
            ItemStack stack = user.getStackInHand(hand);
            NbtCompound nbt = stack.getOrCreateNbt();
            Tardis tardis = getTardis(world, stack);
            if (tardis != null && nbt.getDouble(FUEL_KEY) >= 50) {
                if (!user.isSneaking()) {
                    boolean obfuscated = nbt.getDouble(FUEL_KEY) <= 250;
                    BlockPos blockPos = tardis.travel().position().getPos();
                    RegistryKey registryKey = tardis.travel().position().getDimension();
                    double nonAccuracy = locatorNonAccuracy(blockPos, user.getBlockPos());
                    String part0 = "( ";
                    String part1 = "Dimension: ";
                    String part2 = registryKey.getValue().getPath();
                    String part3 = " | X: ";
                    String part4 = String.valueOf(blockPos.getX() + ((int) (500 * (Math.random() - Math.random()) * nonAccuracy)));
                    String part5 = " | Y: ";
                    String part6 = String.valueOf(blockPos.getY() + ((int) (10 * (Math.random() - Math.random()) * nonAccuracy)));
                    String part7 = " | Z: ";
                    String part8 = String.valueOf(blockPos.getZ() + ((int) (500 * (Math.random() - Math.random()) * nonAccuracy)));
                    String part9 = " | Accuracy: ";
                    String part10 = (int) (100 * (4 - nonAccuracy)) / 4 + "%";
                    String part11 = " )";
                    Text text = Text
                            .literal(part0)
                            .formatted(Formatting.RED)
                            .append(Text.literal(part1).formatted(Formatting.BLUE)
                                    .append(Text.literal(part2).formatted(Formatting.YELLOW)
                                            .append(Text.literal(part3).formatted(Formatting.BLUE))
                                            .append(Text.literal(part4).formatted(Formatting.YELLOW).setStyle(Style.EMPTY.withObfuscated(obfuscated && Math.random() < 0.3)))
                                            .append(Text.literal(part5).formatted(Formatting.BLUE))
                                            .append(Text.literal(part6).formatted(Formatting.YELLOW).setStyle(Style.EMPTY.withObfuscated(obfuscated && Math.random() < 0.3)))
                                            .append(Text.literal(part7).formatted(Formatting.BLUE))
                                            .append(Text.literal(part8).formatted(Formatting.YELLOW).setStyle(Style.EMPTY.withObfuscated(obfuscated && Math.random() < 0.3)))
                                            .append(Text.literal(part9).formatted(Formatting.BLUE))
                                            .append(Text.literal(part10).formatted(Formatting.YELLOW))
                                            .append(Text.literal(part11).formatted(Formatting.RED))
                                    ));
                    user.sendMessage(text, true);
                    user.getItemCooldownManager().set(stack.getItem(), 350);
                    nbt.putDouble(FUEL_KEY, nbt.getDouble(FUEL_KEY) - 50);
                    world.playSound(null, user.getBlockPos(), AITSounds.BLIP, SoundCategory.PLAYERS, 0.75f, 1);
                    return TypedActionResult.pass(user.getStackInHand(hand));
                } else if (nbt.getDouble(FUEL_KEY) >= 500) {
                    boolean obfuscated = nbt.getDouble(FUEL_KEY) <= 250;
                    BlockPos blockPos = tardis.travel().position().getPos();
                    RegistryKey registryKey = tardis.travel().position().getDimension();
                    double nonAccuracy = locatorNonAccuracy(blockPos, user.getBlockPos());
                    String part0 = "( ";
                    String part1 = "Dimension: ";
                    String part2 = registryKey.getValue().getPath();
                    String part3 = " | X: ";
                    String part4 = String.valueOf(blockPos.getX());
                    String part5 = " | Y: ";
                    String part6 = String.valueOf(blockPos.getY());
                    String part7 = " | Z: ";
                    String part8 = String.valueOf(blockPos.getZ());
                    String part9 = " | Accuracy: ";
                    String part10 = "1000%";
                    String part11 = " )";
                    Text text = Text
                            .literal(part0)
                            .formatted(Formatting.RED)
                            .append(Text.literal(part1).formatted(Formatting.BLUE)
                                    .append(Text.literal(part2).formatted(Formatting.YELLOW)
                                            .append(Text.literal(part3).formatted(Formatting.BLUE))
                                            .append(Text.literal(part4).formatted(Formatting.YELLOW))
                                            .append(Text.literal(part5).formatted(Formatting.BLUE))
                                            .append(Text.literal(part6).formatted(Formatting.YELLOW))
                                            .append(Text.literal(part7).formatted(Formatting.BLUE))
                                            .append(Text.literal(part8).formatted(Formatting.YELLOW))
                                            .append(Text.literal(part9).formatted(Formatting.BLUE))
                                            .append(Text.literal(part10).setStyle(Style.EMPTY.withBold(true)).formatted(Formatting.YELLOW))
                                            .append(Text.literal(part11).formatted(Formatting.RED))
                                    ));
                    user.sendMessage(text, true);
                    user.getItemCooldownManager().set(stack.getItem(), 350);
                    nbt.putDouble(FUEL_KEY, nbt.getDouble(FUEL_KEY)-500);
                    world.playSound(null, user.getBlockPos(), AITSounds.BLIP, SoundCategory.PLAYERS, 0.75f, 1);
                    return TypedActionResult.pass(user.getStackInHand(hand));
                } else {
                    user.sendMessage(Text.literal("Not Enough AU").formatted(Formatting.RED)
                            .append(Text.literal("( ").formatted(Formatting.BLUE))
                            .append(Text.literal(nbt.getDouble(FUEL_KEY) + " / 500")).formatted(Formatting.YELLOW)
                            .append(Text.literal(" )").formatted(Formatting.BLUE))

                            , true);
                    world.playSound(null, user.getBlockPos(), AITSounds.BLIP_FAIL, SoundCategory.PLAYERS, 0.75f, 1);
                    return TypedActionResult.fail(user.getStackInHand(hand));
                }
            } else if (tardis != null) {
                user.sendMessage(Text.literal("BATTERY DEAD").setStyle(Style.EMPTY.withBold(true)).formatted(Formatting.RED), true);
                world.playSound(null, user.getBlockPos(), AITSounds.BLIP_FAIL, SoundCategory.PLAYERS, 0.75f, 1);
                return TypedActionResult.fail(user.getStackInHand(hand));
            } else {
                user.sendMessage(Text.literal("TARDIS not linked!").formatted(Formatting.YELLOW), true);
                world.playSound(null, user.getBlockPos(), AITSounds.BLIP_FAIL, SoundCategory.PLAYERS, 0.75f, 1);
                return TypedActionResult.fail(user.getStackInHand(hand));
            }

    }

    private double locatorNonAccuracy(BlockPos tardisPos, BlockPos playerPos) {
        double xDiff = Math.abs(playerPos.getX() - tardisPos.getX());
        double zDiff = Math.abs(playerPos.getZ() - tardisPos.getZ());
        double totDiff = MathHelper.sqrt((float) (xDiff*xDiff+zDiff*zDiff));

        if (totDiff > 5000)
            return 3;


        if (totDiff > 2000)
            return 1;

        if (totDiff > 1000)
            return 0.75;

        if (totDiff > 500)
            return 0.5;

        if (totDiff > 200)
            return 0.25;


        return 0;
    }

    @Override
    public double getMaxFuel(ItemStack stack) {
        return 1000;
    }

    @Override
    public String getFuelKey() {
        return FUEL_KEY;
    }

    private static final Text TEXT_ARTRON = Text.translatable("message.ait.tooltips.artron_units").formatted(Formatting.BLUE);
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int fuel = (int) Math.round(this.getCurrentFuel(stack));
        boolean acceptableFuel = this.getCurrentFuel(stack) > this.getMaxFuel(stack) / 4;

        tooltip.add(TEXT_ARTRON.copy().append(Text.literal(String.valueOf(fuel))
                .formatted(acceptableFuel ? Formatting.GREEN : Formatting.RED)));

        super.appendTooltip(stack, world, tooltip, context);
    }
}
