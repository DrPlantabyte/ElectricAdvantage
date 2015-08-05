package cyano.electricadvantage.machines;

import java.util.Arrays;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.electricadvantage.init.Power;

public abstract class ElectricMachineTileEntity extends cyano.poweradvantage.api.simple.TileEntitySimplePowerConsumer{

	private final ItemStack[] inventory;
	private final int[] inputSlots;
	private final int[] outputSlots;
	private final int[] ioSlots;
	private final int[] otherSlots;
	
	public ElectricMachineTileEntity(String name,int numInputSlots, int numOutputSlots, int numOtherSlots) {
		super(Power.electric_power, 1000f, name);
		inventory = new ItemStack[numInputSlots+numOutputSlots+numOtherSlots];
		inputSlots = new int[numInputSlots];
		outputSlots = new int[numOutputSlots];
		ioSlots = new int[numInputSlots+numOutputSlots];
		otherSlots = new int[numOtherSlots];
		for(int i = 0; i < inventory.length; i++){
			if(i < inputSlots.length)inputSlots[i] = i;
			if(i < outputSlots.length)outputSlots[i] = inputSlots.length + i;
			if(i < ioSlots.length)ioSlots[i] = i;
			if(i < otherSlots.length)otherSlots[i] = inputSlots.length + outputSlots.length + i;
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
		if(old.getBlock() instanceof ElectricMachineBlock 
				&& (Boolean)old.getValue(ElectricMachineBlock.ACTIVE) != active){
			getWorld().setBlockState(getPos(), old.withProperty(ElectricMachineBlock.ACTIVE, active));
		}
	}

	public ItemStack getInputSlot(int i){
		if(i < inputSlots.length){
			return inventory[inputSlots[i]];
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
			return null;
		}
	}
	
	public ItemStack getOutputSlot(int i){
		if(i < outputSlots.length){
			return inventory[inputSlots.length + outputSlots[i]];
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
			return null;
		}
	}
	
	public ItemStack getOtherSlot(int i){
		if(i < otherSlots.length){
			return inventory[inputSlots.length + outputSlots.length + otherSlots[i]];
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
			return null;
		}
	}
	

	public int numberOfInputSlots(){return inputSlots.length;}
	public int numberOfOutputSlots(){return outputSlots.length;}
	public int numberOfOtherputSlots(){return otherSlots.length;}

	
	@Override
	protected ItemStack[] getInventory() {
		return inventory;
	}

	@SideOnly(Side.CLIENT)
	public abstract float[] getProgress();
	
	@Override
	public void writeToNBT(NBTTagCompound tagRoot){
		super.writeToNBT(tagRoot);
		saveTo(tagRoot);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagRoot){
		super.readFromNBT(tagRoot);
		loadFrom(tagRoot);
	}

	protected abstract void saveTo(NBTTagCompound tagRoot);
	protected abstract void loadFrom(NBTTagCompound tagRoot);

	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return ioSlots;
	}
	
	@Override
	public boolean canExtractItem(final int slot, final ItemStack targetItem, final EnumFacing side) {
		return isInArray(slot,outputSlots);
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
	
	public ItemStack insertItemToInputSlots(ItemStack itemStack){
		Item item = itemStack.getItem();
		for(int i = 0; i < inputSlots.length; i++){
			int slot = inputSlots[i];
			ItemStack slotContent = inventory[slot];
			if(slotContent == null){
				// empty slot
				inventory[slot] = itemStack.copy();
				itemStack.stackSize = 0;
				return null;
			} else if(slotContent.getItem().equals(item)){
				// found slot with same item
				if(slotContent.stackSize < slotContent.getMaxStackSize()){
					// increase stack
					int delta = Math.min(itemStack.stackSize, slotContent.getMaxStackSize() - slotContent.stackSize);
					slotContent.stackSize += delta;
					itemStack.stackSize -= delta;
					if(itemStack.stackSize <= 0) return null; // done
				}
			}
		}
		// return left-over
		return itemStack;
	}
	
	public ItemStack insertItemToOutputSlots(ItemStack itemStack){
		Item item = itemStack.getItem();
		for(int i = 0; i < outputSlots.length; i++){
			int slot = outputSlots[i];
			ItemStack slotContent = inventory[slot];
			if(slotContent == null){
				// empty slot
				inventory[slot] = itemStack.copy();
				itemStack.stackSize = 0;
				return null;
			} else if(slotContent.getItem().equals(item)){
				// found slot with same item
				if(slotContent.stackSize < slotContent.getMaxStackSize()){
					// increase stack
					int delta = Math.min(itemStack.stackSize, slotContent.getMaxStackSize() - slotContent.stackSize);
					slotContent.stackSize += delta;
					itemStack.stackSize -= delta;
					if(itemStack.stackSize <= 0) return null; // done
				}
			}
		}
		// return left-over
		return itemStack;
	}
	
	public int getComparatorOutput(){
		// proportional to number of input slot items
		int sum = 0;
		int total = 0;
		for(int i = 0; i < numberOfInputSlots(); i++){
			ItemStack item = getInputSlot(i);
			if(item == null){
				total += 64;
			} else {
				sum += item.stackSize;
				total += item.getMaxStackSize();
			}
		}
		return 15 * sum / total;
	}

}
