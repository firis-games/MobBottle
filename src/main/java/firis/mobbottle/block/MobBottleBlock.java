package firis.mobbottle.block;

import com.mojang.serialization.MapCodec;
import firis.mobbottle.MobBottle.FirisBlockEntityType;
import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.MobBottle.FirisItems;
import firis.mobbottle.block.entity.MobBottleBlockEntity;
import firis.mobbottle.util.FirisUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class MobBottleBlock extends BaseEntityBlock {

	//MobBottle Properties
	public static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isRedstoneConductor((BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) -> false)
			.isSuffocating((BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) -> false)
			.pushReaction(PushReaction.BLOCK)
			.sound(SoundType.GLASS);

	//MobBottle Codec
	public static final MapCodec<MobBottleBlock> CODEC = simpleCodec(MobBottleBlock::new);

	//モブボトル当たり判定
	protected static final VoxelShape VS_MOB_BOTTLE_BLOCK = Shapes.create(
			new AABB(2.0D / 16.0D, 0.0D / 16.0D, 2.0D / 16.0D, 
					14.0D / 16.0D, 16.0D / 16.0D, 14.0D / 16.0D));

	public MobBottleBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}
	
	/**
	 * ブロック当たり判定
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return VS_MOB_BOTTLE_BLOCK;
	}
	
	/**
	 * FullBlockでない場合は1.0Fを返却する
	 */
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}

	/**
	 * 描画用当たり判定
	 */
	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MobBottleBlockEntity(pos, state);
	}
	
	/**
	 * ブロック設置時にアイテム情報を設定
	 */
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		Optional<MobBottleBlockEntity> optBlockEntity = level.getBlockEntity(pos, FirisBlockEntityType.BLOCK_ENTITY_TYPE.get());
		if (!optBlockEntity.isEmpty()) {
			//ブロック描画情報を設定
			optBlockEntity.get().setMobBottleData(stack, getHorizontalDirection(entity));
		}
	}
	
	/**
	 * ブロック破壊時にブロック情報を設定
	 */
	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof MobBottleBlockEntity) {
			MobBottleBlockEntity mobBottleBlockEntity = (MobBottleBlockEntity)blockentity;
			if (!level.isClientSide) {
				ItemStack itemstack = new ItemStack(FirisItems.MOB_BOTTLE.get());
				mobBottleBlockEntity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}
	
	/**
	 * Entityが向いている方角を取得
	 */
	protected Direction getHorizontalDirection(@Nullable LivingEntity entity) {
		return entity == null ? Direction.NORTH : entity.getDirection();
	}
	
	/**
	 * ブロックを右クリック
	 */
	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof MobBottleBlockEntity) {
			MobBottleBlockEntity mbBlockEntity = (MobBottleBlockEntity) blockentity;
			if (stack.isEmpty()) return InteractionResult.SUCCESS;
			Block block = Block.byItem(stack.getItem());
			
			String itemId = FirisUtil.getIdFromItem(stack.getItem(), "");
			
			//モブボトルの場合
			if (block.equals(FirisBlocks.MOB_BOTTLE.get())) {
				mbBlockEntity.setMobBottleBlock(FirisBlocks.MOB_BOTTLE_EMPTY.get());
			}
			else if (itemId.endsWith("_sword")) {
				//外装ケースを消す
				mbBlockEntity.setMobBottleBlock(Blocks.AIR);
			}
			//ブロックの場合(空気ブロックでない場合)
			else if (!block.defaultBlockState().isAir()) {
				//外装ブロックを設定
				mbBlockEntity.setMobBottleBlock(block);
			}
			//クワ系アイテムの場合
			else if (itemId.endsWith("_hoe")) {
				//拡大
				mbBlockEntity.setMobBottleScalePlus();
			}
			else if (itemId.endsWith("_axe")) {
				//縮小
				mbBlockEntity.setMobBottleScaleMinus();
			}
			else if (itemId.endsWith("_pickaxe")) {
				//上方向へ調整
				mbBlockEntity.setMobBottlePositionPlus();
			}
			else if (itemId.endsWith("_shovel")) {
				//下方向へ調整
				mbBlockEntity.setMobBottlePositionMinus();
			}
			else if (itemId.endsWith("minecraft:writable_book")) {

				//モブボトルのコピー情報
				CompoundTag mbTag = mbBlockEntity.getCopyMobBottleTag();
				
				//ペン付き本の初期設定
				ItemStack bookStack = new ItemStack(Items.WRITTEN_BOOK);

				//書き込み済み本の情報を設定
				WrittenBookContent bookCmp = new WrittenBookContent(
						Filterable.passThrough("MobBottle[" + pos.toShortString() + "]"),
						player.getName().getString(),
						0,
						List.of(),
						true
				);
				bookStack.set(DataComponents.WRITTEN_BOOK_CONTENT, bookCmp);

				//カスタムデータにモブボトル情報を設定
				CompoundTag customTag = new CompoundTag();
				customTag.put("mobbottle", mbTag);
				bookStack.set(DataComponents.CUSTOM_DATA, CustomData.of(customTag));

				player.setItemInHand(hand, bookStack);
			}
			else if (itemId.endsWith("minecraft:written_book")) {
				//カスタムデータ取得
				CustomData cstmData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
				CompoundTag cstmTag = cstmData.copyTag();

				cstmTag.getCompound("mobbottle").ifPresent(tag -> {
					mbBlockEntity.setCopyMobBottleTag(tag);
				});
			}
			else if (itemId.endsWith("minecraft:stick")) {
				//人形モードを設定
				mbBlockEntity.setFigureMode();
			}			
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected MapCodec<MobBottleBlock> codec() {
		return CODEC;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}
}
