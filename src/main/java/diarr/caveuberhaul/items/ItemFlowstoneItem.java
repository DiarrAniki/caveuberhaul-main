package diarr.caveuberhaul.items;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.blocks.BlockStalagmite;
import diarr.caveuberhaul.blocks.BlockStalagtite;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemFlowstoneItem extends Item {
    public ItemFlowstoneItem(int i) {
        super(i);
    }
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
        //0 = unten, 1= oben
        if(world.getBlockId(blockX,blockY,blockZ)== CaveUberhaul.flowstone.id||world.getBlockId(blockX,blockY,blockZ)==CaveUberhaul.flowstonePillar.id)
        {
            if(side == Side.TOP &&CaveUberhaul.flowstoneStalagmite1.canPlaceBlockAt(world,blockX,blockY+1,blockZ))
            {
                world.setBlockWithNotify(blockX,blockY+1,blockZ,CaveUberhaul.flowstoneStalagmite1.id);
                world.playBlockSoundEffect(player, blockX,blockY+1,blockZ,Block.stone, EnumBlockSoundEffectType.PLACE);
                stack.consumeItem(player);
                return true;
            }
            else if(side == Side.BOTTOM &CaveUberhaul.flowstoneStalagtite1.canPlaceBlockAt(world,blockX,blockY-1,blockZ))
            {
                world.setBlockWithNotify(blockX,blockY-1,blockZ,CaveUberhaul.flowstoneStalagtite1.id);
                world.playBlockSoundEffect(player, blockX,blockY-1,blockZ,Block.stone, EnumBlockSoundEffectType.PLACE);
                stack.consumeItem(player);
                return true;
            }
        }
        else if(Block.getBlock(world.getBlockId(blockX,blockY,blockZ)) instanceof BlockStalagmite &&CaveUberhaul.flowstoneStalagmite1.canPlaceBlockAt(world,blockX,blockY+1,blockZ))
        {
            world.setBlockWithNotify(blockX,blockY+1,blockZ,CaveUberhaul.flowstoneStalagmite1.id);
            world.playBlockSoundEffect(player, blockX,blockY+1,blockZ,Block.stone, EnumBlockSoundEffectType.PLACE);
            stack.consumeItem(player);
            return true;
        }
        else if(Block.getBlock(world.getBlockId(blockX,blockY,blockZ)) instanceof BlockStalagtite &&CaveUberhaul.flowstoneStalagtite1.canPlaceBlockAt(world,blockX,blockY-1,blockZ))
        {
            world.setBlockWithNotify(blockX,blockY-1,blockZ,CaveUberhaul.flowstoneStalagtite1.id);
            world.playBlockSoundEffect(player, blockX,blockY-1,blockZ,Block.stone, EnumBlockSoundEffectType.PLACE);
            stack.consumeItem(player);
            return true;
        }
        return super.onItemUse(stack, player, world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
    }

}
