package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerConnectorContext;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import cyano.poweradvantage.init.Fluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;

import java.util.HashSet;
import java.util.Set;

public class ElectricPumpTileEntity extends ElectricMachineTileEntity implements IFluidHandler{


	private final FluidTank tank;
	public static final float ENERGY_COST_PIPE = 100f;
	public static final float ENERGY_COST_VERTICAL = 32f;
	public static final float ENERGY_COST_PUMP = 1000f;
	public static final int VOLUME_PER_PUMP = 1000;
	public static final byte PUMP_INTERVAL = 32;
	public static final byte PIPE_INTERVAL = 11;
	private static final int limit = 5 * 5 * 5; // do not search more than this many blocks at a time



	private final int[] dataSyncArray = new int[4];
	private byte timeUntilNextPump = PUMP_INTERVAL;
	
	

	public ElectricPumpTileEntity() {
		super(ElectricPumpTileEntity.class.getSimpleName(), 0, 0, 0, new ConduitType[]{Power.ELECTRIC_POWER, Fluids.fluidConduit_general},new float[]{ENERGY_COST_PUMP + 256 * ENERGY_COST_VERTICAL,1000f});
		tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	}
	

	private int timeToSound = 0;

	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			// server-side logic
			net.minecraft.tileentity.TileEntityPiston k;
			if(!hasRedstoneSignal()){
				if(timeUntilNextPump > 0) timeUntilNextPump--;
				if(timeUntilNextPump == 0 && getTank().getFluidAmount() <= 0){
					World w = getWorld();
					boolean success = false;
					BlockPos target = this.getPos().down();
					while(target.getY() > 0 && w.getBlockState(target).getBlock() == cyano.electricadvantage.init.Blocks.pump_pipe_electric){
						target = target.down();
					}
					if(target.getY() > 0 && w.isAirBlock(target) && getEnergy() >= ENERGY_COST_PIPE){
						// place pipe
						w.setBlockState(target, cyano.electricadvantage.init.Blocks.pump_pipe_electric.getDefaultState());
						this.subtractEnergy(ENERGY_COST_PIPE, Power.ELECTRIC_POWER);
						timeUntilNextPump = PIPE_INTERVAL;
						playSoundEffect(target.getX()+0.5, target.getY()+0.5, target.getZ()+0.5, SoundEvents.BLOCK_STONE_STEP, 0.3f, 1f);
					} else {
						// pump fluids
						BlockPos fluidSource = null;
						Set<BlockPos> searchSpace = new HashSet<>(limit + 1); // pre-allocate max memory usage
						fluidSource = scan(searchSpace,target.down(), limit);
						if(fluidSource == null) fluidSource = scan(searchSpace,target.north(), limit);
						if(fluidSource == null) fluidSource = scan(searchSpace,target.west(), limit);
						if(fluidSource == null) fluidSource = scan(searchSpace,target.south(), limit);
						if(fluidSource == null) fluidSource = scan(searchSpace,target.east(), limit);
						if(fluidSource != null){
							float cost = ENERGY_COST_PUMP+ENERGY_COST_VERTICAL*(getPos().getY()-fluidSource.getY());
							// Found a fluid to suck-up!
							IBlockState blockstate = w.getBlockState(fluidSource);
							Fluid f = getFluid(blockstate);
							if(f != null && getEnergy() >= cost){
								this.getTank().fill(new FluidStack(f,FluidContainerRegistry.BUCKET_VOLUME), true);
								this.subtractEnergy(cost, Power.ELECTRIC_POWER);
								w.setBlockToAir(fluidSource);
								success = true;
							}
						}
						timeUntilNextPump = PUMP_INTERVAL;
					}
					if(success){
						playSoundEffect(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5, SoundEvents.ITEM_BUCKET_FILL, 0.5f, 1f);
						playSoundEffect(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5, SoundEvents.BLOCK_PISTON_EXTEND, 0.3f, 1f);
						timeToSound = 14;
					}
				}
			}
			if(timeToSound == 1){
				playSoundEffect(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5, SoundEvents.BLOCK_PISTON_CONTRACT, 0.3f, 1f);
			}
			if(timeToSound > 0) timeToSound--;
		}
	}
	
	
	@Override
	public boolean isPowered() {
		return getEnergy() > ENERGY_COST_PUMP;
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
	

	private Fluid getFluid(IBlockState blockstate) {
		return FluidRegistry.lookupFluidForBlock(blockstate.getBlock());
	}



	private BlockPos scan(Set<BlockPos> searchSpace, BlockPos coord, int limit) {
		if(isFluidBlock(coord) == false) return null;
		do{
			if(isSourceBlock(coord)){
				return coord;
			} else {
				searchSpace.add(coord);
				BlockPos next;
				boolean nomore = true;
				for(EnumFacing dir : EnumFacing.VALUES){
					next = coord.offset(dir);
					if(!searchSpace.contains(next) && isFluidBlock(next)){
						coord = next;
						nomore = false;
						break;
					}
				}
				if(nomore){
					return null;
				}
			}
		} while(searchSpace.size() < limit);
		return null;
	}
	
	private boolean isSourceBlock( BlockPos coord){
		IBlockState bs = getWorld().getBlockState(coord);
		if(bs.getBlock() instanceof BlockLiquid){
			return (Integer)bs.getValue(BlockDynamicLiquid.LEVEL) == 0;
		} else if(bs.getBlock() instanceof BlockFluidClassic){
			return ((BlockFluidClassic)bs.getBlock()).isSourceBlock(getWorld(), coord);
		}else{
			return false;
		}
	}



	private boolean isFluidBlock(BlockPos coord) {
		World w = getWorld();
		if(w.isBlockLoaded(coord) == false) return false;
		Block b = w.getBlockState(coord).getBlock();
		return (b instanceof BlockLiquid || b instanceof IFluidBlock);
	}

	
	private boolean redstone = true;
	private float oldEnergy = 0;
	private int oldFluid = 0;
	@Override
	public void powerUpdate(){
		// deliberately NOT calling super.powerUpdate()
		if(this.getTank().getFluidAmount() > 0){
			ConduitType type = Fluids.fluidToConduitType(getTank().getFluid().getFluid());
			float availableAmount = getTank().getFluidAmount();
			float delta = ConduitRegistry.transmitPowerToConsumers(availableAmount, cyano.poweradvantage.init.Fluids.fluidConduit_general, type, 
					PowerRequest.LAST_PRIORITY, getWorld(), getPos(), this);
			if(delta > 0){
				getTank().drain(Math.max((int)delta,1),true); // no free energy!
			}
		}
		// powerUpdate occurs once every 8 world ticks and is scheduled such that neighboring 
		// machines don't powerUpdate in the same world tick. To reduce network congestion, 
		// I'm doing the synchonization logic here instead of in the tickUpdate method
		boolean updateFlag = false;

		if(oldEnergy != getEnergy()){
			oldEnergy = getEnergy();
			updateFlag = true;
		}
		if(oldFluid != getTank().getFluidAmount()){
			oldFluid = getTank().getFluidAmount();
			updateFlag = true;
		}

		redstone = hasRedstoneSignal();
		this.setPowerState(this.isPowered());

		if(updateFlag){
			super.sync();
		}
	}
	@Override
	protected boolean hasRedstoneSignal() {
		return getWorld().isBlockPowered(getPos());
	}
	
	

	public FluidTank getTank(){
		return tank;
	}

	private final ConduitType[] types = {Power.ELECTRIC_POWER, Fluids.fluidConduit_general};

	@Override
	public ConduitType[] getTypes(){
		return types;
	}

	@Override
	public int[] getDataFieldArray() {
		return dataSyncArray;
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataSyncArray[0] = Float.floatToRawIntBits(this.getEnergy());
		dataSyncArray[1] = this.getTank().getFluidAmount();
		dataSyncArray[2] = (this.getTank().getFluidAmount() > 0 ? FluidRegistry.getFluidID(this.getTank().getFluid().getFluid()) : FluidRegistry.getFluidID(FluidRegistry.WATER));
		dataSyncArray[3] = this.timeUntilNextPump;
	}

	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataSyncArray[0]), Power.ELECTRIC_POWER);
		this.getTank().setFluid(new FluidStack(FluidRegistry.getFluid(dataSyncArray[2]),dataSyncArray[1]));
		this.timeUntilNextPump = (byte)dataSyncArray[3];
	}

	

	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
	public void saveTo(final NBTTagCompound tagRoot) {
		NBTTagCompound tankTag = new NBTTagCompound();
		this.getTank().writeToNBT(tankTag);
		tagRoot.setTag("Tank", tankTag);
		tagRoot.setByte("NextPump", (byte)this.timeUntilNextPump);
	}
	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
	public void loadFrom(final NBTTagCompound tagRoot) {
		if (tagRoot.hasKey("Tank")) {
			NBTTagCompound tankTag = tagRoot.getCompoundTag("Tank");
			getTank().readFromNBT(tankTag);
			if(tankTag.hasKey("Empty")){
				// empty the tank if NBT says its empty (not default behavior of Tank.readFromNBT(...) )
				getTank().setFluid(null);
			}
		}

		if (tagRoot.hasKey("NextPump")) {
			this.timeUntilNextPump = tagRoot.getByte("NextPump");
		}
	}

	@Override
	public int getComparatorOutput() {
		return 15 * getTank().getFluidAmount() / getTank().getCapacity();
	}

