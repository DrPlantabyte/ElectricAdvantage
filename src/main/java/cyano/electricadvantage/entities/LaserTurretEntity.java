package cyano.electricadvantage.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class LaserTurretEntity extends net.minecraft.entity.EntityLiving{
// LASER attacks based on the guardian
	

	private EntityLivingBase attackTarget;

	public LaserTurretEntity(World world) {
		super(world);
		EntityGuardian g;
	}

	/**
	 * Gets the active target the Task system uses for tracking
	 */
	public EntityLivingBase getAttackTarget()
	{
		return this.attackTarget;
	}

	/**
	 * Sets the active target the Task system uses for tracking
	 */
	public void setAttackTarget(EntityLivingBase target)
	{
		this.attackTarget = target;
		net.minecraftforge.common.ForgeHooks.onLivingSetAttackTarget(this, target);
	}




}
