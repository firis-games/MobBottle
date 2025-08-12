package firis.mobbottle.datagen;

import firis.mobbottle.MobBottle;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MobBottle.MODID)
public class ModDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {

        //レシピの登録
        event.createProvider(ModRecipeProvider.Runner::new);
        //LootTableの登録
        event.createProvider(ModLootTableDataGenerators::new);
    }
}
