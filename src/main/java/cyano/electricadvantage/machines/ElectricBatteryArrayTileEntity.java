package cyano.electricadvantage.machines;

import cyano.electricadvantage.common.IRechargeableItem;
import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ElectricBatteryArrayTileEntity extends ElectricGeneratorTileEntity{
	
	public ElectricBatteryArrayTileEntity(){
		super(ElectricBatteryArrayTileEntity.class.getSimpleName(),8);
	}
	
	
	///// methods overrides to make this an energy storage machine /////
	@Override
	protected float transmitPowerToConsumers(float amount, ConduitType type, byte priority){
		if(this.hasRedstoneSignal()){
			// disabled by redstone signal
			return 0f;
		} else {
			return super.transmitPowerToConsumers(amount,type,priority);
		}
	}
	

	@Override
	public boolean isPowerSink(){
		return true;
	}
	

	@Override
	public PowerRequest getPowerRequest(ConduitType offer) {
		if(ConduitType.areSameType(Power.ELECTRIC_POWER, offer)){
			PowerRequest request = new PowerRequest(PowerRequest.BACKUP_PRIORITY,
					(this.getEnergyCapacity() - this.getEnergy()),
					this);
			return request;
		} else {
			return PowerRequest.REQUEST_NOTHING;
		}
	}
	

	@Override
	protected byte getMinimumSinkPriority(){
		return PowerRequest.BACKUP_PRIORITY+1; // 1 greater than our own priority to prevent cycling
	}
	
	///// end of energy storage overrides /////
	
	
	///// energy handling /////
	@Override
	public float getEnergy(){
		float sum = 0;
		for(int i = 0; i < numberOfInputSlots(); i++){
			sum += getEnergyStored(this.getInputSlot(i));
		}
		return sum;
	}
	
	@Override
	public float getEnergyCapacity(){
		float sum = 0;
		for(int i = 0; i < numberOfInputSlots(); i++){
			sum += getEnergyCapacity(this.getInputSlot(i));
		}
		return sum;
	}
	
	public void setEnergy(float energy, ConduitType type){
		addEnergy(energy - getEnergy(),type);
	}
	
	public float addEnergy(float energy, ConduitType type){
		float delta = 0;
		if(energy != 0){
			for(int i = 0; i < numberOfInputSlots(); i++){
				ItemStack stack  = getInputSlot(i);
				if(stack == null) continue;
				Item item = stack.getItem();
				if(item instanceof IRechargeableItem){
					delta += ((IRechargeableItem)item).addEnergy(stack, energy-delta, type);
				}
			}
		}
		return delta;
	}
	///// end of energy handling /////
	
	private float[] oldE = null;
	@Override
	public void powerUpdate(){
		boolean flag = false;
		if(oldE == null){
			oldE = new float[numberOfInputSlots()];
		}
		

		float availableEnergy = this.getEnergy();
		if(availableEnergy > 0 && !this.hasRedstoneSignal()){
			ConduitType type = this.getType();
			this.subtractEnergy(this.transmitPowerToConsumers(availableEnergy, type, getMinimumSinkPriority()),type);
		}
		
		for(int i = 0; i < numberOfInputSlots(); i++){
			float e = getEnergyStored(this.getInputSlot(i));
			if(oldE[i] != e){
				flag = true;
				oldE[i] = e;
			}
		}
		if(flag) this.sync();
	}

	@Override
	public float getPowerOutput() {
		// not used
		return 0;
	}
	
	
	public float[] batteryCharges(){
		float[] values = new float[this.numberOfInputSlots()];
		for(int i = 0; i < values.length; i++){
			float e = getEnergyStored(this.getInputSlot(i));
			float eMax = getEnergyCapacity(this.getInputSlot(i));
			float v;
			if(eMax > 0){
				v = e / eMax;
			} else {
				v = 0;
			}
			values[i] = v;
		}
		return values;
	}
	

	public static float getEnergyCapacity(ItemStack item){
		if(item == null) return 0;
		Item i = item.getItem();
		if(i instanceof IRechargeableItem){
			return ((IRechargeableItem)i).getMaxEnergy(item);
		} else {
			return 0;
		}
	}
	
	public static float getEnergyStored(ItemStack item){
		if(item == null) return 0;
		Item i = item.getItem();
		if(i instanceof IRechargeableItem){
			return ((IRechargeableItem)i).getEnergy(item);
		} else {
			return 0;
		}
	}
	

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return (item != null) && item.getItem() instanceof IRechargeableItem;
	}

	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		// do nothing (inventory already handled
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		// do nothing (inventory already handled
	}

	private final int[] dataArray = new int[0];
	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	@Override
	public void onDataFieldUpdate() {
		// do nothing (inventory already handled
	}

	@Override
	public void prepareDataFieldsForSync() {
		// do nothing (inventory already handled
	}

	@Override
	public void tickUpdate(boolean isServer) {
		// do nothing
	}

}
