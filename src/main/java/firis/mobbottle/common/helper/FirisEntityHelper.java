package firis.mobbottle.common.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class FirisEntityHelper {

	/**
	 * EntityのCompoundTagからEntityを生成する
	 * @param tag
	 * @return
	 */
	public static Entity createEntityFromTag(CompoundTag tag, Level level) {

		//nullチェック
		if (tag == null) return null;

		Entity entity = null;
		try {
			//Entity生成
			Optional<EntityType<?>> optEntityType = EntityType.by(tag);
			entity = optEntityType.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
			
			//情報の上書き
			entity.load(tag);
		} catch (Exception e) {
			entity = null;
		}
		return entity;
	}
	
	/**
	 * EntityからCompoundTagを生成する
	 * @param entity
	 * @return
	 */
	public static CompoundTag createTagFromEntity(Entity entity) {
		
		CompoundTag tag = new CompoundTag();		
		entity.save(tag);
		
		return tag;
	}
	
}
