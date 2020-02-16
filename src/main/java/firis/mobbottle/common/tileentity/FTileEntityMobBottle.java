package firis.mobbottle.common.tileentity;

import java.util.HashMap;
import java.util.Map;

import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.common.config.FirisConfig;
import firis.mobbottle.common.helpler.EntityLivingHelper;
import firis.mobbottle.common.helpler.VanillaNetworkHelper;
import net.blacklab.lmr.client.renderer.entity.RenderLittleMaid.MaidMotion;
import net.blacklab.lmr.entity.littlemaid.EntityLittleMaid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FTileEntityMobBottle extends AbstractTileEntity implements ITickable {

	/**
	 * ボトルカバー定義
	 */
	public static enum EnumBottleCoverType {
		MOB_BOTTLE(0, FirisBlocks.MOB_BOTTLE_EMPTY.getDefaultState(), true, 0),
		IRON_PLATE(1, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState(), false, 2),
		GOLD_PLATE(2, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState(), false, 2),
		STONE_PLATE(3, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), false, 2),
		WOOD_PLATE(4, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), false, 2),
		EMPTY(5, null, false, 0);
		
		//ID
		private int id;
		//描画用Block情報
		private IBlockState state;
		//透過ブロックフラグ
		private boolean isAlpha;
		//描画Entityの位置(0-16で設定して初期化時にtranslate設定値へ変換)
		private float entityPosition = 0;
		
		private EnumBottleCoverType(int id, IBlockState state, boolean isAlpha, int entityPosition) {
			this.id = id;
			this.state = state;
			this.isAlpha = isAlpha;
			this.entityPosition = ((float)entityPosition) / 16.0F;
		}
		public int getId() {
			return this.id;
		}
		public IBlockState getBlockState() {
			return this.state;
		}
		public boolean getIsAlpha() {
			return this.isAlpha;
		}
		public float getEntityPosition() {
			return this.entityPosition;
		}
		public static EnumBottleCoverType getBottleCoverTypeFromId(int id) {
			for (EnumBottleCoverType type : EnumBottleCoverType.values()) {
				if (id == type.getId()) {
					return type;
				}
			}
			return MOB_BOTTLE;
		}
		public static EnumBottleCoverType getNextBottleCoverType(EnumBottleCoverType bottleCoverType) {
			EnumBottleCoverType[] bottleCoverTypes = EnumBottleCoverType.values();
			int idx = 0;
			for (int i = 0; i < bottleCoverTypes.length; i++) {
				if (bottleCoverTypes[i] == bottleCoverType) {
					idx = i + 1;
				}
			}
			if (idx >= bottleCoverTypes.length) {
				return EnumBottleCoverType.MOB_BOTTLE;
			} else {
				return bottleCoverTypes[idx];
			}
		}
	}
	
	protected NBTTagCompound itemStackNBT;
	protected boolean isMob = false;
	protected EntityLiving renderEntityLiving = null;
	protected EnumFacing facing = EnumFacing.NORTH;
	protected EnumBottleCoverType bottleCoverType = EnumBottleCoverType.MOB_BOTTLE;
	protected float scale = FirisConfig.cfg_display_entity_default_scale;
	
	/**
	 * モブボトルの初期化
	 */
	public void initMobBottle(ItemStack stack, EnumFacing facing) {
		this.itemStackNBT = stack.serializeNBT();
		this.isMob = EntityLivingHelper.isEntityFromItemStack(stack);
		this.facing = facing;
		this.setScale(FirisConfig.cfg_display_entity_default_scale);
		
		//空のモブボトルの場合はbottleCoverType固定
		if (!stack.hasTagCompound()) {
			this.bottleCoverType = EnumBottleCoverType.MOB_BOTTLE;
		} else {
			this.bottleCoverType = EnumBottleCoverType.getBottleCoverTypeFromId(FirisConfig.cfg_display_bottle_cover_type);
		}
		
	}
	
	public ItemStack getItemStackToMobBottle() {
		return new ItemStack(itemStackNBT);
	}
	
	public EnumFacing getFacing() {
		return this.facing;
	}
	
	public EnumBottleCoverType getBottleCoverType() {
		return this.bottleCoverType;
	}
	
	public float getScale() {
		return this.scale;
	}
	
	/**
	 * 次のカバータイプを設定
	 */
	public void setNextBottleCoverType() {
		
		this.bottleCoverType = EnumBottleCoverType.getNextBottleCoverType(this.bottleCoverType);
		
		//同期
		VanillaNetworkHelper.sendPacketTileEntity(this);
		
	}
	
	/**
	 * スケールサイズを変更する
	 * 現在サイズにscaleを加算する
	 */
	public void setChangeScale(boolean isSmall) {
		
		float addScale = FirisConfig.cfg_display_scale_up_down_value;
		
		addScale = isSmall ? addScale * -1 : addScale;
		this.setScale(this.scale + addScale);
		
		//同期
		VanillaNetworkHelper.sendPacketTileEntity(this);
		
	}
	
	/**
	 * モブボトルを回転させる
	 */
	public void setRotationBottle() {
		
		this.facing = this.facing.rotateY();
		
		//同期
		VanillaNetworkHelper.sendPacketTileEntity(this);
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		
		super.readFromNBT(compound);
		
		this.itemStackNBT = (NBTTagCompound) compound.getTag("mob_bottle");
		this.isMob = compound.getBoolean("is_mob");
		this.facing = EnumFacing.getHorizontal(compound.getInteger("facing"));
		this.bottleCoverType = EnumBottleCoverType.getBottleCoverTypeFromId(compound.getInteger("bottle_cover_type"));
		//後方互換用初期値設定
		float scale = compound.getFloat("scale");
		if (scale == 0.0F) scale = FirisConfig.cfg_display_entity_default_scale;
		this.setScale(scale);
		
		this.lmrfpMaidMotion = compound.getInteger("lmrfp_maid_motion");
		
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		
		compound.setTag("mob_bottle", this.itemStackNBT);
		compound.setBoolean("is_mob", this.isMob);
		compound.setInteger("facing", this.facing.getHorizontalIndex());
		compound.setInteger("bottle_cover_type", this.bottleCoverType.getId());
		compound.setFloat("scale", this.scale);

		compound.setInteger("lmrfp_maid_motion", this.lmrfpMaidMotion);
		
		return compound;
	}
	
	@SideOnly(Side.CLIENT)
	public EntityLiving getRenderEntityLiving() {
		
		//ItemStackRender
		if (rendererEntityLivingMap.size() != 0) {
			return this.rendererEntityLivingMap.get(this.itemStackNBT);
		}
		
		//初回のみ生成する
		if (this.isMob && this.renderEntityLiving == null) {
			this.renderEntityLiving = (EntityLiving) EntityLivingHelper.spawnEntityFromItemStack(getItemStackToMobBottle(), Minecraft.getMinecraft().world, 0, 0, 0);
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
			//モブアニメーションの制御
			if (FirisConfig.cfg_general_enable_mob_bottle_animation) {
				this.renderEntityLiving.ticksExisted++;
			} else {
				this.renderEntityLiving.ticksExisted = 0;
			}
		}
		
		//リトルメイド連携
		this.updateLittleMaid();
	}
	
	/**
	 * TileEntityItemStackRenderer用処理
	 */
	private Map<NBTTagCompound, EntityLiving> rendererEntityLivingMap = new HashMap<>();
	
	/**
	 * TileEntityItemStackRendererモブボトルの初期化
	 * EntityLivingをMapでキャッシュする
	 */
	@SideOnly(Side.CLIENT)
	public void initMobBottleItemStackRenderer(ItemStack stack) {
		
		//初期化
		this.itemStackNBT = null;
		this.bottleCoverType = EnumBottleCoverType.MOB_BOTTLE;
		
		if (!stack.hasTagCompound()) return;
		
		//設定から取得
		this.bottleCoverType = EnumBottleCoverType.getBottleCoverTypeFromId(FirisConfig.cfg_display_bottle_cover_type);
		
		this.itemStackNBT = stack.serializeNBT();
		this.facing = EnumFacing.WEST;
		this.setScale(FirisConfig.cfg_display_entity_default_scale);
		
		//インスタンス生成
		if (!rendererEntityLivingMap.containsKey(itemStackNBT)) {
			EntityLiving tmpRenderEntityLiving = (EntityLiving) EntityLivingHelper.spawnEntityFromItemStack(getItemStackToMobBottle(), Minecraft.getMinecraft().world, 0, 0, 0);
			if (tmpRenderEntityLiving != null) {
				tmpRenderEntityLiving.ticksExisted = 0;
				rendererEntityLivingMap.put(this.itemStackNBT, tmpRenderEntityLiving);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public boolean getItemStackRenderer() {
		if (rendererEntityLivingMap.size() != 0) {
			return this.rendererEntityLivingMap.get(this.itemStackNBT) != null ? true : false;
		}
		return false;
	}
	
	/**
	 * 最大値と最小値を制限したscaleを設定する
	 * min:0.1F max:5.0F
	 */
	private void setScale(float scale) {
		scale = Math.min(scale, 5.0F);
		scale = Math.max(scale, 0.1F);
		this.scale = scale;
	}
	
	/**
	 * モブボトルブロックの描画範囲
	 */
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
		
		if (this.renderEntityLiving == null) return super.getRenderBoundingBox();
		
		//EntityのRender範囲をもとにブロック描画範囲を指定する
		AxisAlignedBB aabb = this.renderEntityLiving.getRenderBoundingBox();
		double scaleRate = this.scale * 2.0D;
        return new AxisAlignedBB(
        		pos.add(aabb.minX * scaleRate, aabb.minY * scaleRate, aabb.minZ * scaleRate), 
        		pos.add(aabb.maxX * scaleRate, aabb.maxY * scaleRate, aabb.maxZ * scaleRate));
	}
	
	
	
	//--------------------------------------------------------------------------------
	
	private int lmrfpMaidMotion = 1;
	
	/**
	 * メイドさんのモーションを変更する
	 */
	public void setNextMaidMotion() {
		
		if (!FirisConfig.cfg_general_enable_lmrfp_collaboration) return;
		
		//Entityがメイドさんの場合のみ処理を行う
		if (this.renderEntityLiving instanceof EntityLittleMaid) {
			
			//次モーションのIDを取得する
			this.lmrfpMaidMotion = MaidMotion.getMaidMotionFromId(lmrfpMaidMotion).next().getId();
			
			//Noneの場合は飛ばす
			if (this.lmrfpMaidMotion == 0) this.lmrfpMaidMotion = 1;
			
			//同期
			VanillaNetworkHelper.sendPacketTileEntity(this);
		}
	}
	
	/**
	 * リトルメイド連携
	 * 連携設定が有効な場合のみ処理を行う
	 */
	protected void updateLittleMaid() {
		
		if (!FirisConfig.cfg_general_enable_lmrfp_collaboration) return;
		
		//Entityがメイドさんの場合のみ処理を行う
		if (this.renderEntityLiving instanceof EntityLittleMaid) {
			EntityLittleMaid entityMaid = (EntityLittleMaid) this.renderEntityLiving;
			
			//視線をリセット
			entityMaid.rotationPitch = 0.0F;
			entityMaid.rotationYaw = 0.0F;
			entityMaid.prevRotationPitch = 0.0F;
			entityMaid.prevRotationYaw = 0.0F;
			
			//MaidMostion設定
			MaidMotion motion = MaidMotion.getMaidMotionFromId(this.lmrfpMaidMotion);
			entityMaid.setMaidMotion(motion);
			
		}
	}
}