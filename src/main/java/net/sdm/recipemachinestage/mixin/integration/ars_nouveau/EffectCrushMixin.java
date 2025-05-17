package net.sdm.recipemachinestage.mixin.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(value = EffectCrush.class, remap = false)
public class EffectCrushMixin {

    private static Player player = null;
    @Inject(method = "onResolve", at = @At("HEAD"))
    public void sdm$onResolve(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, CallbackInfo ci){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(RecipeRegistry.CRUSH_TYPE.get())) return;

        if(shooter instanceof Player player){
            EffectCrushMixin.player = player;
        }
    }
    @Inject(method = "onResolve", at = @At("RETURN"))
    public void sdm$onResolvePost(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, CallbackInfo ci){
        player = null;
    }

    @Inject(method = "crushItems", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void sdm$onResolveBlock(Level world, List<ItemEntity> itemEntities, int maxItemCrush, CallbackInfo ci, List recipes, CrushRecipe lastHit, int itemsCrushed){
        if(player != null) {
            Iterator d1 = recipes.iterator();
            while (d1.hasNext()) {
                CrushRecipe recipe = (CrushRecipe) d1.next();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData _player = PlayerHelper.getPlayerByGameProfile(player.getServer(), player.getGameProfile().getId());
                    if(_player != null) {
                        if(!_player.hasStage(recipeBlockType.stage))
                            d1.remove();
                    }
                }
            }
        }
    }
}
