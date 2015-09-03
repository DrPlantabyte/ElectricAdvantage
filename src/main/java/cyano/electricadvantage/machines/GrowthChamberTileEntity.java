package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import net.minecraft.item.ItemStack;

public class GrowthChamberTileEntity extends cyano.poweradvantage.api.simple.TileEntitySimplePowerConsumer {

	private final ItemStack[] inventory = new ItemStack[9]; // 3 input slots, 6 output slots
	
	public GrowthChamberTileEntity() {
		super(Power.GROWTHCHAMBER_POWER, 32, GrowthChamberTileEntity.class.getName());
	}


	@Override
	public void tickUpdate(boolean isServer) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int[] getDataFieldArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ItemStack[] getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDataFieldUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareDataFieldsForSync() {
		// TODO Auto-generated method stub
		
	}

}
