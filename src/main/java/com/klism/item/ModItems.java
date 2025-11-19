package com.klism.item;

import com.klism.Takeabow;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems{

    public static final Item SHORTBOW = registerItem("shortbow",
            new ShortBowItem(new Item.Settings().maxDamage(384)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM,
                Identifier.of(Takeabow.MOD_ID, name), item);
    }

    public static void registerModItems(){
        Takeabow.LOGGER.info("Registering items for " + Takeabow.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(SHORTBOW);
        });
    }
}