package firis.mobbottle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firis.mobbottle.client.renderer.MobBottleBlockEntityRenderer;
import firis.mobbottle.common.block.MobBottleBlock;
import firis.mobbottle.common.block.MobBottleEmptyBlock;
import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import firis.mobbottle.common.item.MobBottleBlockItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("mobbottle")
public class MobBottle
{
	public static final String MODID = "mobbottle";
	
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
	
    /**
     * ブロック参照用定義
     */
    @ObjectHolder(MobBottle.MODID)
	public static class FirisBlocks {
		public final static Block MOB_BOTTLE = null;
    	public final static Block MOB_BOTTLE_EMPTY = null;
	}
	/**
     * アイテム参照用定義
     */
    @ObjectHolder(MobBottle.MODID)
	public static class FirisItems {
    	public final static Item MOB_BOTTLE = null;
    	public final static Item MOB_BOTTLE_EMPTY = null;
	}
	/**
	 * BlockEntityType参照用定義
	 */
    @ObjectHolder(MobBottle.MODID)
    public static class FirisBlockEntityType {
    	@ObjectHolder(MODID+":mob_bottle_be")
	    public static final TileEntityType<MobBottleBlockEntity> BLOCK_ENTITY_TYPE = null;
	}
	
	/**
	 * 各種イベント登録
	 */
    public MobBottle() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	
    	/**
    	 * ブロック登録イベント
    	 */
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        	
        	//モブボトル登録
            event.getRegistry().register(new MobBottleBlock()
            		.setRegistryName(MODID, "mob_bottle"));
            
            //空のモブボトル登録
            event.getRegistry().register(new MobBottleEmptyBlock()
            		.setRegistryName(MODID, "mob_bottle_empty"));
            
        }
        
        /**
         * アイテム登録イベント
         * @param event
         */
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        	
        	//モブボトル
        	event.getRegistry().register(new MobBottleBlockItem(FirisBlocks.MOB_BOTTLE)
        			.setRegistryName(MODID, "mob_bottle"));
        	
        	//空のモブボトル
        	event.getRegistry().register(new BlockItem(FirisBlocks.MOB_BOTTLE_EMPTY, 
        			(new Item.Properties()).stacksTo(64).tab(ItemGroup.TAB_TOOLS))
        			.setRegistryName(MODID, "mob_bottle_empty"));
        	
        }
        
        /**
    	 * BlockEntity登録イベント
    	 */
        @SubscribeEvent
        public static void onBlockEntityType(final RegistryEvent.Register<TileEntityType<?>> event) {
        	
        	//MobBottleBlockEntity登録
        	event.getRegistry().register(TileEntityType.Builder.of(
        			MobBottleBlockEntity::new, 
        			FirisBlocks.MOB_BOTTLE
        			).build(null).setRegistryName(MODID, "mob_bottle_be"));
        	
        }
    }
    
    @Mod.EventBusSubscriber(modid=MobBottle.MODID, value=Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientRegistryEvents {
    	
    	/**
    	 * 描画系登録イベント
    	 * @param event
    	 */
    	@SubscribeEvent
    	public static void onRegisterRenderers(FMLClientSetupEvent event) {
    		
    		//対象ブロックのテクスチャを透過する設定
    		RenderTypeLookup.setRenderLayer(FirisBlocks.MOB_BOTTLE, RenderType.translucent());
    		RenderTypeLookup.setRenderLayer(FirisBlocks.MOB_BOTTLE_EMPTY, RenderType.translucent());
    		
    		//BER登録
    		ClientRegistry.bindTileEntityRenderer(
    				FirisBlockEntityType.BLOCK_ENTITY_TYPE,
    				MobBottleBlockEntityRenderer::new
    		);
    	}
    }
}
