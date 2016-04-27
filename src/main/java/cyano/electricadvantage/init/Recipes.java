package cyano.electricadvantage.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cyano.basemetals.registry.CrusherRecipeRegistry;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.RecipeMode;

public abstract class Recipes {

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();
		Items.init();

		RecipeMode recipeMode = PowerAdvantage.recipeMode;
		
		// Recipes for all recipe modes
		OreDictionary.registerOre("blockBrick", net.minecraft.init.Blocks.BRICK_BLOCK);
		OreDictionary.registerOre("gunpowder", net.minecraft.init.Items.GUNPOWDER);
		GameRegistry.addSmelting(Items.lithium_powder,new ItemStack(Items.lithium_ingot),0.5f);
		GameRegistry.addSmelting(Blocks.lithium_ore,new ItemStack(Items.lithium_ingot),0.5f);
		CrusherRecipeRegistry.addNewCrusherRecipe("oreLithium",new ItemStack(Items.lithium_powder,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("ingotLithium",new ItemStack(Items.lithium_powder,1));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreSulfur",new ItemStack(Items.sulfur_powder,4));
		
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.electric_conduit,6),"xxx","ccc","xxx",'x',"plastic",'c',"ingotCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.electric_conduit,6),"xxx","ccc","xxx",'x',"rubber",'c',"ingotCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.electric_conduit,1),"xx","cc",'x',"plastic",'c',"rodCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.electric_conduit,1),"xx","cc",'x',"rubber",'c',"rodCopper"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.blank_circuit_board,2),"plastic","plateCopper"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.control_circuit,1),Items.blank_circuit_board,"microchip","solder"));
		GameRegistry.addSmelting(Items.silicon_blend, new ItemStack(Items.silicon_ingot), 0.5f);
		GameRegistry.addSmelting(Items.solder_blend, new ItemStack(Items.solder), 0.5f);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.silicon_blend,1),"sand","dustCarbon"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.solder_blend,3),"dustTin","dustTin","dustLead"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.solder_blend,3),"dustTin","dustTin","dustSilver"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.led_bar,3),"ggg","xxx","ccc",'g',"paneGlass",'x',"microchip",'c',"wire"));

		GameRegistry.addRecipe(batteryRecipe(Items.lead_acid_battery,"ingotLead","sulfur",net.minecraft.init.Items.WATER_BUCKET));
		GameRegistry.addRecipe(batteryRecipe(Items.lead_acid_battery,"ingotLead","dustSulfur",net.minecraft.init.Items.WATER_BUCKET));
		GameRegistry.addRecipe(batteryRecipe(Items.nickel_hydride_battery,"ingotNickel","dustRedstone",net.minecraft.init.Items.WATER_BUCKET));
		GameRegistry.addRecipe(batteryRecipe(Items.alkaline_battery,"ingotIron","gunpowder","ingotZinc"));
		GameRegistry.addRecipe(batteryRecipe(Items.lithium_battery,"ingotLithium","dustRedstone","dustCarbon"));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.electric_track,1),Blocks.electric_conduit, cyano.poweradvantage.init.Blocks.steel_frame));
		
		// non-apocalyctic recipes (high-tech machines cannot be crafted in post-apocalyspe mode)
		if(recipeMode != RecipeMode.APOCALYPTIC){
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.photovoltaic_generator,1),"ggg","sss","wuw",'g',"paneGlass",'s',"ingotSilicon",'w',"wire",'u',"PSU"));
		}
		
		
		// recipe-mode specific recipes
		if(recipeMode == RecipeMode.TECH_PROGRESSION){
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.integrated_circuit,3),"prp","sss","ccc",'p',"plastic",'s',"ingotSilicon",'r',"dustRedstone",'c',"nuggetCopper"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.integrated_circuit,3),"prp","sss","ccc",'p',"plastic",'s',"ingotSilicon",'r',"dustRedstone",'c',"nuggetTin"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.integrated_circuit,3),"prp","sss","ccc",'p',"plastic",'s',"ingotSilicon",'r',"dustRedstone",'c',"nuggetGold"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.power_supply_unit,1),"wcw"," s ",'w',"wire",'c',"circuitBoard",'s',"plateSteel"));
		} else if(recipeMode == RecipeMode.APOCALYPTIC){
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.steam_powered_generator, new ItemStack(Items.power_supply_unit,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.arc_furnace, new ItemStack(Items.power_supply_unit,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.photovoltaic_generator, new ItemStack(Items.power_supply_unit,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Items.power_supply_unit, new ItemStack(Items.control_circuit,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Items.control_circuit, new ItemStack(Items.integrated_circuit,1));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.power_supply_unit,1),"wcw"," s ",'w',"wire",'c',"circuitBoard",'s',"plateSteel"));
		} else {
			// normal
			OreDictionary.registerOre("solder", cyano.basemetals.init.Items.lead_ingot);
			OreDictionary.registerOre("solder", cyano.basemetals.init.Items.tin_ingot);
			OreDictionary.registerOre("solder", cyano.basemetals.init.Items.silver_ingot);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.integrated_circuit,3),"sss","ccc",'s',"ingotSilicon",'c',"nuggetCopper"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.integrated_circuit,3),"sss","ccc",'s',"ingotSilicon",'c',"nuggetTin"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.integrated_circuit,3),"sss","ccc",'s',"ingotSilicon",'c',"nuggetGold"));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.blank_circuit_board,2),"plastic","ingotCopper"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.power_supply_unit,1),"wcw"," s ",'w',"wire",'c',"circuitBoard",'s',"ingotSteel"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.power_supply_unit,1),"wcw"," s ",'w',"wire",'c',"circuitBoard",'s',"ingotIron"));
		}

		// Machine recipes
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.steam_powered_generator, "conduitSteam","governor"));
		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.arc_furnace, "bbb","bub","bbb",'b',"blockBrick",'u',"PSU"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.hydroelectric_generator, "sprocket", "sprocket"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.battery_array, "chest"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.rock_crusher, "sprocket", "gemDiamond"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.laser_turret, "gemDiamond", "gemEmerald"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.laser_turret, "gemEmerald", "gemDiamond"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.laser_drill, "blockDiamond"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.fabricator, net.minecraft.init.Blocks.CRAFTING_TABLE));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.growth_chamber, net.minecraft.init.Items.FLOWER_POT, "microchip"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.growth_chamber_controller, net.minecraft.init.Items.FLOWER_POT, "circuitBoard"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.oven, "paneGlass", "PSU"));

		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.electric_switch," L ","pfp",'L',net.minecraft.init.Blocks.LEVER,'p',"wire",'f',"frameSteel"));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.electric_still, net.minecraft.init.Items.BUCKET, net.minecraft.init.Items.BUCKET));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.electric_pump, net.minecraft.init.Blocks.PISTON, net.minecraft.init.Items.BUCKET));
		GameRegistry.addRecipe(electricMachineRecipe(Blocks.plastic_refinery, net.minecraft.init.Blocks.PISTON, "sprocket"));
		
		
		initDone = true;
	}

	private static ShapedOreRecipe electricMachineRecipe(Block output, Object item){
		return new ShapedOreRecipe(output, "uX ","pmp",'X',item,'u',"PSU",'p',"plateSteel",'m',"frameSteel");
	}

	private static ShapedOreRecipe electricMachineRecipe(Block output, Object item1, Object item2){
		return new ShapedOreRecipe(output, "uXY","pmp",'X',item1,'Y',item2,'u',"PSU",'p',"plateSteel",'m',"frameSteel");
	}
	
	private static ShapedOreRecipe batteryRecipe(Item output, Object top, Object middle, Object bottom){
		return new ShapedOreRecipe(output, "pXp","pYp","pZp",'X',top,'Y',middle,'Z',bottom,'p',"plastic");
	}
}
