package cyano.electricadvantage.machines;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HydroelectricGeneratorTileEntity extends ElectricGeneratorTileEntity {

	public HydroelectricGeneratorTileEntity() {
		super(HydroelectricGeneratorTileEntity.class.getName(), 0);
	}

	@Override
	public void tickUpdate(boolean isServer) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public float getPowerOutput() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return false;
	}

	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getDataFieldArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDataFieldUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareDataFieldsForSync() {
		// TODO Auto-generated method stub
		
	}

}
