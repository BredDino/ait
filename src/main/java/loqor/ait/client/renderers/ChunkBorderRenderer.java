package loqor.ait.client.renderers;

import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColorHelper;

import loqor.ait.client.data.ClientLandingManager;
import loqor.ait.client.renderers.entities.ControlEntityRenderer;

public class ChunkBorderRenderer {
    private final MinecraftClient client;
    private static final int DARK_CYAN = ColorHelper.Argb.getArgb(255, 0, 155, 155);
    private static final int YELLOW = ColorHelper.Argb.getArgb(255, 255, 255, 0);

    public ChunkBorderRenderer(MinecraftClient client) {
        this.client = client;
    }

    public boolean shouldRender() {
        return ControlEntityRenderer.isPlayerHoldingScanningSonic() && ClientLandingManager.getInstance().getRegion(client.world, client.player.getBlockPos()).isPresent(); // TODO - constant calling of getRegion is extremely bad. work now, performance later :)
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        int k;
        int j;
        Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
        float f = (float)((double)this.client.world.getBottomY() - cameraY);
        float g = (float)((double)this.client.world.getTopY() - cameraY);
        ChunkPos chunkPos = entity.getChunkPos();
        float h = (float)((double)chunkPos.getStartX() - cameraX);
        float i = (float)((double)chunkPos.getStartZ() - cameraZ);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        for (j = 2; j < 16; j += 2) {
            k = DARK_CYAN;
            vertexConsumer.vertex(matrix4f, h + (float)j, f, i).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, f, i).color(k).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, g, i).color(k).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, g, i).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, f, i + 16.0f).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, f, i + 16.0f).color(k).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, g, i + 16.0f).color(k).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, g, i + 16.0f).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (j = 2; j < 16; j += 2) {
            k = DARK_CYAN;
            vertexConsumer.vertex(matrix4f, h, f, i + (float)j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h, f, i + (float)j).color(k).next();
            vertexConsumer.vertex(matrix4f, h, g, i + (float)j).color(k).next();
            vertexConsumer.vertex(matrix4f, h, g, i + (float)j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, f, i + (float)j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, f, i + (float)j).color(k).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, g, i + (float)j).color(k).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, g, i + (float)j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (j = this.client.world.getBottomY(); j <= this.client.world.getTopY(); j += 2) {
            float l = (float)((double)j - cameraY);
            int m = DARK_CYAN;
            vertexConsumer.vertex(matrix4f, h, l, i).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(m).next();
            vertexConsumer.vertex(matrix4f, h, l, i + 16.0f).color(m).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, l, i + 16.0f).color(m).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, l, i).color(m).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(m).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(2.0));
        for (j = 0; j <= 16; j += 16) {
            for (int k2 = 0; k2 <= 16; k2 += 16) {
                vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k2).color(0.25f, 0.25f, 1.0f, 0.0f).next();
                vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k2).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k2).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k2).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            }
        }
        for (j = this.client.world.getBottomY(); j <= this.client.world.getTopY(); j += 16) {
            float l = (float)((double)j - cameraY);
            vertexConsumer.vertex(matrix4f, h, l, i).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i + 16.0f).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, l, i + 16.0f).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, l, i).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(0.25f, 0.25f, 1.0f, 0.0f).next();
        }
    }

    public void tryRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (!this.shouldRender()) return;

        this.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

    private static ChunkBorderRenderer INSTANCE;
    public static ChunkBorderRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChunkBorderRenderer(MinecraftClient.getInstance());
        }
        return INSTANCE;
    }
}
