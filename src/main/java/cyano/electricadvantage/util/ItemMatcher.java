package cyano.electricadvantage.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemMatcher{
	final ItemStack item;
	final List<ItemMatcher> array;
	final Block block;
	final String name;

	public ItemMatcher(String oreDictRef){
		name = oreDictRef;
		item = null;
		block = null;
		array = null;
	}
	public ItemMatcher(Item i){
		name = null;
		item = new ItemStack(i,1,OreDictionary.WILDCARD_VALUE);
		block = null;
		array = null;
	}
	public ItemMatcher(Block i){
		name = null;
		item = null;
		block = i;
		array = null;
	}
	public ItemMatcher(ItemStack i){
		name = null;
		item = i.copy();
		block = null;
		if(i.getItem().isDamageable()){
			i.setItemDamage(OreDictionary.WILDCARD_VALUE);
		}
		array = null;
	}
	public ItemMatcher(List expandedList) {
		name = null;
		item = null;
		block = null;
		array = new ArrayList<>(expandedList.size());
		for(Object o : expandedList){
			if(o instanceof ItemStack){
				array.add(new ItemMatcher((ItemStack)o));
			} else {
				throw new ClassCastException(this.getClass().getName()+" does not support item of class type "+o.getClass());
			}
		}
	}
	

	public boolean matches(ItemRecord i){
		return matches(i.getItem());
	}
	public boolean matches(ItemStack i){
		if(name != null){
			for(ItemStack n : OreDictionary.getOres(name)){
				if(OreDictionary.itemMatches(i, n, false)){
					return true;
				}
			}
			return false;
		} else if(block != null){
			return OreDictionary.itemMatches(new ItemStack(block,1,OreDictionary.WILDCARD_VALUE), i, false);
		} else if(array != null){
			for(ItemMatcher m : array){
				if(m.matches(i)){
					return true;
				}
			}
			return false;
		} else {
			return OreDictionary.itemMatches(item, i, false);
		}
	}
	
	public Collection<ItemStack> getValidItems() {
		if(name != null){
			return OreDictionary.getOres(name);
		} else if(block != null){
			ItemStack[] items = new ItemStack[1];
			items [0] = new ItemStack(block,1,OreDictionary.WILDCARD_VALUE);
			return Arrays.asList(items);
		} else if(array != null){
			List<ItemStack> items = new ArrayList<>(array.size());
			for(ItemMatcher m : array){
				items.addAll(m.getValidItems());
			}
			return items;
		} else {
			ItemStack[] items = new ItemStack[1];
			items [0] = item;
			return Arrays.asList(items);
		}
	}
	
	@Override
	public String toString(){
		if(name != null){
			return "\""+name+"\"";
		} else if(block != null){
			return block.toString();
		} else if(array != null){
			return Arrays.toString(array.toArray());
		} else {
			return item.toString();
		}
	}
}