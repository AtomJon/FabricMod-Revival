package lassevkp.revivals.block;

import lassevkp.revivals.Revivals;
import lassevkp.revivals.blockEntity.RitualTableEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block COOL_BLOCK = registerBlock("cool_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.ANCIENT_DEBRIS)));

    public static final Block RITUAL_TABLE = registerBlock("ritual_table",
            new RitualTableBlock(FabricBlockSettings.create()));
    public static final BlockEntity RITUAL_TABLE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Revivals.MOD_ID, "ritual_table"),
            FabricBlockEntityTypeBuilder.create(RitualTableEntity::new, RITUAL_TABLE).build(null));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(Revivals.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(Revivals.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        Revivals.LOGGER.info("Registering blocks for " + Revivals.MOD_ID);
    }


}
