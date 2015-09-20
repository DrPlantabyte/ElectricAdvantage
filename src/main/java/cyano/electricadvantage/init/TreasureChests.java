package cyano.electricadvantage.init;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.RecipeMode;

public abstract class TreasureChests {

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();
		Items.init();
		addChestLoot(new ItemStack(Items.power_supply_unit,1),2.5f,1,1);
		addChestLoot(new ItemStack(Blocks.electric_conduit,1),5f,4,8);
		addChestLoot(new ItemStack(Items.lead_acid_battery,1),4f,1,1);
		addChestLoot(new ItemStack(Items.nickel_hydride_battery,1),3f,1,1);
		addChestLoot(new ItemStack(Items.alkaline_battery,1),2f,1,1);
		addChestLoot(new ItemStack(Items.lithium_battery,1),1f,1,1);
		
		if(PowerAdvantage.recipeMode == RecipeMode.APOCALYPTIC){
			addChestLoot(new ItemStack(Items.integrated_circuit,1),2.5f,1,5);
			addChestLoot(new ItemStack(Items.control_circuit,1),1.25f,1,1);
			addChestLoot(new ItemStack(Blocks.photovoltaic_generator,1),0.5f,1,1);
			addChestLoot(new ItemStack(Items.solder,3),2f,1,1);
		}
		
		initDone = true;
	}
	

	
	private static WeightedRandomChestContent makeChestLootEntry(ItemStack itemStack, int spawnWeight, int minQuantity,int maxQuantity){
		if(itemStack == null) return null;
		if(spawnWeight <= 0) return null;
		return new WeightedRandomChestContent(itemStack,minQuantity,maxQuantity,spawnWeight);
	}
	
	private static void addChestLoot(ItemStack item, float weight, int number, int range){
		WeightedRandomChestContent loot = makeChestLootEntry(item,Math.max(1, Math.max(1, (int)(weight*PowerAdvantage.chestLootFactor))),number,number+range);
		if(loot != null){
			ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(loot);
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(loot);
			ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(loot);
		}
	}
}
