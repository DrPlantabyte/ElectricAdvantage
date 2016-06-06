package cyano.electricadvantage.machines;

import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

import java.util.Arrays;
import java.util.List;

public abstract class ElectricMachineTileEntity extends cyano.poweradvantage.api.simple.TileEntitySimplePowerMachine{

	private final ItemStack[] inventory;
	private final int[] inputSlots;
	private final int[] outputSlots;
	private final int[] ioSlots;
	private final int[] otherSlots;
	
	
	
	public ElectricMachineTileEntity(String name,float bufferSize,int numInputSlots, int numOutputSlots, int numOtherSlots) {
		this(name,Power.ELECTRIC_POWER,bufferSize,numInputSlots,numOutputSlots,numOtherSlots);
	}
	
	public ElectricMachineTileEntity(String name, ConduitType powerType, float bufferSize,int numInputSlots, int numOutputSlots, int numOtherSlots) {
		super(powerType, bufferSize, name);
		inventory = new ItemStack[numInputSlots+numOutputSlots+numOtherSlots];
		inputSlots = new int[numInputSlots];
		outputSlots = new int[numOutputSlots];
		ioSlots = new int[numInputSlots+numOutputSlots];
		otherSlots = new int[numOtherSlots];
		for(int i = 0; i < inventory.length; i++){
			if(i < inputSlots.length)inputSlots[i] = i;
			if(i < outputSlots.length)outputSlots[i] = inputSlots.length + i;
			if(i < ioSlots.length)ioSlots[i] = i;
			if(i < otherSlots.length)otherSlots[i] = inputSlots.length + outputSlots.length + i;
		}
	}
	public ElectricMachineTileEntity(String name,int numInputSlots, int numOutputSlots, int numOtherSlots) {
		this(name,500f,numInputSlots,numOutputSlots,numOtherSlots);
	}
	public ElectricMachineTileEntity(String name,int numInputSlots, int numOutputSlots, int numOtherSlots, ConduitType[] energyTypes, float[] energyBuffers) {
		super(energyTypes, energyBuffers, name);
		inventory = new ItemStack[numInputSlots+numOutputSlots+numOtherSlots];
		inputSlots = new int[numInputSlots];
		outputSlots = new int[numOutputSlots];
		ioSlots = new int[numInputSlots+numOutputSlots];
		otherSlots = new int[numOtherSlots];
		for(int i = 0; i < inventory.length; i++){
			if(i < inputSlots.length)inputSlots[i] = i;
			if(i < outputSlots.length)outputSlots[i] = inputSlots.length + i;
			if(i < ioSlots.length)ioSlots[i] = i;
			if(i < otherSlots.length)otherSlots[i] = inputSlots.length + outputSlots.length + i;
		}
	}
	
