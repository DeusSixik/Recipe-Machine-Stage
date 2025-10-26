package dev.behindthescenery.sdmrecipemachinestages.compat.agritech;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import dev.behindthescenery.sdmrecipemachinestages.RMSApi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenCodeType.Name("mods.rms.compat.RMSAgriTech")
public class RMSAgriTech {

    @ZenCodeType.Method
    public static void addPlanter(Object[] items, String stage) {
        addPlanter(List.of(items), stage);
    }

    public static void addPlanter(List<Object> items, String stage) {
        RMSApi.register(AgriPlanter.class, getItems(items), stage, RMSApi.RecipeBlockClass.Input);
    }

    @ZenCodeType.Method
    public static void addTreePlanter(Object[] items, String stage) {
        addPlanter(List.of(items), stage);
    }

    public static void addTreePlanter(List<Object> items, String stage) {
        RMSApi.register(AgriTree.class, getItems(items), stage, RMSApi.RecipeBlockClass.Input);
    }

    public static List<ItemStack> getItems(final List<Object> items) {
        final List<ItemStack> list = new ArrayList<>();
        for (final Object element : items) {
            if(element instanceof ItemStack stack)
                list.add(stack);
            else if(element instanceof Item item)
                list.add(item.getDefaultInstance());
        }
        return list;
    }

    public static class AgriTree {}

    public static class AgriPlanter {}
}
