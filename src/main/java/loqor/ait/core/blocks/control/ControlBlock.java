package loqor.ait.core.blocks.control;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import loqor.ait.core.blockentities.control.ControlBlockEntity;
import loqor.ait.core.blocks.types.HorizontalDirectionalBlock;
import loqor.ait.core.item.SonicItem;
import loqor.ait.core.item.control.ControlBlockItem;

public abstract class ControlBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    public ControlBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Item asItem() {
        return null; // this.getItem();
    }

    // protected abstract ControlBlockItem getItem();

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        Optional<String> id = ControlBlockItem.findControlId(itemStack);

        if (id.isEmpty())
            return;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ControlBlockEntity))
            return;

        ((ControlBlockEntity) be).setControl(id.get());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof ControlBlockEntity be)) return ActionResult.FAIL;

        if (isHoldingScanningSonic(player)) sendSonicMessage((ServerPlayerEntity) player, be);

        boolean success = be.run((ServerPlayerEntity) player, false);

        return (success) ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient()) return;

        if (!(world.getBlockEntity(pos) instanceof ControlBlockEntity be)) return;

        if (isHoldingScanningSonic(player)) sendSonicMessage((ServerPlayerEntity) player, be);

        be.run((ServerPlayerEntity) player, true);

        return;
    }

    protected static boolean isHoldingScanningSonic(PlayerEntity player) {
        return SonicItem.findPreviousMode(player.getMainHandStack()) == SonicItem.Mode.SCANNING;
    }
    protected static void sendSonicMessage(ServerPlayerEntity player, ControlBlockEntity entity) {
        player.sendMessage(Text.literal(entity.getControl().getId().toUpperCase()).formatted(Formatting.AQUA));
    }
}
