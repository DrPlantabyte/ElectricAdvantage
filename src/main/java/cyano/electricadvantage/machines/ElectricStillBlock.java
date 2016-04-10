package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerConnectorContext;
import cyano.poweradvantage.init.Fluids;
import net.minecraft.world.World;

public class ElectricStillBlock extends ElectricMachineBlock{

	@Override
	public ElectricMachineTileEntity createNewTileEntity(World w, int m) {
		return new ElectricStillTileEntity();
	}

///// Overrides to make this a multi-type block /////
	

	@Override
	public boolean canAcceptConnection(PowerConnectorContext connection){
		return ConduitType.areSameType(Power.ELECTRIC_POWER, connection.powerType)
				|| Fluids.isFluidType(connection.powerType);
	}

	private final ConduitType[] types = {Power.ELECTRIC_POWER, Fluids.fluidConduit_general};

	@Override
	public ConduitType[] getTypes(){
		return types;
	}/**
	 * Determines whether this block/entity should receive energy
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType p){
		return ConduitType.areSameType(Power.ELECTRIC_POWER,p) || Fluids.isFluidType(p);
	}
	/**
	 * Determines whether this block/entity can provide energy
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType p){
		return Fluids.isFluidType(p);
	}
	///// end multi-type overrides /////
	
}
