package me.firerising.campmc.items.loaders;

import me.firerising.campmc.items.ItemLoader;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class ItemsAdderLoader implements ItemLoader {
    @Override
    public String getName() {
        return "itemsadder";
    }

    @Override
    public Function<ItemStack, Boolean> loadItem(String item) {
        return itemStack ->{
            CustomStack customStack = CustomStack.getInstance(item);
            if (customStack == null) {
                return false;
            }
            return itemStack.isSimilar(customStack.getItemStack());
        };
    }
}