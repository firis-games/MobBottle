package firis.mobbottle.client.tesr;

import firis.mobbottle.common.config.FirisConfig;
import firis.mobbottle.common.tileentity.FTileEntityMobBottle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * モブボトルRenderer
 */
public class FTileMobBottleSpRenderer extends TileEntitySpecialRenderer<FTileEntityMobBottle> {
	
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
		
		EntityLiving renderEntityLiving = te.getRenderEntityLiving();
		if (renderEntityLiving == null) return;
		
		//向きにあわせて回転させる
		switch (te.getFacing()) {
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
		
	}
	
}
