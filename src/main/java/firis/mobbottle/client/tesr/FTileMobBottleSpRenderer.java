package firis.mobbottle.client.tesr;

import firis.mobbottle.common.tileentity.FTileEntityMobBottle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLiving;

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
		
		float scale = 0.45F;
		
		//サイズを調整する
		GlStateManager.scale(scale, scale, scale);
		
		//ぷるぷる震える対策
		partialTicks = 0;
		
		Minecraft.getMinecraft().getRenderManager().renderEntity(renderEntityLiving, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
		
	}
	
}
