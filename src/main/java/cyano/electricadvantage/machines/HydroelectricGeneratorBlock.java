package cyano.electricadvantage.machines;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class HydroelectricGeneratorBlock extends ElectricGeneratorBlock {

	public HydroelectricGeneratorBlock(){
		super();
	}
	
	@Override
	public ElectricGeneratorTileEntity createNewTileEntity(World w, int m) {
		return new HydroelectricGeneratorTileEntity();
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		return super.canPlaceBlockAt(worldIn, pos) && !worldIn.getBlockState( pos.down()).getBlock().getMaterial().isSolid();
	}
}
