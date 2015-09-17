package cyano.electricadvantage.machines;

import cyano.electricadvantage.entities.HydroturbineEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HydroelectricGeneratorTileEntity extends ElectricGeneratorTileEntity {

	
	public static final float ENERGY_PER_TICK = 4;
	
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
