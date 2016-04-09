package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LEDTileEntity extends TileEntitySimplePowerMachine {

	private static final float ENERGY_COST = 0.125f;
	
	public LEDTileEntity() {
		super(Power.ELECTRIC_POWER, ENERGY_COST * 4, LEDTileEntity.class.getName());
	}

	private final ItemStack[] none = new ItemStack[0];
	@Override
	protected ItemStack[] getInventory() {
		return none;
	}
	
	private final int[] dataArray = new int[1];
	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}


	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataArray[0]),Power.ELECTRIC_POWER);
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataArray[0] = Float.floatToIntBits(getEnergy(Power.ELECTRIC_POWER));
	}
	

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		// used to allow change in blockstate without interrupting the TileEntity or the GUI
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public void tickUpdate(boolean arg0) {
		// do nothing
	}
	
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		boolean on = getEnergy(Power.ELECTRIC_POWER) > 0;
		IBlockState bs = getWorld().getBlockState(getPos());
		if(on){
			this.subtractEnergy(ENERGY_COST, Power.ELECTRIC_POWER);
		}
		if((Boolean)bs.getValue(LEDBlock.LIT) != on){
			getWorld().setBlockState(getPos(),bs.withProperty(LEDBlock.LIT, on));
		}
	}
	
	@Override
	public PowerRequest getPowerRequest(ConduitType type){
		if(!ConduitType.areSameType(Power.ELECTRIC_POWER,type)) return PowerRequest.REQUEST_NOTHING;
		float amount = this.getEnergyCapacity(Power.ELECTRIC_POWER) - this.getEnergy(Power.ELECTRIC_POWER);
		if(amount > 0){
			return new PowerRequest(PowerRequest.HIGH_PRIORITY,amount,this);
		} else {
			return PowerRequest.REQUEST_NOTHING;
		}
	}

	@Override
	public boolean isPowerSink(ConduitType conduitType) {
		return true;
	}

	@Override
	public boolean isPowerSource(ConduitType conduitType) {
		return false;
	}
}
