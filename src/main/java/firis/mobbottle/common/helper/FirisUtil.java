package firis.mobbottle.common.helper;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class FirisUtil {

	
	/***
	 * BlockIdからBlockを取得する
	 * 
	 * @return　取得できない場合はdefBlockの値を返却する
	 */
	public static Block getBlockFromId(String blockId, Block defBlock) {
		
		Block block = defBlock;
		
		//IDからリソースキーを取得
		ResourceKey<Block> referenceKey = ResourceKey.create(Registries.BLOCK, new ResourceLocation(blockId));
		
		//リソースキーからBlockオブジェクトを取得
		Optional<Holder.Reference<Block>> holderBlock = ForgeRegistries.BLOCKS.getDelegate(referenceKey);
		if (!holderBlock.isEmpty()) {
			block = holderBlock.get().get();			
		}
		
		return block;
	}
	
	
	/***
	 * BlockからBlockIdを取得する
	 * 
	 * @return　取得できない場合はdefBlockIdの値を返却する
	 */
	public static String getIdFromBlock(Block block, String defBlockId) {
		
		String blockId = defBlockId;
		
		Optional<Holder.Reference<Block>> holderBlock = ForgeRegistries.BLOCKS.getDelegate(block);
		if (!holderBlock.isEmpty()) {
			ResourceLocation rl = holderBlock.get().key().location();
			blockId = rl.toString();
		}
		
		return blockId;
	}
	
	
	/***
	 * ItemからItemIdを取得する
	 * 
	 * @return　取得できない場合はdefItemIdの値を返却する
	 */
	public static String getIdFromItem(Item item, String defItemId) {
		
		String itemId = defItemId;
		
		Optional<Holder.Reference<Item>> holderItem = ForgeRegistries.ITEMS.getDelegate(item);
		if (!holderItem.isEmpty()) {
			ResourceLocation rl = holderItem.get().key().location();
			itemId = rl.toString();
		}
		
		return itemId;
		
	}
}
