package cyano.electricadvantage.machines;

import cyano.electricadvantage.blocks.ElectricConduitBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class LEDBlock extends ElectricConduitBlock implements ITileEntityProvider{


	public static final PropertyBool LIT = PropertyBool.create("lit");
	
	public LEDBlock(){
		super();
		this.setDefaultState(getDefaultState().withProperty(LIT, false));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new LEDTileEntity();
	}
	
	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos){
		IBlockState bs = world.getBlockState(pos);
		if(bs.getBlock() == this && (Boolean)bs.getValue(LIT) == true){
			return 15;
		} else {
			return 0;
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState bs){
		if(bs.getBlock() == this && (Boolean)bs.getValue(LIT) == true){
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public IBlockState getStateFromMeta(int m){
		return this.getDefaultState().withProperty(LIT, (m & 1) != 0);
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { WEST, DOWN, SOUTH, EAST, UP, NORTH, LIT });
	}
	
	@Override
	public boolean isPowerSink(){
		return true;
	}
}
