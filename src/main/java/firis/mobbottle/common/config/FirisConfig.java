package firis.mobbottle.common.config;

import java.io.File;

import firis.mobbottle.MobBottle;
import net.minecraftforge.common.config.Configuration;

public class FirisConfig {

	public static Configuration config;
	
	public static String CATEGORY_GENERAL = "General";
	public static String CATEGORY_DISPLAY = "Display";
	
	/**
	 * モブボトルのブロック設置
	 */
	public static boolean cfg_general_enable_mob_bottle_blocks = true;
	
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
		
		//Display
		//Entityの標準倍率
		cfg_display_entity_default_scale = config.getFloat("DefaultScale", CATEGORY_DISPLAY, 
				0.45F, 0.0F, 5.0F, 
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
		
		config.save();
		
	}

}
