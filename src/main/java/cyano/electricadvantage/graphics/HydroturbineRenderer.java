package cyano.electricadvantage.graphics;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.entities.HydroturbineEntity;
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
	public void doRender(final Entity entity, final double x, final double y, final double z, final float scale, final float partialTick) {
		if(entity instanceof HydroturbineEntity){
			HydroturbineEntity e = (HydroturbineEntity)entity;
			GlStateManager.pushMatrix();
			this.bindEntityTexture(e);
			GlStateManager.translate((float)x, (float)y, (float)z);
			GlStateManager.enableRescaleNormal();
			//   final float scale = this.scale;
			//   GlStateManager.scale(scale / 1.0f, scale / 1.0f, scale / 1.0f);
			final Tessellator instance = Tessellator.getInstance();
			final WorldRenderer worldRenderer = instance.getWorldRenderer();
			final float topHeight = 1f;
			final float bottomHeight = 0.375f;
			final float radius = 0.0625f;
			final float shaftMinU =- 0.5f;
			final float shaftMinV =- 0.5f;
			final float shaftMaxU =- 1.0f;
			final float shaftMaxV =- 1.0f;
			GlStateManager.rotate(e.rotationYaw, 0.0f, 1.0f, 0.0f);
			this.bindTexture(this.getEntityTexture(e));
			worldRenderer.startDrawingQuads();
			worldRenderer.setNormal(0.0f, 1.0f, 0.0f);

			// render shaft
			worldRenderer.addVertexWithUV(0.5f+radius, bottomHeight, 0.5f-radius, shaftMinU,shaftMinV);
			worldRenderer.addVertexWithUV(0.5f-radius, bottomHeight, 0.5f-radius, shaftMaxU,shaftMinV);
			worldRenderer.addVertexWithUV(0.5f-radius, topHeight   , 0.5f-radius, shaftMaxU,shaftMaxV);
			worldRenderer.addVertexWithUV(0.5f+radius, topHeight   , 0.5f-radius, shaftMinU,shaftMaxV);

			worldRenderer.addVertexWithUV(0.5f-radius, bottomHeight, 0.5f+radius, shaftMinU,shaftMinV);
			worldRenderer.addVertexWithUV(0.5f+radius, bottomHeight, 0.5f+radius, shaftMaxU,shaftMinV);
			worldRenderer.addVertexWithUV(0.5f+radius, topHeight   , 0.5f+radius, shaftMaxU,shaftMaxV);
			worldRenderer.addVertexWithUV(0.5f-radius, topHeight   , 0.5f+radius, shaftMinU,shaftMaxV);
			// render axel
			// TODO: implement
			// render spinning (or not) blades
			// TODO: implement
			
			instance.draw();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			super.doRender(e, x, y, z, scale, partialTick);
		}
	}


	@Override
	protected ResourceLocation getEntityTexture(final Entity e) {
		return texture;
	}
}
