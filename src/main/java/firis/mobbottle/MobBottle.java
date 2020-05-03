package firis.mobbottle;

import org.apache.logging.log4j.Logger;

import firis.mobbottle.client.teisr.FTileMobBottleItemStackRenderer;
import firis.mobbottle.client.tesr.FTileMobBottleSpRenderer;
import firis.mobbottle.common.block.FBlockMobBottle;
import firis.mobbottle.common.block.FBlockMobBottleEmpty;
import firis.mobbottle.common.config.FirisConfig;
import firis.mobbottle.common.entity.FEntityItemAntiDamage;
import firis.mobbottle.common.item.FItemMobBottle;
import firis.mobbottle.common.proxy.IProxy;
import firis.mobbottle.common.tileentity.FTileEntityMobBottle;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
		modid = MobBottle.MODID, 
		name = MobBottle.NAME,
		version = MobBottle.VERSION,
		dependencies = MobBottle.MOD_DEPENDENCIES,
		acceptedMinecraftVersions = MobBottle.MOD_ACCEPTED_MINECRAFT_VERSIONS,
		guiFactory = "firis.mobbottle.client.config.FirisConfigGuiFactory"
)
@EventBusSubscriber
public class MobBottle
{
    public static final String MODID = "mobbottle";
    public static final String NAME = "Mob Bottle";
    public static final String VERSION = "0.5";
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
    	public final static Item MOB_BOTTLE_EMPTY = null;
    }
    
    /**
     * ブロックインスタンス保持用
     */
    @ObjectHolder(MobBottle.MODID)
    public static class FirisBlocks {
    	public final static FBlockMobBottle MOB_BOTTLE = null;
    	public final static Block MOB_BOTTLE_EMPTY = null;
    }
    
    /**
     * FMLPreInitializationEvent
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        
        logger.info(MobBottle.NAME + " Starting...");
        
        //設定読込
        FirisConfig.init(event.getSuggestedConfigurationFile());
        
        //モブボトル
        GameRegistry.registerTileEntity(FTileEntityMobBottle.class, 
				new ResourceLocation(MobBottle.MODID, "firis_mob_bottle"));
        
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
    	
    	//モブボトル
    	event.getRegistry().register(new FBlockMobBottle()
    			.setRegistryName(MODID, "mob_bottle")
    			.setUnlocalizedName("mob_bottle"));
    	
    	//モブボトル（空）
    	event.getRegistry().register(new FBlockMobBottleEmpty()
    			.setRegistryName(MODID, "mob_bottle_empty")
    			.setUnlocalizedName("mob_bottle_empty"));
    	
    }
    
    /**
     * アイテムを登録するイベント
     */
    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event) {
    	
    	//モブボトル
    	event.getRegistry().register(new FItemMobBottle()
    			.setRegistryName(MODID, "mob_bottle")
    			.setUnlocalizedName("mob_bottle"));
    	
    	//モブボトル（空）
    	event.getRegistry().register(new ItemBlock(FirisBlocks.MOB_BOTTLE_EMPTY)
    			.setRegistryName(MODID, "mob_bottle_empty")
    			.setUnlocalizedName("mob_bottle_empty"));
    }
    
    /**
     * モデル登録イベント
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    protected static void registerModels(ModelRegistryEvent event) {
    	
    	//モブボトル
		ModelLoader.setCustomModelResourceLocation(FirisItems.MOB_BOTTLE, 0,
				new ModelResourceLocation(FirisItems.MOB_BOTTLE.getRegistryName(), "inventory"));
		
    	//モブボトル(空)
		ModelLoader.setCustomModelResourceLocation(FirisItems.MOB_BOTTLE_EMPTY, 0,
				new ModelResourceLocation(FirisItems.MOB_BOTTLE.getRegistryName(), "inventory"));
		
    	//モブボトルTESR
    	ClientRegistry.bindTileEntitySpecialRenderer(FTileEntityMobBottle.class, new FTileMobBottleSpRenderer());
    	
    	if (FirisConfig.cfg_general_enable_mob_bottle_teisr) {
	    	//モブボトルのTEISR登録
	    	FirisItems.MOB_BOTTLE.setTileEntityItemStackRenderer(new FTileMobBottleItemStackRenderer());
    	}
    }
    
    /**
     * Entity登録イベント
     */
    @SubscribeEvent
    protected static void registerEntitys(RegistryEvent.Register<EntityEntry> event) {
    	int entityId = 0;
    	EntityRegistry.registerModEntity(new ResourceLocation(MobBottle.MODID, "entityitem_antidamage"), 
    			FEntityItemAntiDamage.class, 
    			"Anti Dmage EntityItem", 
    			entityId, 
    			MobBottle.INSTANCE, 32, 5, true);
    }
    
    /**
     * サウンド登録イベント
     * @param event
     */
    @SubscribeEvent
    protected static void registerSoundEvent(RegistryEvent.Register<SoundEvent> event) {
    
    	if (FirisConfig.cfg_general_mob_bottle_glass_sound) return;
    	
    	//ボトルの破壊音イベント作成
    	SoundEvent soundEventBottle = new SoundEvent(new ResourceLocation(MobBottle.MODID, "bottle"));
    	soundEventBottle.setRegistryName(soundEventBottle.getSoundName());
    	
    	//サウンド登録
    	event.getRegistry().register(soundEventBottle);
    	
    	//サウンドタイプ生成
    	SoundType soundTypeBottle = new SoundType(0.5F, 1.0F, soundEventBottle, soundEventBottle, soundEventBottle, soundEventBottle, soundEventBottle);
    	
    	//モブボトルの破壊音設定
    	//ブロック登録のタイミングの前にこの処理が動かないため
    	//あとからSoundTypeを登録する
    	FirisBlocks.MOB_BOTTLE.setSoundType(soundTypeBottle);
    	
    }
}
