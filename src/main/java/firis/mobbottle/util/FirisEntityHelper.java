package firis.mobbottle.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Optional;

public class FirisEntityHelper {

    /**
     * EntityのCompoundTagからEntityを生成する
     */
    public static Entity createEntityFromTag(CompoundTag tag, Level level) {

        //nullチェック
        if (tag == null) return null;

        Entity entity = null;
        try {
            //Tag -> ValueInput
            ValueInput input = TagValueInput.create(
                    ProblemReporter.DISCARDING,
                    level.registryAccess(),
                    tag
            );

            //EntityType取得
            Optional<EntityType<?>> optEntityType = EntityType.by(input);
            if (optEntityType.isEmpty()) {
                return null;
            }

            //Entity生成
            entity = optEntityType.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
            if (entity != null) {
                //情報の上書き
                entity.load(input);
            }

        } catch (Exception e) {
            entity = null;
        }
        return entity;
    }

    /**
     * EntityからCompoundTagを生成する
     */
    public static CompoundTag createTagFromEntity(Entity entity) {

        CompoundTag tag = new CompoundTag();

        if (entity != null) {
            TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            entity.save(output);
            tag = output.buildResult();
        }
        return tag;
    }

}
