package firis.mobbottle.client.model;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * モブボトルのItemStack描画用
 *
 */
public class BakedModelMobBottle implements IBakedModel {
	
	protected IBakedModel baseModel;
	
	public BakedModelMobBottle(IBakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return this.baseModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return this.baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return this.baseModel.isGui3d();
	}

	/**
	 * TileEntityItemStackRendererを使う場合はtrueを設定する
	 */
	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return this.baseModel.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return this.baseModel.getOverrides();
	}

	/**
	 * 一人称・三人称・GUI描画などの描画位置を制御する
	 * nullが返却される場合は何も制御しない（default実装）
	 */
	@Override
	public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		transform = cameraTransformType;
        return Pair.of(this, transforms.get(cameraTransformType).getMatrix());
    }
	
	/**
	 * 一人称・三人称・GUI描画などの描画位置設定
	 */
    public static Map<TransformType, TRSRTransformation> transforms = ImmutableMap.<TransformType, TRSRTransformation>builder()
          .put(TransformType.GUI, get(0, 0, 0, 30, 225, 0, 0.625f))
          .put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0, 2.5f, 0, 75, 45, 0, 0.375f))
          .put(TransformType.THIRD_PERSON_LEFT_HAND, get(0, 2.5f, 0, 75, 45, 0, 0.375f))
          .put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f))
          .put(TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 0, 0, 225, 0, 0.4f))
          .put(TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.25f))
          .put(TransformType.HEAD, get(0, 0, 0, 0, 0, 0, 1))
          .put(TransformType.FIXED, get(0, 0, 0, 0, 0, 0, 1))
          .put(TransformType.NONE, get(0, 0, 0, 0, 0, 0, 0))
          .build();
    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16),
              TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null);
    }
    
	/**
	 * GUI描画時にopenGL制御をやるためTransformTypeを保持する
	 */
	private TransformType transform = TransformType.NONE;
    @Nonnull
    public TransformType getTransform() {
        return transform;
    }
}
