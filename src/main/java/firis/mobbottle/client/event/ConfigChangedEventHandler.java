package firis.mobbottle.client.event;

import firis.mobbottle.MobBottle;
import firis.mobbottle.common.config.FirisConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigChangedEventHandler {

	/**
	 * Config変更後の処理
	 */
	@SubscribeEvent
	public static void onPostConfigChangedEvent(ConfigChangedEvent.PostConfigChangedEvent event) {
		if (MobBottle.MODID.equals(event.getModID())) {
			//設定値の更新
			FirisConfig.syncConfig();
		}
	}
}
