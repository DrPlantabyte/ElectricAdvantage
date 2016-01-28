package cyano.electricadvantage.gui;

import java.util.HashMap;
import java.util.Map;

import cyano.poweradvantage.api.simple.SimpleMachineGUI.GUIContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GUIHelper {
	private static final int INDICATOR_LED_X = 182;
	private static final int INDICATOR_LED_Y = 2;
	private static final int INDICATOR_LED_W = 7;
	private static final int INDICATOR_LED_H = 7;
	

	private static final int LED_BAR_X = 180;
	private static final int LED_BAR_Y = 27;
	private static final int LED_BAR_H = 9;
	private static final int LED_BAR_W8 = 4;
	

	private static final Map<ResourceLocation,ResourceLocation> realTextureLocationCache = new HashMap<>();
	
	public static void drawIndicatorLight(boolean on,GUIContainer gc, int xOffset, int yOffset){
		if(on){
			gc.drawTexturedModalRect(8+xOffset, 8+yOffset, INDICATOR_LED_X, INDICATOR_LED_Y, INDICATOR_LED_W, INDICATOR_LED_H);
		}
	}
	
	public static void drawProgressBar(int x, int y, float progress, GUIContainer gc){
		int n = (int)(8 * (progress + 0.0625f) );
		if(n <= 0) return;
		int w = 1 + (n * LED_BAR_W8);
		gc.drawTexturedModalRect(x, y, LED_BAR_X, LED_BAR_Y, w, LED_BAR_H);
	}
	

	public static void drawFluidBar(FluidStack fs, float barHeight, int xPos, int yPos, 
			GUIContainer guiContainer, int x, int y,
			ResourceLocation displayImage){
		final int w = 16;
		final int barSlotHeight = 60;
		final int h = (int)(barSlotHeight * barHeight);
		final float fluidTexWidth = 16;
		final float fluidTexHeight = 512;
		final float texPerPixel = 4 * (fluidTexWidth / fluidTexHeight) / barSlotHeight;
		if(barHeight > 0 && fs != null && fs.getFluid() != null){
			ResourceLocation fluidTexture = realTextureLocationCache.computeIfAbsent(fs.getFluid().getStill(fs),
					(ResourceLocation r) -> new ResourceLocation(r.getResourceDomain(),"textures/".concat(r.getResourcePath()).concat(".png"))
					);
			guiContainer.mc.renderEngine.bindTexture(fluidTexture);
			
			guiContainer.drawModalRectWithCustomSizedTexture(x+xPos, y+yPos+barSlotHeight-h, 0, 0, w, h, 16, h);//h * texPerPixel); // x, y, u, v, width, height, textureWidth, textureHeight
		}
			guiContainer.mc.renderEngine.bindTexture(displayImage);
		
	}
}
