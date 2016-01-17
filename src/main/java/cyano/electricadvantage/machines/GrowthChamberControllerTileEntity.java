package cyano.electricadvantage.machines;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.init.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;

public class GrowthChamberControllerTileEntity extends cyano.poweradvantage.api.simple.TileEntitySimplePowerSource implements IFluidHandler{


	static final float ELECTRICITY_PER_UNIT = 4f;
	static final float WATER_PER_UNIT = 0.25f;
	static final float SOIL_PER_UNIT = 1.25e-4f;
	static final float SOIL_PER_BLOCK = 1f;
	static final float MAX_SOIL = SOIL_PER_BLOCK * 1.5f;
	static final int OUT_OF_DATE_LIMIT = 30;

	private final FluidTank tank;

	private final ItemStack[] inventory;

	private float soil = 0f;


	private final int[] dataSyncArray = new int[4];

	public GrowthChamberControllerTileEntity() {
		super(Power.GROWTHCHAMBER_POWER, 100, GrowthChamberControllerTileEntity.class.getSimpleName());
		tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 2);
		inventory = new ItemStack[1];
	}

	private boolean redstone = true;

	private int timeSinceLastPowerRequest = 100;

	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			// server-side logic
			if(MAX_SOIL - soil > SOIL_PER_BLOCK && isDirt(inventory[0])){
				soil += SOIL_PER_BLOCK;
				inventory[0].stackSize--;
				if(inventory[0].stackSize <= 0){
					inventory[0] = null;
				}
			}
			if(timeSinceLastPowerRequest < OUT_OF_DATE_LIMIT){
				timeSinceLastPowerRequest++;
			}
		}
	}
	
	private static boolean isDirt(ItemStack i){
		if(i == null) return false;
		for(ItemStack d : OreDictionary.getOres("blockDirt")){
			if(OreDictionary.itemMatches(d, i, false)){
				return true;
			}
		}
		return false;
	}



	private boolean hasRedstoneSignal() {
		return getWorld().isBlockPowered(getPos());
	}


	private float oldEnergy = 0;
	private float oldSoil = 0;
	private int oldWater = 0;
	private int oldCounter = OUT_OF_DATE_LIMIT;
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		// powerUpdate occurs once every 8 world ticks and is scheduled such that neighboring 
		// machines don't powerUpdate in the same world tick. To reduce network congestion, 
		// I'm doing the synchonization logic here instead of in the tickUpdate method
		boolean updateFlag = false;

		if(oldEnergy != getEnergy()){
			oldEnergy = getEnergy();
			updateFlag = true;
		}
		if(oldSoil != soil){
			oldSoil = soil;
			updateFlag = true;
		}
		if(oldWater != getTank().getFluidAmount()){
			oldWater = getTank().getFluidAmount();
			updateFlag = true;
		}
		if(oldCounter != timeSinceLastPowerRequest){
			updateFlag = true;
			oldCounter = timeSinceLastPowerRequest;
		}

		redstone = hasRedstoneSignal();

		if(updateFlag){
			super.sync();
		}
	}

	public float getWaterLevel(){
		return ((float)getTank().getFluidAmount()) / ((float)getTank().getCapacity());
	}

	public float getEnergyLevel(){
		return this.getEnergy() / this.getEnergyCapacity();
	}

	public float getSoil(){
		return soil;
	}

	public void setSoil(float soil){
		this.soil = soil;
	}
	public void addSoil(float soil){
		this.soil = Math.min(MAX_SOIL, this.soil + soil);
	}


	public FluidTank getTank(){
		return tank;
	}

	@Override
	protected ItemStack[] getInventory() {
		return inventory;
	}

	@Override
	public int[] getDataFieldArray() {
		return dataSyncArray;
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataSyncArray[0] = Float.floatToRawIntBits(this.getEnergy());
		dataSyncArray[1] = getTank().getFluidAmount();
		dataSyncArray[2] = Float.floatToRawIntBits(this.getSoil());
		dataSyncArray[3] = timeSinceLastPowerRequest;
	}

	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataSyncArray[0]), this.getType());
		this.getTank().setFluid(new FluidStack(FluidRegistry.WATER,dataSyncArray[1]));
		this.setSoil(Float.intBitsToFloat(dataSyncArray[2]));
		timeSinceLastPowerRequest = dataSyncArray[3];
	}


	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
	public void writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		NBTTagCompound tankTag = new NBTTagCompound();
		this.getTank().writeToNBT(tankTag);
		tagRoot.setTag("Tank", tankTag);
		tagRoot.setFloat("soil", soil);
	}
	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
	public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if (tagRoot.hasKey("Tank")) {
			NBTTagCompound tankTag = tagRoot.getCompoundTag("Tank");
			getTank().readFromNBT(tankTag);
			if(tankTag.hasKey("Empty")){
				// empty the tank if NBT says its empty (not default behavior of Tank.readFromNBT(...) )
				getTank().setFluid(null);
			}
		}
		if(tagRoot.hasKey("soil")){
			this.soil = tagRoot.getFloat("soil");
		}
	}

	@Override
	public boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
		return isDirt(srcItem) && slot == 0;
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack srcItem) {
		return isDirt(srcItem) && slot == 0;
	}
	
	@Override
	public boolean canExtractItem(final int slot, final ItemStack targetItem, final EnumFacing side) {
		return false;
	}
	
	public boolean isPowered(){
		return this.getEnergy() > 0 || timeSinceLastPowerRequest < OUT_OF_DATE_LIMIT;
	}

	///// Overrides to make this a multi-type block /////
	@Override
	public boolean isPowerSink(){
		return true;
	}
	@Override
	public boolean isPowerSource(){
		return true;
	}

	/**
	 * Adds "energy" as a fluid to the FluidTank returned by getTank(). This implementation ignores 
	 * all non-fluid energy types.
	 * @param amount amount of energy/fluid to add
	 * @param type the type of energy/fluid being added.
	 * @return The amount that was actually added
	 */
	@Override
	public float addEnergy(float amount, ConduitType type){
		if(Fluids.isFluidType(type)){
			// water
			if(this.canFill(null, Fluids.conduitTypeToFluid(type))){
				return this.fill(null, new FluidStack(Fluids.conduitTypeToFluid(type),(int)amount), true);
			} else {
				return 0;
			}
		} else if(ConduitType.areSameType(type, Power.ELECTRIC_POWER)){
			// electricity
			float capacity = (this.getEnergyCapacity() - this.getEnergy());
			capacity = Math.min(capacity, soil / SOIL_PER_UNIT);
			capacity = Math.min(capacity, tank.getFluidAmount() / WATER_PER_UNIT);
			float delta = Math.min(ELECTRICITY_PER_UNIT * capacity,amount);
			this.addEnergy(delta / ELECTRICITY_PER_UNIT, getType());
			soil -= delta * SOIL_PER_UNIT;
			tank.drain(Math.max(1, (int)(delta * WATER_PER_UNIT)), true);
			return delta;
		} else if(ConduitType.areSameType(type, getType())){
			// greenhouse energy
			return super.addEnergy(amount, type);
		}
		// unsupported energy type
		return 0;
	}
	/**
	 * Sets the tank contents using the energy API method
	 * @param amount amount of energy/fluid to add
	 * @param type the type of energy/fluid being added.
	 */
	@Override
	public void setEnergy(float amount,ConduitType type) {
		if(Fluids.isFluidType(type)){
			getTank().setFluid(new FluidStack(Fluids.conduitTypeToFluid(type),(int)amount));
		} else if(ConduitType.areSameType(type, Power.ELECTRIC_POWER)){
			this.addEnergy(amount / ELECTRICITY_PER_UNIT, getType());
		} else if(ConduitType.areSameType(type, getType())){
			super.setEnergy(amount, type);
		}
	}
	/**
	 * Subtracts "energy" as a fluid to the FluidTank returned by getTank(). This implementation 
	 * ignores all non-fluid energy types.
	 * @param amount amount of energy/fluid to add
	 * @param type the type of energy/fluid being added.
	 * @return The amount that was actually added
	 */
	@Override
	public float subtractEnergy(float amount, ConduitType type){
		if(Fluids.isFluidType(type)){
			if(this.canDrain(null, Fluids.conduitTypeToFluid(type))){
				return -1*this.drain(null, new FluidStack(Fluids.conduitTypeToFluid(type),(int)amount), true).amount;
			} else {
				return 0;
			}
		}else{
			return this.addEnergy(-1*amount, type);
		}
	}

	@Override
	public PowerRequest getPowerRequest(ConduitType offer) {
		if(redstone) return PowerRequest.REQUEST_NOTHING;
		if(Fluids.conduitTypeToFluid(offer) == FluidRegistry.WATER){
			PowerRequest request = new FluidRequest(FluidRequest.MEDIUM_PRIORITY+1,
					(getTank().getCapacity() - getTank().getFluidAmount()),
					this);
			return request;
		} else if(ConduitType.areSameType(offer, Power.ELECTRIC_POWER)){
			timeSinceLastPowerRequest = 0;
			float powerWanted = (this.getEnergyCapacity() - this.getEnergy());
			powerWanted = Math.min(powerWanted, soil / SOIL_PER_UNIT);
			powerWanted = Math.min(powerWanted, tank.getFluidAmount() / WATER_PER_UNIT);
			return new PowerRequest(PowerRequest.MEDIUM_PRIORITY,ELECTRICITY_PER_UNIT * powerWanted,this);
		} else {
			return PowerRequest.REQUEST_NOTHING;
		}
	}

	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 * @param type The type of energy in the conduit
	 * @param blockFace The side through-which the energy is flowing
	 * @return true if this conduit can flow the given energy type through the given face, false 
	 * otherwise
	 */
	public boolean canAcceptType(ConduitType type, EnumFacing blockFace){
		return ConduitType.areSameType(getType(), type) || ConduitType.areSameType(Power.ELECTRIC_POWER, type)
				 || ConduitType.areSameType(Fluids.fluidConduit_general, type);
	}
	/**
	 * Determines whether this conduit is compatible with a type of energy through any side
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given energy type through one or more of its block 
	 * faces, false otherwise
	 */
	public boolean canAcceptType(ConduitType type){
		return ConduitType.areSameType(getType(), type) || ConduitType.areSameType(Power.ELECTRIC_POWER, type)
				 || ConduitType.areSameType(Fluids.fluidConduit_general, type);
	}
	///// end multi-type overrides /////



	///// IFluidHandler /////

	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 * @param forReal if true, then the fluid in the tank will change
	 */
	@Override
	public int fill(EnumFacing face, FluidStack fluid, boolean forReal) {
		if(getTank().getFluidAmount() <= 0 || getTank().getFluid().getFluid().equals(fluid.getFluid())){
			return getTank().fill(fluid, forReal);
		} else {
			return 0;
		}
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 * @param forReal if true, then the fluid in the tank will change
	 */
	@Override
	public FluidStack drain(EnumFacing face, FluidStack fluid, boolean forReal) {
		if(getTank().getFluidAmount() > 0 && getTank().getFluid().getFluid().equals(fluid.getFluid())){
			return getTank().drain(fluid.amount,forReal);
		} else {
			return new FluidStack(getTank().getFluid().getFluid(),0);
		}
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param amount The amount of fluid being added/removed
	 * @param forReal if true, then the fluid in the tank will change
	 */
	@Override
	public FluidStack drain(EnumFacing face, int amount, boolean forReal) {
		if(getTank().getFluidAmount() > 0 ){
			return getTank().drain(amount,forReal);
		} else {
			return null;
		}
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 */
	@Override
	public boolean canFill(EnumFacing face, Fluid fluid) {
		if(fluid != FluidRegistry.WATER) return false;
		if(getTank().getFluid() == null) return true;
		return getTank().getFluidAmount() <= getTank().getCapacity() && fluid.equals(getTank().getFluid().getFluid());
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 */
	@Override
	public boolean canDrain(EnumFacing face, Fluid fluid) {
		if(getTank().getFluid() == null) return false;
		return getTank().getFluidAmount() > 0 && fluid.equals(getTank().getFluid().getFluid());
	}

	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @return array of FluidTankInfo describing all of the FluidTanks
	 */
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing face) {
		FluidTankInfo[] arr = new FluidTankInfo[1];
		arr[0] = getTank().getInfo();
		return arr;
	}

	///// end of IFluidHandler methods /////
	
	public int getComparatorOutput(){
		if(this.soil > SOIL_PER_UNIT && this.getTank().getFluidAmount() > WATER_PER_UNIT
				&& this.getEnergy() > ELECTRICITY_PER_UNIT){
			return 15;
		} else {
			return 0;
		}
	}
}
