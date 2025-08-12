package firis.mobbottle.datagen;

import firis.mobbottle.MobBottle;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableDataGenerators extends LootTableProvider {

    public ModLootTableDataGenerators(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output,
                Set.of(),
                //ブロック用ルートテーブル
                List.of(new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)),
                registries);
    }

    public static class ModBlockLootTables extends BlockLootSubProvider {

        public ModBlockLootTables(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        /***
         * generateで設定するブロックのみ指定する
         * 設定以外のブロックを返すとエラーとなる
         */
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Arrays.asList(MobBottle.FirisBlocks.MOB_BOTTLE_EMPTY.get());
        }

        /***
         * LootTable設定
         */
        @Override
        protected void generate() {
            //基本ドロップの設定
            //空のモブボトル
            this.dropSelf(MobBottle.FirisBlocks.MOB_BOTTLE_EMPTY.get());
        }

    }
}
