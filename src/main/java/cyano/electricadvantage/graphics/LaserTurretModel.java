package cyano.electricadvantage.graphics;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class LaserTurretModel extends ModelBase{
	
	public ModelRenderer barrel;
	public ModelRenderer body;
	
	public LaserTurretModel(){
		this(0f);
	}
	public LaserTurretModel(float padding){
		barrel = new ModelRenderer(this, 0, 0);
		barrel.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 8, padding);
		barrel.setRotationPoint(0.0F, 0.0F, 0.0F);

		body = new ModelRenderer(this, 0, 0);
		body.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, padding);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		
	}
	
	@Override
	public void render(Entity e, float time, float swing, float f3, float rY, float rX, float scale)
    {
        this.setRotationAngles(time, swing, f3, rY, rX, scale, e);
        this.barrel.render(scale);
        this.body.render(scale);
    }
	
	public void setRotationAngles(float time, float swing, float f3, float rY, float rX, float scale, Entity e)
    {
        this.barrel.rotateAngleY = rY / (180F / (float)Math.PI);
        this.barrel.rotateAngleX = rX / (180F / (float)Math.PI);
        
        this.body.rotateAngleY = rY / (180F / (float)Math.PI);
        this.body.rotateAngleX = 0;
    }

}
