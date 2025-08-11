package firis.mobbottle.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

/***
 * モブボトルブロック描画
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
		
		//Mobの描画
		Entity entity = blockEntity.getRenderEntity();
		Direction direction = blockEntity.getRenderDirection();
		if (entity != null) {
			poseStack.pushPose();
			
			float scale = blockEntity.getRenderScale();
			float positionY = blockEntity.getRenderPositionY();
			
			//位置とサイズと方角を設定
			poseStack.translate(0.5D, positionY, 0.5D);
	        poseStack.scale(scale, scale, scale);
	        
	        Quaternionf quaternion = direction.getRotation();	        
	        quaternion.mul(new Quaternionf().fromAxisAngleDeg(1, 0, 0, -90f));
	        quaternion.mul(new Quaternionf().fromAxisAngleDeg(0, 1, 0, 180f));
	        poseStack.mulPose(quaternion);

			Minecraft.getInstance().getEntityRenderDispatcher().render(
					entity, 0.0D, 0.0D, 0.0D, 0.0F, poseStack, bufferSource, packedLight);
			
			poseStack.popPose();
		}
		
		//ブロックの描画
		poseStack.pushPose();
		BlockState state = blockEntity.getRenderBlockState();
		
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
        		state, 
        		poseStack, bufferSource, packedLight, packedOverlay,
        		blockEntity.getLevel(),
        		null);
        
        poseStack.popPose();
    }
}
