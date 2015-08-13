package cyano.electricadvantage.init;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cyano.poweradvantage.PowerAdvantage;

public abstract class TreasureChests {

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();
		Items.init();

		
		initDone = true;
	}
	

	
	private static WeightedRandomChestContent makeChestLootEntry(ItemStack itemStack, int spawnWeight, int minQuantity,int maxQuantity){
		if(itemStack == null) return null;
		if(spawnWeight <= 0) return null;
		return new WeightedRandomChestContent(itemStack,minQuantity,maxQuantity,spawnWeight);
	}
	
	private static void addChestLoot(ItemStack item, float weight, int number, int range){
		WeightedRandomChestContent loot = makeChestLootEntry(item,(int)(weight*PowerAdvantage.chestLootFactor),number,number+range);
		if(loot != null){
			ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(loot);
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(loot);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(loot);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CROSSING).addItem(loot);
		}
	}
}
