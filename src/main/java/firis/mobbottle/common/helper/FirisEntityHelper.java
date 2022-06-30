package firis.mobbottle.common.helper;

import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class FirisEntityHelper {

	/**
	 * EntityのCompoundTagからEntityを生成する
	 * @param tag
	 * @return
	 */
	public static Entity createEntityFromTag(CompoundNBT tag, World level) {
		
		Entity entity = null;
		try {
			//Entity生成
			Optional<EntityType<?>> optEntityType = EntityType.by(tag);
			entity = optEntityType.get().create(level);
			
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
	public static CompoundNBT createTagFromEntity(Entity entity) {
		
		CompoundNBT tag = new CompoundNBT();		
		entity.save(tag);
		
		return tag;
	}
	
}
