package leon_lp9.compactcrates;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayName(String displayName) {
        itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder setLocalizedName(String localizedName) {
        itemMeta.setLocalizedName(localizedName);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        itemStack.setItemMeta(itemMeta);
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setLeatherArmorColorRGB(int r, int g, int b) {
        if (itemStack.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
            leatherArmorMeta.setColor(Color.fromRGB(r, g, b));
            itemMeta = leatherArmorMeta;
        }
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        if (itemStack.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
            leatherArmorMeta.setColor(color);
            itemMeta = leatherArmorMeta;
        }
        return this;
    }

    public ItemBuilder addBannerPattern(PatternType patternType, DyeColor color) {
        if (itemStack.getType().name().contains("BANNER")) {
            BannerMeta bannerMeta = (BannerMeta) itemMeta;
            bannerMeta.addPattern(new Pattern(color, patternType));
            itemMeta = bannerMeta;
        }
        return this;
    }

    public ItemBuilder addBookPage(String... page) {
        if (itemStack.getType().name().contains("BOOK")) {
            ItemMeta bookMeta = itemMeta;
            bookMeta.setLore(Arrays.asList(page));
            itemMeta = bookMeta;
        }
        return this;
    }

    public ItemBuilder addSkullOwner(String owner) {
        if (itemStack.getType().name().contains("SKULL")) {
            ItemMeta skullMeta = itemMeta;
            skullMeta.setDisplayName(owner);
            itemMeta = skullMeta;
        }
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}