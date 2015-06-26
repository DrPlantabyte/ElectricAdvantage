package cyano.electricadvantage.init;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.FMLLog;
import cyano.electricadvantage.enchanments.*;

public class Enchantments {

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		

		
		initDone = true;
	}
	
	private static Enchantment addEnchantment(Enchantment e){
		Enchantment.addToBookList(e);
		return e;
	}
	
	private static int getNextEnchantmentID(){
		return Enchantment.enchantmentsBookList.length;
	}
}
