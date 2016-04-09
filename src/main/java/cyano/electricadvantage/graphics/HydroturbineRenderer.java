package cyano.electricadvantage.graphics;

import cyano.electricadvantage.ElectricAdvantage;
import cyano.electricadvantage.entities.HydroturbineEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HydroturbineRenderer extends Render{
	private static final ResourceLocation texture = new ResourceLocation(ElectricAdvantage.MODID+":textures/entity/hydroturbine.png");

	private static final float DEGREES_TO_RADIANS = (float)(Math.PI / 180);

	public HydroturbineRenderer(final RenderManager rm) {
		super(rm);
	}

	@Override
	public void doRender(final Entity entity, final double x, final double y, final double z, final float scale, final float partialTick) {
		if(entity instanceof HydroturbineEntity){
			final float pixel = 1.0f / 16;
			HydroturbineEntity e = (HydroturbineEntity)entity;
			GlStateManager.pushMatrix();
			this.bindEntityTexture(e);
			GlStateManager.translate((float)x, (float)y, (float)z);
			GlStateManager.enableRescaleNormal();
			//   final float scale = this.scale;
			//   GlStateManager.scale(scale / 1.0f, scale / 1.0f, scale / 1.0f);
			final Tessellator instance = Tessellator.getInstance();
			final VertexBuffer worldRenderer = instance.getBuffer();
			final float topHeight = 0.5f;
			final float bottomHeight = 0.375f-0.5f;
			final float radius = pixel;
			final float shaftMinU = 0.5f;
			final float shaftMinV = 0.5f;
			final float shaftMaxU = 0.5f+2*pixel;
			final float shaftMaxV = 1.0f;
			
			GlStateManager.translate(0f, 0.5f, 0f);
			GlStateManager.rotate(e.rotationYaw, 0.0f, 1.0f, 0.0f);
			this.bindTexture(this.getEntityTexture(e));
			worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

			// render shaft
			worldRenderer.pos(+radius, bottomHeight, -radius).tex(shaftMinU,shaftMinV).endVertex();
			worldRenderer.pos(-radius, bottomHeight, -radius).tex(shaftMaxU,shaftMinV).endVertex();
			worldRenderer.pos(-radius, topHeight   , -radius).tex(shaftMaxU,shaftMaxV).endVertex();
			worldRenderer.pos(+radius, topHeight   , -radius).tex(shaftMinU,shaftMaxV).endVertex();

			worldRenderer.pos(-radius, bottomHeight, +radius).tex(shaftMinU,shaftMinV).endVertex();
			worldRenderer.pos(+radius, bottomHeight, +radius).tex(shaftMaxU,shaftMinV).endVertex();
			worldRenderer.pos(+radius, topHeight   , +radius).tex(shaftMaxU,shaftMaxV).endVertex();
			worldRenderer.pos(-radius, topHeight   , +radius).tex(shaftMinU,shaftMaxV).endVertex();

			worldRenderer.pos(-radius, bottomHeight, -radius).tex(shaftMinU,shaftMinV).endVertex();
			worldRenderer.pos(-radius, bottomHeight, +radius).tex(shaftMaxU,shaftMinV).endVertex();
			worldRenderer.pos(-radius, topHeight   , +radius).tex(shaftMaxU,shaftMaxV).endVertex();
			worldRenderer.pos(-radius, topHeight   , -radius).tex(shaftMinU,shaftMaxV).endVertex();

			worldRenderer.pos(+radius, bottomHeight, +radius).tex(shaftMinU,shaftMinV).endVertex();
			worldRenderer.pos(+radius, bottomHeight, -radius).tex(shaftMaxU,shaftMinV).endVertex();
			worldRenderer.pos(+radius, topHeight   , -radius).tex(shaftMaxU,shaftMaxV).endVertex();
			worldRenderer.pos(+radius, topHeight   , +radius).tex(shaftMinU,shaftMaxV).endVertex();

			worldRenderer.pos(-radius, bottomHeight, +radius).tex(shaftMinU,shaftMinV).endVertex();
			worldRenderer.pos(-radius, bottomHeight, -radius).tex(shaftMinU+2*pixel,shaftMinV).endVertex();
			worldRenderer.pos(+radius, bottomHeight, -radius).tex(shaftMinU+2*pixel,shaftMaxV).endVertex();
			worldRenderer.pos(+radius, bottomHeight, +radius).tex(shaftMinU,shaftMaxV).endVertex();
			
			// render axel
			final float axelLength = 4 * pixel;
			final float axelRadius = 1 * pixel;
			final float axelMinU = 0;
			final float axelMaxU = 4 * pixel;
			final float axelMaxU2 = 2 * pixel;
			final float axelMinV = 0.5f;
			final float axelMaxV = 0.5f + 2 * pixel;
			final float axelMaxV2 = 0.5f + 4 * pixel;
			
			worldRenderer.pos(+axelRadius, -axelRadius, -pixel           ).tex(axelMinU,axelMinV).endVertex();
			worldRenderer.pos(+axelRadius, -axelRadius, -pixel-axelLength).tex(axelMaxU,axelMinV).endVertex();
			worldRenderer.pos(+axelRadius, +axelRadius, -pixel-axelLength).tex(axelMaxU,axelMaxV).endVertex();
			worldRenderer.pos(+axelRadius, +axelRadius, -pixel           ).tex(axelMinU,axelMaxV).endVertex();
			
			worldRenderer.pos(-axelRadius, +axelRadius, -pixel           ).tex(axelMinU,axelMinV).endVertex();
			worldRenderer.pos(-axelRadius, +axelRadius, -pixel-axelLength).tex(axelMaxU,axelMinV).endVertex();
			worldRenderer.pos(-axelRadius, -axelRadius, -pixel-axelLength).tex(axelMaxU,axelMaxV).endVertex();
			worldRenderer.pos(-axelRadius, -axelRadius, -pixel           ).tex(axelMinU,axelMaxV).endVertex();
			
			worldRenderer.pos(+axelRadius, +axelRadius, -pixel           ).tex(axelMinU,axelMaxV2).endVertex();
			worldRenderer.pos(+axelRadius, +axelRadius, -pixel-axelLength).tex(axelMinU,axelMinV).endVertex();
			worldRenderer.pos(-axelRadius, +axelRadius, -pixel-axelLength).tex(axelMaxU2,axelMinV).endVertex();
			worldRenderer.pos(-axelRadius, +axelRadius, -pixel           ).tex(axelMaxU2,axelMaxV2).endVertex();
			
			worldRenderer.pos(-axelRadius, -axelRadius, -pixel           ).tex(axelMinU,axelMaxV2).endVertex();
			worldRenderer.pos(-axelRadius, -axelRadius, -pixel-axelLength).tex(axelMinU,axelMinV).endVertex();
			worldRenderer.pos(+axelRadius, -axelRadius, -pixel-axelLength).tex(axelMaxU2,axelMinV).endVertex();
			worldRenderer.pos(+axelRadius, -axelRadius, -pixel           ).tex(axelMaxU2,axelMaxV2).endVertex();
			
			worldRenderer.pos(-axelRadius, -axelRadius, -pixel-axelLength).tex(axelMinU,axelMaxV2).endVertex();
			worldRenderer.pos(-axelRadius, +axelRadius, -pixel-axelLength).tex(axelMinU,axelMinV).endVertex();
			worldRenderer.pos(+axelRadius, +axelRadius, -pixel-axelLength).tex(axelMaxU2,axelMinV).endVertex();
			worldRenderer.pos(+axelRadius, -axelRadius, -pixel-axelLength).tex(axelMaxU2,axelMaxV2).endVertex();
			
			// render spinning (or not) blades
			float spin1, spin2, spin3;
			if(e.isSpinning){
				spin1 = e.rotation + HydroturbineEntity.DEGREES_PER_TICK * partialTick;
				spin2 = spin1 + 120;
				spin3 = spin1 - 120;
			} else {
				spin1 = e.rotation;
				spin2 = spin1 + 120;
				spin3 = spin1 - 120;
			}
			final float bladeWidth = 3*pixel;
			final float bladeLength = 6*pixel;
			final float bladeOffset = 1*pixel;
			final float bladeMinU = 0.0f;
			final float bladeMinV = 0.0f;
			final float bladeMaxU = bladeWidth;
			final float bladeMaxV = bladeLength;
			final float depthDelta = pixel;

			final float rotaryWidth = DEGREES_TO_RADIANS*45;
			final float t1 = DEGREES_TO_RADIANS*spin1, t2 = DEGREES_TO_RADIANS*spin2, t3 = DEGREES_TO_RADIANS*spin3;
			final float sin1a = MathHelper.sin(t1);
			final float sin1b = MathHelper.sin(t1+rotaryWidth);
			final float cos1a = MathHelper.cos(t1);
			final float cos1b = MathHelper.cos(t1+rotaryWidth);
			final float sin2a = MathHelper.sin(t2);
			final float sin2b = MathHelper.sin(t2+rotaryWidth);
			final float cos2a = MathHelper.cos(t2);
			final float cos2b = MathHelper.cos(t2+rotaryWidth);
			final float sin3a = MathHelper.sin(t3);
			final float sin3b = MathHelper.sin(t3+rotaryWidth);
			final float cos3a = MathHelper.cos(t3);
			final float cos3b = MathHelper.cos(t3+rotaryWidth);

			worldRenderer.pos(cos1b*bladeOffset, sin1b*bladeOffset, depthDelta-axelLength).tex(bladeMinU,bladeMinV).endVertex(); // inner, leading
			worldRenderer.pos(cos1b*bladeLength, sin1b*bladeLength, depthDelta-axelLength).tex(bladeMinU,bladeMaxV).endVertex(); // outer, leading
			worldRenderer.pos(cos1a*bladeLength, sin1a*bladeLength,           -axelLength).tex(bladeMaxU,bladeMaxV).endVertex(); // outer, trailing
			worldRenderer.pos(cos1a*bladeOffset, sin1a*bladeOffset,           -axelLength).tex(bladeMaxU,bladeMinV).endVertex(); // inner, trailing

			worldRenderer.pos(cos1a*bladeOffset, sin1a*bladeOffset,           -axelLength).tex(bladeMaxU,bladeMinV).endVertex(); // inner, trailing
			worldRenderer.pos(cos1a*bladeLength, sin1a*bladeLength,           -axelLength).tex(bladeMaxU,bladeMaxV).endVertex(); // outer, trailing
			worldRenderer.pos(cos1b*bladeLength, sin1b*bladeLength, depthDelta-axelLength).tex(bladeMinU,bladeMaxV).endVertex(); // outer, leading
			worldRenderer.pos(cos1b*bladeOffset, sin1b*bladeOffset, depthDelta-axelLength).tex(bladeMinU,bladeMinV).endVertex(); // inner, leading


			worldRenderer.pos(cos2b*bladeOffset, sin2b*bladeOffset, depthDelta-axelLength).tex(bladeMinU,bladeMinV).endVertex(); // inner, leading
			worldRenderer.pos(cos2b*bladeLength, sin2b*bladeLength, depthDelta-axelLength).tex(bladeMinU,bladeMaxV).endVertex(); // outer, leading
			worldRenderer.pos(cos2a*bladeLength, sin2a*bladeLength,           -axelLength).tex(bladeMaxU,bladeMaxV).endVertex(); // outer, trailing
			worldRenderer.pos(cos2a*bladeOffset, sin2a*bladeOffset,           -axelLength).tex(bladeMaxU,bladeMinV).endVertex(); // inner, trailing

			worldRenderer.pos(cos2a*bladeOffset, sin2a*bladeOffset,           -axelLength).tex(bladeMaxU,bladeMinV).endVertex(); // inner, trailing
			worldRenderer.pos(cos2a*bladeLength, sin2a*bladeLength,           -axelLength).tex(bladeMaxU,bladeMaxV).endVertex(); // outer, trailing
			worldRenderer.pos(cos2b*bladeLength, sin2b*bladeLength, depthDelta-axelLength).tex(bladeMinU,bladeMaxV).endVertex(); // outer, leading
			worldRenderer.pos(cos2b*bladeOffset, sin2b*bladeOffset, depthDelta-axelLength).tex(bladeMinU,bladeMinV).endVertex(); // inner, leading


			worldRenderer.pos(cos3b*bladeOffset, sin3b*bladeOffset, depthDelta-axelLength).tex(bladeMinU,bladeMinV).endVertex(); // inner, leading
			worldRenderer.pos(cos3b*bladeLength, sin3b*bladeLength, depthDelta-axelLength).tex(bladeMinU,bladeMaxV).endVertex(); // outer, leading
			worldRenderer.pos(cos3a*bladeLength, sin3a*bladeLength,           -axelLength).tex(bladeMaxU,bladeMaxV).endVertex(); // outer, trailing
			worldRenderer.pos(cos3a*bladeOffset, sin3a*bladeOffset,           -axelLength).tex(bladeMaxU,bladeMinV).endVertex(); // inner, trailing

			worldRenderer.pos(cos3a*bladeOffset, sin3a*bladeOffset,           -axelLength).tex(bladeMaxU,bladeMinV).endVertex(); // inner, trailing
			worldRenderer.pos(cos3a*bladeLength, sin3a*bladeLength,           -axelLength).tex(bladeMaxU,bladeMaxV).endVertex(); // outer, trailing
			worldRenderer.pos(cos3b*bladeLength, sin3b*bladeLength, depthDelta-axelLength).tex(bladeMinU,bladeMaxV).endVertex(); // outer, leading
			worldRenderer.pos(cos3b*bladeOffset, sin3b*bladeOffset, depthDelta-axelLength).tex(bladeMinU,bladeMinV).endVertex(); // inner, leading
			
			
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
