package firis.mobbottle.block.entity;

import firis.mobbottle.MobBottle;
import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.component.MobBottleMobData;
import firis.mobbottle.util.FirisUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

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

    protected final MobBottleBlockEntityClient client;

    public MobBottleBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(MobBottle.FirisBlockEntityType.BLOCK_ENTITY_TYPE.get(), p_155229_, p_155230_);

        //Client処理の初期化
        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.client = new MobBottleBlockEntityClient(this);
        } else {
            this.client = null;
        }
    }

    /**
     * ブロック設置時に必要な情報を設定する
     *
     * @param stack アイテム情報を保持
     * @param direction 方角
     */
    public void setMobBottleData(ItemStack stack, Direction direction) {
        this.mobData = stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE);
        this.dataDirection = direction;
    }

    /**
     * モブボトルの外装ブロックを設定する
     *
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
        tag.putFloat("pos_y", this.dataPositionY);
        return tag;
    }

    /**
     * モブボトルの情報を設定
     */
    public void setCopyMobBottleTag(CompoundTag tag) {
        this.setDataBlockFromString(tag.getStringOr("block", ""));
        this.dataScale = tag.getFloatOr("scale", 0.0f);
        this.dataPositionY = tag.getFloatOr("pos_y", 0.0f);
        this.setChanged();
    }

    /***
     * チャンクロード時のTag設定
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        this.saveAdditional(output);
        return output.buildResult();
    }

    /***
     * チャンクロード時のTagロード
     */
    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
    }

    /***
     * 手動同期時のTag設定
     */
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /***
     * 手動同期時のTagロード
     */
    @Override
    public void onDataPacket(Connection net, ValueInput valueInput) {
        super.onDataPacket(net, valueInput);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.store("bottle", CompoundTag.CODEC, this.mobData.getCompoundTag());
        output.putInt("dict", this.dataDirection.get3DDataValue());
        output.putString("block", this.getDataBlockRegistryName());
        output.putFloat("scale", this.dataScale);
        output.putFloat("pos_y", this.dataPositionY);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.read("bottle", CompoundTag.CODEC).ifPresent((bottleData) -> {
            this.mobData = MobBottleMobData.GetFromTag(bottleData);
        });
        this.dataDirection = Direction.from3DDataValue(input.getIntOr("dict", 0));
        this.setDataBlockFromString(input.getStringOr("block", ""));
        this.dataScale = input.getFloatOr("scale", 0.0f);
        this.dataPositionY = input.getFloatOr("pos_y", 0.0f);
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
     * Block.getRegistryNameの代替メソッド
     */
    protected String getDataBlockRegistryName() {
        return FirisUtil.getIdFromBlock(this.getDataBlock(), null);
    }

    /***
     * クライアントから呼ばれる処理
     */
    public MobBottleBlockEntityClient getClient() {
        return this.client;
    }
}
