package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.apotheosis;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.socket.gem.cutting.GemCuttingMenu;
import dev.shadowsoffire.apotheosis.socket.gem.cutting.GemCuttingRecipe;
import dev.shadowsoffire.placebo.menu.PlaceboContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(GemCuttingMenu.class)
public abstract class GemCuttingMenuMixin extends PlaceboContainerMenu {

    @Unique
    private final List<RecipeHolder<GemCuttingRecipe>> bts$cached_recipes = new ArrayList<>();

    @Shadow
    @Final
    protected Player player;

    protected GemCuttingMenuMixin(MenuType<?> type, int id, Inventory pInv) {
        super(type, id, pInv);
    }


    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    public void bts$clickMenuButton(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        if (id != 0) return;
        bts$cached_recipes.clear();
    }

    @Redirect(method = {"isValidLeft", "isValidRight", "isValidTop", "isValidBase", "clickMenuButton"}, at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apotheosis/socket/gem/cutting/GemCuttingMenu;getRecipes(Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    private List<RecipeHolder<GemCuttingRecipe>> bts$redirect$getRecipes(Level level) {
        return bts$getRecipes(level, player, bts$cached_recipes);
    }

    @Unique
    private static List<RecipeHolder<GemCuttingRecipe>> bts$getRecipes(Level level, Player player, List<RecipeHolder<GemCuttingRecipe>> cached_recipes) {
        if(cached_recipes.isEmpty()) {
            if (RMSUtils.hasRestrictionsForType(Apoth.RecipeTypes.GEM_CUTTING)) {
                cached_recipes.addAll(RMSUtils.filterRecipes(level.getRecipeManager().getAllRecipesFor(Apoth.RecipeTypes.GEM_CUTTING), player));
            } else cached_recipes.addAll(level.getRecipeManager().getAllRecipesFor(Apoth.RecipeTypes.GEM_CUTTING));
        }

        return cached_recipes;
    }
}
