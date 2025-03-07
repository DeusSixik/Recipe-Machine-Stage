package net.sdm.recipemachinestage.mixin.integration.tinkers_construct;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.MeltingModule;

import java.util.List;
import java.util.Optional;

@Mixin(MeltingModule.class)
public class MeltingModuleMixin {

    @Unique
    private Player thisEntity;

    @Redirect(method = "meltItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <C extends Container, T extends Recipe<C>> Optional<T> sdm$meltItem(RecipeManager instance, RecipeType<T> type, C container, Level level) {
        Optional<T> recipeOptional = instance.getRecipeFor(type,container,level);
        if(thisEntity == null) return recipeOptional;

        if(recipeOptional.isPresent()) {
            T recipe = recipeOptional.get();

            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getServer(), thisEntity.getGameProfile().getId());
                if (player != null) {
                    if (!player.hasStage(recipeBlockType.stage)) {
                        return Optional.empty();
                    }
                }
            }
        }
        return recipeOptional;
    }

    @Inject(method = "processLoot", at = @At("HEAD"), remap = false)
    private void sdm$processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context, CallbackInfo ci) {
        Entity enity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if(enity == null) return;
        if(enity instanceof Player player)
            thisEntity = player;
        else thisEntity = null;
    }
}
