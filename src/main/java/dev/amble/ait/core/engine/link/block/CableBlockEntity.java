package dev.amble.ait.core.engine.link.block;

import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class CableBlockEntity extends FluidLinkBlockEntity {

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.CABLE_BLOCK_ENTITY_TYPE, pos, state);
    }
}
