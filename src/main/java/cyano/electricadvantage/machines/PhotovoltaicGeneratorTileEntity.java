package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PhotovoltaicGeneratorTileEntity extends ElectricGeneratorTileEntity {
	private final float PI = (float)Math.PI;
	private final float SUM_OF_SINE_TICKS = 24000 / PI;
	public final float SOLAR_PER_DAY = 25000; // half the energy of a piece of coal per day
	public final float MAX_SOLAR_OUTPUT = SOLAR_PER_DAY / SUM_OF_SINE_TICKS;
	private float powerOutput = 0;
	
	public PhotovoltaicGeneratorTileEntity() {
		super(PhotovoltaicGeneratorTileEntity.class.getSimpleName(), 0);
	}

	@Override
	public float getPowerOutput() {
		return powerOutput;
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

	final int[] da = new int[1];
	@Override
	public int[] getDataFieldArray() {
		return da;
	}

	@Override
	public void onDataFieldUpdate() {
		powerOutput = Float.intBitsToFloat(da[0]);
	}

	@Override
	public void prepareDataFieldsForSync() {
		da[0] = Float.floatToIntBits(powerOutput);
	}

	@Override
	public void tickUpdate(boolean isServer) {
		if(isServer){
			World world = getWorld();
			float sinTime = MathHelper.sin((float)world.getWorldTime() * 2.61799E-4f); // (worldTime * pi / 12000)
			float power = Math.max(0,MAX_SOLAR_OUTPUT * sinTime);
			this.addEnergy(power, Power.ELECTRIC_POWER);
			powerOutput = power / MAX_SOLAR_OUTPUT;
		}
	}

}
