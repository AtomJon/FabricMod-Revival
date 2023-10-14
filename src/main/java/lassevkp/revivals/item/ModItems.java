package lassevkp.revivals.item;

import lassevkp.revivals.Revivals;
import lassevkp.revivals.block.ModBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {


    public static final Item RESURRECTION_TOTEM = registerItem("resurrection_totem", new Item(new FabricItemSettings().maxCount(1)));

    public static final ItemGroup MOD_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Revivals.MOD_ID, "revivals"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.revivals"))
                    .icon(() -> new ItemStack(ModItems.RESURRECTION_TOTEM))
                    .entries((displayContext, entries) -> {
                        entries.add(RESURRECTION_TOTEM);
                        entries.add(ModBlocks.RITUAL_TABLE);
                    }).build());


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Revivals.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Revivals.LOGGER.info("Registering items for " + Revivals.MOD_ID);
    }



}
