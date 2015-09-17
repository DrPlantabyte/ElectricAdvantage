package cyano.electricadvantage.machines;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class HydroElectricGeneratorBlock extends ElectricGeneratorBlock {

	public HydroElectricGeneratorBlock(){
		super();
	}
	
	@Override
	public ElectricGeneratorTileEntity createNewTileEntity(World w, int m) {
		return new HydroelectricGeneratorTileEntity();
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState( pos.down()).getBlock().getMaterial().isSolid();
	}
}
