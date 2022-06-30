package firis.mobbottle.common.blockentity;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import firis.mobbottle.MobBottle;
import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.common.helper.FirisEntityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;



public class MobBottleBlockEntity extends TileEntity {

	/**
	 * ItemStackTag情報
	 */
	protected CompoundNBT dataItemStackTag = new CompoundNBT();
	
	/**
	 * ブロックの向き
	 */
	protected Direction dataDirection = Direction.NORTH;
	
	/**
	 * 外装ブロック
	 */
	protected Block dataBlock = null;
	
	/**
	 * モブサイズ
	 */
	protected float dataScale = 0.35F;
	
	/**
	 * モブのY軸設定
	 */
	protected float dataPositionY = 0.0F / 16.0F;
	
	public MobBottleBlockEntity() {
		super(MobBottle.FirisBlockEntityType.BLOCK_ENTITY_TYPE);
	}
	
	/**
	 * ブロック設置時に必要な情報を設定する
	 * @param stack
	 * @param direction
	 */
	public void setMobBottleData(ItemStack stack, Direction direction) {
		this.dataItemStackTag = stack.getOrCreateTag();
		this.dataDirection = direction;
	}
	
	/**
	 * モブボトルの外装ブロックを設定する
	 * @param block
	 */
	public void setMobBottleBlock(Block block) {
		this.dataBlock = block;
	}
	/**
	 * モブボトルのエンティティのサイズ+
	 */
	public void setMobBottleScalePlus() {
		this.dataScale = Math.min(this.dataScale + 0.1F, 2.0F);
	}
	/**
	 * モブボトルのエンティティのサイズ-
	 */
	public void setMobBottleScaleMinus() {
		this.dataScale = Math.max(this.dataScale - 0.1F, 0.05F);
	}
	/**
	 * モブボトルの高さ+
	 */
	public void setMobBottlePositionPlus() {
		this.dataPositionY = Math.min(this.dataPositionY + 1.0F / 16.0F, 1.0F);
	}
	/**
	 * モブボトルの高さ-
	 */
	public void setMobBottlePositionMinus() {
		this.dataPositionY = Math.max(this.dataPositionY - 1.0F / 16.0F, 0.0F);
	}
	/**
	 * モブボトルの情報を取得
	 */
	public CompoundNBT getCopyMobBottleTag() {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("block", this.getDataBlock().getRegistryName().toString());
		tag.putFloat("scale", this.dataScale);
		tag.putFloat("positiony", this.dataPositionY);
		return tag;
	}
	
	/**
	 * モブボトルの情報を設定
	 */
	public void setCopyMobBottleTag(CompoundNBT tag) {
		this.setDataBlockFromString(tag.getString("block"));
		this.dataScale = tag.getFloat("scale");
		this.dataPositionY = tag.getFloat("positiony");
	}
    
	@Override
    public CompoundNBT getUpdateTag() {
		return this.serializeNBT();
    }
	
	@Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.deserializeNBT(tag);
    }
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 0, this.getUpdateTag());
	}
	
	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.put("bottle", this.dataItemStackTag);
		tag.putInt("dict", this.dataDirection.get3DDataValue());
		tag.putString("block", this.getDataBlock().getRegistryName().toString());
		tag.putFloat("scale", this.dataScale);
		tag.putFloat("positiony", this.dataPositionY);
		return tag;
	}
	
	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		this.dataItemStackTag = tag.contains("bottle") ? tag.getCompound("bottle") : new CompoundNBT();
		this.dataDirection = Direction.from3DDataValue(tag.getInt("dict"));
		this.setDataBlockFromString(tag.getString("block"));
		this.dataScale = tag.getFloat("scale");
		this.dataPositionY = tag.getFloat("positiony");
	}
	
	@Nonnull
	protected Block getDataBlock() {
		if (this.dataBlock == null) {
			this.dataBlock = FirisBlocks.MOB_BOTTLE_EMPTY;
		}
		return this.dataBlock;
	}
	
	@SuppressWarnings("deprecation")
	protected void setDataBlockFromString(String blockId) {
		this.dataBlock = Registry.BLOCK.get(new ResourceLocation(blockId));
	}
	
	/**
	 * Drop用ItemStackTagを設定
	 */
	public void saveToItem(ItemStack stack) {
		//ItemStackがMob情報を持っている場合はTagを設定する
		if (this.dataItemStackTag.contains("mob")) {
			stack.setTag(this.dataItemStackTag);
		}
	}
	
	/**
	 * 描画用Entityキャッシュ
	 */
	@OnlyIn(Dist.CLIENT)
	protected Entity renderEntityCache = null;
	
	@OnlyIn(Dist.CLIENT)
	protected boolean isRenderEntityCache = false;
	
	/**
	 * 描画用Entity取得
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public Entity getRenderEntity() {
		if (this.renderEntityCache == null && !this.isRenderEntityCache) {
			CompoundNBT tag = this.dataItemStackTag.contains("mob") ? this.dataItemStackTag.getCompound("mob") : null;
			this.renderEntityCache = FirisEntityHelper.createEntityFromTag(tag, this.level);
			this.isRenderEntityCache = true;
		}
		return this.renderEntityCache;
	}
	
	/**
	 * 描画用の方角を取得
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public Direction getRenderDirection() {
		return this.dataDirection;
	}
	
	/**
	 * 描画用の外装ブロックを取得
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public BlockState getRenderBlockState() {
		return this.getDataBlock().defaultBlockState();
	}
	
	/**
	 * 描画用のサイズ
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public float getRenderScale() {
		return this.dataScale;
	}
	
	/**
	 * 描画用のY軸
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public float getRenderPositionY() {
		return this.dataPositionY;
	}
	
	
	// BlockEntityWithoutLevelRenderer対応
	//**************************************************
	@OnlyIn(Dist.CLIENT)
	private Map<CompoundNBT, Entity> renderEntityCacheMap = new HashMap<>();
	
	/**
	 * アイテム描画に必要な情報を設定する
	 * @param stack
	 * @param direction
	 */
	@OnlyIn(Dist.CLIENT)
	public void setMobBottleDataFromBEWLR(ItemStack stack) {
		this.dataItemStackTag = stack.getOrCreateTag();
		this.dataDirection = Direction.EAST;
		
		//キャッシュに存在しない場合はgetRenderEntityでEntityを生成する
		if (!this.renderEntityCacheMap.containsKey(this.dataItemStackTag)) {
			this.renderEntityCache = null;
			this.isRenderEntityCache = false;
			this.renderEntityCacheMap.put(this.dataItemStackTag, this.getRenderEntity());
		}
		
		//キャッシュからEntityを反映
		this.renderEntityCache = this.renderEntityCacheMap.get(this.dataItemStackTag);
		this.isRenderEntityCache = true;
		this.level = Minecraft.getInstance().level;
	}
	
}
