package firis.mobbottle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firis.mobbottle.client.renderer.MobBottleBlockEntityRenderer;
import firis.mobbottle.common.block.MobBottleBlock;
import firis.mobbottle.common.block.MobBottleEmptyBlock;
import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import firis.mobbottle.common.item.MobBottleBlockItem;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

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
	public static class FirisBlocks {
	    public static final RegistryObject<Block> MOB_BOTTLE = RegistryObject.create(new ResourceLocation(MODID, "mob_bottle"), ForgeRegistries.BLOCKS);
	    public static final RegistryObject<Block> MOB_BOTTLE_EMPTY = RegistryObject.create(new ResourceLocation(MODID, "mob_bottle_empty"), ForgeRegistries.BLOCKS);		
	}
	/**
     * アイテム参照用定義
     */
	public static class FirisItems {
		public static final RegistryObject<Item> MOB_BOTTLE = RegistryObject.create(new ResourceLocation(MODID, "mob_bottle"), ForgeRegistries.ITEMS);
		public static final RegistryObject<Item> MOB_BOTTLE_EMPTY = RegistryObject.create(new ResourceLocation(MODID, "mob_bottle"), ForgeRegistries.ITEMS);
	}
	/**
	 * BlockEntityType参照用定義
	 */
	public static class FirisBlockEntityType {
	    public static final RegistryObject<BlockEntityType<MobBottleBlockEntity>> BLOCK_ENTITY_TYPE = RegistryObject.create(new ResourceLocation(MODID, "mob_bottle_be"), ForgeRegistries.BLOCK_ENTITIES);
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
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	
    	/**
    	 * ブロック登録イベント
    	 */
        @SubscribeEvent
        public static void onBlocksRegistry(RegisterEvent event) {
        	event.register(ForgeRegistries.Keys.BLOCKS,
    			helper -> {
    				//モブボトル登録
    				helper.register(new ResourceLocation(MODID, "mob_bottle"), 
    						new MobBottleBlock());
    				//空のモブボトル登録
    				helper.register(new ResourceLocation(MODID, "mob_bottle_empty"), 
    						new MobBottleEmptyBlock());
    			}
        	);
        }
        
        /**
         * アイテム登録イベント
         * @param event
         */
        @SubscribeEvent
        public static void onItemsRegistry(RegisterEvent event) {
        	event.register(ForgeRegistries.Keys.ITEMS,
    			helper -> {
    				//モブボトル登録
    				helper.register(new ResourceLocation(MODID, "mob_bottle"), 
    	        			new MobBottleBlockItem(FirisBlocks.MOB_BOTTLE.get()));
    				
    				//空のモブボトル登録
    				helper.register(new ResourceLocation(MODID, "mob_bottle_empty"), 
    	        			new BlockItem(FirisBlocks.MOB_BOTTLE_EMPTY.get(), 
    	                			(new Item.Properties()).stacksTo(64).tab(CreativeModeTab.TAB_TOOLS)));
    			}
        	);
        }
        
        /**
    	 * BlockEntity登録イベント
    	 */
        @SubscribeEvent
        public static void onBlockEntityType(RegisterEvent event) {
        	event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES,
    			helper -> {
    				//モブボトル登録
    				helper.register(new ResourceLocation(MODID, "mob_bottle_be"), 
    	        			BlockEntityType.Builder.of(
    	                			MobBottleBlockEntity::new, 
    	                			FirisBlocks.MOB_BOTTLE.get()
    	                			).build(null));
    			}
        	);
        }
    }
    
    @Mod.EventBusSubscriber(modid=MobBottle.MODID, value=Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientRegistryEvents {
    	
    	/**
    	 * 描画系登録イベント
    	 * @param event
    	 */
    	@SubscribeEvent
    	public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    		
    		//対象ブロックのテクスチャを透過する設定
    		ItemBlockRenderTypes.setRenderLayer(FirisBlocks.MOB_BOTTLE.get(), RenderType.translucent());
    		ItemBlockRenderTypes.setRenderLayer(FirisBlocks.MOB_BOTTLE_EMPTY.get(), RenderType.translucent());
    		
    		//BER登録
    		event.registerBlockEntityRenderer(
    				FirisBlockEntityType.BLOCK_ENTITY_TYPE.get(),
    				MobBottleBlockEntityRenderer::new
    		);
    	}
    }
}
