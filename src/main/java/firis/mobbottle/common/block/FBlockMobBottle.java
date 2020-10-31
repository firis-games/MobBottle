package firis.mobbottle.common.block;

import firis.mobbottle.common.config.FirisConfig;
import firis.mobbottle.common.tileentity.FTileEntityMobBottle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FBlockMobBottle extends BlockContainer {

	//モブボトル当たり判定
	public static final AxisAlignedBB MOB_BOTTLE_BLOCK_AABB = new AxisAlignedBB(
			2.0D / 16.0D, 0.0D / 16.0D, 2.0D / 16.0D, 
			14.0D / 16.0D, 16.0D / 16.0D, 14.0D / 16.0D);
	
	//プレート当たり判定
	public static final AxisAlignedBB MOB_BOTTLE_PLATE_BLOCK_AABB = new AxisAlignedBB(
			1.0D / 16.0D, 0.0D / 16.0D, 1.0D / 16.0D, 
			15.0D / 16.0D, 1.0D / 16.0D, 15.0D / 16.0D);
	
	/**
	 * コンストラクタ
	 */
	public FBlockMobBottle() {
		super(Material.PISTON);
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new FTileEntityMobBottle();
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }
	
	/**
	 * ブロックのテクスチャを透過する
	 */
	@Override
    @SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
	/**
	 * ブロック破壊時のイベント
	 */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity != null && tileentity instanceof FTileEntityMobBottle) {
        	
        	FTileEntityMobBottle tile = (FTileEntityMobBottle) tileentity;
        	
        	ItemStack dropStack = tile.getItemStackToMobBottle();
        	
        	Block.spawnAsEntity(worldIn, pos, dropStack);
        	worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    
    @Deprecated
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	//プレートタイプ
    	if (FirisConfig.cfg_general_mob_bottle_collision_type == 1) {
            return MOB_BOTTLE_PLATE_BLOCK_AABB;
    	}
    	//デフォルト
        return MOB_BOTTLE_BLOCK_AABB;
    }
	
    /**
     * 通常ドロップ抑制
     */
    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    	
    }
    
    /**
     * ブロックを右クリック
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	//メインハンド以外は処理スキップ
    	if (!(hand == EnumHand.MAIN_HAND)) return false;
    	//アイテムが空の場合はスキップ
    	if (playerIn.getHeldItem(hand).isEmpty()) return false;

    	TileEntity tileentity = worldIn.getTileEntity(pos);
    	if (!(tileentity instanceof FTileEntityMobBottle)) return false;
    	FTileEntityMobBottle tileBottle = ((FTileEntityMobBottle)tileentity);
    	
    	//手持ちアイテムのIDを取得する
    	String handItemId = playerIn.getHeldItem(hand).getItem().getRegistryName().toString();
    	
    	//金のツルハシを持っている場合にBottleCoverTypeを変更する
    	if (handItemId.indexOf(FirisConfig.cfg_display_tool_cover_type) != -1) {
    		tileBottle.setNextBottleCoverType();
    		return true;
    		
   		//金のクワを持っている場合にサイズを大きくする
    	} else if (handItemId.indexOf(FirisConfig.cfg_display_tool_scale_up) != -1) {
    		//大きくなる
    		tileBottle.setChangeScale(false);
    		return true;
    		
   		//金のオノを持っている場合にサイズを小さくする
    	} else if (handItemId.indexOf(FirisConfig.cfg_display_tool_scale_down) != -1) {
    		//小さくなる
    		tileBottle.setChangeScale(true);
    		return true;
    		
   		//金のシャベルを持っている場合にサイズを大きくする
    	} else if (handItemId.indexOf(FirisConfig.cfg_display_tool_rotation) != -1) {
    		//回転させる
    		tileBottle.setRotationBottle();
    		return true;
    	
    	//砂糖でメイドさん連携
    	} else if (FirisConfig.cfg_general_enable_lmrfp_collaboration
    			&& Items.SUGAR.getRegistryName().toString().equals(handItemId)) {
    		//モーション変更
    		tileBottle.setNextMaidMotion();
    		return true;
    	}
    	
        return false;
    }
    
    /**
     * アクセス修飾子をpublicへ変更
     */
    public Block setSoundType(SoundType sound) {
        return super.setSoundType(sound);
    }
}
