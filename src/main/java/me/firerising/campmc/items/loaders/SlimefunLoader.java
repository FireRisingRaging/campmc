package me.firerising.campmc.items.loaders;

import me.firerising.campmc.items.ItemLoader;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class SlimefunLoader implements ItemLoader {
    @Override
    public String getName() {
        return "slimefun";
    }

    @Override
    public Function<ItemStack, Boolean> loadItem(String item) {
        return itemStack -> {
            SlimefunItem slimefunItem = SlimefunItem.getByID(item);
            if (slimefunItem == null) {
                return false;
            }

            return slimefunItem.isItem(itemStack);
        };
    }
}