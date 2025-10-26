package dev.behindthescenery.sdmrecipemachinestages.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class RecipeBlockInput extends AbstractRecipeBlock {

    private final List<ItemStack> input_items;

    public RecipeBlockInput(Class<?> blockProduction, String stageId, List<ItemStack> input_items) {
        super(blockProduction, stageId);
        this.input_items = input_items;
    }

    public boolean contains(ItemStack itemStack) {
        for (ItemStack inputItem : input_items) {
            if(!(ItemStack.matches(inputItem, itemStack) || ItemStack.isSameItemSameComponents(inputItem, itemStack)))
                return false;
        }

        return true;
    }

    public boolean contains(Item item) {
        for (ItemStack inputItem : input_items) {
            if(!(inputItem.is(item)))
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "RecipeBlockInput{" +
                "input_items=" + input_items.stream().map(s -> s.getCount() + "x " + BuiltInRegistries.ITEM.getKey(s.getItem())).toList() +
                ", blockProduction=" + blockProduction.getName() +
                ", stageId='" + stageId + '\'' +
                '}';
    }
}
