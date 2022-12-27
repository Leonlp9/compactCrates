package leon_lp9.compactcrates;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemChecker {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemChecker(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public boolean isDisplayName(String displayName) {
        return itemMeta.getDisplayName().equals(displayName);
    }

    public boolean isLocalizedName(String localizedName) {
        return itemMeta.getLocalizedName().equals(localizedName);
    }

    public boolean isLore(String... lore) {
        return itemMeta.getLore().equals(lore);
    }

    public boolean isUnbreakable(boolean unbreakable) {
        return itemMeta.isUnbreakable() == unbreakable;
    }

    public boolean isDurability(short durability) {
        return itemStack.getDurability() == durability;
    }

    public boolean hasEnchantment(Enchantment enchantment, int level) {
        return itemMeta.hasEnchant(enchantment) && itemMeta.getEnchantLevel(enchantment) == level;
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return itemMeta.hasEnchant(enchantment);
    }

    //Contains DisplayName
    public boolean isDisplayNameContains(String displayName) {
        return itemMeta.getDisplayName().contains(displayName);
    }

    //Contains LocalizedName
    public boolean isLocalizedNameContains(String localizedName) {
        return itemMeta.getLocalizedName().contains(localizedName);
    }

    //Contains Lore
    public boolean isLoreContains(String... lore) {
        return itemMeta.getLore().contains(lore);
    }

}