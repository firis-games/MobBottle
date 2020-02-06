package firis.mobbottle.common.item;

import java.util.List;

import javax.annotation.Nullable;

import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.MobBottle.FirisItems;
import firis.mobbottle.common.config.FirisConfig;
import firis.mobbottle.common.entity.FEntityItemAntiDamage;
import firis.mobbottle.common.helpler.EntityLivingHelper;
import firis.mobbottle.common.tileentity.FTileEntityMobBottle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * モブボトル
 */
public class FItemMobBottle extends ItemBlock {

	/**
	 * コンストラクタ
	 */
	public FItemMobBottle() {
		super(FirisBlocks.MOB_BOTTLE);
		this.setMaxStackSize(16);
	}
	
	/**
	 * 左クリックからのアイテム化
	 */
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
		//Mobアイテム化
		return createEntityItemStack(stack, EnumHand.MAIN_HAND, player, entity);
    }
	
	/**
	 * Shift＋右クリックからのアイテム化
	 */
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
		//Mobアイテム化
		return createEntityItemStack(stack, hand, playerIn, target);
    }
	
	/**
	 * Mobを生成する
	 */
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if (!player.isSneaking() || !FirisConfig.cfg_general_enable_mob_bottle_blocks) {
			ItemStack stack = player.getHeldItem(hand);
			
			if (!stack.isEmpty() 
					&& stack.getItem() instanceof FItemMobBottle
					&& stack.hasTagCompound()) {
				
				BlockPos position = pos.offset(facing);
				double x = position.getX() + 0.5;
				double y = position.getY();
				double z = position.getZ() + 0.5;
				
				//Mobのスポーン
				EntityLivingHelper.spawnEntityFromItemStack(stack, worldIn, x, y, z);
				
				//Tag情報を初期化
				stack.setTagCompound(null);
				
				return EnumActionResult.SUCCESS;
	
			}
			
	        return EnumActionResult.PASS;
		}
		
		//スニークの場合はブロック設置
		EnumActionResult ret = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		if (EnumActionResult.SUCCESS == ret) {
			ItemStack stack = player.getHeldItem(hand);
			stack.shrink(1);
			//クリエイティブでも強制で使用する
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);				
			}
		}
		return ret;
    }
	
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("item.mob_bottle.info"));
		if (stack.hasTagCompound()) {
			//Mob名
			if (stack.getTagCompound().hasKey("mob_name")) {
				tooltip.add("Mob : " + stack.getTagCompound().getString("mob_name"));
			}
		}
    }
	
	/**
	 * 耐性EntityItemを利用する
	 */
	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}
	
	/**
	 * 耐性EntityItemを生成する
	 * voidダメージ以外は無効化する
	 */
	@Override
	@Nullable
    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
		EntityItem entity = new FEntityItemAntiDamage(world, location.posX, location.posY, location.posZ, itemstack);
		entity.setDefaultPickupDelay();

		entity.motionX = location.motionX;
		entity.motionY = location.motionY;
		entity.motionZ = location.motionZ;
		
        return entity;
    }
	
	/**
	 * NBTタグを持つ場合にエフェクト表示
	 */
	@Override
	@SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return stack.hasTagCompound();
    }	
	
	/**
	 * ブロック設置後にItemStackのNBT情報を保存
	 */
	@Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {

		boolean ret = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		
		//TileEntityにNBTを保存する
    	TileEntity tileentity = world.getTileEntity(pos);
    	if (tileentity == null) return false;
    	if (!(tileentity instanceof FTileEntityMobBottle)) return false;
    	
    	//Stackを1へ変更
    	ItemStack setStack = stack.copy();
    	setStack.setCount(1);
    	
    	FTileEntityMobBottle tile = (FTileEntityMobBottle) tileentity;
    	tile.initMobBottle(setStack, player.getHorizontalFacing());

		tileentity.markDirty();
		
		return ret;
		
    }
	
	/**
	 * アイテム名 + モブ名
	 */
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String displayName = super.getItemStackDisplayName(stack);
		if (stack.hasTagCompound()) {
			//Mob名
			if (stack.getTagCompound().hasKey("mob_name")) {
				displayName += "[" + stack.getTagCompound().getString("mob_name") + "]";
			}
		}
		return displayName;
	}
	
	/**
	 * Mobをアイテム化
	 * @return
	 */
	public boolean createEntityItemStack(ItemStack stack, EnumHand hand, EntityPlayer player, Entity entity) {
	
		//EntityLivingチェック
		if (!(entity instanceof EntityLiving)) {
			return false;
		}
		EntityLiving entityLiving = (EntityLiving) entity;
		
		//NBTがある場合は何もしない
		if (stack.hasTagCompound()) {
			return true;
		}
		
		//モブ捕獲判定
		if (!isMobCatch(entityLiving)) {
			return false;
		}
		
		ItemStack bottleStack = new ItemStack(FirisItems.MOB_BOTTLE);
		
		//Mob用スポーン情報の書き込み
		bottleStack = EntityLivingHelper.getItemStackFromEntity(entityLiving, bottleStack);
		
		//Mob消去
		entityLiving.setDead();
		
		//アイテムをプレイヤーに設定
		setItemStackPlayerHand(bottleStack, hand, player);
		
		return true;
	}
	
	/**
	 * 対象のMobがキャッチできるか判断する
	 * @param entiy
	 * @return
	 */
	private boolean isMobCatch(EntityLiving living) {
		
		//パッケージとClassにbossという文字がある場合は対象外とする
		if (living.getClass().toString().toLowerCase().indexOf("boss") != -1) {
			return false;
		}
		return true;
	}
	
	/**
	 * 指定ハンドのアイテムを消費してItemStackをプレイヤーにセットする
	 */
	private void setItemStackPlayerHand(ItemStack setStack, EnumHand hand, EntityPlayer player) {
        
		ItemStack handStack = player.getHeldItem(hand);
		
		//アイテムセット
		if (handStack.getCount() == 1) {
			//手持ちがスタックされていない場合
			player.setHeldItem(hand, setStack);
		} else {
			
			handStack.shrink(1);
			
			//他に在庫がある場合
			if (!player.inventory.addItemStackToInventory(setStack)) {
                player.dropItem(setStack, false);
            }
		}
    }
	
}
