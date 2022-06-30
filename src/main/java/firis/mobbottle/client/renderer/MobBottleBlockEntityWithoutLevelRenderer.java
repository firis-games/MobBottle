package firis.mobbottle.client.renderer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobBottleBlockEntityWithoutLevelRenderer extends ItemStackTileEntityRenderer {

	//描画用のBlockEntity
	protected MobBottleBlockEntity blockEntity = null;
	
	public MobBottleBlockEntityWithoutLevelRenderer() {
		super();
	}
	
	@Override
	public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemCameraTransforms.TransformType transformType, @Nonnull MatrixStack poseStack, @Nonnull IRenderTypeBuffer buffer, int x, int y) {
		
		//描画用情報設定
		if (blockEntity == null) {
			blockEntity = new MobBottleBlockEntity();
		}
		blockEntity.setMobBottleDataFromBEWLR(stack);
		
		//描画
		TileEntityRendererDispatcher.instance.renderItem(blockEntity, poseStack, buffer, x, y);
		
	}

}
