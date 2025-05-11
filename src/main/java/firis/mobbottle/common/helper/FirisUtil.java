package firis.mobbottle.common.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class FirisUtil {

	
	/***
	 * BlockIdからBlockを取得する
	 * 
	 * @return　取得できない場合はdefBlockの値を返却する
	 */
	public static Block getBlockFromId(String blockId, Block defBlock) {
		//IDからBlockを取得
		Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
		if (block == Blocks.AIR) {
			//AIRは取得できないと判断
			block = defBlock;
		}
		return block;
	}
	
	
	/***
	 * BlockからBlockIdを取得する
	 * 
	 * @return　取得できない場合はdefBlockIdの値を返却する
	 */
	public static String getIdFromBlock(Block block, String defBlockId) {
		//BlockからIDを取得
		String blockId = defBlockId;
		ResourceLocation rlBlockId = BuiltInRegistries.BLOCK.getKey(block);
		if (rlBlockId != null) {
			blockId = rlBlockId.toString();
		}
		return blockId;
	}
	
	
	/***
	 * ItemからItemIdを取得する
	 * 
	 * @return　取得できない場合はdefItemIdの値を返却する
	 */
	public static String getIdFromItem(Item item, String defItemId) {
		//ItemからIDを取得
		String itemId = defItemId;
		ResourceLocation rlItemId = BuiltInRegistries.ITEM.getKey(item);
		if (rlItemId != null) {
			itemId = rlItemId.toString();
		}
		return itemId;
	}
}
