package cyano.electricadvantage.entities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class HydroTurbineEntity extends net.minecraft.entity.Entity{

	public static final double DEGREES_PER_TICK = 18;
	public double rotation = 0;
	public double orientationAngle = 0;
	public boolean isSpinning = false;
	public TileEntity parent = null;
	
	public HydroTurbineEntity(World w) {
		super(w);
		this.width = 0.9375F;
		this.height = 0.9375F;
	}
	public HydroTurbineEntity(World w, BlockPos parentTileEntity) {
		this(w);
		this.parent = w.getTileEntity(parentTileEntity);
	}

	public HydroTurbineEntity(World w, TileEntity parentTileEntity) {
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
		if(this.worldObj.isRemote){
			// client-side only
			IBlockState bs = this.worldObj.getBlockState(getPosition());
			isSpinning = bs.getBlock() == net.minecraft.init.Blocks.flowing_water;
			if(isSpinning){
				rotation += DEGREES_PER_TICK;
				
			}
		} else {
			// server-side only
			if(parent == null || parent.isInvalid()){
				this.kill();
			}
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
		// TODO Auto-generated method stub
		
	}

}
