package cyano.electricadvantage.util.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class SerializedInventory extends HashMap<ItemRecord,Integer>{

	public SerializedInventory(){
		super();
	}
	public SerializedInventory(int initCap){
		super(initCap);
	}
	
	public SerializedInventory copy(){
		SerializedInventory out = new SerializedInventory(this.size());
		out.addAll(this);
		return out;
	}
	
	

	public Integer get(ItemRecord key){
		return super.get(key);
	}
	
	private ItemRecord getMatchingRecord(ItemMatcher m){
		for(ItemRecord r : keySet()){
			if(m.matches(r)){
				return r;
			}
		}
		return null;
	}
	
	public Integer get(ItemMatcher m){
		return get(getMatchingRecord(m));
	}
	
	@Override
	public void putAll(Map<? extends ItemRecord,? extends Integer> other){
		this.addAll(other);
	}
	
	public void addAll(Map<? extends ItemRecord,? extends Integer> other){
		for(Map.Entry<? extends ItemRecord,? extends Integer> e : other.entrySet()){
			if(this.containsKey(e.getKey())){
				this.put(e.getKey(), this.get(e.getKey())+e.getValue());
			} else {
				this.put(e.getKey(), e.getValue());
			}
		}
	}
	
	public void add(Iterable<ItemStack> items){
		for(ItemStack item : items){
			if(item == null) continue;
			ItemRecord r = new ItemRecord(item);
			if(!this.containsKey(r)){
				this.put(r, 1);
			} else {
				this.put(r, this.get(r)+item.stackSize);
			}
		}
	}
	
	public void add(ItemStack... items){
		for(ItemStack item : items){
			if(item == null) continue;
			ItemRecord r = new ItemRecord(item);
			if(!this.containsKey(r)){
				this.put(r, item.stackSize);
			} else {
				this.put(r, this.get(r)+item.stackSize);
			}
		}
	}
	
	public boolean contains(ItemRecord itemRef){
		return containsKey(itemRef);
	}
	
	public boolean decrement(ItemRecord itemRef){
		if(contains(itemRef) && get(itemRef) > 0){
			int i = get(itemRef);
			i--;
			if(i <= 0){
				remove(itemRef);
			} else {
				put(itemRef,i);
			}
			return true;
		} else {
			return false;
		}
	}
	

	public boolean contains(ItemMatcher matcher){
		for(ItemRecord r : keySet()){
			if(matcher.matches(r)) return true;
		}
		return false;
	}
	
	public boolean decrement(ItemMatcher matcher){
		ItemRecord itemRef = getMatchingRecord(matcher);
		if(itemRef == null) return false;
		return decrement(itemRef);
	}
	
	/**
	 * Splits the itemstacks into a series of unstacked items.
	 * @param inventory
	 * @return
	 */
	public static SerializedInventory serialize(Collection<ItemStack> inventory){
		SerializedInventory serialized = new SerializedInventory(inventory.size()*16);
		for(ItemStack i : inventory){
			if(i == null) continue;
			ItemStack temp = i.copy();
			int count = i.stackSize;
			temp.stackSize = 1;
			for(int n = 0; n < count; n++){
				serialized.add(temp.copy());
			}
		}
		return serialized;
	}
	public static SerializedInventory serialize(ItemStack... inventory){
		return serialize(Arrays.asList(inventory));
	}
	
	public static List<ItemStack> deserialize(SerializedInventory serializedInventory){
		List<ItemStack> output = new ArrayList<>(serializedInventory.size());
		for(Map.Entry<ItemRecord,Integer> e : serializedInventory.entrySet()){
			if(e.getKey() == null) continue;
			ItemStack k = e.getKey().getItem().copy();
			k.stackSize = e.getValue();
			while(k.stackSize > k.getMaxStackSize()){
				ItemStack k2 = k.copy();
				k2.stackSize = k.getMaxStackSize();
				output.add(k2);
				k.stackSize -= k.getMaxStackSize();
			}
			output.add(k);
		}
		return output; 
	}
	

	public List<ItemStack> deserialize(){
		return deserialize(this);
	}
	
	@Override
	public String toString(){
		return Arrays.toString(this.deserialize().toArray());
	}
}
