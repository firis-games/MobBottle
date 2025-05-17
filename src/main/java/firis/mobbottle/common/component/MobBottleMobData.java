package firis.mobbottle.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/***
 * モブボトルアイテムのデータ定義
 * @param tag
 * @param name
 */
public record MobBottleMobData(CompoundTag tag, String name) {

    /**
     * 空のMobBottleMobDataを生成
     */
    public static final MobBottleMobData Empty() {return new MobBottleMobData(new CompoundTag(), "");}

    /**
     * CODEC
     */
    public static final Codec<MobBottleMobData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            CompoundTag.CODEC.fieldOf("tag").forGetter(MobBottleMobData::tag),
                            Codec.STRING.fieldOf("name").forGetter(MobBottleMobData::name)
                    )
                    .apply(instance, MobBottleMobData::new)
    );

    /**
     * STREAM_CODEC
     */
    public static final StreamCodec<ByteBuf, MobBottleMobData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, MobBottleMobData::tag,
            ByteBufCodecs.STRING_UTF8, MobBottleMobData::name,
            MobBottleMobData::new
    );

    /***
     * コンストラクタ
     * @param tag
     * @param name
     */
    public MobBottleMobData(CompoundTag tag, String name) {
        this.tag = tag.copy();
        this.name = name;
    }

    /**
     * tagの取得Override
     * 書き換えられても影響がないようにコピーを渡す
     * @return
     */
    @Override
    public CompoundTag tag()
    {
        return this.tag.copy();
    }

    /***
     * モブ情報
     * @return
     */
    public boolean isEmpty()
    {
        return this.tag.isEmpty();
    }

    /***
     * CompoundTagへ変換
     * @return
     */
    public CompoundTag getCompoundTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("tag", this.tag.copy());
        tag.putString("name", this.name);
        return tag;
    }

    /***
     * CompoundTagから変換
     * @param tag
     * @return
     */
    public static MobBottleMobData GetFromTag(CompoundTag tag)
    {
        if (!tag.contains("tag") || !tag.contains("name")) {
            return MobBottleMobData.Empty();
        }
        return new MobBottleMobData(tag.getCompound("tag"), tag.getString("name"));
    }
}
