package cyano.electricadvantage.machines;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;

public class SteamPoweredElectricGeneratorTileEntity extends ElectricGeneratorTileEntity{


	
	private final float maxSteam = 32;
	private float steam = 0;
	private final float steamDecay = 0.0625f*8;
	private final float maxSteamConversionPerTick = 1.75f;
	
	private float lastTransmissionCurrent = 0;
	
	private final int[] dataArray = new int[3];
	
	public SteamPoweredElectricGeneratorTileEntity() {
		super(SteamPoweredElectricGeneratorTileEntity.class.getSimpleName(), 0);
	}

	@Override public void powerUpdate(){
		if(steam > steamDecay)steam -= steamDecay;
		if(hasRedstoneSignal() == false){
			float steamDemand = (getEnergyCapacity() - getEnergy()) * Power.ELECTRICITY_TO_STEAM;
			float delta = Math.min(Math.min(steam, steamDemand),maxSteamConversionPerTick*8);
			steam -= delta;
			addEnergy(delta * Power.STEAM_TO_ELECTRICITY, Power.ELECTRIC_POWER);
			super.setActive(steam > 0);
		} else {
			super.setActive(false);
		}
		float before = getEnergy();
		super.powerUpdate();
		float after = getEnergy();
		lastTransmissionCurrent = Math.max(1f, (before - after) * Power.ELECTRICITY_TO_STEAM / (maxSteam - steamDecay));
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
	public boolean isActive(){
		return (hasRedstoneSignal() == false) && steam > 0;
	}
	
	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataArray[0]), Power.ELECTRIC_POWER);
		this.setEnergy(Float.intBitsToFloat(dataArray[1]), SteamPoweredElectricGeneratorBlock.STEAM_POWER);
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
	public boolean isPowerSink(){
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
		if(ConduitType.areSameType(type, SteamPoweredElectricGeneratorBlock.STEAM_POWER)){
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
		if(ConduitType.areSameType(type, SteamPoweredElectricGeneratorBlock.STEAM_POWER)){
			steam = amount;
		} else {
			super.setEnergy(amount, type);
		}
	}
	
	@Override
	public PowerRequest getPowerRequest(ConduitType offer) {
		if(ConduitType.areSameType(offer, SteamPoweredElectricGeneratorBlock.STEAM_POWER)){
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
		return ConduitType.areSameType(getType(), type) || ConduitType.areSameType(SteamPoweredElectricGeneratorBlock.STEAM_POWER, type);
	}
	/**
	 * Determines whether this conduit is compatible with a type of energy through any side
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given energy type through one or more of its block 
	 * faces, false otherwise
	 */
	public boolean canAcceptType(ConduitType type){
		return ConduitType.areSameType(getType(), type) || ConduitType.areSameType(SteamPoweredElectricGeneratorBlock.STEAM_POWER, type);
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
