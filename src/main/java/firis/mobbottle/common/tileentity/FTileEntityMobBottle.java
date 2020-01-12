package firis.mobbottle.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class FTileEntityMobBottle extends AbstractTileEntity {

	protected NBTTagCompound itemStackNBT;

	public void setItemStackNBT(NBTTagCompound compound) {
		this.itemStackNBT = compound;
	}
	
	public NBTTagCompound getItemStackNBT() {
		return this.itemStackNBT;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		
		super.readFromNBT(compound);
		
		this.itemStackNBT = (NBTTagCompound) compound.getTag("mob_bottle");
		
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		
		compound.setTag("mob_bottle", this.itemStackNBT);
		
		return compound;
	}
}
