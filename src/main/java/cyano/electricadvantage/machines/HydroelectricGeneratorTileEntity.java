package cyano.electricadvantage.machines;

import cyano.electricadvantage.entities.HydroturbineEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.Random;

public class HydroelectricGeneratorTileEntity extends ElectricGeneratorTileEntity {

	
	public static final float ENERGY_PER_TICK = 4;
	
	private static final long checkInterval = 101;
	private static final Random initRand = new Random();
	private final long checkOffset = initRand.nextInt((int)checkInterval);
	
	private boolean isInitialized;
	
	public HydroelectricGeneratorTileEntity() {
		super(HydroelectricGeneratorTileEntity.class.getName(), 0);
		isInitialized = false;
	}

	@Override
	public void tickUpdate(boolean isServer) {
		if(isServer){
			if(!isInitialized){
				initialize();
			}
			if((getWorld().getTotalWorldTime() + checkOffset) % checkInterval == 0){
				// periodically check if the hydroturbine entity is still present
				List turbines = getWorld().getEntitiesWithinAABB(HydroturbineEntity.class, new AxisAlignedBB(
						getPos().getX(),getPos().getY()-1,getPos().getZ(),
						getPos().getX()+1,getPos().getY(),getPos().getZ()+1));
				if(turbines == null || turbines.isEmpty()){
					// turbine entity got deleted
					initialize();
				}
			}
		}
	}
	
	private void initialize() {
		isInitialized = true;
		if(getPos().getY() > 0){
			getWorld().spawnEntityInWorld(new HydroturbineEntity(getWorld(),this));
		}
	}

	@Override
	public float getPowerOutput() {
		if(this.isActive()){
			return 15;
		} else {
			return 0;
		}
	}

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return false;
	}

	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		tagRoot.setBoolean("initDone", this.isInitialized);
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		if(tagRoot.hasKey("initDone")){
			this.isInitialized = tagRoot.getBoolean("initDone");
		}
	}

	final int[] dataArray = new int[1];
	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataArray[0]), getType());
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataArray[0] = Float.floatToIntBits(getEnergy());
	}

	@Override
	public void setActive(boolean active){
		// make public
		super.setActive(active);
	}
}
