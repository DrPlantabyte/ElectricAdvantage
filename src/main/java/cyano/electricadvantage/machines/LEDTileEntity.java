package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerConsumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class LEDTileEntity extends TileEntitySimplePowerConsumer{

	private static final float ENERGY_COST = 0.5f;
	
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
		this.setEnergy(Float.intBitsToFloat(dataArray[0]), getType());
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataArray[0] = Float.floatToIntBits(getEnergy());
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
		boolean on = getEnergy() > 0;
		IBlockState bs = getWorld().getBlockState(getPos());
		if(on){
			this.subtractEnergy(ENERGY_COST, getType());
		}
		if((Boolean)bs.getValue(LEDBlock.LIT) != on){
			getWorld().setBlockState(getPos(),bs.withProperty(LEDBlock.LIT, on));
		}
	}

}
