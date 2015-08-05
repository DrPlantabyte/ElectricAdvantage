package cyano.electricadvantage.machines;

import java.util.Arrays;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.electricadvantage.init.Power;

public abstract class ElectricGeneratorTileEntity extends cyano.poweradvantage.api.simple.TileEntitySimplePowerSource {

	private final ItemStack[] inventory;
	private final int[] inputSlots;
	
	public ElectricGeneratorTileEntity(String name,int numInputSlots) {
		super(Power.electric_power, 2000, name);
		inventory = new ItemStack[numInputSlots];
		inputSlots = new int[numInputSlots];
		for(int i = 0; i < inventory.length; i++){
			if(i < inputSlots.length)inputSlots[i] = i;
		}
	}


	private boolean redstone = false;
	private float oldEnergy = 0f;
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		
		redstone = getWorld().isBlockPowered(getPos());
		
		if(oldEnergy != this.getEnergy()){
			this.sync();
		}
	}
	

	
	protected void setActive(boolean active){
		IBlockState old = getWorld().getBlockState(getPos());
		if(old.getBlock() instanceof ElectricGeneratorBlock 
				&& (Boolean)old.getValue(ElectricGeneratorBlock.ACTIVE) != active){
			getWorld().setBlockState(getPos(), old.withProperty(ElectricGeneratorBlock.ACTIVE, active));
		}
	}

	
	@SideOnly(Side.CLIENT)
	public abstract float getPowerOutput();

	public ItemStack getInputSlot(int i){
		if(i < inputSlots.length){
			return inventory[inputSlots[i]];
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
			return null;
		}
	}
	

	public int numberOfInputSlots(){return inputSlots.length;}
	
	
	@Override
	protected ItemStack[] getInventory() {
		return inventory;
	}

	
	@Override
	public boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
		return isValidInputItem(srcItem) && isInArray(slot,inputSlots);
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack srcItem) {
		return isValidInputItem(srcItem) && isInArray(slot,inputSlots);
	}
	
	protected abstract boolean isValidInputItem(ItemStack item);
	
	protected final boolean isInArray(int i, int[] arr){
		for(int n = 0; n < arr.length; n++){
			if(arr[n] == i)return true;
		}
		return false;
	}
	
	

	public int getComparatorOutput(){
		return (int)(15 * this.getEnergy() / this.getEnergyCapacity());
	}

}
