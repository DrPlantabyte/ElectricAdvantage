package cyano.electricadvantage.machines;

import net.minecraft.world.World;

public class ElectricFurnaceBlock extends ElectricMachineBlock{
	
	public ElectricFurnaceBlock(){
		super();
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World w, int m) {
		return new ElectricFurnaceTileEntity();
	}

}
