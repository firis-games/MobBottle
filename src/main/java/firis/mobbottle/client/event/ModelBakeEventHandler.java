package firis.mobbottle.client.event;

import firis.mobbottle.MobBottle;
import firis.mobbottle.client.model.BakedModelMobBottle;
import firis.mobbottle.client.teisr.FTileMobBottleItemStackRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModelBakeEventHandler {

	/**
	 * Item描画でTESRを利用する設定
	 * @param event
	 */
	@SubscribeEvent
	public static void onModelBakeEvent(ModelBakeEvent event) {
		
		//BakedModel取得
		ModelResourceLocation mrlMobBottle = new ModelResourceLocation(MobBottle.MODID + ":mob_bottle", "inventory");
		IBakedModel modelMobBottle = event.getModelRegistry().getObject(mrlMobBottle);
		
		//BakedModelMobBottleインスタンスを保持
		FTileMobBottleItemStackRenderer.bakedModelMobBottleInstance = new BakedModelMobBottle(modelMobBottle);
		
		//BakedModelの差し替え
		event.getModelRegistry().putObject(mrlMobBottle, FTileMobBottleItemStackRenderer.bakedModelMobBottleInstance);
		
	}
	
	
}
