package firis.mobbottle.common.block;

import javax.annotation.Nullable;

import firis.mobbottle.MobBottle.FirisBlocks;
import firis.mobbottle.MobBottle.FirisItems;
import firis.mobbottle.common.blockentity.MobBottleBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;


public class MobBottleBlock extends ContainerBlock {

	//モブボトル当たり判定
	protected static final VoxelShape VS_MOB_BOTTLE_BLOCK = VoxelShapes.create(
			new AxisAlignedBB(2.0D / 16.0D, 0.0D / 16.0D, 2.0D / 16.0D, 
					14.0D / 16.0D, 16.0D / 16.0D, 14.0D / 16.0D));
		
	public MobBottleBlock() {
		super((AbstractBlock.Properties.of(Material.PISTON)).sound(SoundType.GLASS));
		
	}
	
	/**
	 * ブロック当たり判定
	 */
	@Override
	public VoxelShape getShape(BlockState p_48816_, IBlockReader p_48817_, BlockPos p_48818_, ISelectionContext p_48819_) {
		return VS_MOB_BOTTLE_BLOCK;
	}
	
	/**
	 * FullBlockでない場合は1.0Fを返却する
	 */
	@Override
	public float getShadeBrightness(BlockState p_48731_, IBlockReader p_48732_, BlockPos p_48733_) {
		return 1.0F;
	}

	/**
	 * 空の光を透過する
	 */
	@Override
	public boolean propagatesSkylightDown(BlockState p_48740_, IBlockReader p_48741_, BlockPos p_48742_) {
		return true;
	}
	
	/**
	 * 描画用当たり判定
	 */
	@Override
	public VoxelShape getVisualShape(BlockState p_48735_, IBlockReader p_48736_, BlockPos p_48737_, ISelectionContext p_48738_) {
		return VoxelShapes.empty();
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new MobBottleBlockEntity();
	}
	
	/**
	 * ブロック設置時にアイテム情報を設定
	 */
	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		TileEntity optBlockEntity = level.getBlockEntity(pos);
		if (optBlockEntity instanceof MobBottleBlockEntity) {
			//ブロック描画情報を設定
			((MobBottleBlockEntity) optBlockEntity).setMobBottleData(stack, getHorizontalDirection(entity));
		}
	}
	
	/**
	 * ブロック破壊時にブロック情報を設定
	 */
	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof MobBottleBlockEntity) {
			MobBottleBlockEntity mobBottleBlockEntity = (MobBottleBlockEntity)blockentity;
			if (!level.isClientSide) {
				ItemStack itemstack = new ItemStack(FirisItems.MOB_BOTTLE);
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
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockHitResult) {
		TileEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof MobBottleBlockEntity) {
			MobBottleBlockEntity mbBlockEntity = (MobBottleBlockEntity) blockentity;
			ItemStack stack = player.getItemInHand(hand);
			if (stack.isEmpty()) return ActionResultType.SUCCESS;
			Block block = Block.byItem(stack.getItem());
			String itemId = stack.getItem().getRegistryName().toString();
			
			//モブボトルの場合
			if (block.equals(FirisBlocks.MOB_BOTTLE)) {
				mbBlockEntity.setMobBottleBlock(FirisBlocks.MOB_BOTTLE_EMPTY);
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
				CompoundNBT mbTag = mbBlockEntity.getCopyMobBottleTag();
				
				//ペン付き本の初期設定
				ItemStack bookStack = new ItemStack(Items.WRITTEN_BOOK);
				bookStack.getOrCreateTag();
				bookStack.addTagElement("author", StringNBT.valueOf(player.getName().getString()));
				bookStack.addTagElement("filtered_title", StringNBT.valueOf("MobBottle"));
				bookStack.addTagElement("title", StringNBT.valueOf("MobBottle[" + pos.toShortString() + "]"));
				ListNBT listtag = new ListNBT();
				listtag.add(StringNBT.valueOf(mbTag.toString()));
				bookStack.addTagElement("pages", listtag);
				
				//モブボトル情報を設定
				bookStack.addTagElement("mobbottle", mbTag);
				
				player.setItemInHand(hand, bookStack);
			}
			else if (itemId.endsWith("minecraft:written_book")) {
				CompoundNBT mbTag = stack.getTagElement("mobbottle");
				if (mbTag != null) {
					mbBlockEntity.setCopyMobBottleTag(mbTag);
				}
			}
		}
		return ActionResultType.SUCCESS;
	}
}
