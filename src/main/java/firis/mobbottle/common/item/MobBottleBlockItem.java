package firis.mobbottle.common.item;

import java.util.List;

import firis.mobbottle.MobBottle;
import firis.mobbottle.client.renderer.MobBottleBlockEntityWithoutLevelRenderer;
import firis.mobbottle.common.component.MobBottleMobData;
import firis.mobbottle.common.helper.FirisEntityHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * モブボトルアイテム
 */
public class MobBottleBlockItem extends BlockItem {

	public MobBottleBlockItem(Block block) {
		super(block,
				(new Item.Properties())
				.stacksTo(1)
				.component(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE.get(), MobBottleMobData.EMPTY)
		);
	}
	
	/**
	 * アイテムを使用する
	 */
	@Override
	public InteractionResult useOn(UseOnContext context) {
		
		//スニーク中
		if (!context.getPlayer().isShiftKeyDown()) {
			ItemStack stack = context.getItemInHand();
			if (!this.isCatchMobBottle(stack)) {
				CompoundTag mobTag = stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE).tag();
				Entity entity = FirisEntityHelper.createEntityFromTag(mobTag, context.getLevel());
				if (entity != null) {
					//スポーン
					entity.moveTo(context.getClickLocation());
					if (context.getLevel() instanceof ServerLevel) {
						((ServerLevel)context.getLevel()).addFreshEntityWithPassengers(entity);
					}
					//Tag情報を初期化
					stack.set(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE, MobBottleMobData.EMPTY);
				}
			}
			return InteractionResult.SUCCESS;
		}
		
		//ブロック設置処理
		return super.useOn(context);
	}
	
	/**
	 * Entityを右クリック
	 */
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
		//モブ情報取得
		catchMobBottle(player, livingEntity, hand);
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		//モブ情報取得
		catchMobBottle(player, entity, InteractionHand.MAIN_HAND);
		return true;
	}
	
	/**
	 * モブを捕獲する
	 * @return
	 */
	protected boolean catchMobBottle(Player player, Entity entity, InteractionHand hand) {
		ItemStack handStack = player.getItemInHand(hand);
		
		if (!this.isCatchMobBottle(handStack)) return false;
		
		//騎乗中は拒否
		if (entity.getVehicle() != null) {
			return false;
		}
		
		//モブ情報取得
		CompoundTag mobTag = FirisEntityHelper.createTagFromEntity(entity);
		String mobName = entity.getDisplayName().getString();
		
		//ItemStackへモブ情報を設定
		handStack.set(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE, new MobBottleMobData(mobTag, mobName));
		//モブを消去
		entity.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
		return true;
	}
	
	/**
	 * キャッチ可能か判定する
	 * @param stack
	 * @return
	 */
	protected boolean isCatchMobBottle(ItemStack stack) {
		return stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE).isEmpty();
	}
	
	/**
	 * エンチャント表示
	 */
	@Override
	public boolean isFoil(ItemStack stack) {
		return !stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE).isEmpty();
	}
	
	/**
	 * アイテム名
	 */
	@Override
	public Component getName(ItemStack stack) {
		Component component = Component.translatable(this.getDescriptionId(stack));
		String mobName = stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE).name();
		if (!"".equals(mobName)) {
			component = Component.translatable(component.getString() + "  " + mobName + "");
		}
		return component;
	}
	
	/**
	 * info表示追加
	 */
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		String mobName = stack.get(MobBottle.FirisDataComponentType.MOBBOTTLE_TYPE).name();
		if (!"".equals(mobName)) {
			tooltipComponents.add(Component.translatable("info.mobbottle.mob_bottle_in", mobName).withStyle(ChatFormatting.DARK_AQUA));
		} else {
			tooltipComponents.add(Component.translatable("info.mobbottle.mob_bottle").withStyle(ChatFormatting.LIGHT_PURPLE));
		}
	}
	
	/**
	 * BlockEntityWithoutLevelRenderer描画用定義
	 */
	@Override
	@Deprecated(forRemoval = true, since = "1.21")
	public void initializeClient(java.util.function.Consumer<net.neoforged.neoforge.client.extensions.common.IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private final MobBottleBlockEntityWithoutLevelRenderer renderer = new MobBottleBlockEntityWithoutLevelRenderer();
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}
}
