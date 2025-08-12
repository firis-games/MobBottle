package firis.mobbottle.datagen;

import firis.mobbottle.MobBottle;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {


    private static final TagKey<Item> TAG_GLASS_CHEAP = TagKey.create(
            Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks/cheap"));

    private static final TagKey<Item> TAG_GEM = TagKey.create(
            Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MobBottle.MODID, "gems"));


    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {

        //レシピ登録
        //モブボトル
        ShapedRecipeBuilder
                .shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, MobBottle.FirisItems.MOB_BOTTLE.get(), 1)
                .pattern("www")
                .pattern("gdg")
                .pattern("ggg")
                .define('g', TAG_GLASS_CHEAP)
                .define('w', ItemTags.WOODEN_SLABS)
                .define('d', TAG_GEM)
                .unlockedBy("has_gem", this.has(TAG_GEM))
                .save(this.output);

        //モブボトル -> 空のモブボトル
        ShapelessRecipeBuilder
                .shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, MobBottle.FirisItems.MOB_BOTTLE_EMPTY.get(), 1)
                .requires(MobBottle.FirisItems.MOB_BOTTLE.get())
                .unlockedBy("has_gem", this.has(TAG_GEM))
                .save(this.output, MobBottle.MODID + ":mob_bottle_1");

        //空のモブボトル -> モブボトル
        ShapelessRecipeBuilder
                .shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, MobBottle.FirisItems.MOB_BOTTLE.get(), 1)
                .requires(MobBottle.FirisItems.MOB_BOTTLE_EMPTY.get())
                .unlockedBy("has_gem", this.has(TAG_GEM))
                .save(this.output, MobBottle.MODID + ":mob_bottle_2");
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        public String getName() {
            return "ModRecipeProvider";
        }
    }
}
