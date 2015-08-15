package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class PhotovoltaicGeneratorTileEntity extends ElectricGeneratorTileEntity {

	private static final float PI = (float)Math.PI;
	private static final float SUM_OF_SINE_TICKS = 24000 / PI / 8;
	public static final float SOLAR_PER_DAY = 12500; // quarter the energy of a piece of coal per day
	public static final float MAX_SOLAR_OUTPUT = PI * SOLAR_PER_DAY / SUM_OF_SINE_TICKS;
	public static final float BLOCKLIGHT_FACTOR = 0.1f; // light from blocks less effective than sunlight
	private float powerOutput = 0;
	
	public PhotovoltaicGeneratorTileEntity() {
		super(PhotovoltaicGeneratorTileEntity.class.getSimpleName(), 0);
	}

	@Override
	public float getPowerOutput() {
		return powerOutput;
	}
	
	@Override
	public int getComparatorOutput(){
		return (int)(15 * powerOutput);
	}


	@Override
	public boolean isActive(){
		return (hasRedstoneSignal() == false) && powerOutput > 0.0001;
	}
	
	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return false;
	}

	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		// do nothing
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		// do nothing
	}

	final int[] dataArray = new int[1];
	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	@Override
	public void onDataFieldUpdate() {
		powerOutput = Float.intBitsToFloat(dataArray[0]);
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataArray[0] = Float.floatToIntBits(powerOutput);
	}

	@Override
	public void tickUpdate(boolean isServer) {
		// do nothing
	}
	float oldPower = 0;
	@Override
	public void powerUpdate(){
		float power;
		if(hasRedstoneSignal() == false){
			float solar = getLightIntensityAt(getPos(),getWorld());
			if(getWorld().isRaining()){
				solar *= 0.25f;
			}
			power = Math.max(0,MAX_SOLAR_OUTPUT * solar);
			this.addEnergy(power, Power.ELECTRIC_POWER);
		} else {
			power = 0f;
		}
		powerOutput = power / MAX_SOLAR_OUTPUT;
		if(powerOutput != oldPower){
			oldPower = powerOutput;
			this.sync();
		}
		
		super.powerUpdate();
	}
	
	public static float getLightIntensityAt(BlockPos pos, World w){
		float light;
		if(w.canSeeSky(pos) && (w.provider.getHasNoSky() == false)){
			// outdoor light imposes no penalty
			light = MathHelper.sin((float)w.getWorldTime() * 2.61799E-4f); // (worldTime * pi / 12000);
		} else {
			float blockLight = w.getLightFromNeighbors(pos) * 6.66666E-2f; // block light / 15
			light = BLOCKLIGHT_FACTOR * blockLight;
		}
		return light;
	}

}
