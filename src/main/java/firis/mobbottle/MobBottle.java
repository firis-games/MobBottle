package firis.mobbottle;

import org.apache.logging.log4j.Logger;

import firis.mobbottle.common.proxy.IProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
		modid = MobBottle.MODID, 
		name = MobBottle.NAME,
		version = MobBottle.VERSION,
		dependencies = MobBottle.MOD_DEPENDENCIES,
		acceptedMinecraftVersions = MobBottle.MOD_ACCEPTED_MINECRAFT_VERSIONS
)
@EventBusSubscriber
public class MobBottle
{
    public static final String MODID = "mobbottle";
    public static final String NAME = "Mob Bottle";
    public static final String VERSION = "0.1";
    public static final String MOD_DEPENDENCIES = "required-after:forge@[1.12.2-14.23.5.2768,);after:jei@[1.12.2-4.13.1.220,)";
    public static final String MOD_ACCEPTED_MINECRAFT_VERSIONS = "[1.12.2]";
    
    public static Logger logger;
    
    @Instance(MobBottle.MODID)
    public static MobBottle INSTANCE;
    
    @SidedProxy(serverSide = "firis.mobbottle.common.proxy.CommonProxy", 
    		clientSide = "firis.mobbottle.client.proxy.ClientProxy")
    public static IProxy proxy;
    
    /**
     * アイテムインスタンス保持用
     */
    @ObjectHolder(MobBottle.MODID)
    public static class FirisItems {
    	public final static Item MOB_BOTTLE = null;
    }
    
    /**
     * ブロックインスタンス保持用
     */
    @ObjectHolder(MobBottle.MODID)
    public static class FirisBlocks {
    	public final static Block MOB_BOTTLE = null;
    }
    
    /**
     * FMLPreInitializationEvent
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        
        logger.info(MobBottle.NAME + " Starting...");
        
    	//Event登録
    	proxy.registerEvent();
    }
    
    /**
     * FMLInitializationEvent
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
    
    /**
     * FMLPostInitializationEvent
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
    
    /**
     * ブロックを登録するイベント
     */
    @SubscribeEvent
    protected static void registerBlocks(RegistryEvent.Register<Block> event) {
    }
    
    /**
     * アイテムを登録するイベント
     */
    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event) {
    }
    
    /**
     * モデル登録イベント
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    protected static void registerModels(ModelRegistryEvent event) {
    }
}
