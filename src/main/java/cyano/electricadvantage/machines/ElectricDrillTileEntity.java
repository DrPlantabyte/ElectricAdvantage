package cyano.electricadvantage.machines;

import java.util.Arrays;
import java.util.List;

import cyano.electricadvantage.init.Blocks;
import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.util.InventoryWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ElectricDrillTileEntity extends ElectricMachineTileEntity{


	public static final int MAX_RANGE = 64;
	public static final float ENERGY_COST_MOVE = 250f;
	public static final float ENERGY_COST_PROGRESS_TICK = 32f;
	public static final float MINING_TIME_FACTOR = 12.0f;

	
	private final int[] dataSyncArray = new int[4];
	private int progress = 0;
	private int progressGoal = 0;
	private BlockPos targetBlockCoord = null;
	private Block targetBlock = null;
	private List<ItemStack> targetBlockItems = null;
	private int laserLength = 0;

	
	private boolean deferred = false;
	
	public ElectricDrillTileEntity() {
		super(ElectricDrillTileEntity.class.getSimpleName(), 
				0, 6, 0);
	}

	@Override
	public boolean isPowered() {
		return getEnergy() > ENERGY_COST_PROGRESS_TICK;
	}

	private float[] progArr = new float[1];
	@Override
	public float[] getProgress() {
		if(progressGoal > 0){
			progArr[0] = (float)progress / (float)progressGoal;
		} else {
			progArr[0] = 0;
		}
		return progArr;
	}
	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			if(deferred){
				targetBlock(targetBlockCoord);
			}
			updateFacing();
			
			// disabled by redstone
			if(hasRedstoneSignal()){
				if (progress > 0){
					progress = 0;
				}
			} else {
				if(targetBlockCoord != null){
					// mining time
					if(getEnergy() > ENERGY_COST_PROGRESS_TICK && hasSpaceForItems(targetBlockItems) && canMine(targetBlockCoord)){
						this.subtractEnergy(ENERGY_COST_PROGRESS_TICK, Power.ELECTRIC_POWER);
						progress++;
						if(progress >= progressGoal){
							// Mined it!
							getWorld().playSoundEffect(targetBlockCoord.getX()+0.5, targetBlockCoord.getY()+0.5, targetBlockCoord.getZ()+0.5, targetBlock.stepSound.getBreakSound(), 0.5f, 1f);
							getWorld().playSoundEffect(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5, "gravel.dig", 0.5f, 1f);
							getWorld().setBlockToAir(targetBlockCoord);
							for(ItemStack item : targetBlockItems){
								addItem(item);
							}
							untargetBlock();
						}
					}
				}
				
			}
		}
		
	}
	
	private boolean hasSpaceForItems(List<ItemStack> items) {
		if(items.size() == 1){
			return super.hasSpaceForItemInOutputSlots(items.get(0));
		} else {
			for(ItemStack i : items){
				if(super.hasSpaceForItemInOutputSlots(i) == false){
					return false;
				}
			}
			return true;
		}
	}

	public ItemStack addItem(ItemStack in){
		return super.insertItemToOutputSlots(in);
	}

	private EnumFacing trackDirection(){
		for(EnumFacing dir : EnumFacing.values()){
			if(getWorld().getBlockState(getPos().offset(dir)).getBlock() == Blocks.electric_track){
				return dir;
			}
		}
		return null;
	}
	
	private boolean followTrack(){
		if(this.getEnergy() < ENERGY_COST_MOVE) return false;
		EnumFacing trackDir = trackDirection();
		if(trackDir == null) return false;
		
		// clone this block into neighboring block
		this.untargetBlock();
		World w = getWorld();
		BlockPos nextPos = getPos().offset(trackDir);
		w.setBlockState(nextPos, w.getBlockState(getPos()), 2);
		ElectricDrillTileEntity te = (ElectricDrillTileEntity)w.getTileEntity(nextPos);
		NBTTagCompound dataTransfer = new NBTTagCompound();
		this.writeToNBT(dataTransfer);
		te.readFromNBT(dataTransfer);
		te.setPos(nextPos);
		te.validate();
		te.markDirty();
		Arrays.fill(this.getInventory(), null);
		
		// replace this block with steam pipe
		w.setBlockState(getPos().offset(getFacing()), cyano.poweradvantage.init.Blocks.steel_frame.getDefaultState());
		w.setBlockState(getPos(), Blocks.electric_conduit.getDefaultState(), 2);
		
		
		this.subtractEnergy(ENERGY_COST_MOVE, getType());
		return true;
	}
	

	@Override
	public int[] getDataFieldArray() {
		return dataSyncArray;
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataSyncArray[0] = Float.floatToRawIntBits(this.getEnergy());
		dataSyncArray[1] = progress;
		dataSyncArray[2] = progressGoal;
		dataSyncArray[3] = laserLength;
	}

	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataSyncArray[0]), this.getType());
		this.progress = dataSyncArray[1];
		progressGoal = dataSyncArray[2];
		laserLength = dataSyncArray[3];
	}
	private float oldEnergy = 0;
	private int oldProgress = 0;
	private int oldLength = 0;
	@Override public void powerUpdate(){
		if(deferred){
			targetBlock(targetBlockCoord);
		}
		super.powerUpdate();
		boolean flagSync = progress != oldProgress || oldEnergy != getEnergy();
		oldProgress = progress;
		oldEnergy = getEnergy();
		
		boolean redstone = hasRedstoneSignal();
		
		
		EnumFacing f = getFacing();
		BlockPos n = getPos().offset(f);
		
		// manage drill bits and find next block
		if(redstone || this.getEnergy() <= 0){
			// no power
			flagSync = true;
			this.untargetBlock();
		} else {
			// drill baby drill!
			if(targetBlockCoord == null ){
				// find new block
				boolean hitEnd = false;
				for(int i = 0; i <= MAX_RANGE ; i++){
					if(i == MAX_RANGE){
						hitEnd = true;
						break;
					}
				
					if(getWorld().isAirBlock(n) || getWorld().getBlockState(n).getBlock().isReplaceable(getWorld(), n)){
						// this block is not worth mining, replace it
						n = n.offset(f);
						if(n.getY() == 0 || n.getY() == 255){
							hitEnd = true;
							break;
						}
					} else {
						// found a block!
						if(canMine(n)){
							targetBlock(n);
						} else {
							hitEnd = true;
						}
						flagSync = true;
						break;
					}
				}
				// if hit end of range, move along track
				if(hitEnd){
					boolean moved = followTrack();
					if(moved){
						cyano.poweradvantage.conduitnetwork.ConduitRegistry.getInstance()
								.conduitBlockRemovedEvent(getWorld(), getWorld().provider.getDimensionId(), getPos(), getType());
						return;
					}
				}
			} else {
				// currently drilling a block
				// block validation
				if(getWorld().isAirBlock(targetBlockCoord) || getWorld().getBlockState(targetBlockCoord).getBlock() != targetBlock){
					// Block changed! invalidate!
					untargetBlock();
					flagSync = true;
				}
			}
		}
		
		if(oldLength != laserLength){
			oldLength = laserLength;
			flagSync = true;
		}
		
		if(flagSync){
			this.sync();
		}
		

		// push inventory to adjacent chest
		BlockPos adj = getPos().offset(f.getOpposite());
		if(!redstone && !getWorld().isAirBlock(adj)){
			inventoryTransfer(adj,f);
		}
		
		// fry any idiots who stand in the laser's path
		if(targetBlock != null && targetBlockCoord != null){
			BlockPos pos = getPos();
			AxisAlignedBB beamArea = new AxisAlignedBB(targetBlockCoord.getX(),targetBlockCoord.getY(),targetBlockCoord.getZ(),
					pos.getX()+1,pos.getY()+1,pos.getZ()+1);
			List<Entity> victims = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, beamArea);
			for(Entity e : victims){
				e.setFire(3);
				e.attackEntityFrom(Power.laser_damage, 2f);
			}
		}
	}

	
	public int getDrillLength(){
		return laserLength;
	}
	
	public void calculateDrillLength(){
		if(this.isPowered() && this.targetBlock != null && this.targetBlockCoord != null){
			BlockPos pos = getPos();
			// distance calculation is taking a short-cut by assuming that 2 out of the 3 XYZ coordinates are identical
			laserLength = MathHelper.abs_int((pos.getX() - targetBlockCoord.getX()) + (pos.getY() - targetBlockCoord.getY()) + (pos.getZ() - targetBlockCoord.getZ()));
		} else {
			laserLength = 0;
		}
	}

	private void targetBlock (BlockPos n){
		progress = 0;
		targetBlockCoord = n;
		progressGoal = this.getBlockStrength(n);
		targetBlock = getWorld().getBlockState(n).getBlock();
		targetBlockItems = targetBlock.getDrops(getWorld(), n, getWorld().getBlockState(n), 0);
		deferred = false;
	}
	
	private void deferredTargetBlock (BlockPos n){
		targetBlockCoord = n;
		deferred = true;
	}
	
	private void untargetBlock(){
		progress = 0;
		progressGoal = 0;
		targetBlockCoord = null;
		targetBlock = null;
		targetBlockItems = null;
	}
	
	private EnumFacing facingCache = null;
	public EnumFacing getFacing(){
		if(facingCache == null) updateFacing();
		return facingCache;
	}
	private void updateFacing(){
		facingCache = (EnumFacing)worldObj.getBlockState(getPos()).getValue(ElectricDrillBlock.FACING);
	}
	
	
	private boolean canMine(BlockPos coord){
		Block b = getWorld().getBlockState(coord).getBlock();
		return !(b == net.minecraft.init.Blocks.bedrock || b == net.minecraft.init.Blocks.barrier);
	}
	
	private int getBlockStrength(BlockPos coord){
		if(getWorld().isAirBlock(coord)){
			return 0;
		}
		Block block = getWorld().getBlockState(coord).getBlock();
		return (int)(Math.max(MINING_TIME_FACTOR * block.getBlockHardness(getWorld(), coord),0.5f * MINING_TIME_FACTOR));
	}
	
	

	private void inventoryTransfer(BlockPos adj, EnumFacing otherFace) {
		ItemStack[] inventory = this.getInventory();
		TileEntity te = getWorld().getTileEntity(adj);
		if(te instanceof IInventory ){
			ISidedInventory inv = InventoryWrapper.wrap((IInventory)te);
			int[] accessibleSlots = inv.getSlotsForFace(otherFace);
			if(accessibleSlots.length == 0) return;
			for(int mySlot = 0; mySlot < inventory.length; mySlot++){
				if(inventory[mySlot] == null) continue;
				for(int i = 0; i < accessibleSlots.length; i++){
					int theirSlot = accessibleSlots[i];
					ItemStack theirItem = inv.getStackInSlot(theirSlot);
					if(inv.canInsertItem(theirSlot, inventory[mySlot], otherFace)){
						if(theirItem == null){
							ItemStack newItem = inventory[mySlot].copy();
							newItem.stackSize = 1;
							inv.setInventorySlotContents(theirSlot, newItem);
							inventory[mySlot].stackSize--;
							if(inventory[mySlot].stackSize <= 0) inventory[mySlot] = null;
							return;
						} else if(ItemStack.areItemsEqual(theirItem, inventory[mySlot]) 
								&& ItemStack.areItemStackTagsEqual(theirItem, inventory[mySlot])
								&& theirItem.stackSize < theirItem.getMaxStackSize()
								&& theirItem.stackSize < inv.getInventoryStackLimit()){
							theirItem.stackSize++;
							inventory[mySlot].stackSize--;
							if(inventory[mySlot].stackSize <= 0) inventory[mySlot] = null;
							return;
						}
					}
				}
			}
			
		}
	}


	
	public float getProgressLevel(){
		if(progressGoal > 0) 
			return (float)progress / (float)progressGoal;
		return 0;
	}
	
	
	
	
	@Override
	protected void saveTo(NBTTagCompound tagRoot) {
		tagRoot.setShort("progress",(short)progress);
		if(targetBlockCoord != null){
			tagRoot.setInteger("targetX", targetBlockCoord.getX());
			tagRoot.setInteger("targetY", targetBlockCoord.getY());
			tagRoot.setInteger("targetZ", targetBlockCoord.getZ());
		}
	}

	@Override
	protected void loadFrom(NBTTagCompound tagRoot) {
		if(tagRoot.hasKey("progress")){
			progress = tagRoot.getShort("progress");
		}
		if(tagRoot.hasKey("targetX") && tagRoot.hasKey("targetY") && tagRoot.hasKey("targetZ")){
			int x = tagRoot.getInteger("targetX");
			int y = tagRoot.getInteger("targetY");
			int z = tagRoot.getInteger("targetZ");
			// Note: world object for tile entities is set AFTER loading them from NBT
			if(getWorld() == null){
				this.deferredTargetBlock(new BlockPos(x,y,z));
			} else {
				this.targetBlock(new BlockPos(x,y,z));
			}
		}
	}

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return true;
	}
	
	@Override
	protected void setActiveState(boolean active){
		IBlockState oldState = getWorld().getBlockState(getPos());
		if(oldState.getBlock() instanceof ElectricDrillBlock 
				&& (Boolean)oldState.getValue(ElectricDrillBlock.ACTIVE) != active ){
			final TileEntity save = this;
			final World w = getWorld();
			final BlockPos pos = this.getPos();
			IBlockState newState = oldState.withProperty(ElectricDrillBlock.ACTIVE, active);
			w.setBlockState(pos, newState,3);
			if(save != null){
				w.removeTileEntity(pos);
				save.validate();
				w.setTileEntity(pos, save);
			}
		}
	}
	@Override
	protected void setPowerState(boolean powered){
		IBlockState oldState = getWorld().getBlockState(getPos());
		if(oldState.getBlock() instanceof ElectricDrillBlock 
				&& (Boolean)oldState.getValue(ElectricDrillBlock.POWERED) != powered ){
			final TileEntity save = this;
			final World w = getWorld();
			final BlockPos pos = this.getPos();
			IBlockState newState = oldState.withProperty(ElectricDrillBlock.POWERED, powered);
			w.setBlockState(pos, newState,3);
			if(save != null){
				w.removeTileEntity(pos);
				save.validate();
				w.setTileEntity(pos, save);
			}
		}
	}

	@Override
	public boolean isActive(){
		return isPowered() && (!this.hasRedstoneSignal() && (this.getEnergy() > 0 || (Boolean)getWorld().getBlockState(getPos()).getValue(ElectricDrillBlock.ACTIVE)));
	}
}
