package firis.mobbottle.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MobBottleBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {

	//描画用のBlockEntity
	protected MobBottleBlockEntity blockEntity = null;
	
	public MobBottleBlockEntityWithoutLevelRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}
	
	@Override
	public void renderByItem(@Nonnull ItemStack stack, ItemDisplayContext itemDisplayContext, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int x, int y) {
		
		//描画用情報設定
		if (blockEntity == null) {
			blockEntity = new MobBottleBlockEntity(BlockPos.ZERO, FirisBlocks.MOB_BOTTLE.get().defaultBlockState());
		}
		blockEntity.setMobBottleDataFromBEWLR(stack);
		
		//描画
		Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, poseStack, buffer, x, y);
		
	}

}
