package cyano.electricadvantage.init;

import java.util.HashMap;
import java.util.Map;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.blocks.ElectricConduitBlock;
import cyano.electricadvantage.blocks.Ore;
import cyano.electricadvantage.machines.*;
import cyano.poweradvantage.api.GUIBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public abstract class Blocks {
	private static final Map<String,Block> allBlocks = new HashMap<>();

	public static Block electric_conduit;
	public static Block lithium_ore;
	public static Block sulfur_ore;
	
	public static GUIBlock steam_powered_generator;
	public static GUIBlock photovoltaic_generator;

	public static GUIBlock arc_furnace;
	public static GUIBlock battery_array;
	

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		Items.init();
		
		electric_conduit = addBlock(new ElectricConduitBlock(),"electric_conduit","wire","conduitElectricity","powerCable","cableElectric");
		lithium_ore = addBlock(new Ore(1),"li_ore","oreLithium");
		sulfur_ore = addBlock(new Ore(new ItemStack(Items.sulfur_powder,1),0,3),"sulfur_ore","oreSulfur");

		photovoltaic_generator = (GUIBlock)addBlock(new PhotovoltaicGeneratorBlock(),"photovoltaic_generator");
		steam_powered_generator = (GUIBlock)addBlock(new SteamPoweredElectricGeneratorBlock(),"steam_powered_generator");

		arc_furnace = (GUIBlock)addBlock(new ElectricFurnaceBlock(),"electric_furnace");
		battery_array = (GUIBlock)addBlock(new ElectricBatteryArrayBlock(),"electric_battery_array");
		
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
