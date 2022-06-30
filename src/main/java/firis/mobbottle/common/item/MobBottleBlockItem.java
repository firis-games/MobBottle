package firis.mobbottle.common.item;

import java.util.List;

import javax.annotation.Nullable;

import firis.mobbottle.client.renderer.MobBottleBlockEntityWithoutLevelRenderer;
import firis.mobbottle.common.helper.FirisEntityHelper;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;



/**
 * モブボトルアイテム
 */
public class MobBottleBlockItem extends BlockItem {

	public MobBottleBlockItem(Block block) {
		super(block, (new Item.Properties())
				.stacksTo(1)
				.tab(ItemGroup.TAB_TOOLS)
				.setISTER(() -> MobBottleBlockEntityWithoutLevelRenderer::new));
	}
	
	/**
	 * アイテムを使用する
	 */
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		
		//スニーク中
		if (!context.getPlayer().isShiftKeyDown()) {
			ItemStack stack = context.getItemInHand();
			if (!this.isCatchMobBottle(stack)) {
				Entity entity = FirisEntityHelper.createEntityFromTag(stack.getTag().getCompound("mob"), context.getLevel());
				if (entity != null) {
					//スポーン
					entity.moveTo(context.getClickLocation());
					if (context.getLevel() instanceof ServerWorld) {
						((ServerWorld)context.getLevel()).addFreshEntityWithPassengers(entity);
					}
					//Tag情報を初期化
					stack.setTag(null);
				}
			}
			return ActionResultType.SUCCESS;
		}
		
		//ブロック設置処理
		return super.useOn(context);
	}
	
	/**
	 * Entityを右クリック
	 */
	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity livingEntity, Hand hand) {
		//モブ情報取得
		catchMobBottle(player, livingEntity, hand);
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		//モブ情報取得
		catchMobBottle(player, entity, Hand.MAIN_HAND);
		return true;
	}
	
	/**
	 * モブを捕獲する
	 * @return
	 */
	protected boolean catchMobBottle(PlayerEntity player, Entity entity, Hand hand) {
		ItemStack handStack = player.getItemInHand(hand);
		
		if (!this.isCatchMobBottle(handStack)) return false;

		//モブ情報取得
		CompoundNBT mobTag = FirisEntityHelper.createTagFromEntity(entity);
		String mobName = entity.getDisplayName().getString();
		
		//ItemStackへモブ情報を設定
		CompoundNBT stackTag = handStack.getOrCreateTag();
		stackTag.put("mob", mobTag);
		stackTag.putString("mob_name", mobName);

		//モブを消去
		entity.remove(true);
		return true;
	}
	
	/**
	 * キャッチ可能か判定する
	 * @param stack
	 * @return
	 */
	protected boolean isCatchMobBottle(ItemStack stack) {
		return stack.getTagElement("mob") == null;
	}
	
	/**
	 * エンチャント表示
	 */
	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("mob");
	}
	
	/**
	 * アイテム名
	 */
	@Override
	public ITextComponent getName(ItemStack stack) {
		ITextComponent component = new TranslationTextComponent(this.getDescriptionId(stack));
		String mobName = stack.getOrCreateTag().getString("mob_name");
		if (!"".equals(mobName)) {
			component = new TranslationTextComponent(component.getString() + "  " + mobName + "");
		}
		return component;
	}
	
	/**
	 * info表示追加
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> listComponent, ITooltipFlag tooltipFlag) {
		String mobName = stack.getOrCreateTag().getString("mob_name");
		if (!"".equals(mobName)) {
			listComponent.add((new TranslationTextComponent("info.mobbottle.mob_bottle_in", mobName)).withStyle(TextFormatting.DARK_AQUA));
		} else {
			listComponent.add((new TranslationTextComponent("info.mobbottle.mob_bottle")).withStyle(TextFormatting.LIGHT_PURPLE));
		}
	}
	
}
