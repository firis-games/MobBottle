package firis.mobbottle.common.block;

import java.util.Optional;

import javax.annotation.Nullable;

import firis.mobbottle.MobBottle.FirisBlockEntityType;
import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.MobBottle.FirisItems;
import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MobBottleBlock extends BaseEntityBlock {

	//モブボトル当たり判定
	protected static final VoxelShape VS_MOB_BOTTLE_BLOCK = Shapes.create(
			new AABB(2.0D / 16.0D, 0.0D / 16.0D, 2.0D / 16.0D, 
					14.0D / 16.0D, 16.0D / 16.0D, 14.0D / 16.0D));
		
	public MobBottleBlock() {
		super((BlockBehaviour.Properties.of(Material.PISTON)).sound(SoundType.GLASS));
		
	}
	
	/**
	 * ブロック当たり判定
	 */
	@Override
	public VoxelShape getShape(BlockState p_48816_, BlockGetter p_48817_, BlockPos p_48818_, CollisionContext p_48819_) {
		return VS_MOB_BOTTLE_BLOCK;
	}
	
	/**
	 * FullBlockでない場合は1.0Fを返却する
	 */
	@Override
	public float getShadeBrightness(BlockState p_48731_, BlockGetter p_48732_, BlockPos p_48733_) {
		return 1.0F;
	}

	/**
	 * 空の光を透過する
	 */
	@Override
	public boolean propagatesSkylightDown(BlockState p_48740_, BlockGetter p_48741_, BlockPos p_48742_) {
		return true;
	}
	
	/**
	 * 描画用当たり判定
	 */
	@Override
	public VoxelShape getVisualShape(BlockState p_48735_, BlockGetter p_48736_, BlockPos p_48737_, CollisionContext p_48738_) {
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
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
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
		super.playerWillDestroy(level, pos, state, player);
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
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof MobBottleBlockEntity) {
			MobBottleBlockEntity mbBlockEntity = (MobBottleBlockEntity) blockentity;
			ItemStack stack = player.getItemInHand(hand);
			if (stack.isEmpty()) return InteractionResult.SUCCESS;
			Block block = Block.byItem(stack.getItem());
			String itemId = stack.getItem().getRegistryName().toString();
			
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
				bookStack.getOrCreateTag();
				bookStack.addTagElement("author", StringTag.valueOf(player.getName().getString()));
				bookStack.addTagElement("filtered_title", StringTag.valueOf("MobBottle"));
				bookStack.addTagElement("title", StringTag.valueOf("MobBottle[" + pos.toShortString() + "]"));
				ListTag listtag = new ListTag();
				listtag.add(StringTag.valueOf(mbTag.toString()));
				bookStack.addTagElement("pages", listtag);
				
				//モブボトル情報を設定
				bookStack.addTagElement("mobbottle", mbTag);
				
				player.setItemInHand(hand, bookStack);
			}
			else if (itemId.endsWith("minecraft:written_book")) {
				CompoundTag mbTag = stack.getTagElement("mobbottle");
				if (mbTag != null) {
					mbBlockEntity.setCopyMobBottleTag(mbTag);
				}
			}
			
			
			//各機能を実装
			
		}
		return InteractionResult.SUCCESS;
	}
}
