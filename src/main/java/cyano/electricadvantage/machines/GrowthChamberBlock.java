package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class GrowthChamberBlock extends ElectricMachineBlock{

	public GrowthChamberBlock(){
		super(Material.piston,Power.GROWTHCHAMBER_POWER);
	}

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World w, int m) {
		return new GrowthChamberTileEntity();
	}
}
