package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.electricadvantage.util.farming.VirtualCrop;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;

import java.util.Arrays;
import java.util.List;

public class GrowthChamberTileEntity extends ElectricMachineTileEntity {

	private static final int TICKS_PER_GROWTH = 100;
	private static final float ENERGY_PER_TICK = 1;
	private static final int GROWTH_AREA = 3;
	private final int[] progression = new int[GROWTH_AREA];
	private final int[] progressionMax = new int[GROWTH_AREA];
	private final VirtualCrop[] crops = new VirtualCrop[GROWTH_AREA];
	
	public GrowthChamberTileEntity() {
		super(GrowthChamberTileEntity.class.getSimpleName(),Power.GROWTHCHAMBER_POWER, ENERGY_PER_TICK*64, GROWTH_AREA, 6, 0);
	}


	@Override
	public void tickUpdate(boolean isServer) {
		if(isServer){
			boolean[] recalculate = inventoryChanged();
			boolean active = false;
			boolean flagSync = false;
			for(int slot = 0; slot < GROWTH_AREA; slot++){
				if(recalculate[slot] || progression[slot] >= progressionMax[slot]){
					crops[slot] = getVirtualCrop(getInputSlot(slot));
					progression[slot] = 0;
					if(crops[slot] != null){
						progressionMax[slot] = crops[slot].getMaxGrowth() * TICKS_PER_GROWTH;
					} else {
						progressionMax[slot] = 0;
					}
					flagSync = true;
				}
			}
			if(hasRedstoneSignal() || getEnergy(Power.GROWTHCHAMBER_POWER) < ENERGY_PER_TICK){
				active = false;
				for(int slot = 0; slot < GROWTH_AREA; slot++){
					if(progression[slot] > 0){
						progression[slot] = 0;
						flagSync = true;
					}
				}
			}else{
				for(int slot = 0; slot < GROWTH_AREA; slot++){
					if(crops[slot] != null){
						active = true;
						if(getEnergy(Power.GROWTHCHAMBER_POWER) < ENERGY_PER_TICK) break;
						progression[slot]++;
						subtractEnergy(ENERGY_PER_TICK,Power.GROWTHCHAMBER_POWER);
						if(progression[slot] % TICKS_PER_GROWTH == 0){
							if(crops[slot].grow()){
								// Growth done!
								List<ItemStack> harvest = crops[slot].getHarvest();
								if(harvest != null && (!harvest.isEmpty()) 
										&& this.hasSpaceForItemInOutputSlots(harvest.get(0))){
									playSoundEffect(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5, SoundEvents.BLOCK_GRASS_BREAK, 0.35f, 1f);
									for(ItemStack i : harvest){
										ItemStack remainder = this.insertItemToOutputSlots(i);
										if(remainder != null){
											getWorld().spawnEntityInWorld(
													new EntityItem(
															getWorld(),
															2*(getWorld().rand.nextDouble()-0.5),
															2*(getWorld().rand.nextDouble()-0.5),
															2*(getWorld().rand.nextDouble()-0.5),
															remainder
													)
											);
										}
									}
									crops[slot] = null;
									ItemStack seed = getInputSlot(slot);
									seed.stackSize--;
									if(seed.stackSize <= 0){
										setInputSlot(slot,null);
									}
								}
								progression[slot] = 0;
								progressionMax[slot] = 0;
							}
							if(crops[slot] != null){
								progressionMax[slot] = crops[slot].getMaxGrowth() * TICKS_PER_GROWTH;
							} else {
								progressionMax[slot] = 0;
							}
						}
					} else {
						progression[slot] = 0;
						progressionMax[slot] = 0;
					}
				}

			}
			if(flagSync){
				this.sync();
			}
			this.setActiveState(active);
		}
	}


	
	
