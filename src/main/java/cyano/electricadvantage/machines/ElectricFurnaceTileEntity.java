package cyano.electricadvantage.machines;

import java.util.Arrays;

import cyano.electricadvantage.init.Power;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;

public class ElectricFurnaceTileEntity extends ElectricMachineTileEntity{
	

	public static final float ENERGY_PER_TICK = 16f;

	

	private short[] burnTime = new short[4];
	private final short totalBurnTime = 200;
	

	public ElectricFurnaceTileEntity() {
		super(ElectricFurnaceTileEntity.class.getSimpleName(), 
				4, 6, 0);
		Arrays.fill(burnTime, (short)0);
	}


	
	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			boolean flag = false;
			boolean active = false;
			for(int i = 0; i < this.numberOfInputSlots(); i++){
				if(getEnergy() > ENERGY_PER_TICK){
					if(canSmelt(i)){
						subtractEnergy(ENERGY_PER_TICK,Power.ELECTRIC_POWER);
						burnTime[i]++;
						if(burnTime[i] >= totalBurnTime){
							doSmelt(i);
							burnTime[i] = 0;
						}
						flag = true;
						active = true;
					} else {
						burnTime[i] = 0;
					}
				}
			}
			if(getEnergy() < ENERGY_PER_TICK){
				for(int i = 0; i < this.numberOfInputSlots(); i++){
					if(burnTime[i] > 0) burnTime[i]--;
				}
			}
			this.setActive(active && getEnergy() >= ENERGY_PER_TICK);
			
			if (flag){
				this.sync();
			}
		}
	}
	
	
	private boolean canSmelt(int slot){
		ItemStack input = this.getInputSlot(slot);
		if(input == null) return false;
		ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input);
		if(output == null) return false;
		return this.hasSpaceForItemInOutputSlots(output);
	}
	
	private void doSmelt(int slot){
		ItemStack input = this.getInputSlot(slot);
		ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input).copy();
		this.insertItemToOutputSlots(output);
		input.stackSize--;
		if(input.stackSize <= 0){
			setInputSlot(slot,null);
		}
	}
	
	private final float[] progress = new float[4];
	@Override
	public float[] getProgress() {
		for(int i = 0; i < progress.length; i++){
			progress[i] = (float)burnTime[i] / (float)totalBurnTime;
		}
		return progress;
	}
	
	
	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		NBTTagList burnTimes = new NBTTagList();
		for(int i = 0; i < numberOfInputSlots(); i++){
			burnTimes.appendTag(new NBTTagShort(burnTime[i]));
		}
		tagRoot.setTag("cookTime", burnTimes);
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		NBTTagList burnTimes = tagRoot.getTagList("cookTime", 2);
		for(int i = 0; i < numberOfInputSlots(); i++){
			burnTime[i] = ((NBTTagShort)burnTimes.get(i)).getShort();
		}
	}

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		if(item == null) return false;
		return FurnaceRecipes.instance().getSmeltingResult(item) != null;
	}

	private int[] dataArray = null;
	@Override
	public int[] getDataFieldArray() {
		if(dataArray == null){
			dataArray = new int[numberOfInputSlots()];
		}
		return dataArray;
	}

	@Override
	public void onDataFieldUpdate() {
		int[] arr = getDataFieldArray();
		for(int i = 0; i < numberOfInputSlots(); i++){
			burnTime[i] = (short) arr[i];
		}
	}

	@Override
	public void prepareDataFieldsForSync() {
		int[] arr = getDataFieldArray();
		for(int i = 0; i < numberOfInputSlots(); i++){
			arr[i] = burnTime[i];
		}
	}

}
