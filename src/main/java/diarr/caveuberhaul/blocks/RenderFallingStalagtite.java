package diarr.caveuberhaul.blocks;

import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TextureFX;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.Side;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Properties;

public class RenderFallingStalagtite extends EntityRenderer<Entity> {
    private RenderBlocks d = new RenderBlocks();

    public RenderFallingStalagtite() {
        this.shadowSize = 0.5F;
    }

    public void doRenderFallingStalagtite(EntityFallingStalagtite entityFallingStalagtite, double d, double d1, double d2, float f, float f1) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        Block block = Block.blocksList[entityFallingStalagtite.blockID];
        //World world = entityFallingStalagtite.getWorld();
        this.loadTexture("/terrain.png");
        int j = block.getBlockTextureFromSideAndMetadata(Side.BOTTOM, 0);
        Tessellator tessellator = Tessellator.instance;
        int k = j % Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        int l = j / Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        double d3 = (double)((float)k / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES));
        double d4 = (double)(((float)k + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES));
        double d5 = (double)((float)l / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES));
        double d6 = (double)(((float)l + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES));
        double d7 = -0.5;
        double d8 = 0.5;
        double d9 = -0.5;
        double d10 = 0.5;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(d7, + 0.5, d9, d3, d5);
        tessellator.addVertexWithUV(d7, - 0.5, d9, d3, d6);
        tessellator.addVertexWithUV(d8, - 0.5, d10, d4, d6);
        tessellator.addVertexWithUV(d8, + 0.5, d10, d4, d5);
        tessellator.addVertexWithUV(d8, + 0.5, d10, d3, d5);
        tessellator.addVertexWithUV(d8, - 0.5, d10, d3, d6);
        tessellator.addVertexWithUV(d7, - 0.5, d9, d4, d6);
        tessellator.addVertexWithUV(d7, + 0.5, d9, d4, d5);
        tessellator.addVertexWithUV(d7, + 0.5, d10, d3, d5);
        tessellator.addVertexWithUV(d7, - 0.5, d10, d3, d6);
        tessellator.addVertexWithUV(d8, - 0.5, d9, d4, d6);
        tessellator.addVertexWithUV(d8, + 0.5, d9, d4, d5);
        tessellator.addVertexWithUV(d8, + 0.5, d9, d3, d5);
        tessellator.addVertexWithUV(d8, - 0.5, d9, d3, d6);
        tessellator.addVertexWithUV(d7, - 0.5, d10, d4, d6);
        tessellator.addVertexWithUV(d7, + 0.5, d10, d4, d5);
        tessellator.draw();
        GL11.glPopMatrix();
    }


    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        this.doRenderFallingStalagtite((EntityFallingStalagtite) entity, d, d1, d2, f, f1);
    }
}