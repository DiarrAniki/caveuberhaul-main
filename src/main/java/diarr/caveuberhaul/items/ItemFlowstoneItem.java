package diarr.caveuberhaul.items;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.blocks.BlockStalagmite;
import diarr.caveuberhaul.blocks.BlockStalagtite;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.world.World;

import java.util.Random;

public class ItemFlowstoneItem extends Item {
    Random rand = new Random();
    public ItemFlowstoneItem(int i) {
        super(i);
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        Minecraft mc = Minecraft.getMinecraft(Minecraft.class);

        if (mc.objectMouseOver != null && mc.objectMouseOver.entity == null) {
            int x = mc.objectMouseOver.x;
            int y = mc.objectMouseOver.y;
            int z = mc.objectMouseOver.z;

            int sideHit = mc.objectMouseOver.side.getId();

            //0 = unten, 1= oben
            if(world.getBlockId(x,y,z)== CaveUberhaul.flowstone.id||world.getBlockId(x,y,z)==CaveUberhaul.flowstonePillar.id)
            {
                if(sideHit == 1&&CaveUberhaul.flowstoneStalagmite1.canPlaceBlockAt(world,x,y+1,z))
                {
                    world.setBlockWithNotify(x,y+1,z,CaveUberhaul.flowstoneStalagmite1.id);
                    world.playBlockSoundEffect(x,y+1,z,Block.stone, EnumBlockSoundEffectType.PLACE);
                    if(entityplayer.getGamemode() == Gamemode.survival) {
                        itemstack.stackSize--;
                    }
                    entityplayer.swingItem();
                    return itemstack;
                }
                else if(sideHit == 0&&CaveUberhaul.flowstoneStalagtite1.canPlaceBlockAt(world,x,y-1,z))
                {
                    world.setBlockWithNotify(x,y-1,z,CaveUberhaul.flowstoneStalagtite1.id);
                    world.playBlockSoundEffect(x,y-1,z,Block.stone, EnumBlockSoundEffectType.PLACE);
                    if(entityplayer.getGamemode() == Gamemode.survival) {
                        itemstack.stackSize--;
                    }
                    entityplayer.swingItem();
                    return itemstack;
                }
            }
            else if(Block.getBlock(world.getBlockId(x,y,z)) instanceof BlockStalagmite &&CaveUberhaul.flowstoneStalagmite1.canPlaceBlockAt(world,x,y+1,z))
            {
                world.setBlockWithNotify(x,y+1,z,CaveUberhaul.flowstoneStalagmite1.id);
                world.playBlockSoundEffect(x,y+1,z,Block.stone, EnumBlockSoundEffectType.PLACE);
                if(entityplayer.getGamemode() == Gamemode.survival) {
                    itemstack.stackSize--;
                }
                entityplayer.swingItem();
                return itemstack;
            }
            else if(Block.getBlock(world.getBlockId(x,y,z)) instanceof BlockStalagtite &&CaveUberhaul.flowstoneStalagtite1.canPlaceBlockAt(world,x,y-1,z))
            {
                world.setBlockWithNotify(x,y-1,z,CaveUberhaul.flowstoneStalagtite1.id);
                world.playBlockSoundEffect(x,y-1,z,Block.stone, EnumBlockSoundEffectType.PLACE);
                if(entityplayer.getGamemode() == Gamemode.survival) {
                    itemstack.stackSize--;
                }
                entityplayer.swingItem();
                return itemstack;
            }
        }
        entityplayer.swingItem();
        return itemstack;
    }
}