///// Overrides to make this a multi-type block /////
	/**
	 * Determines whether this block/entity should receive energy 
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType p){
		return ConduitType.areSameType(Power.ELECTRIC_POWER,p);
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
			if(amount > 0 && this.canFill(null, Fluids.conduitTypeToFluid(type))){
				return this.fill(null, new FluidStack(Fluids.conduitTypeToFluid(type),(int)amount), true);
			} else if (amount < 0 && this.canDrain(null, Fluids.conduitTypeToFluid(type))){
				return -1 * this.drain(null, (int)amount, true).amount;
			}
		}
		return super.addEnergy(amount, type);
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
		return addEnergy(-1*amount,type);
	}

	@Override
	public PowerRequest getPowerRequest(ConduitType offer) {
		if(! redstone && ConduitType.areSameType(offer, Power.ELECTRIC_POWER)){
			return new PowerRequest(PowerRequest.LOW_PRIORITY,this.getEnergyCapacity() - this.getEnergy(), this);
		}
		return PowerRequest.REQUEST_NOTHING;
	}


	@Override
	public boolean canAcceptConnection(PowerConnectorContext c){
		return ConduitType.areSameType(getType(), c.powerType) || Fluids.isFluidType( c.powerType);
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
		if(getTank().getFluidAmount() <= 0 || getTank().getFluid().getFluid().equals(fluid.getFluid())){
			return getTank().fill(fluid, forReal);
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
		if(fluid == null) return false;
		if(getTank().getFluidAmount() <= 0) return true;
		return getTank().getFluidAmount() <= getTank().getCapacity() && fluid.equals(getTank().getFluid().getFluid());
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 */
	@Override
	public boolean canDrain(EnumFacing face, Fluid fluid) {
		if(fluid == null) return false;
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
	///// end multi-type overrides /////





}
