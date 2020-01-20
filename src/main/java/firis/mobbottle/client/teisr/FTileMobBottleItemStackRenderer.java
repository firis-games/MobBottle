package firis.mobbottle.client.teisr;

import firis.mobbottle.client.model.BakedModelMobBottle;
import firis.mobbottle.common.tileentity.FTileEntityMobBottle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

/**
 * モブボトルTileEntityItemStackRenderer
 *
 */
public class FTileMobBottleItemStackRenderer extends TileEntityItemStackRenderer {

	/**
	 * TransformTypeを描画時に取得するためインスタンスを保持する
	 */
	public static BakedModelMobBottle bakedModelMobBottleInstance = null;
	
	/**
	 * ItemStack描画用TileEntity
	 */
	private FTileEntityMobBottle tileMobBottle = new FTileEntityMobBottle();
	
	@Override
	public void renderByItem(ItemStack itemStack) {
		this.renderByItem(itemStack, 0.0F);
    }
	
	/**
	 * ItemStackの描画
	 */
	@Override
	public void renderByItem(ItemStack itemStack, float partialTicks) {
		
		GlStateManager.pushMatrix();
		
		//TESR描画
		this.tileMobBottle.initMobBottleItemStackRenderer(itemStack);
		TileEntityRendererDispatcher.instance.render(this.tileMobBottle, 0.0D, 0.0D, 0.0D, 0.0F);
		
		//GUI時のopenGL設定
		//Minecraft.getMinecraft().getRenderManager().renderEntityを利用していると
		//周りが暗い場合にGUI描画が青暗くなるためその対策
		if (TransformType.GUI.equals(bakedModelMobBottleInstance.getTransform())) {
			//GUIで暗くなる対策
	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GlStateManager.disableTexture2D();
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
        GlStateManager.popMatrix();
	}
}
