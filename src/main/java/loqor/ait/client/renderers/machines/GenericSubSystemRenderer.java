package loqor.ait.client.renderers.machines;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

import loqor.ait.client.models.machines.GenericSubSystemModel;
import loqor.ait.client.renderers.MultiBlockStructureRenderer;
import loqor.ait.client.util.ClientLightUtil;
import loqor.ait.core.engine.StructureHolder;
import loqor.ait.core.engine.SubSystem;
import loqor.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
import net.minecraft.item.ItemStack;
import org.joml.Vector3f;

public class GenericSubSystemRenderer<T extends GenericStructureSystemBlockEntity> implements BlockEntityRenderer<T>, ClientLightUtil.Renderable<T> {
    private GenericSubSystemModel model;

    public GenericSubSystemRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new GenericSubSystemModel();
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        SubSystem system = entity.system();
        if (entity.hasSystem() && system != null && !system.isUsable() && system instanceof StructureHolder holder) {
            MultiBlockStructureRenderer.instance().render(holder.getStructure(), entity.getPos(), entity.getWorld(), matrices, vertexConsumers, true);
        }

        matrices.translate(0.5f, -0.5f, 0.5f);

        ItemStack stack = entity.getSourceStack().orElse(null);
        boolean hasStack = stack != null && !stack.isEmpty();

        ModelPart cube = this.model.getPart().getChild("cube_r3");
        cube.visible = !hasStack;

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(GenericSubSystemModel.TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        if (hasStack) {
            matrices.push();
            double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 18.0;

            // mm yes render the model twice
//            cube.visible = true;
//            this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(GenericSubSystemModel.TEXTURE)),
//                    light, overlay, 1.0F, 1.0F, 1.0F, (float) Math.abs(offset) + 0.1f);

            matrices.translate(0, 0.85f + (offset / 2), 0);

            Vector3f scale = MinecraftClient.getInstance().getItemRenderer().getModel(stack, entity.getWorld(), null, 0).getTransformation().firstPersonRightHand.scale;
            matrices.scale(0.7f, 0.7f, 0.7f);
            matrices.scale(scale.x, scale.y, scale.z);

            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light,
                    overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

        matrices.pop();
    }

    @Override
    public void render(T entity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
