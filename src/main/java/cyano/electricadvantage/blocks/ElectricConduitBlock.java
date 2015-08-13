package cyano.electricadvantage.blocks;

import cyano.electricadvantage.init.Power;
import net.minecraft.block.material.Material;

public class ElectricConduitBlock  extends cyano.poweradvantage.api.simple.BlockSimplePowerConduit{

	public ElectricConduitBlock() {
		super(Material.piston, 0.75f, 2f/16f, Power.ELECTRIC_POWER);
	}
}
