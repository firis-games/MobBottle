package firis.mobbottle.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import firis.mobbottle.block.entity.MobBottleBlockEntity;
import firis.mobbottle.block.entity.MobBottleBlockEntityClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

/***
 * モブボトルブロック描画
 * @OnlyIn(Dist.CLIENT)
 */
public class MobBottleBlockEntityRenderer implements BlockEntityRenderer<MobBottleBlockEntity> {

    protected BlockEntityRendererProvider.Context context;

    public MobBottleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    /***
     * モブボトルブロック描画処理
     */
    @Override
    public void render(MobBottleBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 vec3) {

        MobBottleBlockEntityClient blockEntityClient = blockEntity.getClient();

        //Mobの描画
        Entity entity = blockEntityClient.getRenderEntity();
        Direction direction = blockEntityClient.getRenderDirection();
        if (entity != null) {
            poseStack.pushPose();

            float scale = blockEntityClient.getRenderScale();
            float positionY = blockEntityClient.getRenderPositionY();

            //位置とサイズと方角を設定
            poseStack.translate(0.5d, positionY, 0.5d);
            poseStack.scale(scale, scale, scale);

            Quaternionf quaternion = direction.getRotation();
            quaternion.mul(new Quaternionf().fromAxisAngleDeg(1, 0, 0, -90f));
            quaternion.mul(new Quaternionf().fromAxisAngleDeg(0, 1, 0, 180f));
            poseStack.mulPose(quaternion);

            Minecraft.getInstance().getEntityRenderDispatcher().render(
                    entity, 0.0d, 0.0d, 0.0d, 0.0f, poseStack, bufferSource, packedLight);

            poseStack.popPose();
        }

        //ブロックの描画
        poseStack.pushPose();
        BlockState state = blockEntityClient.getRenderBlockState();

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                state,
                poseStack, bufferSource, packedLight, packedOverlay,
                blockEntity.getLevel(),
                null);

        poseStack.popPose();
    }

    /***
     * 描画範囲の指定
     * 1ブロックより0.5広げる
     */
    @Override
    public AABB getRenderBoundingBox(MobBottleBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(0.5d);
    }

}
