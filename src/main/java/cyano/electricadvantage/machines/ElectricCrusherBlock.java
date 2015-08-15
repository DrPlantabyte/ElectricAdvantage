package cyano.electricadvantage.machines;

import net.minecraft.world.World;

public class ElectricCrusherBlock extends ElectricMachineBlock{
	
	public ElectricCrusherBlock(){
		super();
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World w, int m) {
		return new ElectricCrusherTileEntity();
	}

}
