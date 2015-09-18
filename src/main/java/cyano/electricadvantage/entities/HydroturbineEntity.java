package cyano.electricadvantage.entities;

import java.util.List;

import cyano.electricadvantage.init.Power;
import cyano.electricadvantage.machines.HydroelectricGeneratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class HydroturbineEntity extends net.minecraft.entity.Entity{

	public static final float DEGREES_PER_TICK = 30;
	private static final float RADIANS_TO_DEGREES = (float)(180 / Math.PI);
	private static final float DEGREES_TO_RADIANS = (float)(Math.PI / 180);
	public static final float PROPELLER_DAMAGE = 5f;
	public float rotation = 0;
	public boolean isSpinning = false;
	public TileEntity parent = null;
	private BlockPos parentCoord = null;
	private final long roffset;
	
	public HydroturbineEntity(World w) {
		super(w);
		this.width = 0.9375F;
		this.height = 0.9375F;
		this.setSize(this.width, this.height);
		this.preventEntitySpawning = true;
		roffset = w.rand.nextInt(8);
	}
	public HydroturbineEntity(World w, BlockPos parentTileEntity) {
		this(w,w.getTileEntity(parentTileEntity));
	}

	public HydroturbineEntity(World w, TileEntity parentTileEntity) {
		this(w);
		this.parent = parentTileEntity;
		BlockPos p = parentTileEntity.getPos();
		this.setPosition(p.getX()+0.5, p.getY()-1, p.getZ()+0.5);
	}

	@Override
	protected void entityInit() {
		// do nothing
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		IBlockState bs = this.worldObj.getBlockState(getPosition());
		Block b = bs.getBlock();
		Vec3 directionVector;
		if(b instanceof BlockLiquid){
			directionVector = ((BlockLiquid)b).modifyAcceleration(getEntityWorld(), getPosition(), null, new Vec3(0,0,0));
			isSpinning = !(directionVector.xCoord == 0 && directionVector.zCoord == 0);
		} else {
			isSpinning = false;
			directionVector = new Vec3(0,0,0);
		}
		if(this.getEntityWorld().isRemote){
			// client-side only
			if(isSpinning){
				rotation += DEGREES_PER_TICK;
				this.rotationYaw = RADIANS_TO_DEGREES * (float)Math.atan2(-directionVector.zCoord, directionVector.xCoord) -90;
			} else {
				// do nothing
			}
		} else {
			// server-side only
			if(parent == null && parentCoord != null){
				parent = this.getEntityWorld().getTileEntity(parentCoord);
				parentCoord = null;
			}
			if(parent == null || parent.isInvalid()){
				this.kill();
			} else if(parent instanceof HydroelectricGeneratorTileEntity){
				HydroelectricGeneratorTileEntity generator = (HydroelectricGeneratorTileEntity)parent;
				if(isSpinning){
					generator.addEnergy(HydroelectricGeneratorTileEntity.ENERGY_PER_TICK, Power.ELECTRIC_POWER);
				}
				generator.setActive(isSpinning);
			}
			if(isSpinning && getEntityWorld().getTotalWorldTime() % 8 == roffset){
				// slice-n-dice unfortunate souls caught in the blades
				List<Entity> victims = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox());
				for(Entity e : victims){
					e.attackEntityFrom(Power.propeller_damage, PROPELLER_DAMAGE);
				}
			}
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound root) {
		if(root.hasKey("parentPos")){
			int[] coords = root.getIntArray("parentPos");
			if(coords.length >= 3){
				parentCoord = new BlockPos(coords[0],coords[1],coords[2]);
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound root) {
		if(parent != null){
			int[] coords = new int[3];
			BlockPos p = parent.getPos();
			coords[0] = p.getX();
			coords[1] = p.getY();
			coords[2] = p.getZ();
			root.setIntArray("parentPos", coords);
		}
	}
	
}
