package cyano.electricadvantage.common;

import cyano.poweradvantage.api.ConduitType;
import net.minecraft.item.ItemStack;

public interface IRechargeableItem {
	
	
	public float getEnergy(ItemStack stack);
	
	public void setEnergy(ItemStack stack, float energy);
	/**
	 * Adds energy to a battery item
	 * @param stack battery item stack
	 * @param energy energy to add
	 * @param energyType The type of energy being added
	 * @return Returns the actual amount of energy added
	 */
	public float addEnergy(ItemStack stack, float energy, ConduitType energyType);
	
	public float getMaxEnergy(ItemStack stack);
	
	public ConduitType getEnergyType();
}
