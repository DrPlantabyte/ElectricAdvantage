package cyano.electricadvantage.machines;

import net.minecraft.world.World;

public class ElectricCrusherBlock extends ElectricMachineBlock{
	// TODO: figure out why this block turns into a different block when it gets power
	public ElectricCrusherBlock(){
		super();
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World w, int m) {
		return new ElectricCrusherTileEntity();
	}

}
