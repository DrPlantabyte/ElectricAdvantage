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
import cyano.electricadvantage.util.RecipeDeconstructor;
import cyano.electricadvantage.util.SerializedInventory;
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

/* TODO list
 * 
 */
@Mod(modid = ElectricAdvantage.MODID, version = ElectricAdvantage.VERSION, name=ElectricAdvantage.NAME, 
		dependencies = "required-after:poweradvantage;required-after:basemetals")
public class ElectricAdvantage
{/** The identifier for this mod */
	public static final String MODID = "electricadvantage";
	/** The display name for this mod */
	public static final String NAME = "Electric Advantage";
	/** The version of this mod, in the format major.minor.update */
	public static final String VERSION = "0.0.6";

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

		// TODO: remove debug code
				fabTest();
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
	
	// TODO: remove debug code
	private void fabTest(){
		ItemStack[] baseInventory = {
				new ItemStack(net.minecraft.init.Blocks.log,64,0),
				new ItemStack(net.minecraft.init.Blocks.cobblestone,64,0),
				new ItemStack(net.minecraft.init.Blocks.stone,64,0),
				new ItemStack(net.minecraft.init.Items.redstone,64,0),
				new ItemStack(net.minecraft.init.Items.iron_ingot,64,0),
				new ItemStack(cyano.poweradvantage.init.Items.bioplastic_ingot,64,0),
				new ItemStack(cyano.basemetals.init.Items.copper_ingot,64,0),
				null,
				null,
				null
		};

		FMLLog.info("%s","Starting inventory:");
		printInventory(Arrays.asList(baseInventory));

		test(new ItemStack(net.minecraft.init.Blocks.oak_fence,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Blocks.piston,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Blocks.stonebrick,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Blocks.stonebrick,1,3), baseInventory);
		test(new ItemStack(net.minecraft.init.Blocks.furnace,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Items.repeater,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Items.armor_stand,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Items.stick,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Items.iron_pickaxe,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Items.bucket,1,0), baseInventory);
		test(new ItemStack(cyano.electricadvantage.init.Blocks.electric_conduit,1,0), baseInventory);
		

		test(new ItemStack(net.minecraft.init.Blocks.diamond_block,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Items.map,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Items.sugar,1,0), baseInventory);
		test(new ItemStack(net.minecraft.init.Items.brewing_stand,1,0), baseInventory);
		
	}
	
	// TODO: remove debug code
	private void test(ItemStack itemStack,ItemStack[] baseInventory) {

		FMLLog.info("Attempting to craft %s",itemStack);
		AtomicReference<ItemStack> callback = new AtomicReference<>();
		long t0 = System.nanoTime();
		SerializedInventory result = RecipeDeconstructor.getInstance().attemptToCraft(itemStack, SerializedInventory.serialize(baseInventory), callback);
		long t1 = System.nanoTime();
		if(result == null){
			FMLLog.info("%s","Failed to craft!");
		} else {
			FMLLog.info("Successfully crafted %s. Remaining inventory is:",callback.get());
			printInventory(result.deserialize());
		}
		FMLLog.info("Test completed in %s us\n\n",(t1-t0)*0.001);
	}

	// TODO: remove debug code
	private void printInventory(Iterable<ItemStack> e){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(ItemStack i : e){
			if(!first)sb.append("; ");
			sb.append(String.valueOf(i));
			first = false;
		}
		FMLLog.info("%s",sb.toString());
	}

}
