package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import cyano.poweradvantage.init.Fluids;
import cyano.poweradvantage.registry.still.recipe.DistillationRecipe;
import cyano.poweradvantage.registry.still.recipe.DistillationRecipeRegistry;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

import java.util.Arrays;

public class ElectricStillTileEntity extends ElectricMachineTileEntity implements IFluidHandler{



	private final FluidTank outputTank;
	private final FluidTank inputTank;
	private final int speed = 1;
	private final float electricityPerDistill = 16f;

	private final int[] dataSyncArray = new int[5];
	
	public ElectricStillTileEntity() {
		super(ElectricStillTileEntity.class.getSimpleName(), 0, 0, 0, new ConduitType[]{Power.ELECTRIC_POWER, Fluids.fluidConduit_general},new float[]{500f,1000f});
		outputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME );
		inputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	}
	

	private boolean wasActive = false;
	private int timeSinceSound = 0;
	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			// server-side logic
			if(!this.hasRedstoneSignal()){
				if(getInputTank().getFluidAmount() > 0 
						&& getEnergy() >= electricityPerDistill
						&& canDistill(getInputTank().getFluid())){
					distill();
					if(!wasActive){
						this.setActiveState(true);
						wasActive = true;
					}
					this.subtractEnergy(electricityPerDistill, Power.ELECTRIC_POWER);
					if(timeSinceSound > 200){
						playSoundEffect(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5, SoundEvents.BLOCK_LAVA_AMBIENT, 0.3f, 1.5f);
						timeSinceSound = 0;
					}
					timeSinceSound++;
				} else {
					if(wasActive){
						this.setActiveState(false);
						wasActive = false;
					}
				}
			} else {
				if(wasActive){
					this.setActiveState(false);
					wasActive = false;
				}
			}
		}
	}

	

	private boolean canDistill(Fluid f) {
		if(f == null) return false;
		FluidStack fs = new FluidStack(f,getInputTank().getCapacity());
		return canDistill(fs);
	}

	private boolean canDistill(FluidStack fs) {
		DistillationRecipe recipe = DistillationRecipeRegistry.getInstance().getDistillationRecipeForFluid(fs.getFluid());
		if(recipe == null) return false;
		if(this.getOutputTank().getFluidAmount() > 0){
			return recipe.isValidInput(fs) && recipe.isValidOutput(getOutputTank().getFluid());
		} else {
			return recipe.isValidInput(fs);
		}
	}

	private void distill(){
		DistillationRecipe recipe = DistillationRecipeRegistry.getInstance()
				.getDistillationRecipeForFluid(getInputTank().getFluid().getFluid());
		FluidStack output = recipe.applyRecipe(inputTank.getFluid(), speed);
		if(getOutputTank().getFluidAmount() <= 0){
			getOutputTank().setFluid(output);
		} else {
			getOutputTank().fill(output, true);
		}
	}
	


	@Override
	public boolean isPowered() {
		return getEnergy() > electricityPerDistill;
	}



	private final float[] progs = new float[0];
	@Override
	public float[] getProgress() {
		return progs;
	}


	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return false;
	}

	
	
	public FluidTank getOutputTank(){
		return outputTank;
	}
	public FluidTank getInputTank(){
		return inputTank;
	}
	/**
	 * Determines whether this conduit is compatible with a type of energy through any side
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given energy type through one or more of its block 
	 * faces, false otherwise
	 */
	public boolean canAcceptType(ConduitType type){
		return ConduitType.areSameType(getType(), type) || ConduitType.areSameType(Fluids.fluidConduit_general, type);
	}

	@Override
	public int[] getDataFieldArray() {
		return dataSyncArray;
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataSyncArray[0] = Float.floatToRawIntBits(this.getEnergy());
		dataSyncArray[1] = this.getOutputTank().getFluidAmount();
		dataSyncArray[2] = (this.getOutputTank().getFluidAmount() > 0 ? FluidRegistry.getFluidID(this.getOutputTank().getFluid().getFluid()) : FluidRegistry.getFluidID(FluidRegistry.WATER));
		dataSyncArray[3] = getInputTank().getFluidAmount();
		dataSyncArray[4] = (getInputTank().getFluidAmount() > 0 ? FluidRegistry.getFluidID(getInputTank().getFluid().getFluid()) : FluidRegistry.getFluidID(FluidRegistry.WATER));
	}

	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataSyncArray[0]), Power.ELECTRIC_POWER);
		this.getOutputTank().setFluid(new FluidStack(FluidRegistry.getFluid(dataSyncArray[2]),dataSyncArray[1]));
		this.getInputTank().setFluid(new FluidStack(FluidRegistry.getFluid(dataSyncArray[4]),dataSyncArray[3]));
	}

	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
	public void saveTo(final NBTTagCompound tagRoot) {
		NBTTagCompound tankTag = new NBTTagCompound();
		this.getOutputTank().writeToNBT(tankTag);
		tagRoot.setTag("TankOut", tankTag);
		NBTTagCompound tankTag2 = new NBTTagCompound();
		this.getInputTank().writeToNBT(tankTag2);
		tagRoot.setTag("TankIn", tankTag2);
	}
	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
	public void loadFrom(final NBTTagCompound tagRoot) {
		if (tagRoot.hasKey("TankOut")) {
			NBTTagCompound tankTag = tagRoot.getCompoundTag("TankOut");
			getOutputTank().readFromNBT(tankTag);
			if(tankTag.hasKey("Empty")){
				// empty the tank if NBT says its empty (not default behavior of Tank.readFromNBT(...) )
				getOutputTank().setFluid(null);
			}
		}

		if (tagRoot.hasKey("TankIn")) {
			NBTTagCompound tankTag = tagRoot.getCompoundTag("TankIn");
			getInputTank().readFromNBT(tankTag);
			if(tankTag.hasKey("Empty")){
				// empty the tank if NBT says its empty (not default behavior of Tank.readFromNBT(...) )
				getInputTank().setFluid(null);
			}
		}
	}

	public int getComparatorOutput() {
		return 15 * getInputTank().getFluidAmount() / getInputTank().getCapacity();
	}

	///// Overrides to make this a multi-type block /////
	private boolean redstone = false;
	private float oldEnergy = 0f;
	private int[] syncArrayOld = null;
	private int[] syncArrayNew = null;
	@Override
	public void powerUpdate(){
		// deliberately NOT calling super.powerUpdate()
		if(this.getOutputTank().getFluidAmount() > 0){
			ConduitType type = Fluids.fluidToConduitType(getOutputTank().getFluid().getFluid());
			float availableAmount = getOutputTank().getFluidAmount();
			float delta = ConduitRegistry.transmitPowerToConsumers(availableAmount, cyano.poweradvantage.init.Fluids.fluidConduit_general, type, 
					PowerRequest.LAST_PRIORITY, getWorld(), getPos(), this);
			if(delta > 0){
				getOutputTank().drain(Math.max((int)delta,1),true); // no free energy!
			}
		}
		
		redstone = getWorld().isBlockPowered(getPos());
		this.setPowerState(this.isPowered());
		
		// automatically detect when a sync is needed
		if(syncArrayOld == null || syncArrayNew == null 
				|| this.getDataFieldArray().length != syncArrayOld.length){
			int size = this.getDataFieldArray().length;
			syncArrayOld = new int[size];
			syncArrayNew = new int[size];
		}
		this.prepareDataFieldsForSync();
		System.arraycopy(this.getDataFieldArray(), 0, syncArrayNew, 0, syncArrayNew.length);
		if(!Arrays.equals(syncArrayOld, syncArrayNew)){
			this.sync();
			System.arraycopy(syncArrayNew, 0, syncArrayOld, 0, syncArrayOld.length);
		}
	}
	@Override
	protected boolean hasRedstoneSignal(){
		return redstone;
	}
	/**
	 * Determines whether this block/entity should receive energy
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType p){
		return ConduitType.areSameType(Power.ELECTRIC_POWER,p) || Fluids.isFluidType(p);
	}
	/**
	 * Determines whether this block/entity can provide energy
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType p){
		return Fluids.isFluidType(p);
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
		if(Fluids.isFluidType(type) && type != Fluids.fluidConduit_general){
			if(amount > 0){
				if(this.canFill(null, Fluids.conduitTypeToFluid(type))){
					return this.fill(null, new FluidStack(Fluids.conduitTypeToFluid(type),(int)amount), true);
				} else {
					return 0;
				}
			} else {
				if(this.canDrain(null, Fluids.conduitTypeToFluid(type))){
					return -1*this.drain(null, (int)amount, true).amount;
				} else {
					return 0;
				}
			}
		}
		return super.addEnergy(amount, type);
	}
	
	@Override
	public PowerRequest getPowerRequest(ConduitType offer) {
		if(Fluids.isFluidType(offer) 
				&& DistillationRecipeRegistry.getInstance().getDistillationRecipeForFluid( Fluids.conduitTypeToFluid(offer)) != null){
			if(canDistill(Fluids.conduitTypeToFluid(offer))){
				if(getInputTank().getFluidAmount() > 0
						&& Fluids.conduitTypeToFluid(offer).equals(getInputTank().getFluid().getFluid()) == false) {
					// check that the existing fluid is compatible
					return PowerRequest.REQUEST_NOTHING;
				}
				PowerRequest request = new FluidRequest(FluidRequest.MEDIUM_PRIORITY-1,
						(getInputTank().getCapacity() - getInputTank().getFluidAmount()),
						this);
				return request;
			} else {
				return PowerRequest.REQUEST_NOTHING;
			} 
		} else{
			return super.getPowerRequest(offer);
		}
	}
	
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 * @param forReal if true, then the fluid in the tank will change
	 */
	@Override
	public int fill(EnumFacing face, FluidStack fluid, boolean forReal) {
		if(fluid == null) return 0;
		if(getInputTank().getFluidAmount() <= 0 || getInputTank().getFluid().getFluid().equals(fluid.getFluid())){
			if(canDistill(fluid)){
				return getInputTank().fill(fluid, forReal);
			}
		}
		return 0;
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 * @param forReal if true, then the fluid in the tank will change
	 */
	@Override
	public FluidStack drain(EnumFacing face, FluidStack fluid, boolean forReal) {
		if(getOutputTank().getFluidAmount() > 0 && getOutputTank().getFluid().getFluid().equals(fluid.getFluid())){
			return getOutputTank().drain(fluid.amount,forReal);
		} else {
			return new FluidStack(getOutputTank().getFluid().getFluid(),0);
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
		if(getOutputTank().getFluidAmount() > 0 ){
			return getOutputTank().drain(amount,forReal);
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
		if(fluid == null) return false;
		if(canDistill(fluid) == false) return false;
		if(getInputTank().getFluidAmount() <= 0) return true;
		return getInputTank().getFluidAmount() <= getInputTank().getCapacity() && fluid.equals(getInputTank().getFluid().getFluid());
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 */
	@Override
	public boolean canDrain(EnumFacing face, Fluid fluid) {
		if(fluid == null) return false;
		return getOutputTank().getFluidAmount() > 0 && fluid.equals(getOutputTank().getFluid().getFluid());
	}

	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @return array of FluidTankInfo describing all of the FluidTanks
	 */
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing face) {
		FluidTankInfo[] arr = new FluidTankInfo[2];
		arr[0] = getInputTank().getInfo();
		arr[1] = getOutputTank().getInfo();
		return arr;
	}


	private final ConduitType[] types = {Power.ELECTRIC_POWER, Fluids.fluidConduit_general};

	@Override
	public ConduitType[] getTypes(){
		return types;
	}
	///// end multi-type overrides /////


}
