package cyano.electricadvantage.items;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;


public class BatteryItem extends net.minecraft.item.Item{
	private final float capacity;
	private final NumberFormat nf;
	
	public BatteryItem(float capacity){
		this.capacity = capacity;
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
	}
	
	public static float getEnergyCapacity(ItemStack item){
		Item i = item.getItem();
		if(i instanceof BatteryItem){
			return ((BatteryItem)i).capacity;
		} else {
			return 0;
		}
	}
	
	public static float getEnergyStored(ItemStack item){
		Item i = item.getItem();
		if(i instanceof BatteryItem){
			return ((BatteryItem)i).getEnergy(item);
		} else {
			return 0;
		}
	}
	
	public float getEnergy(ItemStack stack) {
		if(stack.getItem() instanceof BatteryItem){
			NBTTagCompound dataTag;
			if(!stack.hasTagCompound()){
				return 0;
			} else {
				dataTag = stack.getTagCompound();
				if(dataTag.hasKey("energy")){
					return dataTag.getFloat("energy");
				} else {
					return 0;
				}
			}
		} else {
			return 0;
		}
	}
	
	public void setEnergy(ItemStack stack, float energy){
		if(stack.getItem() instanceof BatteryItem){
			NBTTagCompound dataTag;
			if(!stack.hasTagCompound()){
				dataTag = new NBTTagCompound();
			} else {
				dataTag = stack.getTagCompound();
			}
			dataTag.setFloat("energy", energy);
		}
	}
	/**
	 * Adds energy to a battery item
	 * @param stack battery item stack
	 * @param energy energy to add
	 * @return Returns the actual amount of energy added
	 */
	public float addEnergy(ItemStack stack, float energy){
		if(stack.getItem() instanceof BatteryItem){
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
				currentEnergy = 0;
			}
			float newEnergy = Math.min(currentEnergy+energy, battery.capacity);
			dataTag.setFloat("energy", newEnergy);
			stack.setTagCompound(dataTag);
			return newEnergy - currentEnergy;
		} else {
			return 0;
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b){
		super.addInformation(stack,player,list,b);
		StringBuilder sb = new StringBuilder();
		float max = getEnergyCapacity(stack);
		if(max <= 0) return; // not a battery!
		float e = getEnergyStored(stack);
		//sb.append(nf.format(e)).append('/').append(nf.format(max)).append(" kJ");
		sb.append(nf.format(100*e/max)).append("% ").append(StatCollector.translateToLocal("tooltip.battery.charge"));
		list.add(sb.toString());
		StringBuilder sb2 = new StringBuilder();
		sb2.append('(').append(nf.format(max)).append("kJ ").append(StatCollector.translateToLocal("tooltip.battery.capacity")).append(')');
		list.add(sb2.toString());
	}
}
