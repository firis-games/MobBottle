package firis.mobbottle;

import com.mojang.logging.LogUtils;
import firis.mobbottle.block.MobBottleBlock;
import firis.mobbottle.block.MobBottleEmptyBlock;
import firis.mobbottle.block.entity.MobBottleBlockEntity;
import firis.mobbottle.client.renderer.MobBottleBlockEntityRenderer;
import firis.mobbottle.client.renderer.MobBottleBlockEntitySpecialModelRenderer;
import firis.mobbottle.component.MobBottleMobData;
import firis.mobbottle.item.MobBottleBlockItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(MobBottle.MODID)
public class MobBottle {
	public static final String MODID = "mobbottle";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * ブロック参照用定義
     */
	public static class FirisBlocks {
		public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

		public static final DeferredBlock<Block> MOB_BOTTLE = BLOCKS.register("mob_bottle", () ->
				new MobBottleBlock(
						MobBottleBlock.PROPERTIES.setId(
								ResourceKey.create(Registries.BLOCK,
										ResourceLocation.fromNamespaceAndPath(MODID, "mob_bottle")))));
	    public static final DeferredBlock<Block> MOB_BOTTLE_EMPTY = BLOCKS.register("mob_bottle_empty", () ->
				new MobBottleEmptyBlock(
						MobBottleBlock.PROPERTIES.setId(
								ResourceKey.create(Registries.BLOCK,
										ResourceLocation.fromNamespaceAndPath(MODID, "mob_bottle_empty")))));
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

	    public static final Supplier<BlockEntityType<MobBottleBlockEntity>> BLOCK_ENTITY_TYPE = REGISTER.register("mob_bottle_be",
				() -> new BlockEntityType<>(MobBottleBlockEntity::new, FirisBlocks.MOB_BOTTLE.get()));
	}

	/**
	 * DataComponentType参照用定義
	 */
	public static class FirisDataComponentType {
		public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

		public static final Supplier<DataComponentType<MobBottleMobData>> MOBBOTTLE_TYPE = REGISTRAR.registerComponentType(
				"mob_data_type",
				builder -> builder
						.persistent(MobBottleMobData.CODEC)
						.networkSynchronized(MobBottleMobData.STREAM_CODEC)
		);
	}

	/**
	 * 各種イベント登録
	 */
    public MobBottle(IEventBus modEventBus) {

		//共通処理
        modEventBus.addListener(this::commonSetup);
		//クリエイティブタブ登録
		modEventBus.addListener(this::CreativeModeTabEventBuildContents);

        //関連オブジェクト登録
        FirisBlocks.BLOCKS.register(modEventBus);
        FirisItems.ITEMS.register(modEventBus);
		FirisBlockEntityType.REGISTER.register(modEventBus);
		FirisDataComponentType.REGISTRAR.register(modEventBus);

	}

    private void commonSetup(final FMLCommonSetupEvent event) {
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

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
    	@SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }

		/**
		 * ブロック描画系登録イベント
		 */
		@SubscribeEvent
		public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
			//BER登録
			event.registerBlockEntityRenderer(
					FirisBlockEntityType.BLOCK_ENTITY_TYPE.get(),
					MobBottleBlockEntityRenderer::new
			);
		}

		/**
		 * アイテム描画イベント登録
		 * @param event
		 */
		@SubscribeEvent
		public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
			event.register(
					ResourceLocation.fromNamespaceAndPath(MobBottle.MODID, "mobbottle_special"),
					MobBottleBlockEntitySpecialModelRenderer.Unbaked.MAP_CODEC
			);
		}
    }
}
