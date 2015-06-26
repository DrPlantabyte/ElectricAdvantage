package cyano.electricadvantage.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cyano.basemetals.registry.CrusherRecipeRegistry;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.RecipeMode;
import cyano.electricadvantage.ElectricAdvantage;

public class Recipes {

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();
		Items.init();
		
		
		// Recipes for all recipe modes
		
		
		
		// recipe-mode specific recipes
		RecipeMode recipeMode = PowerAdvantage.recipeMode;
		if(recipeMode == RecipeMode.TECH_PROGRESSION){
			
		} else if(recipeMode == RecipeMode.APOCALYPTIC){
			
		} else {
			// normal
			
		}

		
		
		initDone = true;
	}

	private static ShapedOreRecipe steamMachineRecipe(Block output, Object item){
		return new ShapedOreRecipe(output, "gXg","pmp",'X',item,'g',"governor",'p',"plateIron",'m',"frameSteel");
	}

	private static ShapedOreRecipe steamMachineRecipe(Block output, Object item1, Object item2){
		return new ShapedOreRecipe(output, " Y ","gXg","pmp",'X',item1,'Y',item2,'g',"governor",'p',"plateIron",'m',"frameSteel");
	}
}
