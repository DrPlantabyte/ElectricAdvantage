package cyano.electricadvantage.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import cyano.poweradvantage.api.GUIBlock;
import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.blocks.*;
import cyano.electricadvantage.machines.*;

public abstract class Blocks {
	private static final Map<String,Block> allBlocks = new HashMap<>();

	public static Block electric_conduit;
	
	public static GUIBlock steam_powered_generator;
	public static GUIBlock photovoltaic_generator;
	
	public static GUIBlock electric_furnace;
	

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		electric_conduit = addBlock(new ElectricConduitBlock(),"electric_conduit","wire","conduitElectricity","powerCable","cableElectric");

		photovoltaic_generator = (GUIBlock)addBlock(new PhotovoltaicGeneratorBlock(),"photovoltaic_generator");
		steam_powered_generator = (GUIBlock)addBlock(new SteamPoweredElectricGeneratorBlock(),"steam_powered_generator");

		electric_furnace = (GUIBlock)addBlock(new ElectricFurnaceBlock(),"electric_furnace");
		
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
