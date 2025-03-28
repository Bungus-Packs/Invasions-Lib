package bunguspacks.invasionslib.item;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup INVASION_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(InvasionsLib.MOD_ID, "invasion_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.invasions"))
                    .icon(() -> new ItemStack(ModBlocks.INVASION_BEACON)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.INVASION_BEACON);
                    }).build());

    public static void registerItemGroups() {

    }
}
