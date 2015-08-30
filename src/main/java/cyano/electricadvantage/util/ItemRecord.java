package cyano.electricadvantage.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemRecord{
	final int hashCode;
	final ItemStack item;
	
	public ItemRecord(ItemStack itemStack){
		this.item = itemStack.copy();
		if(item.getItemDamage() == OreDictionary.WILDCARD_VALUE
				|| item.getItem().isDamageable()
				|| item.getItemDamage() == 0){
			hashCode = item.getUnlocalizedName().hashCode();
		} else {
			hashCode = item.getUnlocalizedName().hashCode() * 57 * item.getItemDamage();
		}
	}
	
	public ItemStack getItem(){return item;}
	
	@Override public int hashCode(){ return hashCode;}
	
	@Override 
	public boolean equals(Object other){
		if(other == this) return true;
		if(other == null) return false;
		if(other instanceof ItemRecord && this.hashCode() == other.hashCode()){
			if(this.item.getItemDamage() == OreDictionary.WILDCARD_VALUE || this.item.getItem().isDamageable()){
				return this.item.getItem().equals(((ItemRecord)other).item.getItem());
			} else {
				return ItemStack.areItemsEqual(item, ((ItemRecord)other).item);
			}
		}
		return false;
	}
}
