package firis.mobbottle.client.proxy;

import firis.mobbottle.client.event.ConfigChangedEventHandler;
import firis.mobbottle.common.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy {
	@Override
	public void registerEvent() {
		
		//GuiConfig更新イベント登録
		MinecraftForge.EVENT_BUS.register(ConfigChangedEventHandler.class);
		
	}
}
