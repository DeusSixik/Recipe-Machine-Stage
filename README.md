# Recipe Machine Stage
This mod provides the ability to block recipes for mechanisms, similar to how it is implemented in Recipe Stages for the workbench.

## [CurseForge](https://www.curseforge.com/minecraft/mc-mods/recipe-machine-stages)


## Patches or fixes
- **AbstractFurnaceBlockEntity** By default, recipes in ovens cannot produce recipes where there is more than 1 item in the entry slot.

## Plans
- [x] Improve API performance
- [x] Support Fabric/Neoforge
- [x] Support KubeJS
- [x] Support CraftTweaker
- [x] Support JEI
- [ ] Support REI
- [ ] Support EMI
- [ ] Transfer all possible mod support
- [ ] Add the missing parts of the code. For example, support for JEI/REI/EMI mods
- [ ] Support for adding a recipe for mods

## How to use it ?
The mod supports both KubeJS and CraftTweaker

> [!WARNING] 
> **Important: Registration order matters!** <br>
> If a recipe is already restricted individually (via `addRecipe` or `addRecipes`), subsequent bulk restrictions (`addRecipeByMod`, `addRecipeByMachine`) **will ignore it**. <br>
> This allows you to create exceptions: for instance, tying a specific recipe to an early stage, while locking the rest of the machine behind a later stage.

### KubeJS
```javascript
RMSEvents.register(event => {
    // --- Individual Restrictions ---
    // event.addRecipe(recipeType: string, recipe_id: string, stage: string)
    event.addRecipe('create:milling', 'create:milling/fern', 'two')
    
    // event.addRecipes(recipeType: string, recipe_ids: string[], stage: string)
    event.addRecipes('minecraft:smelting', ['minecraft:stone', 'minecraft:iron_ingot'], 'one')
    
    // --- Bulk Restrictions ---
    // event.addRecipeByMod(recipeType: string, modId: string, stage: string)
    event.addRecipeByMod('minecraft:smelting', 'create', 'create')
    
    // event.addRecipeByMods(recipeType: string, modIds: string[], stage: string)
    event.addRecipeByMods('minecraft:smelting', ['create', 'minecraft'], 'one')
    
    // event.addRecipeByMachine(recipeType: string, stage: string)
    event.addRecipeByMachine('minecraft:smelting', 'one')


    // ==========================================
    // PRIORITY EXAMPLE (Creating Exceptions)
    // ==========================================
    
    // 1. First, tie a specific battery recipe to the 'battery_stage' stage.
    event.addRecipe("modern_industrialization:assembler", "modern_industrialization:assembler_generated/electric_age/battery/cadmium_battery", "battery_stage");
    
    // 2. Then, lock the ENTIRE assembler behind the 'assembler_recipes_stage' stage.
    // Result: The assembler requires 'assembler_recipes_stage', but the cadmium battery recipe remains accessible at the 'test' stage.
    event.addRecipeByMachine("modern_industrialization:assembler", "assembler_recipes_stage");
})
```

### CraftTweaker
In `CraftTweaker`, the queue processing logic is identical: specific recipes must be declared before bulk ones to create exceptions.

```ts
import mods.rms.RMS;

// --- Method Signatures ---
// Individual:
RMS.addRecipe(recipeType as string, recipeID as string, stage as string)
RMS.addRecipe(recipeType as string, recipeIDs as string[], stage as string)
RMS.addRecipe(recipeType as RecypeType, recipeID as string, stage as string)
RMS.addRecipe(recipeType as RecypeType, recipeIDs as string[], stage as string)

// Bulk:
RMS.addRecipeByMod(recipeType as string, modId as string, stage as string)
RMS.addRecipeByMod(recipeType as string, modIds as string[], stage as string)
RMS.addRecipeByMod(recipeType as RecypeType, modId as string, stage as string)
RMS.addRecipeByMod(recipeType as RecypeType, modIds as string[], stage as string)
RMS.addRecipeByMachine(recipeType as RecypeType, stage as string)

// --- Priority Example ---
RMS.addRecipe("modern_industrialization:assembler", "modern_industrialization:assembler_generated/electric_age/battery/cadmium_battery", "battery_stage");
RMS.addRecipeByMachine("modern_industrialization:assembler", "assembler_recipes_stage");
```