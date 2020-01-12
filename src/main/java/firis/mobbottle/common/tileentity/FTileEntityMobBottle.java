package firis.mobbottle.common.tileentity;

import firis.mobbottle.common.helpler.EntityLivingHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FTileEntityMobBottle extends AbstractTileEntity {

	protected NBTTagCompound itemStackNBT;
	protected boolean isMob = false;
	protected EntityLiving renderEntityLiving = null;

	/**
	 * モブボトルの初期化
	 */
	public void initMobBottle(ItemStack stack) {
		this.itemStackNBT = stack.serializeNBT();
		this.isMob = EntityLivingHelper.isEntityFromItemStack(stack);
	}
	
	public ItemStack getItemStackToMobBottle() {
		return new ItemStack(itemStackNBT);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		
		super.readFromNBT(compound);
		
		this.itemStackNBT = (NBTTagCompound) compound.getTag("mob_bottle");
		this.isMob = compound.getBoolean("is_mob");
		
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		
		compound.setTag("mob_bottle", this.itemStackNBT);
		compound.setBoolean("is_mob", this.isMob);
		
		return compound;
	}
	
	@SideOnly(Side.CLIENT)
	public EntityLiving getRenderEntityLiving() {
		
		//初回のみ生成する
		if (this.isMob && this.renderEntityLiving == null) {
			this.renderEntityLiving = (EntityLiving) EntityLivingHelper.spawnEntityFromItemStack(getItemStackToMobBottle(), this.getWorld(), 0, 0, 0);
		}
		return this.renderEntityLiving;
		
	}
}