	private final int[] dataArray = new int[1+GROWTH_AREA*2];
	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataArray[0]), this.getType());
		System.arraycopy(dataArray, 1, progression, 0, progression.length);
		System.arraycopy(dataArray, 1+GROWTH_AREA, progressionMax, 0, progressionMax.length);
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataArray[0] = Float.floatToIntBits(this.getEnergy(Power.GROWTHCHAMBER_POWER));
		System.arraycopy(progression, 0, dataArray, 1, progression.length);
		System.arraycopy(progressionMax, 0, dataArray, 1+GROWTH_AREA, progressionMax.length);
	}


	@Override
	public boolean isPowered() {
		return this.getEnergy(Power.GROWTHCHAMBER_POWER) > ENERGY_PER_TICK;
	}


	private float[] progressBars = new float[GROWTH_AREA];
	@Override
	public float[] getProgress() {
		for(int i = 0; i < progression.length; i++){
			if(progressionMax[i] <= 0){
				progressBars[i] = 0;
			} else {
				progressBars[i] = Math.max(0.125976563F,(float)progression[i] / (float)progressionMax[i]);
			}
		}
		return progressBars;
	}


	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		byte[] growthStages = new byte[GROWTH_AREA];
		for(int i = 0; i < growthStages.length; i++){
			if(crops[i] != null)
				growthStages[i] = crops[i].getCurrentGrowth();
		}
		tagRoot.setByteArray("growth", growthStages);
		tagRoot.setIntArray("prog", progression);
	}


	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		if(tagRoot.hasKey("growth")){
			byte[] growthStages = tagRoot.getByteArray("growth");
			for(int i = 0; i < growthStages.length && i < GROWTH_AREA; i++){
				if(crops[i] == null){
					crops[i] = getVirtualCrop(this.getInputSlot(i));
				}
				if(crops[i] != null){
					crops[i].setCurrentGrowth(growthStages[i]);
				}
			}
		}
		if(tagRoot.hasKey("prog")){
			System.arraycopy(tagRoot.getIntArray("prog"), 0, progression, 0, progression.length);
		}
	}


	private VirtualCrop getVirtualCrop(ItemStack inputSlot) {
		if(inputSlot == null) return null;
		try{
			return VirtualCrop.createVirtualCrop(inputSlot, getWorld(), getPos());
		} catch(Exception ex){
			FMLLog.severe("Error occured while trying to make a virtual crop for %s:\n%s", inputSlot,ex);
			return null;
		}
	}


	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return getVirtualCrop(item) != null;
	}


	private ItemStack[] oldInventory = null;
	private boolean[] inventoryChanged(){
		ItemStack[] newInventory = getInventory();
		boolean[] changes = new boolean[newInventory.length];
		if(oldInventory == null){
			oldInventory = new ItemStack[newInventory.length];
			for(int i = 0; i < oldInventory.length; i++){
				oldInventory[i] = (newInventory[i] == null ? null : newInventory[i].copy());
			}
			Arrays.fill(changes, true);
			return changes;
		}
		for(int i = 0; i < oldInventory.length; i++){
			changes[i] = !ItemStack.areItemsEqual(oldInventory[i], newInventory[i]);
		}
		for(int i = 0; i < oldInventory.length; i++){
			oldInventory[i] = (newInventory[i] == null ? null : newInventory[i].copy());
		}
		return changes;
	}

	@Override
	public boolean isPowerSink(ConduitType t){
		return ConduitType.areSameType(Power.GROWTHCHAMBER_POWER,t);
	}

	@Override
	public PowerRequest getPowerRequest(ConduitType t){
		if( ConduitType.areSameType(Power.GROWTHCHAMBER_POWER,t)){
			return new PowerRequest(
					PowerRequest.MEDIUM_PRIORITY,
					this.getEnergyCapacity(Power.GROWTHCHAMBER_POWER) - this.getEnergy(Power.GROWTHCHAMBER_POWER),
					this
			);
		}
		return PowerRequest.REQUEST_NOTHING;
	}
}
