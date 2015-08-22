package cyano.electricadvantage.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LaserTurretTileEntity extends TileEntity implements IUpdatePlayerListBox{

	private static final float RADIANS_TO_DEGREES = (float)(180 / Math.PI);
	private static final float DEGREES_TO_RADIANS = (float)(Math.PI / 180);
	public final static float IDLE_ROTATION_PER_TICK = 2f; // in degrees per tick
	/** target rotation around Y axis */
	public float rotTargetYaw=0;
	/** target rotation up and down */
	public float rotTargetPitch=0;
	/** rotation around Y axis */
	public float rotYaw=0;
	/** rotation up and down */
	public float rotPitch=0;
	/** previous rotation around Y axis */
	public float rotOldYaw=0;
	/** previous rotation up and down */
	public float rotOldPitch=0;
	/** true when tracking a target, alse otherwise */
	public boolean targetLocked = false;
	/** entityID of target (used by server) */
	private int targetID = Integer.MIN_VALUE;
	
	/** rotation speed (non-idle), in degrees per tick */
	public final float speed = 9f;
	
	public LaserTurretTileEntity(){
		super();
	}

	@Override
	public void update() {
		if(getWorld().isRemote){
			// client-side only
			World w = getWorld();
			if(targetLocked == false){
				storeOld();
				rotPitch = 0;
				rotYaw += IDLE_ROTATION_PER_TICK;
				if(rotYaw > 180 ){
					rotYaw -= 360;
					rotOldYaw -= 360;
				}
			} else {
				storeOld();
				if((rotTargetYaw - rotYaw) > 180){
					rotYaw += 360;
					rotOldYaw += 360;
				} else if((rotTargetYaw - rotYaw) < -180){
					rotYaw -= 360;
					rotOldYaw -= 360;
				}
				rotYaw = clampDelta(rotYaw,rotTargetYaw,speed);
				rotPitch = clampDelta(rotPitch,rotTargetPitch,speed);
			}
			
			//TODO: remove testing code
			EntityPlayer p = w.getClosestPlayer(getPos().getX(), getPos().getY(), getPos().getZ(), 8);
			if(p != null){
				targetLocked = true;
				Vec3 coord = p.getPositionVector().addVector(0, p.height*0.5, 0);
				targetPosition(coord);
			} else {
				targetLocked = false;
			}
			// end of testing code
		}
	}
	
	@SideOnly(Side.SERVER)
	public void targetEntity(Entity e){
		this.targetID = e.getEntityId();
		this.targetLocked = true;
		this.markDirty();
	}
	
	private void targetPosition(Vec3 coord) {
		BlockPos pos = getPos();
		double x = pos.getX()+0.5;
		double y = pos.getY()+0.5;
		double z = pos.getZ()+0.5;
		double dist = distance(x,y,z,coord.xCoord, coord.yCoord, coord.zCoord);
		this.rotTargetYaw = RADIANS_TO_DEGREES * atan2(coord.zCoord - z, coord.xCoord - x);
		this.rotTargetPitch = RADIANS_TO_DEGREES * asin((coord.yCoord - y)/dist);
	}
	
	private double distance(double x1,double y1,double z1,double x2,double y2,double z2){
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		return MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
	}

	private float atan2(double dy, double dx){
		return (float)Math.atan2(dy, dx);
	}
	
	private float asin(double d){
		return (float)Math.asin(d);
	}

	private static float clampDelta(float old, float goal, float maxDelta){
		float delta = goal - old;
		if(Math.abs(delta) > delta){
			if(delta < 0){
				delta = -1*maxDelta;
			} else {
				delta = maxDelta;
			}
			return old + delta;
		}else{
			return goal;
		}
	}
	
	private void storeOld(){
		rotOldYaw = rotYaw;
		rotOldPitch = rotPitch;
	}
	
	
	
	@Override
	public void writeToNBT(NBTTagCompound root){
		super.writeToNBT(root);
		root.setFloat("pitch", rotPitch);
		root.setFloat("yaw", rotYaw);
		root.setBoolean("lock", targetLocked);
	}
	

	@Override
	public void readFromNBT(NBTTagCompound root){
		super.readFromNBT(root);
		if(root.hasKey("pitch")){
			this.rotPitch = root.getFloat("pitch");
			this.rotOldPitch = rotPitch;
			this.rotTargetPitch = rotPitch;
		}
		if(root.hasKey("yaw")){
			this.rotYaw = root.getFloat("yaw");
			this.rotOldYaw = rotYaw;
			this.rotTargetYaw = rotYaw;
		}
		if(root.hasKey("lock")){
			targetLocked = root.getBoolean("lock");
		}
	}

	/**
	 * Turns the data field NBT into a network packet
	 */
	@Override 
	public Packet getDescriptionPacket(){
		NBTTagCompound nbtTag = new NBTTagCompound();
		nbtTag.setBoolean("lock", targetLocked);
		if(targetLocked){
			if(targetID != Integer.MIN_VALUE && getWorld().getEntityByID(targetID) != null){
				Entity e = getWorld().getEntityByID(targetID);
				Vec3 pos = e.getPositionVector().addVector(0, e.height*0.5, 0);
				NBTTagCompound target = new NBTTagCompound();
				target.setDouble("x", pos.xCoord);
				target.setDouble("y", pos.yCoord);
				target.setDouble("z", pos.zCoord);
				nbtTag.setTag("target", target);
			} else {
				nbtTag.setFloat("targetPitch", rotTargetPitch);
				nbtTag.setFloat("targetYaw", rotTargetYaw);
			}
		}
		return new S35PacketUpdateTileEntity(this.pos, 0, nbtTag);
	}
	/**
	 * Receives the network packet made by <code>getDescriptionPacket()</code>
	 */
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound tag = packet.getNbtCompound();
		if(tag.hasKey("lock")){
			this.targetLocked = tag.getBoolean("lock");
			if(tag.hasKey("target")){
				NBTTagCompound target = tag.getCompoundTag("target");
				double x = target.getDouble("x");
				double y = target.getDouble("y");
				double z = target.getDouble("z");
				this.targetPosition(new Vec3(x,y,z));
			} else {
				if(tag.hasKey("targetPitch")){
					this.rotTargetPitch = tag.getFloat("targetPitch");
				}
				if(tag.hasKey("targetYaw")){
					this.rotTargetYaw = tag.getFloat("targetYaw");
				}
			}
		}
		
	}

}
