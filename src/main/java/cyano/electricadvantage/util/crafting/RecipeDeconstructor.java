package cyano.electricadvantage.util.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RecipeDeconstructor {

	private static final boolean enableDebugOutput = false;
	
	private static RecipeDeconstructor instance = null;
	private static final Lock initLock = new ReentrantLock();
	
	public static int RECURSION_LIMIT = 5;
	

	private Map<ItemRecord,List<IRecipe>> recipeCache = null;
	
	
	// thread-safe singleton initialization
	public static RecipeDeconstructor getInstance(){
		if(instance == null){
			initLock.lock();
			try{
				if(instance == null){
					instance = new RecipeDeconstructor();
				}
			}finally{
				initLock.unlock();
			}
		}
		return instance;
	}
	
	public List<IRecipe> getRecipesForItem(ItemStack item){
		if(recipeCache == null) initializeRecipeCache();
		return recipeCache.get(new ItemRecord(item));
	}
	
	
	public void initializeRecipeCache(){
		recipeCache = new HashMap<>();
		for(Object o : CraftingManager.getInstance().getRecipeList()){
			if(o instanceof IRecipe){
				IRecipe recipe = (IRecipe)o;
				ItemStack output = recipe.getRecipeOutput();
				ItemRecord key;
				try{
					if(output == null || output.getItem() == null || output.stackSize <= 0) {
						// invalid recipe
						continue;
					}
					key = new ItemRecord(output);
				} catch(Exception ex){
					// extra insurance
					FMLLog.severe("%s: ERROR! Encountered corrupted item! One of the installed mods added an invalid crafting recipe to the game registry.",this.getClass().getName());
					continue;
				}
				if(recipeCache.containsKey(key) == false){
					recipeCache.put(key, new ArrayList<IRecipe>());
				}
				recipeCache.get(key).add(recipe);
			}
		}
	}
	
	public void invalidateRecipeCache(){
		recipeCache = null;
	}

	/**
	 * Attempts to craft a specified item from the provided inventory of serialized items, 
	 * recursively searching for recipes if needed ingredients. This method returns the new state of 
	 * the inventory after crafting if successful, or null if the item cannot be crafted.
	 * @param craftingTarget The item you want to craft.
	 * @param serializedInventory The inventory to use, serialized such that all items stacks have a 
	 * size of 1
	 * @param output Atomic reference used to pass the actual product of crafting back to the caller
	 * @return Returns the new inventory after crafting, or null if the item cannot be crafted from 
	 * the provided inventory
	 */
	public SerializedInventory attemptToCraft(ItemStack craftingTarget, SerializedInventory serializedInventory, AtomicReference<ItemStack> output){
		return attemptToCraft( craftingTarget, serializedInventory, output, 0);
	}
	/**
	 * Attempts to craft a specified item from the provided inventory of serialized items, 
	 * recursively searching for recipes if needed ingredients. This method returns the new state of 
	 * the inventory after crafting if successful, or null if the item cannot be crafted.
	 * @param craftingTarget The item you want to craft.
	 * @param serializedInventory The inventory to use, serialized into a SerializedInventory 
	 * instance
	 * @param output Atomic reference used to pass the actual product of crafting back to the caller
	 * @param recursionDepth Used to control how deep the recursion goes. Use 0 if you are calling
	 * this method.
	 * @return Returns the new inventory after crafting, or null if the item cannot be crafted from 
	 * the provided inventory
	 */
	private SerializedInventory attemptToCraft(ItemStack craftingTarget, SerializedInventory serializedInventory, AtomicReference<ItemStack> output, int recursionDepth){
		// check recusion limit
		if(recursionDepth > RECURSION_LIMIT) return null;
		if(craftingTarget == null) return null;
		
		debug("Looking up recipe for %s in %s",craftingTarget,serializedInventory);
		// handle wild-card items by attempting to craft each valid variant
		if(craftingTarget.getMetadata() == OreDictionary.WILDCARD_VALUE){
			debug("%s has wild-card meta value",craftingTarget);
			SerializedInventory attempt1 = attemptToCraft(craftingTarget,serializedInventory,output,recursionDepth+1);
			if(attempt1 != null || craftingTarget.getItem().isDamageable()) return attempt1;
			ItemStack newTarget = craftingTarget.copy();
			for(int meta = 0; meta < 16; meta++){
				debug("Trying recipe with meta value ",meta);
				newTarget.setItemDamage(meta);
				SerializedInventory attempt2 = attemptToCraft(newTarget,serializedInventory,output,recursionDepth+1);
				if(attempt2 != null) return attempt2;
			}
			return null;
		}
		// get recipes for item
		List<IRecipe> recipes = getRecipesForItem(craftingTarget);
		if(recipes == null || recipes.isEmpty()) return null;
		
		for(IRecipe recipe : recipes){
			// make local copy of inventory and marshal the recipe into a list of item matchers
			recipeAttempt:{
				SerializedInventory tempInv = serializedInventory.copy();
				List<ItemMatcher> ingredients = marshalCraftingRecipe(recipe);
				if(ingredients == null || ingredients.isEmpty()) continue;

				debug("Recipe: %s",ingredients);
				
				// for each ingredient, either find it in the inventory (and remove it) or craft it from the inventory
				for(ItemMatcher ingredient : ingredients){
					if(tempInv.decrement(ingredient)){
						debug("Consumed %s",ingredient);
						// inventory contained requested ingredient, move on to the next
						continue;
					} else {
						// ingredient not found, try to craft it
						Collection<ItemStack> validItems = ingredient.getValidItems();
						if(validItems == null || validItems.isEmpty()){
							// no valid items (unused ore dictionary name?)
							// cannot craft
							break recipeAttempt;
						}
						boolean failure = true;
						for(ItemStack vi : validItems){
							AtomicReference<ItemStack> ret = new AtomicReference<>();
							SerializedInventory r = attemptToCraft(vi,tempInv,ret,recursionDepth+1);
							if(r != null) {
								ItemStack y = ret.get();
								if(y.stackSize > 1){
									y.stackSize--;
									r.add(y);
								}
								tempInv = r;
								failure = false;
								break;
							}
						}
						if(failure) break recipeAttempt;
					}
				}
				// if we made it this far, then we successfully crafted the item
				// set the output callback
				debug("Craft successful; New inventory: %s",tempInv);
				output.set(recipe.getRecipeOutput().copy());
				// return the new state of the inventory
				return tempInv;
			}
		}
		// no valid recipes found
		return null;
	}
	
	

	private static List<ItemMatcher> marshalCraftingRecipe(IRecipe recipe) {
		if(recipe instanceof ShapedRecipes){
			return marshalCraftingRecipe(((ShapedRecipes)recipe).recipeItems);
		} else if(recipe instanceof ShapelessRecipes){
			return marshalCraftingRecipe(((ShapelessRecipes)recipe).recipeItems);
		} else if(recipe instanceof ShapedOreRecipe){
			return marshalCraftingRecipe(((ShapedOreRecipe)recipe).getInput());
		} else if(recipe instanceof ShapelessOreRecipe){
			return marshalCraftingRecipe(((ShapelessOreRecipe)recipe).getInput());
		} else {
			// unsupported recipe type
			return null;
		}
	}
	private static List<ItemMatcher> marshalCraftingRecipe(ItemStack[] recipeItems) {
		return marshalCraftingRecipe(Arrays.asList(recipeItems));
	}
	private static List<ItemMatcher> marshalCraftingRecipe(Object[] recipeItems) {
		return marshalCraftingRecipe(Arrays.asList(recipeItems));
	}
	private static List<ItemMatcher> marshalCraftingRecipe(List recipeItems) {
		List<ItemMatcher> output = new ArrayList<>(recipeItems.size());
		for(Object o : recipeItems){
			if(o == null) continue;
			if(o instanceof ItemStack){
				output.add(new ItemMatcher((ItemStack)o));
			} else if(o instanceof String){
				output.add(new ItemMatcher((String)o));
			} else if(o instanceof Block){
				output.add(new ItemMatcher((Block)o));
			} else if(o instanceof Item){
				output.add(new ItemMatcher((Item)o));
			} else if(o instanceof List){
				output.add(new ItemMatcher((List)o));
			} else {
				FMLLog.severe("%s: Recipe item %s of class type %s was not recognized!", 
						RecipeDeconstructor.class.getName(),
						o.toString(),
						o.getClass().getName());
			}
		}
		return output;
	}
	
	private static void debug(String msg, Object... data){
		if(enableDebugOutput){
			FMLLog.info(RecipeDeconstructor.class.getName()+": "+msg, data);
		}
	}
}
