package cyano.electricadvantage.machines;

import net.minecraft.world.World;

public class ElectricCrusherBlock extends ElectricMachineBlock{
	// NOTE: If a block's meta value goes above 0xF, then the block turns into a different block when it state changes
	public ElectricCrusherBlock(){
		super();
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World w, int m) {
		return new ElectricCrusherTileEntity();
	}

}
