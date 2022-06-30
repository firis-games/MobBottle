package firis.mobbottle.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class MobBottleEmptyBlock extends Block {

	//モブボトル当たり判定
	protected static final VoxelShape VS_MOB_BOTTLE_BLOCK = VoxelShapes.create(
			new AxisAlignedBB(2.0D / 16.0D, 0.0D / 16.0D, 2.0D / 16.0D, 
					14.0D / 16.0D, 16.0D / 16.0D, 14.0D / 16.0D));
		
	public MobBottleEmptyBlock() {
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
}
