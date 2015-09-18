package cyano.electricadvantage.machines;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import cyano.electricadvantage.util.crafting.RecipeDeconstructor;
import cyano.electricadvantage.util.crafting.SerializedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;

public class ElectricFabricatorTileEntity extends ElectricMachineTileEntity {

	public static final float ENERGY_PER_PROGRESS_TICK = 16;
	
	private FSM state = FSM.INACTIVE;
	private int progress;
	private final int progressGoal = 200;
	
	public ElectricFabricatorTileEntity() {
		super(ElectricFabricatorTileEntity.class.getName(), 12, 2, 1);
	}


	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			boolean changeDetected = inventoryChanged(); // we want this to execute every tick, even when not used
			switch(state){
				// finite-state-machine
				case INACTIVE:{
					progress = 0;
					if((!hasRedstoneSignal()) && isPowered()){
						state = FSM.READY;
					}
					break;
				}
				case WAITING:{
					if(changeDetected){
						state = FSM.READY;
					}
					break;
				}
				case READY:{
					progress = 0;
					if(hasRedstoneSignal() || (!isPowered())){
						state = FSM.INACTIVE;
					} else {
						if(canCraft()){
							state = FSM.CRAFTING;
						} else {
							state = FSM.WAITING;
						}
					}
					break;
				}
				case CRAFTING:{
					if(changeDetected && !canCraft()){
						state = FSM.READY;
					} else if(getEnergy() < ENERGY_PER_PROGRESS_TICK){
						state = FSM.INACTIVE;
					}else if(progress >= progressGoal){
						state = FSM.CRAFT_COMPLETE;
					} else {
						progress++;
						subtractEnergy(ENERGY_PER_PROGRESS_TICK,getType());
					}
					break;
				}
				case CRAFT_COMPLETE:{
					doCraft();
					getWorld().playSoundEffect(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5, "random.break", 0.5f, 1f);
					state = FSM.READY;
					break;
				}
				default:
					throw new IllegalStateException("FSM state "+state.name()+" not recognized");
			}
			this.setActiveState(state == FSM.READY || state == FSM.CRAFTING || state == FSM.CRAFT_COMPLETE );
			
		}
		
	}
	
	private int oldProgress = 0;
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		if(oldProgress != progress){
			oldProgress = progress;
			this.sync();
		}
	}
	
	private ItemStack[] oldInventory = null;
	private boolean inventoryChanged(){
		if(oldInventory == null){
			ItemStack[] newInventory = getInventory();
			oldInventory = new ItemStack[newInventory.length];
			for(int i = 0; i < oldInventory.length; i++){
				oldInventory[i] = (newInventory[i] == null ? null : newInventory[i].copy());
			}
			return true;
		}
		ItemStack[] newInventory = getInventory();
		boolean changed = false;
		for(int i = 0; i < oldInventory.length; i++){
			if(!ItemStack.areItemStacksEqual(oldInventory[i], newInventory[i])){
				changed = true;
				break;
			}
		}
		for(int i = 0; i < oldInventory.length; i++){
			oldInventory[i] = (newInventory[i] == null ? null : newInventory[i].copy());
		}
		return changed;
	}
	
	private boolean canCraft(){
		ItemStack referenceItem = super.getOtherSlot(0);
		// first check if null
		if(referenceItem == null) return false;
		// then check if it is craftable from our input inventory
		AtomicReference<ItemStack> output = new AtomicReference<>();
		ItemStack[] inv = Arrays.copyOf(getInventory(), this.numberOfInputSlots());
		SerializedInventory result = RecipeDeconstructor.getInstance().attemptToCraft(referenceItem, 
				SerializedInventory.serialize(inv), output);
		if(result == null || output.get() == null) return false;
		// then check if there is space in the output buffer
		if(!this.hasSpaceForItemInOutputSlots(output.get())) return false;
		// finally check if the input buffer can hold the new inventory
		if(result.deserialize().size() > this.numberOfInputSlots()) return false;
		return true;
	}
	
	private void doCraft(){
		ItemStack referenceItem = super.getOtherSlot(0);
		// assuming that canCraft() was already used to check validity
		AtomicReference<ItemStack> output = new AtomicReference<>();
		ItemStack[] inv = Arrays.copyOf(getInventory(), this.numberOfInputSlots());
		SerializedInventory result = RecipeDeconstructor.getInstance().attemptToCraft(referenceItem, 
				SerializedInventory.serialize(inv), output);
		this.insertItemToOutputSlots(output.get());
		List<ItemStack> newInventory = result.deserialize();
		for(int slot = 0; slot < this.numberOfInputSlots(); slot++){
			if(slot < newInventory.size()){
				this.setInputSlot(slot, newInventory.get(slot));
			} else {
				this.setInputSlot(slot, null);
			}
		}
	}
	
	@Override
	public boolean isPowered() {
		return getEnergy() > ENERGY_PER_PROGRESS_TICK;
	}

	private final float[] progArr = new float[1];
	@Override
	public float[] getProgress() {
		progArr[0] = (float)progress / (float) progressGoal;
		return progArr;
	}

	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		tagRoot.setShort("progress", (short)progress);
		tagRoot.setByte("FSM", (byte)state.ordinal());
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		if(tagRoot.hasKey("progress")){
			this.progress = tagRoot.getShort("progress");
		}
		if(tagRoot.hasKey("FSM")){
			this.state = FSM.values()[tagRoot.getByte("FSM")];
		}
	}

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return true;
	}

	private final int[] dataArray = new int[2];
	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataArray[0] = Float.floatToIntBits(getEnergy());
		dataArray[1] = progress;
	}
	
	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataArray[0]), getType());
		this.progress = dataArray[1];
	}


	/*
	 * finite state machine (FSM):
	 * inactive                            
	 *   ^                                 
	 *   |                                 
	 * power/redstone signal               
	 *   |                                 
	 *   |    /-- cannot craft item --> waiting
	 *   |   /                              |
	 *   |  / /- inventory/ref item change -/
	 *   V / V                             
	 *   ready ------ can craft item ------> crafting
	 *   ^ ^                                 / |   
	 *   |  \-- inventory/ref item change --/  |   
	 *   |                                     |
	 *   |                             progress completes
	 *   |                                     |
	 *   |                                     V
	 *    \----------------------------- craft complete
	 *                                     
	 */
	
	private enum FSM{
		INACTIVE,
		WAITING,
		READY,
		CRAFTING,
		CRAFT_COMPLETE
	}
}