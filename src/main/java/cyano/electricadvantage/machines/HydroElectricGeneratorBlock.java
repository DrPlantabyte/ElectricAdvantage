package cyano.electricadvantage.machines;

import net.minecraft.world.World;

public class HydroElectricGeneratorBlock extends ElectricGeneratorBlock {

	public HydroElectricGeneratorBlock(){
		super();
	}
	
	@Override
	public ElectricGeneratorTileEntity createNewTileEntity(World w, int m) {
		return new HydroelectricGeneratorTileEntity();
	}

}
