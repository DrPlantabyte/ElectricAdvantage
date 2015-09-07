package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;

public class ElectricOvenTileEntity extends ElectricMachineTileEntity{


	public static final float ENERGY_PER_TICK = 4f;
	private final short totalCookTime = 100;
	private short progress = 0;
	
	public ElectricOvenTileEntity() {
		super(ElectricOvenTileEntity.class.getName(), 1, 1, 0);
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		//FMLLog.info("Energy is %s", getEnergy()); // TODO: remove
		if(isServerWorld){
			boolean active = false;
			if(getInputSlot(0) == null || hasRedstoneSignal()){
				progress = 0;
			} else if(getEnergy() >= ENERGY_PER_TICK){
				ItemStack output = FurnaceRecipes.instance().getSmeltingResult(getInputSlot(0));
				if(isFoodItem(output) && hasSpaceForItemInOutputSlots(output)){
					subtractEnergy(ENERGY_PER_TICK,Power.ELECTRIC_POWER);
					progress++;
					if(progress >= totalCookTime){
						this.insertItemToOutputSlots(output.copy());
						progress = 0;
					}
					active = true;
				} else {
					progress = 0;
				}
			} else {
				if(progress > 0) progress--;
			}
			this.setActiveState(active);
		}
		
	}
	
	public static boolean isFoodItem(ItemStack item){
		if(item == null) return false;
		Item i = item.getItem();
		return (i instanceof net.minecraft.item.ItemFood);
	}
	
	public static boolean becomesFoodItem(ItemStack item){
		if(item == null) return false;
		return isFoodItem(FurnaceRecipes.instance().getSmeltingResult(item));
		
	}
	
	short oldValue = 0;
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		if(oldValue != progress){
			oldValue = progress;
			this.sync();
		}
	}
	
	@Override
	public boolean isPowered() {
		return getEnergy() > ENERGY_PER_TICK;
	}

	private final float[] progs = new float[1];
	@Override
	public float[] getProgress() {
		progs[0] = (float)progress / (float)totalCookTime;
		return progs;
	}

	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		tagRoot.setShort("cookTime", progress);
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		if(tagRoot.hasKey("cookTime")){
			progress = tagRoot.getShort("cookTime");
		}
	}

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return becomesFoodItem(item);
	}

	final int[] dataArray = new int[2];
	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	@Override
	public void onDataFieldUpdate() {
		dataArray[0] = Float.floatToIntBits(getEnergy());
		dataArray[1] = progress;
	}

	@Override
	public void prepareDataFieldsForSync() {
		setEnergy(Float.intBitsToFloat(dataArray[0]),getType());
		progress = (short)dataArray[1];
	}


}
