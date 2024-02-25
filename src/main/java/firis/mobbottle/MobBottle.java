package firis.mobbottle;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import firis.mobbottle.client.renderer.MobBottleBlockEntityRenderer;
import firis.mobbottle.common.block.MobBottleBlock;
import firis.mobbottle.common.block.MobBottleEmptyBlock;
import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import firis.mobbottle.common.item.MobBottleBlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(MobBottle.MODID)
public class MobBottle
{
	public static final String MODID = "mobbottle";
	
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * ブロック参照用定義
     */
	public static class FirisBlocks {
		public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

		public static final DeferredBlock<Block> MOB_BOTTLE = BLOCKS.register("mob_bottle", () -> new MobBottleBlock(MobBottleBlock.PROPERTIES));
	    public static final DeferredBlock<Block> MOB_BOTTLE_EMPTY = BLOCKS.register("mob_bottle_empty", () -> new MobBottleEmptyBlock(MobBottleBlock.PROPERTIES));
	}
	/**
     * アイテム参照用定義
     */
	public static class FirisItems {
		public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

		public static final DeferredItem<BlockItem> MOB_BOTTLE = ITEMS.register("mob_bottle", () -> new MobBottleBlockItem(FirisBlocks.MOB_BOTTLE.get()));
		public static final DeferredItem<BlockItem> MOB_BOTTLE_EMPTY = ITEMS.registerSimpleBlockItem("mob_bottle_empty", FirisBlocks.MOB_BOTTLE_EMPTY);
	}
	/**
	 * BlockEntityType参照用定義
	 */
	public static class FirisBlockEntityType {
		public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

	    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MobBottleBlockEntity>> BLOCK_ENTITY_TYPE = REGISTER.register("mob_bottle_be", () -> BlockEntityType.Builder.of(MobBottleBlockEntity::new, FirisBlocks.MOB_BOTTLE.get()).build(null));
	}	
	
	/**
	 * 各種イベント登録
	 */
    public MobBottle(IEventBus modEventBus) {

		// Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // 関連オブジェクト登録
        FirisBlocks.BLOCKS.register(modEventBus);
        FirisItems.ITEMS.register(modEventBus);
		FirisBlockEntityType.REGISTER.register(modEventBus);

		// クリエイティブタブ
		modEventBus.addListener(this::CreativeModeTabEventBuildContents);

		// Renderer登録
		if (FMLEnvironment.dist == Dist.CLIENT) {
			modEventBus.addListener(this::onRegisterRenderers);
		}
	}
    
    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }
    
    /**
     * クリエイティブタブ登録イベント
     */
	private void CreativeModeTabEventBuildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(FirisBlocks.MOB_BOTTLE_EMPTY.get());
			event.accept(FirisBlocks.MOB_BOTTLE.get());
		}
	}

	/**
	 * 描画系登録イベント
	 * @param event
	 */
	@OnlyIn(Dist.CLIENT)
	public void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		//BER登録
		event.registerBlockEntityRenderer(
				FirisBlockEntityType.BLOCK_ENTITY_TYPE.get(),
				MobBottleBlockEntityRenderer::new
		);
	}

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
    	@SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
