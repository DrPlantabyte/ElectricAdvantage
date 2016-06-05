package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerConnectorContext;
import cyano.poweradvantage.api.PowerRequest;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import static cyano.electricadvantage.machines.SteamPoweredElectricGeneratorBlock.STEAM_POWER;

public class SteamPoweredElectricGeneratorTileEntity extends ElectricGeneratorTileEntity{


	
	private final float maxSteam = 32;
	private float steam = 0;
	private final float steamDecay = 0.0625f*8;
	private final float maxSteamConversionPerTick = maxSteam / 8;
	
	private float lastTransmissionCurrent = 0;
	
	private final int[] dataArray = new int[3];
	
	public SteamPoweredElectricGeneratorTileEntity() {
		super(SteamPoweredElectricGeneratorTileEntity.class.getSimpleName(), 0, new ConduitType[]{Power.ELECTRIC_POWER,STEAM_POWER}, new float[]{1000f,32f});
	}

	@Override public void powerUpdate(){
		if(steam > steamDecay)steam -= steamDecay;
		boolean flag = false;
		if(hasRedstoneSignal() == false){
			float max = maxSteamConversionPerTick*8;
			float steamDemand = (getEnergyCapacity() - getEnergy()) * Power.ELECTRICITY_TO_STEAM;
			flag = steam > 0;
			float delta = Math.min(Math.min(steam, steamDemand),max);
			steam -= delta;
			lastTransmissionCurrent = delta / max;
			addEnergy(delta * Power.STEAM_TO_ELECTRICITY, Power.ELECTRIC_POWER);
			this.sync();
		} else {
			lastTransmissionCurrent = 0;
			flag = false;
		}
		super.powerUpdate();
		super.setActive(flag);
		if(flag && getWorld().rand.nextInt(200) == 0){
			playSoundEffect(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5, SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.25f, 1f);
		}
	}

	@Override
	public float getPowerOutput() {
		return Math.max(getEnergy() / getEnergyCapacity(), lastTransmissionCurrent);
	}

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return false;
	}

	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	
	
	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataArray[0]), Power.ELECTRIC_POWER);
		this.setEnergy(Float.intBitsToFloat(dataArray[1]), STEAM_POWER);
		lastTransmissionCurrent = Float.intBitsToFloat(dataArray[2]);
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataArray[0] = Float.floatToIntBits(getEnergy());
		dataArray[1] = Float.floatToIntBits(steam);
		dataArray[2] = Float.floatToIntBits(lastTransmissionCurrent);
	}

	@Override
	public void tickUpdate(boolean arg0) {
		// do nothing
	}

///// Overrides to make this a multi-type block /////
@Override
public boolean isPowerSink(ConduitType pt){
	return ConduitType.areSameType(STEAM_POWER,pt);
}
	@Override
	public boolean isPowerSource(ConduitType pt){
		return ConduitType.areSameType(Power.ELECTRIC_POWER,pt);
	}


	@Override
	public boolean canAcceptConnection(PowerConnectorContext c){
		return ConduitType.areSameType(Power.ELECTRIC_POWER,c.powerType)
				|| ConduitType.areSameType(STEAM_POWER,c.powerType);
	}
	private final ConduitType[] types = {Power.ELECTRIC_POWER,STEAM_POWER};
	@Override
	public ConduitType[] getTypes(){
		return types;
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
		if(ConduitType.areSameType(type, STEAM_POWER)){
			float delta = Math.max(Math.min(amount, maxSteam - steam),-1 * steam);
			steam += delta;
			return delta;
		} else {
			return super.addEnergy(amount, type);
		}
	}
	/**
	 * Sets the tank contents using the energy API method
	 * @param amount amount of energy/fluid to add
	 * @param type the type of energy/fluid being added.
	 */
	@Override
	public void setEnergy(float amount,ConduitType type) {
		if(ConduitType.areSameType(type, STEAM_POWER)){
			steam = amount;
		} else {
			super.setEnergy(amount, type);
		}
	}
	
	@Override
	public PowerRequest getPowerRequest(ConduitType offer) {
		if(ConduitType.areSameType(offer, STEAM_POWER)){
			return new PowerRequest(PowerRequest.LOW_PRIORITY, maxSteam - steam,this);
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
		return ConduitType.areSameType(getType(), type) || ConduitType.areSameType(STEAM_POWER, type);
	}
	/**
	 * Determines whether this conduit is compatible with a type of energy through any side
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given energy type through one or more of its block 
	 * faces, false otherwise
	 */
	public boolean canAcceptType(ConduitType type){
		return ConduitType.areSameType(getType(), type) || ConduitType.areSameType(STEAM_POWER, type);
	}
	///// end multi-type overrides /////

	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		tagRoot.setFloat("steam", steam);
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		steam = tagRoot.getFloat("steam");
	}
	
}
