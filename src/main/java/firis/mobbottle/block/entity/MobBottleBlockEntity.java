package firis.mobbottle.block.entity;

import firis.mobbottle.MobBottle;
import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.component.MobBottleMobData;
import firis.mobbottle.helper.FirisEntityHelper;
import firis.mobbottle.helper.FirisUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.HashMap;
import java.util.Map;

public class MobBottleBlockEntity extends BlockEntity {

	/**
	 * MobBottleMobData情報
	 */
	protected MobBottleMobData mobData = MobBottleMobData.Empty();
	
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
		//Client側初期化
		if (FMLEnvironment.dist == Dist.CLIENT) {
			this.initClient();
		}
	}
	
	/**
	 * ブロック設置時に必要な情報を設定する
	 * @param stack
	 * @param direction
	 */
	public void setMobBottleData(ItemStack stack, Direction direction) {
		this.mobData = stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE);
		this.dataDirection = direction;
	}
	
	/**
	 * モブボトルの外装ブロックを設定する
	 * @param block
	 */
	public void setMobBottleBlock(Block block) {
		this.dataBlock = block;
		this.setChanged();
	}
	/**
	 * モブボトルのエンティティのサイズ+
	 */
	public void setMobBottleScalePlus() {
		this.dataScale = Math.min(this.dataScale + 0.1F, 2.0F);
		this.setChanged();
	}
	/**
	 * モブボトルのエンティティのサイズ-
	 */
	public void setMobBottleScaleMinus() {
		this.dataScale = Math.max(this.dataScale - 0.1F, 0.05F);
		this.setChanged();
	}
	/**
	 * モブボトルの高さ+
	 */
	public void setMobBottlePositionPlus() {
		this.dataPositionY = Math.min(this.dataPositionY + 1.0F / 16.0F, 1.0F);
		this.setChanged();
	}
	/**
	 * モブボトルの高さ-
	 */
	public void setMobBottlePositionMinus() {
		this.dataPositionY = Math.max(this.dataPositionY - 1.0F / 16.0F, 0.0F);
		this.setChanged();
	}
	/**
	 * 人形置き形式を設定する
	 */
	public void setFigureMode() {
		this.dataBlock = Blocks.AIR;
		this.dataScale = 1.0F;
		this.dataPositionY = 0.0F;
		this.setChanged();
	}
	/**
	 * モブボトルの情報を取得
	 */
	public CompoundTag getCopyMobBottleTag() {
		CompoundTag tag = new CompoundTag();
		tag.putString("block", this.getDataBlockRegistryName());
		tag.putFloat("scale", this.dataScale);
		tag.putFloat("positiony", this.dataPositionY);
		return tag;
	}

	/**
	 * モブボトルの情報を設定
	 */
	public void setCopyMobBottleTag(CompoundTag tag) {
		this.setDataBlockFromString(tag.getStringOr("block", ""));
		this.dataScale = tag.getFloatOr("scale", 0.0f);
		this.dataPositionY = tag.getFloatOr("positiony", 0.0f);
		this.setChanged();
	}

	@Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag, registries);
		return tag;
    }
	
	@Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
		super.handleUpdateTag(tag, registries);
    }
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		super.onDataPacket(connection, packet, registries);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.put("bottle", this.mobData.getCompoundTag());
		tag.putInt("dict", this.dataDirection.get3DDataValue());
		tag.putString("block", this.getDataBlockRegistryName());
		tag.putFloat("scale", this.dataScale);
		tag.putFloat("positiony", this.dataPositionY);
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.mobData = MobBottleMobData.GetFromTag(tag.getCompoundOrEmpty("bottle"));
		this.dataDirection = Direction.from3DDataValue(tag.getIntOr("dict", 0));
		this.setDataBlockFromString(tag.getStringOr("block", ""));
		this.dataScale = tag.getFloatOr("scale", 0.0f);
		this.dataPositionY = tag.getFloatOr("positiony", 0.0f);
	}
	
	protected Block getDataBlock() {
		if (this.dataBlock == null) {
			this.dataBlock = FirisBlocks.MOB_BOTTLE_EMPTY.get();
		}
		return this.dataBlock;
	}
	
	protected void setDataBlockFromString(String blockId) {
		//BlockIdからBlockを取得
		this.dataBlock = FirisUtil.getBlockFromId(blockId, null);
	}
	
	/**
	 * Drop用ItemStackTagを設定
	 */
	public void saveToItem(ItemStack stack) {
		//ItemStackがMob情報を持っている場合はTagを設定する
		if (!this.mobData.isEmpty()) {
			stack.set(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE, this.mobData);
		}
	}

	/**
	 * 描画用Entityキャッシュ
	 */
	@OnlyIn(Dist.CLIENT)
	protected Entity renderEntityCache;
	
	@OnlyIn(Dist.CLIENT)
	protected boolean isRenderEntityCache;
	
	/**
	 * Clientサイドの初期化
	 */
	@OnlyIn(Dist.CLIENT)
	protected void initClient()
	{
		this.renderEntityCache = null;
		this.isRenderEntityCache = false;
		this.renderEntityCacheMap = new HashMap<>();
	}
	
	/**
	 * 描画用Entity取得
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public Entity getRenderEntity() {
		if (this.renderEntityCache == null && !this.isRenderEntityCache) {
			CompoundTag tag = !this.mobData.isEmpty() ? this.mobData.tag() : null;
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
	private Map<CompoundTag, Entity> renderEntityCacheMap;
	
	/**
	 * アイテム描画に必要な情報を設定する
	 * @param stack
	 */
	@OnlyIn(Dist.CLIENT)
	public void setMobBottleDataFromBEWLR(ItemStack stack) {

		this.setLevel(Minecraft.getInstance().level);

		this.mobData = stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE);
		this.dataDirection = Direction.EAST;

		//キャッシュに存在しない場合はgetRenderEntityでEntityを生成する
		if (!this.renderEntityCacheMap.containsKey(this.mobData.tag())) {
			this.renderEntityCache = null;
			this.isRenderEntityCache = false;
			this.renderEntityCacheMap.put(this.mobData.tag(), this.getRenderEntity());
		}
		
		//キャッシュからEntityを反映
		this.renderEntityCache = this.renderEntityCacheMap.get(this.mobData.tag());
		this.isRenderEntityCache = true;
	}

	/**
	 * アイテム描画に必要な方角を設定する
	 */
	@OnlyIn(Dist.CLIENT)
	public void SetRendererDirection(Direction direction){
		this.dataDirection = direction;
	}
	
	/**
	 * Block.getRegistryNameの代替メソッド
	 */
	protected String getDataBlockRegistryName() {
		return FirisUtil.getIdFromBlock(this.getDataBlock(), null);
	}
}
