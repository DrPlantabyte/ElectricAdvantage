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

public abstract class ElectricGeneratorTileEntity extends cyano.poweradvantage.api.simple.TileEntitySimplePowerMachine {

	private final ItemStack[] inventory;
	private final int[] inputSlots;
	
	
	public ElectricGeneratorTileEntity(String name,int numInputSlots) {
		super(Power.ELECTRIC_POWER, 1000, name);
		inventory = new ItemStack[numInputSlots];
		inputSlots = new int[numInputSlots];
		for(int i = 0; i < inventory.length; i++){
			if(i < inputSlots.length)inputSlots[i] = i;
		}
	}

	public ElectricGeneratorTileEntity(String name,int numInputSlots, ConduitType[] types, float[] energyBuffers) {
		super(types, energyBuffers, name);
		inventory = new ItemStack[numInputSlots];
		inputSlots = new int[numInputSlots];
		for(int i = 0; i < inventory.length; i++){
			if(i < inputSlots.length)inputSlots[i] = i;
		}
	}


	private boolean redstone = false;
	private float oldEnergy = 0f;
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		
		redstone = getWorld().isBlockPowered(getPos());
		
		if(oldEnergy != this.getEnergy(Power.ELECTRIC_POWER)){
			this.sync();
		}
	}
	
	protected boolean hasRedstoneSignal(){
		return redstone;
	}

	
	protected void setActive(boolean active){
		IBlockState old = getWorld().getBlockState(getPos());
		if(old.getBlock() instanceof ElectricGeneratorBlock 
				&& (Boolean)old.getValue(ElectricGeneratorBlock.ACTIVE) != active){
			final TileEntity save = this;
			final World w = getWorld();
			final BlockPos pos = this.getPos();
			w.setBlockState(pos, old.withProperty(ElectricGeneratorBlock.ACTIVE, active),3);
			if(save != null){
				w.removeTileEntity(pos);
				save.validate();
				w.setTileEntity(pos, save);
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		// used to allow change in blockstate without interrupting the TileEntity or the GUI
		return (oldState.getBlock() != newSate.getBlock());
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

	public boolean isActive(){
		return (Boolean)getWorld().getBlockState(getPos()).getValue(ElectricGeneratorBlock.ACTIVE);
	}

	@Override
	public boolean isPowerSink(ConduitType e){
		return false;
	}

	@Override
	public boolean isPowerSource(ConduitType e){
		return true;
	}
	
	public abstract float getPowerOutput();

	public ItemStack getInputSlot(int i){
		if(i < inputSlots.length){
			return inventory[inputSlots[i]];
		} else {
			FMLLog.warning("Bug in class %s. StackTrace: %s",this.getClass().getName(),Arrays.toString(Thread.currentThread().getStackTrace()).replace(',','\n'));
			return null;
		}
	}
	

	public int numberOfInputSlots(){return inputSlots.length;}
	
	
	@Override
	protected ItemStack[] getInventory() {
		return inventory;
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

	public void setEnergy(float amount){
		setEnergy(amount,Power.ELECTRIC_POWER);
	}

	public ConduitType getType() {return Power.ELECTRIC_POWER;}

	public int getComparatorOutput(){
		return (int)(15 * this.getEnergy(Power.ELECTRIC_POWER) / this.getEnergyCapacity(Power.ELECTRIC_POWER));
	}

	
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

	@Override public int getInventoryStackLimit(){return 64;}
}
