package firis.mobbottle.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MobBottleEmptyBlock extends Block {

	//モブボトル当たり判定
	protected static final VoxelShape VS_MOB_BOTTLE_BLOCK = Shapes.create(
			new AABB(2.0D / 16.0D, 0.0D / 16.0D, 2.0D / 16.0D, 
					14.0D / 16.0D, 16.0D / 16.0D, 14.0D / 16.0D));

	public MobBottleEmptyBlock(BlockBehaviour.Properties properties) {
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
	
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
