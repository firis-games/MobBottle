package firis.mobbottle.common.tileentity;

import firis.mobbottle.common.helpler.EntityLivingHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FTileEntityMobBottle extends AbstractTileEntity implements ITickable {

	protected NBTTagCompound itemStackNBT;
	protected boolean isMob = false;
	protected EntityLiving renderEntityLiving = null;
	protected EnumFacing facing = EnumFacing.NORTH;
	
	/**
	 * モブボトルの初期化
	 */
	public void initMobBottle(ItemStack stack, EnumFacing facing) {
		this.itemStackNBT = stack.serializeNBT();
		this.isMob = EntityLivingHelper.isEntityFromItemStack(stack);
		this.facing = facing;
	}
	
	public ItemStack getItemStackToMobBottle() {
		return new ItemStack(itemStackNBT);
	}
	
	public EnumFacing getFacing() {
		return this.facing;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		
		super.readFromNBT(compound);
		
		this.itemStackNBT = (NBTTagCompound) compound.getTag("mob_bottle");
		this.isMob = compound.getBoolean("is_mob");
		this.facing = EnumFacing.getHorizontal(compound.getInteger("facing"));
		
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		
		compound.setTag("mob_bottle", this.itemStackNBT);
		compound.setBoolean("is_mob", this.isMob);
		compound.setInteger("facing", this.facing.getHorizontalIndex());
		
		return compound;
	}
	
	@SideOnly(Side.CLIENT)
	public EntityLiving getRenderEntityLiving() {
		
		//初回のみ生成する
		if (this.isMob && this.renderEntityLiving == null) {
			this.renderEntityLiving = (EntityLiving) EntityLivingHelper.spawnEntityFromItemStack(getItemStackToMobBottle(), this.getWorld(), 0, 0, 0);
			this.renderEntityLiving.ticksExisted = 0;
		}
		return this.renderEntityLiving;
		
	}

	/**
	 * @Intarface ITickable
	 */
	@Override
	public void update() {
		//描画Entityのカウント処理
		if (this.renderEntityLiving != null) {
			this.renderEntityLiving.ticksExisted++;
		}
	}
}
