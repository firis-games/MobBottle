package firis.mobbottle.client.tesr;

import org.lwjgl.opengl.GL11;

import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.client.teisr.FTileMobBottleItemStackRenderer;
import firis.mobbottle.common.config.FirisConfig;
import firis.mobbottle.common.tileentity.FTileEntityMobBottle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * モブボトルRenderer
 */
public class FTileMobBottleSpRenderer extends TileEntitySpecialRenderer<FTileEntityMobBottle> {
	
	/**
	 * ボトルカバー定義
	 */
	private enum EnumBottleCoverType {
		MOB_BOTTLE(0, FirisBlocks.MOB_BOTTLE_EMPTY.getDefaultState(), true, 0),
		IRON_PLATE(1, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState(), false, 2),
		GOLD_PLATE(2, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState(), false, 2),
		STONE_PLATE(3, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), false, 2),
		WOOD_PLATE(4, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), false, 2),
		EMPTY(5, null, false, 0);
		
		//ID
		private int id;
		//描画用Block情報
		private IBlockState state;
		//透過ブロックフラグ
		private boolean isAlpha;
		//描画Entityの位置(0-16で設定して初期化時にtranslate設定値へ変換)
		private float entityPosition = 0;
		
		private EnumBottleCoverType(int id, IBlockState state, boolean isAlpha, int entityPosition) {
			this.id = id;
			this.state = state;
			this.isAlpha = isAlpha;
			this.entityPosition = ((float)entityPosition) / 16.0F;
		}
		public int getId() {
			return this.id;
		}
		public IBlockState getBlockState() {
			return this.state;
		}
		public boolean getIsAlpha() {
			return this.isAlpha;
		}
		public float getEntityPosition() {
			return this.entityPosition;
		}
		public static EnumBottleCoverType getBottleCoverTypeFromId(int id) {
			for (EnumBottleCoverType type : EnumBottleCoverType.values()) {
				if (id == type.getId()) {
					return type;
				}
			}
			return MOB_BOTTLE;
		}
	}
	
	public FTileMobBottleSpRenderer(){
	}
	
	/**
	 * Renderer
	 */
	@Override
	public void render(FTileEntityMobBottle te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		//ブロックの中心へ
        GlStateManager.translate((float)x + 0.5F, (float)y, (float)z + 0.5F);
        this.doRender(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.popMatrix();
	}
	
	/**
	 * インスタントハウス描画
	 * @param te
	 * @param x
	 * @param y
	 * @param z
	 * @param partialTicks
	 * @param destroyStage
	 * @param alpha
	 */
	public void doRender(FTileEntityMobBottle te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		
		//描画設定を取得
		EnumBottleCoverType bottleCoverType = EnumBottleCoverType.getBottleCoverTypeFromId(FirisConfig.cfg_display_bottle_cover_type);

		//Mob描画
		EntityLiving renderEntityLiving = te.getRenderEntityLiving();
		if (renderEntityLiving != null) {
			this.doRenderEntityLiving(renderEntityLiving, bottleCoverType, te, x, y, z, partialTicks, destroyStage, alpha);
		}

		//ガラス瓶を描画する
		this.doRenderBlock(bottleCoverType, te, x, y, z, partialTicks, destroyStage, alpha);		
	}
	
	/**
	 * Mobを描画する
	 */
	private void doRenderEntityLiving(EntityLiving renderEntityLiving, EnumBottleCoverType bottleCoverType, FTileEntityMobBottle te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		
		GlStateManager.pushMatrix();
		
		//Entityの描画位置
		GlStateManager.translate(0.0F, bottleCoverType.getEntityPosition(), 0.0F);
		
		//向きにあわせて回転させる
		setGLFacingRotate(te.getFacing());
		
		float scale = FirisConfig.cfg_display_entity_default_scale;
		
		//サイズを調整する
		GlStateManager.scale(scale, scale, scale);
		
		//Mobごとのサイズ微調整
		AxisAlignedBB aabb = renderEntityLiving.getRenderBoundingBox();
		//基準値
		double base_x = (double) FirisConfig.cfg_display_entity_auto_resize_width;
		double base_y = (double) FirisConfig.cfg_display_entity_auto_resize_height;
		double base_z = (double) FirisConfig.cfg_display_entity_auto_resize_width;
		//描画範囲
		double aabb_x = Math.floor((aabb.maxX - aabb.minX) * 1000) / 1000;
		double aabb_y = Math.floor((aabb.maxY - aabb.minY) * 1000) / 1000;
		double aabb_z = Math.floor((aabb.maxZ - aabb.minZ) * 1000) / 1000;
		double entityScale = 1.0D;
		entityScale = Math.max(entityScale, aabb_x / base_x);
		entityScale = Math.max(entityScale, aabb_y / base_y);
		entityScale = Math.max(entityScale, aabb_z / base_z);
		entityScale = 1.0D /entityScale;

		GlStateManager.scale(entityScale, entityScale, entityScale);

		
		//ぷるぷる震える対策
		partialTicks = 0;
		
		Minecraft.getMinecraft().getRenderManager().renderEntity(renderEntityLiving, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);

		//これを設定しないと描画が暗くなる場合がある
      	GlStateManager.enableRescaleNormal();
      	
      	//ItemStackRendererの場合のみライトマップの制御
      	if (te.getItemStackRenderer()) {
      		TransformType tfType = FTileMobBottleItemStackRenderer.bakedModelMobBottleInstance.getTransform();
      		if (TransformType.GROUND.equals(tfType)) {
          		//GUIで明るくなる対策
      			//ライトマップを有効化
    	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    	        GlStateManager.enableTexture2D();
    	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      		} else if (TransformType.GUI.equals(tfType)) {
          		//GUIで暗くなる対策
      			//ライトマップを無効化
    	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    	        GlStateManager.disableTexture2D();
    	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      		}
      	}
      	
		GlStateManager.popMatrix();

	}
	
	/**
	 * ブロックの描画
	 */
	private void doRenderBlock(EnumBottleCoverType bottleCoverType, FTileEntityMobBottle te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		
		//Emptyの場合は描画しない
		if (bottleCoverType.getBlockState() == null) return;
		
		GlStateManager.pushMatrix();
		
		GlStateManager.rotate(90, 0.0F, 1.0F, 0.0F);
		
		//位置調整
		GlStateManager.translate(-0.5F, 0.0F, 0.5F);

		//ブロック透過設定
		if (bottleCoverType.getIsAlpha()) {
			//半透明のブロックを描画する際に利用する
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		//テクスチャバインド
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		
		//ブロック描画
		Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(
				bottleCoverType.getBlockState(), 1.0F);
		
		GlStateManager.popMatrix();
	}
	
	/**
	 * 方角に応じて回転する
	 */
	private static void setGLFacingRotate(EnumFacing facing) {
		switch (facing) {
			case NORTH:
				break;
			case WEST:
				GlStateManager.rotate(90, 0.0F, 1.0F, 0.0F);
				break;
			case SOUTH:
				GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
				break;
			case EAST:
				GlStateManager.rotate(270, 0.0F, 1.0F, 0.0F);
				break;
			default:
				break;
		}
	}
	
}
