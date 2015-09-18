package cyano.electricadvantage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import cyano.electricadvantage.init.Blocks;
import cyano.electricadvantage.init.Enchantments;
import cyano.electricadvantage.init.Entities;
import cyano.electricadvantage.init.GUI;
import cyano.electricadvantage.init.Items;
import cyano.electricadvantage.init.Recipes;
import cyano.electricadvantage.init.TreasureChests;
import cyano.electricadvantage.util.crafting.RecipeDeconstructor;
import cyano.electricadvantage.util.crafting.SerializedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ElectricAdvantage.MODID, version = ElectricAdvantage.VERSION, name=ElectricAdvantage.NAME, 
		dependencies = "required-after:poweradvantage;required-after:basemetals")
public class ElectricAdvantage
{/** The identifier for this mod */
	public static final String MODID = "electricadvantage";
	/** The display name for this mod */
	public static final String NAME = "Electric Advantage";
	/** The version of this mod, in the format major.minor.update */
	public static final String VERSION = "1.0.0";

	public static ElectricAdvantage INSTANCE = null;

	/**
	 * Pre-initialization step. Used for initializing objects and reading the 
	 * config file
	 * @param event FML event object
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		INSTANCE = this;
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		RecipeDeconstructor.RECURSION_LIMIT = config.getInt("recursion_limit", "fabricator", RecipeDeconstructor.RECURSION_LIMIT, 
				1, 255, 
  "Prevents circular recipes from crashing the fabricator machine. You may need to increase this \n"
+ "number to fabricate especially complex recipes. If fabricators are causing too much server lag, "
+ "try reducing this number.");

		Path orespawnFolder = Paths.get(event.getSuggestedConfigurationFile().toPath().getParent().toString(),"orespawn");
		Path orespawnFile = Paths.get(orespawnFolder.toString(),MODID+".json");
		if(!Files.exists(orespawnFile)){
			try{
				Files.createDirectories(orespawnFile.getParent());
				Files.write(orespawnFile, Arrays.asList(Data.ORESPAWN_FILE_CONTENTS.split("\n")), Charset.forName("UTF-8"));
				cyano.basemetals.BaseMetals.oreSpawnConfigFiles.add(orespawnFile);
			} catch (IOException e) {
				FMLLog.severe(MODID+": Error: Failed to write file "+orespawnFile);
			}
		}
		
		config.save();


		if(event.getSide() == Side.CLIENT){
			clientPreInit(event);
		}
		if(event.getSide() == Side.SERVER){
			serverPreInit(event);
		}
	}

	@SideOnly(Side.CLIENT)
	private void clientPreInit(FMLPreInitializationEvent event){
		// client-only code
	}
	@SideOnly(Side.SERVER)
	private void serverPreInit(FMLPreInitializationEvent event){
		// client-only code
	}
	/**
	 * Initialization step. Used for adding renderers and most content to the 
	 * game
	 * @param event FML event object
	 */
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		Blocks.init();
		Items.init();
		Recipes.init();
		Entities.init();
		GUI.init();
		TreasureChests.init();
		Enchantments.init();

		if(event.getSide() == Side.CLIENT){
			clientInit(event);
		}
		if(event.getSide() == Side.SERVER){
			serverInit(event);
		}
	}
	

	@SideOnly(Side.CLIENT)
	private void clientInit(FMLInitializationEvent event){
		// client-only code
		Items.registerItemRenders(event);
		Blocks.registerItemRenders(event);
		Entities.registerRenderers();
		
	}
	@SideOnly(Side.SERVER)
	private void serverInit(FMLInitializationEvent event){
		// client-only code
	}

	/**
	 * Post-initialization step. Used for cross-mod options
	 * @param event FML event object
	 */
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{

		if(event.getSide() == Side.CLIENT){
			clientPostInit(event);
		}
		if(event.getSide() == Side.SERVER){
			serverPostInit(event);
		}
	}

	@SideOnly(Side.CLIENT)
	private void clientPostInit(FMLPostInitializationEvent event){
		// client-only code
	}
	@SideOnly(Side.SERVER)
	private void serverPostInit(FMLPostInitializationEvent event){
		// server-only code
	}
	
	

}
