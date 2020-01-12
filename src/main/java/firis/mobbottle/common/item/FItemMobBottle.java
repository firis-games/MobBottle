package firis.mobbottle.common.item;

import java.util.List;

import javax.annotation.Nullable;

import firis.mobbottle.common.entity.FEntityItemAntiDamage;
import firis.mobbottle.common.helpler.EntityLivingHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
public class FItemMobBottle extends Item {

	/**
	 * コンストラクタ
	 */
	public FItemMobBottle() {
		super();
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.MISC);
	}
	
	/**
	 * 左クリックからのアイテム化
	 */
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
		//Mobアイテム化
		return createEntityItemStack(stack, player, entity);
    }
	
	/**
	 * Shift＋右クリックからのアイテム化
	 */
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
		//Mobアイテム化
		boolean ret = createEntityItemStack(stack, playerIn, target);
		if (ret && stack.hasTagCompound()) {
			playerIn.setHeldItem(hand, stack);			
		}
		return ret;
    }
	
	/**
	 * Mobを生成する
	 */
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
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
	 * Mobをアイテム化
	 * @return
	 */
	public boolean createEntityItemStack(ItemStack stack, EntityPlayer player, Entity entity) {
	
		//Mobチェック
		if (!(entity instanceof EntityLiving)) {
			return false;
		}
		
		EntityLiving entityLiving = (EntityLiving) entity;
		
		//NBTがある場合は何もしない
		if (stack.hasTagCompound()) {
			return true;
		}
		
		//Mob用スポーン情報の書き込み
		EntityLivingHelper.getItemStackFromEntity(entityLiving, stack);
		
		//Mob消去
		entityLiving.setDead();
		
		return true;
	}
	
}
