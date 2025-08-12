package firis.mobbottle.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.block.entity.MobBottleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/***
 * モブボトルアイテム描画
 */
@OnlyIn(Dist.CLIENT)
public class MobBottleBlockEntitySpecialModelRenderer implements SpecialModelRenderer<ItemStack> {

	//描画用のBlockEntity
	public MobBottleBlockEntity blockEntity = null;

	/***
	 * 描画用パラメータ取得
	 */
	@Nullable
	public ItemStack extractArgument(ItemStack stack) {
		return stack;
	}

	/**
	 * アイテム描画処理
	 */
	@Override
	public void render(@Nullable ItemStack stack, ItemDisplayContext displayContext, PoseStack pose, MultiBufferSource bufferSource, int light, int overlay, boolean hasFoil) {

		//描画用情報設定
		if (blockEntity == null) {
			blockEntity = new MobBottleBlockEntity(BlockPos.ZERO, FirisBlocks.MOB_BOTTLE.get().defaultBlockState());
		}
		blockEntity.setMobBottleDataFromBEWLR(stack);

		pose.pushPose();

		//描画モードごとに調整
		//GUI
		if (displayContext == ItemDisplayContext.GUI) {
			blockEntity.SetRendererDirection(Direction.WEST);

		//一人称右手
		} else if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
			blockEntity.SetRendererDirection(Direction.WEST);

		//一人称左手
		} else if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
			blockEntity.SetRendererDirection(Direction.WEST);

		//三人称右手
		} else if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
			blockEntity.SetRendererDirection(Direction.WEST);

		//三人称左手
		} else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
			blockEntity.SetRendererDirection(Direction.EAST);

		}

		//カメラ位置の取得
		Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

		//モブボトルブロック描画
		Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity)
				.render(blockEntity, 0, pose, bufferSource, light, overlay, cameraPos);

		pose.popPose();
	}

	/***
	 * 描画処理定義
	 */
	public record Unbaked() implements SpecialModelRenderer.Unbaked {

		public static final MapCodec<MobBottleBlockEntitySpecialModelRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new MobBottleBlockEntitySpecialModelRenderer.Unbaked());

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
			return new MobBottleBlockEntitySpecialModelRenderer();
		}
	}

}
