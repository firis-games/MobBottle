package firis.mobbottle.common.config;

import java.io.File;

import firis.mobbottle.MobBottle;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class FirisConfig {

	public static Configuration config;
	
	public static String CATEGORY_GENERAL = "General";
	public static String CATEGORY_DISPLAY = "Display";
	
	/**
	 * モブボトルのブロック設置
	 */
	public static boolean cfg_general_enable_mob_bottle_blocks = true;
	
	/**
	 * モブボトルのアニメーション設定
	 */
	public static boolean cfg_general_enable_mob_bottle_animation = true;
	
	/**
	 * モブボトルのTileEntityItemStackRenderer設定
	 */
	public static boolean cfg_general_enable_mob_bottle_teisr = true;
	
	/**
	 * Entity描画の標準倍率
	 */
	public static float cfg_display_entity_default_scale = 0.45F;
	
	/**
	 * Entityリサイズの基準値
	 */
	public static float cfg_display_entity_auto_resize_width = 0.8F;
	public static float cfg_display_entity_auto_resize_height = 1.9F;
	
	/**
	 * モブボトルのカバータイプ
	 */
	public static int cfg_display_bottle_cover_type = 0;
	
	/**
	 * モブボトルの1回あたりの拡大縮小のサイズ
	 */
	public static float cfg_display_scale_up_down_value = 0.1F;
	
	/**
	 * カバータイプ用ツール
	 */
	public static String cfg_display_tool_cover_type = "";
	/**
	 * 拡大用ツール
	 */
	public static String cfg_display_tool_scale_up = "";
	/**
	 * 縮小用ツール
	 */
	public static String cfg_display_tool_scale_down = "";
	/**
	 * 回転用ツール
	 */
	public static String cfg_display_tool_rotation = "";
	
	
	/**
	 * モブボトルのTileEntityItemStackRenderer設定
	 */
	public static boolean cfg_general_enable_lmrfp_collaboration = false;
	
	/**
	 * モブボトルの当たり判定タイプ
	 */
	public static int cfg_general_mob_bottle_collision_type = 0;
	
	/**
	 * モブボトルのボス捕獲判定
	 */
	public static boolean cfg_general_mob_bottle_capture_boss = false;
	
	
	public static void init(File configFile) {
		
		config = new Configuration(configFile, MobBottle.VERSION, true);
		
		//カテゴリーコメントの設定
		initCategory();
		
		//設定値の同期
		syncConfig();
		
	}
	
	/**
	 * カテゴリーのコメント設定
	 */
	protected static void initCategory() {
		
		config.addCustomCategoryComment("General", "Make the basic settings for the Mob Bottle.");
		
		config.addCustomCategoryComment("Display", "Configure the display settings of the Mob Bottle block Entity.");
		
	}
	
	/**
	 * Config値の同期処理
	 */
	public static void syncConfig() {
		
		//General
		//Blockモードの有効化設定
		cfg_general_enable_mob_bottle_blocks = config.getBoolean("MobBottleBlocks", CATEGORY_GENERAL, 
				true, "Enable to put Mob Bottles as blocks.");
		
		//Mobのアニメーション設定
		cfg_general_enable_mob_bottle_animation = config.getBoolean("MobBottleAnimation", CATEGORY_GENERAL, 
				true, "Set the mobs drawing animation for mob bottle.");
		
		//TileEntityItemStackRenderer設定
		cfg_general_enable_mob_bottle_teisr = config.getBoolean("MobBottleInventoryRenderer", CATEGORY_GENERAL, 
				true, "Set up custom drawings for inventory of mob bottles. If false, TileEntityItemStackRenderer will be invalidated.");
		
		//モブボトルの当たり判定設定
		//0:モブボトル 1:プレート
		cfg_general_mob_bottle_collision_type = config.getInt("MobBottleBlockCollisionType", CATEGORY_GENERAL, 
				0, 0, 1, 
				"Set the collision type of the mob bottle.0:bottle 1:plate");
		
		
		//LMRFP連携
		if (Loader.isModLoaded("lmreengaged")) {
			//メイドさんがロードされている場合は設定を取得する
			cfg_general_enable_lmrfp_collaboration = config.getBoolean("Enable_LittleMaidReengagedFP_Collaboration", CATEGORY_GENERAL, 
					false, "Enable with LittleMaidReengaged Firis's Patch collaboration.");			
		}
		
		//ボスキャッチ判定
		cfg_general_mob_bottle_capture_boss = config.getBoolean("MobBottleCaptureBoss", CATEGORY_GENERAL, 
				false, "Enable the settings to capture bosses.");
		
		//--------------------------------------------------
		
		//Display
		//Entityの標準倍率
		cfg_display_entity_default_scale = config.getFloat("DefaultScale", CATEGORY_DISPLAY, 
				0.45F, 0.1F, 5.0F, 
				"Set the standard display magnification of Entity.");
		
		cfg_display_entity_auto_resize_width = config.getFloat("AutoResizeWidth", CATEGORY_DISPLAY, 
				0.8F, 0.0F, 5.0F, 
				"Set the width at which the entity automatically resizes.");

		cfg_display_entity_auto_resize_height = config.getFloat("AutoRisizeHeight", CATEGORY_DISPLAY, 
				1.9F, 0.0F, 5.0F, 
				"Set the width at height the entity automatically resizes.");
		
		//モブボトルのカバータイプ
		cfg_display_bottle_cover_type = config.getInt("BottleCoverType", CATEGORY_DISPLAY, 
				0, 0, 5, 
				"Set the cover type of the mob bottle.0:bottle 1:iron_plate 2:gold_plate 3:stone_plate 4:wood_plate 5:empty");
		
		//1回あたりの拡大縮小サイズ
		cfg_display_scale_up_down_value = config.getFloat("ChangeTools_ScaleUpDownValue", CATEGORY_DISPLAY, 
				0.1F, 0.1F, 5.0F, 
				"Setting the scale value for one enlargement / reduction.");
		
		//拡大するツール設定
		cfg_display_tool_scale_up = config.getString("ChangeTools_ScaleUp", CATEGORY_DISPLAY,
				"minecraft:golden_hoe", 
				"Tools for expanding mob bottle blocks.");

		//縮小するツール設定
		cfg_display_tool_scale_down = config.getString("ChangeTools_ScaleDown", CATEGORY_DISPLAY,
				"minecraft:golden_axe", 
				"Tool to shrink mob bottle blocks.");
		
		//カバータイプツール設定
		cfg_display_tool_cover_type = config.getString("ChangeTools_CoverType", CATEGORY_DISPLAY,
				"minecraft:golden_pickaxe", 
				"Tool to change cover type of mob bottle block.");
		
		//回転ツール設定
		cfg_display_tool_rotation = config.getString("ChangeTools_Rotation", CATEGORY_DISPLAY,
				"minecraft:golden_shovel", 
				"Tool to rotate mob bottle block.");
		
		config.save();
		
	}

}
