package firis.mobbottle.common.helpler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class EntityLivingHelper {

	/**
	 * EntityLivingからスポーンアイテムを生成する
	 */
	public static ItemStack getItemStackFromEntity(EntityLiving entityliving, ItemStack spawnItem) {
		
		ItemStack stack = ItemStack.EMPTY;
		
		//アイテム化対象化判断する
		if (!isItemFromEntity(entityliving)) {
			return stack;
		}
		
		stack = spawnItem;
		
		NBTTagCompound entityNBT = new NBTTagCompound();
		entityliving.writeToNBT(entityNBT);
    	
    	//一部パラメータをリセット
    	//被ダメ無敵時間
		entityNBT.setShort("HurtTime", (short) 0);
		//モーメント
		entityNBT.setTag("Motion", newDoubleNBTList(0.0D, 0.0D, 0.0D));
		//落下距離
		entityNBT.setFloat("FallDistance", 0.0F);
		//EntityId
		entityNBT.setString("id", getEntityId(entityliving));

		NBTTagCompound stackNBT = new NBTTagCompound();
		stackNBT.setTag("Mob", entityNBT);
		
		stack.setTagCompound(stackNBT);
		
		//モブ名
		stack.getTagCompound().setString("mob_name", entityliving.getName());
		
		return stack;
	}
	
	/**
	 * ItemStackからEntityLivingをスポーンさせる
	 */
	public static Entity spawnEntityFromItemStack(ItemStack stack, World world, double x, double y, double z) {
		
		if (!isEntityFromItemStack(stack)) return null;
		
		NBTTagCompound entityNBT = (NBTTagCompound) stack.getTagCompound().getTag("Mob");
		
		Entity entity = EntityList.createEntityFromNBT(entityNBT, world);

		if (entity != null) {
			entity.setLocationAndAngles(x, y, z, 0,0 );
			if (!world.isRemote) {
				world.spawnEntity(entity);
			}
		}
		return entity;
	}
	
	/**
	 * ItemStackが正常か判断する
	 */
	public static boolean isEntityFromItemStack(ItemStack stack) {
		if (stack.hasTagCompound() == false
				|| !stack.getTagCompound().hasKey("Mob")) return false;
		
		return true;
	}
	
	/**
	 * NBTDoubleのTagListを生成する
	 * @param numbers
	 * @return
	 */
	private static NBTTagList newDoubleNBTList(double... numbers) {
		NBTTagList nbttaglist = new NBTTagList();
		for (double number : numbers) {
			nbttaglist.appendTag(new NBTTagDouble(number));
		}
		return nbttaglist;
	}
	
	/**
	 * EntityLivingからMobIdを取得する
	 */
	private static String getEntityId(EntityLiving entityliving) {
		String mobid = "";
		net.minecraftforge.fml.common.registry.EntityEntry entry = 
				net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(entityliving.getClass());
		if (entry != null) {
			mobid = entry.getRegistryName().toString();
		}
		return mobid;
	}
	
	/**
	 * Mobのアイテム化対象を判断する
	 */
	private static boolean isItemFromEntity(EntityLiving entityliving) {
		return true;
	}
}
