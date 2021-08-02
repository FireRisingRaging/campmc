package me.firerising.campmc.items.loaders;

import com.jojodmo.itembridge.ItemBridge;
import me.firerising.campmc.items.ItemLoader;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class ItemBridgeLoader implements ItemLoader {
    @Override
    public String getName() {
        return "itembridge";
    }

    @Override
    public Function<ItemStack, Boolean> loadItem(String item) {
        return itemStack -> {
            ItemStack itemStackBridge = ItemBridge.getItemStack(item);
            if (itemStackBridge == null) {
                return false;
            }
            return itemStack.isSimilar(itemStackBridge);
        };
    }
}