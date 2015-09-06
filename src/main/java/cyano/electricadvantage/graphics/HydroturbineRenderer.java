package cyano.electricadvantage.graphics;

import cyano.electricadvantage.ElectricAdvantage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HydroturbineRenderer extends Render{
	private static final ResourceLocation texture = new ResourceLocation(ElectricAdvantage.MODID+":textures/entity/hydroturbine.png");

    
    public HydroturbineRenderer(final RenderManager rm) {
        super(rm);
    }
    
    @Override
    public void doRender(final Entity e, final double x, final double y, final double z, final float f1, final float f2) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(e);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
     //   final float scale = this.scale;
     //   GlStateManager.scale(scale / 1.0f, scale / 1.0f, scale / 1.0f);
        final Tessellator instance = Tessellator.getInstance();
        final WorldRenderer worldRenderer = instance.getWorldRenderer();
        final float minU = 0;
        final float maxU = 1;
        final float minV = 0;
        final float maxV = 1;
        final float n = 1.0f;
        final float n2 = 0.5f;
        final float n3 = 0.25f;
        GlStateManager.rotate(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        this.bindTexture(this.getEntityTexture(e));
        worldRenderer.startDrawingQuads();
        worldRenderer.setNormal(0.0f, 1.0f, 0.0f);
        worldRenderer.addVertexWithUV(0.0f - n2, 0.0f - n3, 0.0, minU, maxV);
        worldRenderer.addVertexWithUV(n - n2, 0.0f - n3, 0.0, maxU, maxV);
        worldRenderer.addVertexWithUV(n - n2, 1.0f - n3, 0.0, maxU, minV);
        worldRenderer.addVertexWithUV(0.0f - n2, 1.0f - n3, 0.0, minU, minV);
        instance.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(e, x, y, z, f1, f2);
    }
    
    
    @Override
    protected ResourceLocation getEntityTexture(final Entity e) {
        return texture;
    }
}
