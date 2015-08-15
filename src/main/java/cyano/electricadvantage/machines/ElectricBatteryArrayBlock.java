package cyano.electricadvantage.machines;

import net.minecraft.world.World;

public class ElectricBatteryArrayBlock extends ElectricGeneratorBlock {

	public ElectricBatteryArrayBlock(){
		super();
	}
	
	@Override
	public ElectricGeneratorTileEntity createNewTileEntity(World w, int m) {
		return new ElectricBatteryArrayTileEntity();
	}

}
