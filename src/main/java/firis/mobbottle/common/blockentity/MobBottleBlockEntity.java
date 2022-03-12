package firis.mobbottle.common.blockentity;

import org.checkerframework.checker.nullness.qual.NonNull;

import firis.mobbottle.MobBottle;
import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.common.helper.FirisEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MobBottleBlockEntity extends BlockEntity {

	/**
	 * ItemStackTag情報
	 */
	protected CompoundTag dataItemStackTag = new CompoundTag();
	
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
	
	public MobBottleBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(MobBottle.FirisBlockEntityType.BLOCK_ENTITY_TYPE.get(), p_155229_, p_155230_);
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
	public CompoundTag getCopyMobBottleTag() {
		CompoundTag tag = new CompoundTag();
		tag.putString("block", this.getDataBlock().getRegistryName().toString());
		tag.putFloat("scale", this.dataScale);
		tag.putFloat("positiony", this.dataPositionY);
		return tag;
	}
	
	/**
	 * モブボトルの情報を設定
	 */
	public void setCopyMobBottleTag(CompoundTag tag) {
		this.setDataBlockFromString(tag.getString("block"));
		this.dataScale = tag.getFloat("scale");
		this.dataPositionY = tag.getFloat("positiony");
	}
    
	@Override
    public CompoundTag getUpdateTag() {
		return this.serializeNBT();
    }
	
	@Override
    public void handleUpdateTag(CompoundTag tag) {
        this.deserializeNBT(tag);
    }
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("bottle", this.dataItemStackTag);
		tag.putInt("dict", this.dataDirection.get3DDataValue());
		tag.putString("block", this.getDataBlock().getRegistryName().toString());
		tag.putFloat("scale", this.dataScale);
		tag.putFloat("positiony", this.dataPositionY);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.dataItemStackTag = tag.contains("bottle") ? tag.getCompound("bottle") : new CompoundTag();
		this.dataDirection = Direction.from3DDataValue(tag.getInt("dict"));
		this.setDataBlockFromString(tag.getString("block"));
		this.dataScale = tag.getFloat("scale");
		this.dataPositionY = tag.getFloat("positiony");
	}
	
	@NonNull
	protected Block getDataBlock() {
		if (this.dataBlock == null) {
			this.dataBlock = FirisBlocks.MOB_BOTTLE_EMPTY.get();
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
	@Override
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
			CompoundTag tag = this.dataItemStackTag.contains("mob") ? this.dataItemStackTag.getCompound("mob") : null;
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
	

}