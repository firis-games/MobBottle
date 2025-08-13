package firis.mobbottle.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

public class FirisUtil {

    private static final String EMPTY_BLOCK_ID = "minecraft:air";
    private static final String EMPTY_ITEM_ID = "minecraft:air";

    /***
     * BlockIdからBlockを取得する
     * @param blockId 変換対象のBlockID
     * @param defBlock 変換できない場合のデフォルトブロック（nullの場合はBlocks.AIRを設定）
     * @return Block
     */
    public static Block getBlockFromId(String blockId, Block defBlock) {
        //IDからBlockを取得
        Optional<Holder.Reference<Block>> blockData = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        if (blockData.isEmpty()) {
            if (defBlock == null) {
                return Blocks.AIR;
            }
            return defBlock;
        }
        return blockData.get().value();
    }


    /***
     * BlockからBlockIdを取得する
     * @param block 変換対象のBlock
     * @param defBlockId 変換できない場合のデフォルトブロックID（nullの場合はEMPTY_BLOCK_IDを設定）
     * @return BlockId
     */
    public static String getIdFromBlock(Block block, String defBlockId) {
        //返却用ID
        String blockId = EMPTY_BLOCK_ID;
        //ブロックチェック
        if (BuiltInRegistries.BLOCK.containsValue(block)) {
            //ブロックが存在する場合
            ResourceLocation rlBlockId = BuiltInRegistries.BLOCK.getKey(block);
            if (rlBlockId != null) {
                blockId = rlBlockId.toString();
            }
        } else if (defBlockId != null) {
            blockId = defBlockId;
        }
        return blockId;
    }


    /***
     * ItemからItemIdを取得する
     * @param item 変換対象のItem
     * @param defItemId 変換できない場合のデフォルトアイテムID（nullの場合はEMPTY_ITEM_IDを設定）
     * @return ItemId
     */
    public static String getIdFromItem(Item item, String defItemId) {
        //返却用ID
        String itemId = EMPTY_ITEM_ID;
        //アイテムチェック
        if (BuiltInRegistries.ITEM.containsValue(item)) {
            //アイテムが存在する場合
            ResourceLocation rlItemId = BuiltInRegistries.ITEM.getKey(item);
            if (rlItemId != null) {
                itemId = rlItemId.toString();
            }
        } else if (defItemId != null) {
            itemId = defItemId;
        }
        return itemId;
    }
}
