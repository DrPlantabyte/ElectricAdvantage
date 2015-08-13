package cyano.electricadvantage.init;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import cyano.electricadvantage.ElectricAdvantage;

public abstract class Items {

	
	public static final Map<String,Item> allItems = new HashMap<>();

	
	public static Item blank_circuit_board;
	public static Item control_circuit;
	public static Item integrated_circuit;
	public static Item power_supply_unit;
	public static Item silicon_ingot;
	public static Item silicon_blend;
	public static Item solder_blend;
	public static Item solder;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		Blocks.init();

		blank_circuit_board = addItem("blank_circuit_board",new Item());
		control_circuit = addItem("control_circuit",new Item(),"circuitBoard");
		integrated_circuit = addItem("integrated_circuit",new Item(),"microchip");
		power_supply_unit = addItem("psu",new Item(),"PSU");
		silicon_ingot = addItem("silicon_ingot",new Item(),"ingotSilicon","silicon");
		silicon_blend = addItem("silicon_mix",new Item());
		solder_blend = addItem("solder_mix",new Item());
		solder = addItem("solder",new Item(),"solder","ingotSolder");

		initDone = true;
	}

	private static Item addItem(String unlocalizedName, Item i,String... oreDictNames){
		Item n = addItem(unlocalizedName,i);
		for(String oreDictName : oreDictNames){
			OreDictionary.registerOre(oreDictName, n);
		}
		return n;
	}
	private static Item addItem(String unlocalizedName, Item i){
		i.setUnlocalizedName(ElectricAdvantage.MODID+"."+unlocalizedName);
		GameRegistry.registerItem(i, unlocalizedName);
		i.setCreativeTab(cyano.poweradvantage.init.ItemGroups.tab_powerAdvantage);
		allItems.put(unlocalizedName, i);
		return i;
	}
	
	public static Item getItemByName(String unlocalizedName){
		return allItems.get(unlocalizedName);
	}
	
	public static Set<Map.Entry<String, Item>> getAllRegisteredItems(){
		return allItems.entrySet();
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(FMLInitializationEvent event) {
		for(Map.Entry<String, Item> e :  getAllRegisteredItems()){
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(e.getValue(), 0, 
					new ModelResourceLocation(ElectricAdvantage.MODID+":"+e.getKey(), "inventory"));
		}
	}
	
}
