package cyano.electricadvantage.init;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.blocks.ElectricConduitBlock;
import cyano.electricadvantage.blocks.ElectricScaffoldBlock;
import cyano.electricadvantage.blocks.Ore;
import cyano.electricadvantage.blocks.PumpPipeBlock;
import cyano.electricadvantage.machines.*;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.blocks.BlockPowerSwitch;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public abstract class Blocks {
	private static final Map<String,Block> allBlocks = new HashMap<>();

	public static Block electric_conduit;
	public static Block lithium_ore;
	public static Block sulfur_ore;

	public static Block electric_track;
	public static Block led_bar;
	
	public static GUIBlock steam_powered_generator;
	public static GUIBlock photovoltaic_generator;
	public static GUIBlock hydroelectric_generator;

	public static GUIBlock arc_furnace;
	public static GUIBlock battery_array;
	public static GUIBlock rock_crusher;
	public static GUIBlock laser_drill;
	public static GUIBlock laser_turret;
	public static GUIBlock laser_turret_evil;
	public static GUIBlock fabricator;
	public static GUIBlock growth_chamber;
	public static GUIBlock growth_chamber_controller;
	public static GUIBlock oven;

	public static Block pump_pipe_electric;
	public static Block electric_switch;
	public static GUIBlock electric_still;
	public static GUIBlock electric_pump;
	public static GUIBlock plastic_refinery;
	

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		Items.init();
		
		electric_conduit = addBlock(new ElectricConduitBlock(),"electric_conduit","wire","conduitElectricity","powerCable","cableElectric");
		lithium_ore = addBlock(new Ore(1),"li_ore","oreLithium");
		sulfur_ore = addBlock(new Ore(new ItemStack(Items.sulfur_powder,1),0,3),"sulfur_ore","oreSulfur");
		
		electric_track = addBlock(new ElectricScaffoldBlock(),"electric_track");
		laser_turret = (GUIBlock)addBlock(new LaserTurretBlock(false),"laser_turret");
		laser_turret_evil = (GUIBlock)addBlock(new LaserTurretBlock(true),"laser_turret_evil");
		laser_turret_evil.setUnlocalizedName(ElectricAdvantage.MODID+".laser_turret");
		laser_turret_evil.setCreativeTab(null);
		led_bar = addBlock(new LEDBlock(),"led_bar");

		photovoltaic_generator = (GUIBlock)addBlock(new PhotovoltaicGeneratorBlock(),"photovoltaic_generator");
		hydroelectric_generator = (GUIBlock)addBlock(new HydroelectricGeneratorBlock(),"hydroelectric_generator");
		steam_powered_generator = (GUIBlock)addBlock(new SteamPoweredElectricGeneratorBlock(),"steam_powered_generator");

		arc_furnace = (GUIBlock)addBlock(new ElectricFurnaceBlock(),"electric_furnace");
		battery_array = (GUIBlock)addBlock(new ElectricBatteryArrayBlock(),"electric_battery_array");
		rock_crusher = (GUIBlock)addBlock(new ElectricCrusherBlock(),"electric_crusher");
		laser_drill = (GUIBlock)addBlock(new ElectricDrillBlock(),"electric_drill");
		fabricator = (GUIBlock)addBlock(new ElectricFabricatorBlock(),"electric_fabricator");
		growth_chamber = (GUIBlock)addBlock(new GrowthChamberBlock(),"growth_chamber");
		growth_chamber_controller = (GUIBlock)addBlock(new GrowthChamberControllerBlock(),"growth_chamber_controller");
		oven = (GUIBlock)addBlock(new ElectricOvenBlock(),"electric_oven");
		
		

		electric_switch = addBlock(new BlockPowerSwitch(Power.ELECTRIC_POWER),"electric_switch");
		electric_pump = (GUIBlock)addBlock(new ElectricPumpBlock(),"electric_pump");
		electric_still = (GUIBlock)addBlock(new ElectricStillBlock(),"electric_still");
		plastic_refinery = (GUIBlock)addBlock(new PlasticRefineryBlock(),"plastic_refinery");
		pump_pipe_electric = addBlock(new PumpPipeBlock(),"pump_pipe_electric");
		pump_pipe_electric.setCreativeTab(CreativeTabs.SEARCH);
		
		// required OreDictionary entries
		OreDictionary.registerOre("blockDirt", net.minecraft.init.Blocks.DIRT);
		OreDictionary.registerOre("blockDirt", net.minecraft.init.Blocks.GRASS);
		
		initDone = true;
	}
	

	private static Block addBlock(Block block, String name, String... oreDictNames){
		block.setUnlocalizedName(ElectricAdvantage.MODID+"."+name);
		GameRegistry.registerBlock(block, name);
		block.setCreativeTab(cyano.poweradvantage.init.ItemGroups.tab_powerAdvantage);
		allBlocks.put(name, block);
		for(String oreName : oreDictNames){
			OreDictionary.registerOre(oreName, block);
		}
		return block;
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(FMLInitializationEvent event){
		for(Map.Entry<String, Block> e : allBlocks.entrySet()){
			String name = e.getKey();
			Block block = e.getValue();
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(net.minecraft.item.Item.getItemFromBlock(block), 0, 
				new ModelResourceLocation(ElectricAdvantage.MODID+":"+name, "inventory"));
		}
	}

	
}
