package cyano.electricadvantage.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class LaserTurretEntity extends net.minecraft.entity.EntityLivingBase{
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

	@Override
	public ItemStack getHeldItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getEquipmentInSlot(int slotIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getCurrentArmor(int slotIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack[] getInventory() {
		// TODO Auto-generated method stub
		return null;
	}




}
