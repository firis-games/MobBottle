package firis.mobbottle.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
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
		
	public MobBottleEmptyBlock() {
		super(BlockBehaviour.Properties.copy(Blocks.PISTON).sound(SoundType.GLASS));
		
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
	
	public RenderShape getRenderShape(BlockState p_49232_) {
		return RenderShape.MODEL;
	}
}
