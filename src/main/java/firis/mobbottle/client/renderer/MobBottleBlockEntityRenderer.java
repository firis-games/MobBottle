package firis.mobbottle.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public class MobBottleBlockEntityRenderer implements BlockEntityRenderer<MobBottleBlockEntity> {

	protected BlockEntityRendererProvider.Context context;

	public MobBottleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
	}
	
	@Override
	public void render(MobBottleBlockEntity blockEntity, float p_112308_, PoseStack poseStack,
			MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
		
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
	        
	        Quaternionf quaternion = direction.getRotation();	        
	        quaternion.mul(new Quaternionf().fromAxisAngleDeg(1, 0, 0, -90f));
	        quaternion.mul(new Quaternionf().fromAxisAngleDeg(0, 1, 0, 180f));
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
        		poseStack, p_112310_, p_112311_, p_112312_, 
        		net.neoforged.neoforge.client.model.data.ModelData.EMPTY, 
        		null);
        
        poseStack.popPose();
    }
}
