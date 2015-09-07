package cyano.electricadvantage.entities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class HydroturbineEntity extends net.minecraft.entity.Entity{

	public static final double DEGREES_PER_TICK = 18;
	private static final float RADIANS_TO_DEGREES = (float)(180 / Math.PI);
	private static final float DEGREES_TO_RADIANS = (float)(Math.PI / 180);
	public double rotation = 0;
	public boolean isSpinning = false;
	public TileEntity parent = null;
	
	public HydroturbineEntity(World w) {
		super(w);
		this.width = 0.9375F;
		this.height = 0.9375F;
	}
	public HydroturbineEntity(World w, BlockPos parentTileEntity) {
		this(w);
		this.parent = w.getTileEntity(parentTileEntity);
	}

	public HydroturbineEntity(World w, TileEntity parentTileEntity) {
		this(w);
		this.parent = parentTileEntity;
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		IBlockState bs = this.worldObj.getBlockState(getPosition());
		Block b = bs.getBlock();
		Vec3 directionVector;
		if(b == net.minecraft.init.Blocks.flowing_water){
			directionVector = net.minecraft.init.Blocks.flowing_water.modifyAcceleration(worldObj, getPosition(), null, new Vec3(0,0,0));
			isSpinning = (directionVector.xCoord != 0 || directionVector.zCoord != 0);
		} else {
			isSpinning = false;
			directionVector = new Vec3(0,0,0);
		}
		if(this.worldObj.isRemote){
			// client-side only
			if(isSpinning){
				rotation += DEGREES_PER_TICK;
				this.rotationYaw = RADIANS_TO_DEGREES * (float)Math.atan2(-directionVector.zCoord, directionVector.xCoord);
			} else {
				// do nothing
			}
		} else {
			// server-side only
			// TODO: uncomment
			/*
			if(parent == null || parent.isInvalid()){
				this.kill();
			}
			*/
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
		// Do nothing
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
		// Do nothing
	}

}
