package firis.mobbottle.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class MobBottleBlockEntityRenderer extends TileEntityRenderer<MobBottleBlockEntity> {

	public MobBottleBlockEntityRenderer(TileEntityRendererDispatcher p_i226006_1_) {
		super(p_i226006_1_);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(MobBottleBlockEntity blockEntity, float p_225616_2_, MatrixStack poseStack,
			IRenderTypeBuffer p_112310_, int p_112311_, int p_112312_) {
		
		//Mobの描画
		Entity entity = blockEntity.getRenderEntity();
		Direction direction = blockEntity.getRenderDirection();
		if (entity != null) {
			poseStack.pushPose();
			
			float sclae = blockEntity.getRenderScale();
			float positionY = blockEntity.getRenderPositionY();
			
			//位置とサイズと方角を設定
			poseStack.translate(0.5D, positionY, 0.5D);
	        poseStack.scale(sclae, sclae, sclae);
	        Quaternion quaternion = direction.getRotation();
	        quaternion.mul(Vector3f.XP.rotationDegrees(-90.0F));
	        quaternion.mul(Vector3f.YP.rotationDegrees(180.0F));
	        poseStack.mulPose(quaternion);
			
			Minecraft.getInstance().getEntityRenderDispatcher().render(
					entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 
					poseStack, p_112310_, p_112311_);
			
			poseStack.popPose();
		}
		
		//ブロックの描画
		poseStack.pushPose();
		BlockState state = blockEntity.getRenderBlockState();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
        		state, 
        		poseStack, p_112310_, p_112311_, p_112312_);
        
        poseStack.popPose();
	}
}
