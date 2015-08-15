package cyano.electricadvantage.items;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cyano.electricadvantage.common.IRechargeableItem;
import cyano.electricadvantage.init.Power;
import cyano.poweradvantage.api.ConduitType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;


public class BatteryItem extends net.minecraft.item.Item implements IRechargeableItem {
	private final float capacity;
	private final NumberFormat nf;
	
	public BatteryItem(float capacity){
		this.capacity = capacity;
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		super.setMaxStackSize(1);
	}
	
	@Override
	public float getEnergy(ItemStack stack) {
		if(stack.getItem() instanceof IRechargeableItem){
			BatteryItem battery = (BatteryItem)stack.getItem();
			NBTTagCompound dataTag;
			if(!stack.hasTagCompound()){
				return battery.capacity; // new battery starts with 100% charge
			} else {
				dataTag = stack.getTagCompound();
				if(dataTag.hasKey("energy")){
					return dataTag.getFloat("energy");
				} else {
					return battery.capacity; // new battery starts with 100% charge
				}
			}
		} else {
			return 0;
		}
	}
	@Override
	public void setEnergy(ItemStack stack, float energy){
		if(stack.getItem() instanceof BatteryItem){
			NBTTagCompound dataTag;
			if(!stack.hasTagCompound()){
				dataTag = new NBTTagCompound();
			} else {
				dataTag = stack.getTagCompound();
			}
			dataTag.setFloat("energy", energy);
			stack.setTagCompound(dataTag);
		}
	}
	/**
	 * Adds energy to a battery item
	 * @param stack battery item stack
	 * @param energy energy to add
	 * @return Returns the actual amount of energy added
	 */
	@Override
	public float addEnergy(ItemStack stack, float energy, ConduitType type){
		if(ConduitType.areSameType(type, Power.ELECTRIC_POWER) && stack.getItem() instanceof BatteryItem){
			BatteryItem battery = (BatteryItem)stack.getItem();
			NBTTagCompound dataTag;
			if(!stack.hasTagCompound()){
				dataTag = new NBTTagCompound();
			} else {
				dataTag = stack.getTagCompound();
			}
			float currentEnergy;
			if(dataTag.hasKey("energy")){
				currentEnergy = dataTag.getFloat("energy");
			} else {
				currentEnergy = battery.capacity;
			}
			float newEnergy = Math.max(0, Math.min(currentEnergy+energy, battery.capacity));
			dataTag.setFloat("energy", newEnergy);
			stack.setTagCompound(dataTag);
			return newEnergy - currentEnergy;
		} else {
			return 0;
		}
	}
	
	
	@Override
	public float getMaxEnergy(ItemStack item){
		return this.capacity;
	}
	
	@Override
	public ConduitType getEnergyType(){
		return Power.ELECTRIC_POWER;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b){
		super.addInformation(stack,player,list,b);
		StringBuilder sb = new StringBuilder();
		float max = getMaxEnergy(stack);
		if(max <= 0) return; // not a battery!
		float e = getEnergy(stack);
		//sb.append(nf.format(e)).append('/').append(nf.format(max)).append(" kJ");
		sb.append(nf.format(100*e/max)).append("% ").append(StatCollector.translateToLocal("tooltip.battery.charge"));
		list.add(sb.toString());
		StringBuilder sb2 = new StringBuilder();
		sb2.append('(').append(nf.format(max)).append("kJ ").append(StatCollector.translateToLocal("tooltip.battery.capacity")).append(')');
		list.add(sb2.toString());
	}
}
