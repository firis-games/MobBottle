package firis.mobbottle.block.entity;


import firis.mobbottle.MobBottle;
import firis.mobbottle.util.FirisEntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

/***
 * MobBottleBlockEntityクライアントサイド処理
 * @OnlyIn(Dist.CLIENT)を分離
 */
public class MobBottleBlockEntityClient {

    private final MobBottleBlockEntity blockEntity;

    public MobBottleBlockEntityClient(MobBottleBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        //初期化
        this.initClient();
    }

    /**
     * 描画用Entityキャッシュ
     */
    protected Entity renderEntityCache;

    protected boolean isRenderEntityCache;

    /**
     * Clientサイドの初期化
     */
    protected void initClient() {
        this.renderEntityCache = null;
        this.isRenderEntityCache = false;
        this.renderEntityCacheMap = new HashMap<>();
    }

    /**
     * 描画用Entity取得
     *
     * @return
     */
    public Entity getRenderEntity() {
        if (this.renderEntityCache == null && !this.isRenderEntityCache) {
            CompoundTag tag = !this.blockEntity.mobData.isEmpty() ? this.blockEntity.mobData.tag() : null;
            this.renderEntityCache = FirisEntityHelper.createEntityFromTag(tag, this.blockEntity.getLevel());
            this.isRenderEntityCache = true;
        }
        return this.renderEntityCache;
    }

    /**
     * 描画用の方角を取得
     *
     * @return
     */
    public Direction getRenderDirection() {
        return this.blockEntity.dataDirection;
    }

    /**
     * 描画用の外装ブロックを取得
     *
     * @return
     */
    public BlockState getRenderBlockState() {
        return this.blockEntity.getDataBlock().defaultBlockState();
    }

    /**
     * 描画用のサイズ
     *
     * @return
     */
    public float getRenderScale() {
        return this.blockEntity.dataScale;
    }

    /**
     * 描画用のY軸
     *
     * @return
     */
    public float getRenderPositionY() {
        return this.blockEntity.dataPositionY;
    }


//**************************************************
// アイテム描画用処理
//**************************************************
    private Map<CompoundTag, Entity> renderEntityCacheMap;

    /**
     * アイテム描画に必要な情報を設定する
     *
     * @param stack
     */
    public void setMobBottleDataFromBEWLR(ItemStack stack) {

        this.blockEntity.setLevel(Minecraft.getInstance().level);

        this.blockEntity.mobData = stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE);
        this.blockEntity.dataDirection = Direction.EAST;

        //キャッシュに存在しない場合はgetRenderEntityでEntityを生成する
        if (!this.renderEntityCacheMap.containsKey(this.blockEntity.mobData.tag())) {
            this.renderEntityCache = null;
            this.isRenderEntityCache = false;
            this.renderEntityCacheMap.put(this.blockEntity.mobData.tag(), this.getRenderEntity());
        }

        //キャッシュからEntityを反映
        this.renderEntityCache = this.renderEntityCacheMap.get(this.blockEntity.mobData.tag());
        this.isRenderEntityCache = true;
    }

    /**
     * アイテム描画に必要な方角を設定する
     */
    public void SetRendererDirection(Direction direction) {
        this.blockEntity.dataDirection = direction;
    }

}
