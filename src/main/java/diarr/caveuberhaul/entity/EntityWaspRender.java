package diarr.caveuberhaul.entity;

import net.minecraft.client.render.entity.LivingRenderer;
import net.minecraft.client.render.model.ModelBase;
import org.useless.dragonfly.model.entity.BenchEntityModel;

public class EntityWaspRender extends LivingRenderer<EntityWasp> {
    protected BenchEntityModel modelBench;
    public EntityWaspRender(BenchEntityModel  model, float shadowSize) {
        super(model, shadowSize);
        this.modelBench = model;
    }
}