	private boolean redstone = false;
	private float oldEnergy = 0f;
	private int[] syncArrayOld = null;
	private int[] syncArrayNew = null;
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		
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

	
	protected boolean hasRedstoneSignal(){
		return redstone;
	}

	
	protected void setActiveState(boolean active){
		IBlockState oldState = getWorld().getBlockState(getPos());
		if(oldState.getBlock() instanceof ElectricMachineBlock 
				&& (Boolean)oldState.getValue(ElectricMachineBlock.ACTIVE) != active ){
			final TileEntity save = this;
			final World w = getWorld();
			final BlockPos pos = this.getPos();
			IBlockState newState = oldState.withProperty(ElectricMachineBlock.ACTIVE, active);
			w.setBlockState(pos, newState,3);
			if(save != null){
				w.removeTileEntity(pos);
				save.validate();
				w.setTileEntity(pos, save);
			}
		}
	}
	protected void setPowerState(boolean powered){
		IBlockState oldState = getWorld().getBlockState(getPos());
		if(oldState.getBlock() instanceof ElectricMachineBlock 
				&& (Boolean)oldState.getValue(ElectricMachineBlock.POWERED) != powered ){
			final TileEntity save = this;
			final World w = getWorld();
			final BlockPos pos = this.getPos();
			IBlockState newState = oldState.withProperty(ElectricMachineBlock.POWERED, powered);
			w.setBlockState(pos, newState, 3);
			if(save != null){
				w.removeTileEntity(pos);
				save.validate();
				w.setTileEntity(pos, save);
			}
		}
	}

	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		// used to allow change in blockstate without interrupting the TileEntity or the GUI
		return (oldState.getBlock() != newState.getBlock());
	}

	public boolean isActive(){
		return isPowered() && (!this.hasRedstoneSignal() && (this.getEnergy(Power.ELECTRIC_POWER) > 0 || (Boolean)getWorld().getBlockState(getPos()).getValue(ElectricMachineBlock.ACTIVE)));
	}
	
	public abstract boolean isPowered();

	public ItemStack getInputSlot(int i){
		if(i < inputSlots.length){
			return inventory[inputSlots[i]];
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
			return null;
		}
	}
	
	public ItemStack getOutputSlot(int i){
		if(i < outputSlots.length){
			return inventory[outputSlots[i]];
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
			return null;
		}
	}
	
	public ItemStack getOtherSlot(int i){
		if(i < otherSlots.length){
			return inventory[otherSlots[i]];
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
			return null;
		}
	}
	public void setInputSlot(int i, ItemStack item){
		if(i < inputSlots.length){
			inventory[inputSlots[i]] = item;
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
		}
	}
	
	public void setOutputSlot(int i, ItemStack item){
		if(i < outputSlots.length){
			inventory[ outputSlots[i]] = item;
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
		}
	}
	
	public void setOtherSlot(int i, ItemStack item){
		if(i < otherSlots.length){
			inventory[otherSlots[i]] = item;
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
		}
	}
	

	public int numberOfInputSlots(){return inputSlots.length;}
	public int numberOfOutputSlots(){return outputSlots.length;}
	public int numberOfOtherSlots(){return otherSlots.length;}

	
	@Override
	protected ItemStack[] getInventory() {
		return inventory;
	}

	public abstract float[] getProgress();
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagRoot){
		super.writeToNBT(tagRoot);
		saveTo(tagRoot);
		return tagRoot;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagRoot){
		super.readFromNBT(tagRoot);
		loadFrom(tagRoot);
	}

	protected abstract void saveTo(NBTTagCompound tagRoot);
	protected abstract void loadFrom(NBTTagCompound tagRoot);

	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return ioSlots;
	}
	
	@Override
	public boolean canExtractItem(final int slot, final ItemStack targetItem, final EnumFacing side) {
		return isInArray(slot,outputSlots);
	}

	public boolean hasSpaceForItemInOutputSlots(ItemStack stack){
		ItemStack item = stack.copy();
		for(int slot = 0; slot < numberOfOutputSlots(); slot++){
			ItemStack dest = getOutputSlot(slot);
			if(dest == null) return true;
			int stackLimit = Math.min(dest.getMaxStackSize(), this.getInventoryStackLimit());
			if(dest.stackSize >= stackLimit) continue;
			int combinedStackSize = item.stackSize + dest.stackSize;
			if(ItemStack.areItemsEqual(item, dest) ){
				if(combinedStackSize <= stackLimit){
					return true;
				} else if (item.stackSize <= stackLimit) {
					item.stackSize -= stackLimit - dest.stackSize;
				}
			}
		}
		return false;
	}
	
	public boolean hasSpaceForItemInInputSlots(ItemStack stack){
		ItemStack item = stack.copy();
		for(int slot = 0; slot < numberOfInputSlots(); slot++){
			ItemStack dest = getInputSlot(slot);
			if(dest == null) return true;
			int stackLimit = Math.min(dest.getMaxStackSize(), this.getInventoryStackLimit());
			if(dest.stackSize >= stackLimit) continue;
			int combinedStackSize = item.stackSize + dest.stackSize;
			if(ItemStack.areItemsEqual(item, dest) ){
				if(combinedStackSize <= stackLimit){
					return true;
				} else if (item.stackSize <= stackLimit) {
					item.stackSize -= stackLimit - dest.stackSize;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
		return isValidInputItem(srcItem) && isInArray(slot,inputSlots);
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack srcItem) {
		return isValidInputItem(srcItem) && isInArray(slot,inputSlots);
	}
	
	protected abstract boolean isValidInputItem(ItemStack item);
	
	protected final boolean isInArray(int i, int[] arr){
		for(int n = 0; n < arr.length; n++){
			if(arr[n] == i)return true;
		}
		return false;
	}
	
	public ItemStack insertItemToInputSlots(ItemStack itemStack){
        if(itemStack == null) return null;
        if(itemStack.getItem() == null) return null;
		itemStack = itemStack.copy();
		for(int slot = 0; slot < numberOfInputSlots(); slot++){
			ItemStack slotContent = this.getInputSlot(slot);
			if(slotContent == null){
				// empty slot
				this.setInputSlot(slot,itemStack);
				return null;
			} else if(ItemStack.areItemsEqual(itemStack, slotContent)){
				int stackLimit = Math.min(slotContent.getMaxStackSize(), this.getInventoryStackLimit());
				// found slot with same item
				if(slotContent.stackSize < stackLimit){
					// increase stack
					int delta = Math.min(itemStack.stackSize, stackLimit - slotContent.stackSize);
					slotContent.stackSize += delta;
					itemStack.stackSize -= delta;
					if(itemStack.stackSize <= 0) return null; // done
				}
			}
		}
		// return left-over
		return itemStack;
	}

	protected void playSoundEffect(double x, double y, double z, SoundEvent sound, float volume, float pitch){
		if(getWorld().isRemote) return;
		final double range = 16;
		List<EntityPlayerMP> players = getWorld().getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(
				x - range, y - range, z - range,
				x + range, y + range, z + range));
		for(EntityPlayerMP player : players){
			player.connection.sendPacket(new SPacketCustomSound(sound.getRegistryName().toString(), SoundCategory.BLOCKS,
					x, y, z, (float)volume, (float)pitch));
		}
	}
	
	public ItemStack insertItemToOutputSlots(ItemStack itemStack){
        if(itemStack == null) return null;
        if(itemStack.getItem() == null) return null;
		itemStack = itemStack.copy();
		for(int slot = 0; slot < numberOfOutputSlots(); slot++){
			ItemStack slotContent = this.getOutputSlot(slot);
			if(slotContent == null){
				// empty slot
				this.setOutputSlot(slot,itemStack);
				return null;
			} else if(ItemStack.areItemsEqual(itemStack, slotContent)){
				int stackLimit = Math.min(slotContent.getMaxStackSize(), this.getInventoryStackLimit());
				// found slot with same item
				if(slotContent.stackSize < stackLimit){
					// increase stack
					int delta = Math.min(itemStack.stackSize, stackLimit - slotContent.stackSize);
					slotContent.stackSize += delta;
					itemStack.stackSize -= delta;
					if(itemStack.stackSize <= 0) return null; // done
				}
			}
		}
		// return left-over
		return itemStack;
	}
	
	public int getComparatorOutput(){
		// proportional to number of input slot items
		int sum = 0;
		int total = 0;
		for(int i = 0; i < numberOfInputSlots(); i++){
			ItemStack item = getInputSlot(i);
			if(item == null){
				total += 64;
			} else {
				sum += item.stackSize;
				total += item.getMaxStackSize();
			}
		}
		if(total <= 0) return 0;
		return 15 * sum / total;
	}

	public float getEnergy(){
		return getEnergy(Power.ELECTRIC_POWER);
	}

	public float getEnergyCapacity(){
		return getEnergyCapacity(Power.ELECTRIC_POWER);
	}

	public float addEnergy(float amount){
		return addEnergy(amount,Power.ELECTRIC_POWER);
	}

	public float subtractEnergy(float amount){
		return subtractEnergy(amount,Power.ELECTRIC_POWER);
	}

	public void setEnergy(float amount){ setEnergy(amount,Power.ELECTRIC_POWER); }


	public ConduitType getType() {return Power.ELECTRIC_POWER;}


	@Override public int getInventoryStackLimit(){return 64;}


	@Override
	public boolean isPowerSink(ConduitType e){
		return true;
	}

	@Override
	public boolean isPowerSource(ConduitType e){
		return false;
	}
}
