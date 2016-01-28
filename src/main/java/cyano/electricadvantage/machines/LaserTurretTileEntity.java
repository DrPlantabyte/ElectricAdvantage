package cyano.electricadvantage.machines;

import java.util.ArrayList;
import java.util.List;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.init.Power;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LaserTurretTileEntity extends ElectricMachineTileEntity implements ITickable{

	private static final float RADIANS_TO_DEGREES = (float)(180 / Math.PI);
	private static final float DEGREES_TO_RADIANS = (float)(Math.PI / 180);
	public final static float IDLE_ROTATION_PER_TICK = 2f; // in degrees per tick
	public static final byte LASER_CHARGEUP_TIME = 31;
	private static final byte BLAST_TIME = 10;
	private static final byte HIT_TIME = 7;
	private final static int BURN_TIME = 3;
	public static float ATTACK_DAMAGE = 7f;
	
	public final static float ENERGY_PER_SHOT = 1000;
	public final static float ENERGY_PER_TICK = 1;
	public final static float MAX_BUFFER = ENERGY_PER_SHOT * 2;
	
	private String teamIdentity = null;
	private String playerOwner = null;

	public static final int BLAME_RANGE = 4;
	public static final int TARGET_RANGE = 16;
	public static final int ATTACK_RANGE = 32;
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
	/** entityID of target */
	private int targetID = Integer.MIN_VALUE;
	/** laser attack charge-up */
	private int laserAttack = 0;
	/** used by renderer to draw laser line */
	public double laserBlastLength = 0;
	
	private Vec3 opticPosition = null;
	
	/** rotation speed (non-idle), in degrees per tick */
	public final float speed = 9f;
	
	public LaserTurretTileEntity(){
		super(LaserTurretTileEntity.class.getSimpleName(),MAX_BUFFER,0,0,0);
	}

	@Override
	public boolean isActive(){
		return  (this.getEnergy() >= ENERGY_PER_SHOT) && (!this.hasRedstoneSignal());
	}
	
	@Override
	public void tickUpdate(boolean b) {
		World w = getWorld();
		if(laserAttack > 0){
			laserAttack--;
		}
		boolean isActive = isActive();
		if(w.isRemote){
			// client-side only
			if(isActive){
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
					Entity e = w.getEntityByID(targetID);
					if(e != null){
						lookAt(e);
					}
					if((rotTargetYaw - rotYaw) > 180){
						rotYaw += 360;
						rotOldYaw += 360;
					} else if((rotTargetYaw - rotYaw) < -180){
						rotYaw -= 360;
						rotOldYaw -= 360;
					}
					rotYaw = clampDelta(rotYaw,rotTargetYaw,speed);
					rotPitch = clampDelta(rotPitch,rotTargetPitch,speed);
					
					// Laser attack animation
					if(laserAttack > 0 && laserAttack <= BLAST_TIME){
						drawLaserLine();
					}
				}
			} else {
				storeOld();
				if(rotPitch > -30){
					rotPitch -= IDLE_ROTATION_PER_TICK;
				}
			}
			
		} else {
			// Server side only
			if(isActive){
				if(targetLocked == false){
					double x = this.getOpticPosition().xCoord;
					double y = this.getOpticPosition().yCoord;
					double z = this.getOpticPosition().zCoord;
					List<Entity> entities = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x-TARGET_RANGE, y-TARGET_RANGE, z-TARGET_RANGE, x+TARGET_RANGE, y+TARGET_RANGE, z+TARGET_RANGE));
					final double maxRangeSquared = TARGET_RANGE * TARGET_RANGE;
					for(Entity e : entities){
						if(e.getPositionVector().squareDistanceTo(getOpticPosition()) > maxRangeSquared) continue;
						if(e instanceof EntityPlayer){
							if(isEnemy((EntityPlayer)e)){
								setTarget(e);
								break;
							}
						}
						if(e.isCreatureType(EnumCreatureType.MONSTER, false)){
							if(canSeeEntity(e)){
								setTarget(e);
								break;
							}
						}
					}
				} 

				validateTarget();
				if(targetLocked ){
					if(laserAttack == HIT_TIME){
						// dish out the laser damage
						w.playSoundEffect(getOpticPosition().xCoord, getOpticPosition().yCoord, getOpticPosition().zCoord,
								ElectricAdvantage.INSTANCE.LASER_SOUND,3F,1.1F);
						List<Entity> victims = getLaserAttackVictims();
						Entity e = w.getEntityByID(targetID);
						for(Entity v : victims){
							hurtVictim(v);
						}
						this.subtractEnergy(ENERGY_PER_SHOT, getType());
						if(e.isDead){
							setTarget(null);
						}
						this.sync();
					} else if(laserAttack == 1){
						laserAttack = LASER_CHARGEUP_TIME;
						this.sync();
					}
				}
				this.subtractEnergy(ENERGY_PER_TICK, getType());
			} else {
				if(targetLocked){
					setTarget(null);
				}
			}

		}
	}
	
	private boolean isEnemy(EntityPlayer player) {
		if(player.capabilities.isCreativeMode) return false;
		if(this.teamIdentity == null || this.teamIdentity.isEmpty()){
			return false;
		} else if(player.getTeam() == null){
			return true;
		} else{
			return !teamIdentity.equals(player.getTeam().getRegisteredName());
		}
	}

	
	public boolean showLaserLine(){
		return targetLocked && laserAttack > 0 && laserAttack <= BLAST_TIME;
	}
	
	
	private List<Entity> getLaserAttackVictims(){
		final Vec3 origin = getOpticPosition();
		final Vec3 dir;
		final World w = getWorld();
		final Entity e = w.getEntityByID(targetID);
		if(e != null){
			dir = e.getPositionVector().addVector(0, 0.5*e.height, 0).subtract(origin).normalize();
		} else {
			dir = (new Vec3(Math.cos(DEGREES_TO_RADIANS * rotYaw),Math.sin(rotPitch),Math.sin(rotYaw))).normalize();
		}
		final Vec3 terminus = followRayToSolidBlock(origin,dir,ATTACK_RANGE);
		final double maxDistSqr = origin.squareDistanceTo(terminus);
		final double maxDist = MathHelper.sqrt_double(maxDistSqr);
		List<Entity> potentialVictims = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(
				origin.xCoord - maxDist, origin.yCoord - maxDist, origin.zCoord - maxDist,
				origin.xCoord + maxDist, origin.yCoord + maxDist, origin.zCoord + maxDist));
		List<Entity> victims = new ArrayList<>();
		for(Entity v : potentialVictims){
			if(origin.squareDistanceTo(v.getPositionVector()) < maxDistSqr
					&& rayIntersectsBoundingBox(origin,dir,v.getEntityBoundingBox())){
				victims.add(v);
			}
		}
		return victims;
	}
	
	

	private void hurtVictim(Entity e){
		e.setFire(BURN_TIME);
		if(e instanceof EntityLivingBase){
			((EntityLivingBase)e).attackEntityFrom(Power.laser_damage, ATTACK_DAMAGE);
			BlockPos bp = e.getPosition();
			if(getWorld().isAirBlock(bp) && !getWorld().isAirBlock(bp.down())){
				getWorld().setBlockState(bp, Blocks.fire.getDefaultState());
			}
			if(playerOwner != null && !playerOwner.isEmpty()){
				EntityPlayer p = getWorld().getPlayerEntityByName(playerOwner);
				if(p != null){
					((EntityLivingBase) e).setRevengeTarget(p);
				}
			}
		}
	}
	
	private Vec3 calculateLaserTerminus(){
		Vec3 dir;
		World w = getWorld();
		Entity e = w.getEntityByID(targetID);
		if(e != null){
			dir = e.getPositionVector().addVector(0, 0.5*e.height, 0).subtract(this.getOpticPosition()).normalize();
		} else {
			dir = (new Vec3(Math.cos(DEGREES_TO_RADIANS * rotYaw),Math.sin(rotPitch),Math.sin(rotYaw))).normalize();
		}
		return followRayToSolidBlock(getOpticPosition(),dir,ATTACK_RANGE);
	}
	private Vec3 followRayToSolidBlock(Vec3 origin, Vec3 dir, double maxRange){
		Vec3 max = origin.add(dir).addVector(maxRange * dir.xCoord, maxRange * dir.yCoord, maxRange * dir.zCoord);
		MovingObjectPosition impact = getWorld().rayTraceBlocks(origin, max, true, true, false);
		if(impact != null && impact.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
			final Vec3 impactSite;
			if(impact.hitVec != null){
				impactSite = impact.hitVec;
			} else {
				BlockPos bp = impact.getBlockPos();
				impactSite = new Vec3(bp.getX(), bp.getY(), bp.getZ());
			}
			return impactSite;
		} else {
			return max;
		}
	}
	
	public static boolean rayIntersectsBoundingBox(Vec3 rayOrigin, Vec3 rayDirection, AxisAlignedBB box){
		if(box == null) {
			return false;
		}
		// algorithm from http://gamedev.stackexchange.com/questions/18436/most-efficient-aabb-vs-ray-collision-algorithms
		Vec3 inverse = new Vec3(1.0 / rayDirection.xCoord, 1.0 / rayDirection.yCoord, 1.0 / rayDirection.zCoord);
		double t1 = (box.minX - rayOrigin.xCoord)*inverse.xCoord;
		double t2 = (box.maxX- rayOrigin.xCoord)*inverse.xCoord;
		double t3 = (box.minY - rayOrigin.yCoord)*inverse.yCoord;
		double t4 = (box.maxY - rayOrigin.yCoord)*inverse.yCoord;
		double t5 = (box.minZ - rayOrigin.zCoord)*inverse.zCoord;
		double t6 = (box.maxZ - rayOrigin.zCoord)*inverse.zCoord;

		double tmin = max(max(min(t1, t2), min(t3, t4)), min(t5, t6));
		double tmax = min(min(max(t1, t2), max(t3, t4)), max(t5, t6));

		// if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behind us
		if (tmax < 0)
		{
			return false;
		}

		// if tmin > tmax, ray doesn't intersect AABB
		if (tmin > tmax)
		{
			return false;
		}

		return true;
	}

	
	private static Vec3 mul(Vec3 a, double b){
		return new Vec3(a.xCoord * b, a.yCoord * b, a.zCoord * b);
	}
	private static double max(double a, double b){
		return Math.max(a, b);
	}
	private static double min(double a, double b){
		return Math.min(a, b);
	}
	
	
	private void drawLaserLine() {
		// client-side only
		World w = getWorld();
		if(w.isRemote){
			this.laserBlastLength = getOpticPosition().distanceTo(calculateLaserTerminus());
		}
	}

	private void validateTarget() {
		if(targetLocked){
			Entity e = getWorld().getEntityByID(targetID);
			if(e == null || e.isDead){
				setTarget(null);
				return;
			}
			
			if(getOpticPosition().distanceTo(e.getPositionVector()) > TARGET_RANGE){
				setTarget(null);
				return;
			}
			
			if(!canSeeEntity(e)){
				setTarget(null);
				return;
			}
		}
	}
	
	public boolean canSeeEntity(Entity e){
		if(e == null) return false;
		Vec3 offsetOrigin = getOpticPosition().add(e.getPositionVector().subtract(getOpticPosition()).normalize());
		MovingObjectPosition collision = getWorld().rayTraceBlocks(offsetOrigin, e.getPositionVector().addVector(0, e.height*0.5, 0), true, true, false);
		if(collision != null && collision.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
			return false;
		}
		return true;
	}

	public void setTarget(Entity e){
		if(e == null){
			this.targetID = Integer.MIN_VALUE;
			this.targetLocked = false;
			laserAttack = 0;
		} else {
			this.targetID = e.getEntityId();
			this.targetLocked = true;
			laserAttack = LASER_CHARGEUP_TIME;
			lookAt(e);
		}
		this.sync();
	}

	private void lookAt(Entity e) {
		Vec3 pos = e.getPositionVector().addVector(0, e.height*0.5, 0);
		targetPosition(pos);
	}
	
	private Vec3 getOpticPosition(){
		if(opticPosition == null)setOpticPosition();
		return opticPosition;
	}
	
	public void setOwner(EntityPlayer owner){
		this.setTeam(owner.getTeam());
		playerOwner = owner.getName();
	}
	
	public void setTeam(Team t){
		if(t == null) {
			teamIdentity = null;
		} else {
			teamIdentity = t.getRegisteredName();
		}
	}
	
	@Override 
	public void setPos(BlockPos p){
		super.setPos(p);
		setOpticPosition();
	}
	public final void setOpticPosition(){
		BlockPos p = getPos();
		opticPosition = new Vec3(p.getX()+0.5,p.getY()+0.75,p.getZ()+0.5);
	}
	
	private void targetPosition(Vec3 coord) {
		Vec3 pos = getOpticPosition();
		double x = pos.xCoord;
		double y = pos.yCoord;
		double z = pos.zCoord;
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
	
	
	
	

	/**
	 * Turns the data field NBT into a network packet
	 */
	@Override 
	public Packet getDescriptionPacket(){
		NBTTagCompound nbtTag = new NBTTagCompound();
		nbtTag.setFloat("energy", getEnergy());
		nbtTag.setBoolean("lock", targetLocked);
		if(targetLocked){
			if(targetID != Integer.MIN_VALUE && getWorld().getEntityByID(targetID) != null){
				Entity e = getWorld().getEntityByID(targetID);
				Vec3 pos = e.getPositionVector().addVector(0, e.height*0.5, 0);
				NBTTagCompound target = new NBTTagCompound();
				target.setInteger("id", this.targetID);
				target.setFloat("yaw", rotTargetYaw);
				target.setFloat("pitch", rotTargetPitch);
				target.setByte("attack", (byte)laserAttack);
				nbtTag.setTag("target", target);
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
				this.targetID = target.getInteger("id");
				Entity e = getWorld().getEntityByID(targetID);
				if(e != null){
					this.lookAt(e);
				} else {
					this.rotTargetYaw = target.getFloat("yaw");
					this.rotTargetPitch = target.getFloat("pitch");
				}
				this.laserAttack = target.getByte("attack");
			}
		}
		if(tag.hasKey("energy")){
			this.setEnergy(tag.getFloat("energy"),getType());
		}
	}

	@Override
	public float[] getProgress() {
		// not used
		return new float[0];
	}

	@Override
	protected void saveTo(NBTTagCompound root) {
		root.setFloat("pitch", rotPitch);
		root.setFloat("yaw", rotYaw);
		root.setBoolean("lock", targetLocked);
		if(this.teamIdentity == null){
			root.setString("team", "");
		} else {
			root.setString("team", teamIdentity);
		}
		if(this.playerOwner == null){
			root.setString("player", "");
		} else {
			root.setString("player", playerOwner);
		}
	}

	@Override
	protected void loadFrom(NBTTagCompound root) {
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
		if(root.hasKey("team")){
			String teamName = root.getString("team");
			if(teamName.isEmpty()){
				this.teamIdentity = null;
			}else{
				this.teamIdentity = teamName;
			}
		}

		if(root.hasKey("player")){
			playerOwner = root.getString("player");
		}
	}

	@Override
	protected boolean isValidInputItem(ItemStack item) {
		return false;
	}

	@Override
	public int[] getDataFieldArray() {
		// not used
		return new int[0];
	}

	@Override
	public void onDataFieldUpdate() {
		// not used
	}

	@Override
	public void prepareDataFieldsForSync() {
		// not used
	}

	@Override
	public boolean isPowered() {
		return getEnergy() > ENERGY_PER_TICK;
	}
	
	@Override
	public int getComparatorOutput(){
		if(this.targetLocked){
			return 15;
		} else {
			return (int)(8 * (this.getEnergy() / this.getEnergyCapacity()));
		}
	}
	
	// Helps with laser rendering
	final private int renderRange = ATTACK_RANGE * 2;
	@SideOnly(Side.CLIENT)
	public net.minecraft.util.AxisAlignedBB getRenderBoundingBox()
	{
		return new net.minecraft.util.AxisAlignedBB(getPos().add(-renderRange, -renderRange, -renderRange), getPos().add(renderRange, renderRange, renderRange));
	}
}
