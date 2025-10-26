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

### KubeJs
```js
//RMS.addRecipe(String recipeType, String recipe_id, String stage)
RMS.addRecipe('create:milling', 'create:milling/fern', 'two')

//RMS.addRecipes(String recipeType, String[] recipe_id, String stage)
RMS.addRecipes("minecraft:smelting", ["minecraft:stone", "minecraft:iron_ingot"], "one")

//RMS.addRecipeByMod(String recipeType, String modId, String stage)
RMS.addRecipeByMod("minecraft:smelting", "create", "create")

//RMS.addRecipeByMods(String recipeType, String[] modId, String stage)
RMS.addRecipeByMods("minecraft:smelting", ["create", "minecraft"], "one")
```

### CraftTweaker
`import mods.rms.RMS;`
```ts
import mods.rms.RMS;

RMS.addRecipe(recipeType as string, recipeID as string, stage as string)
RMS.addRecipe(recipeType as string, recipeID as string[], stage as string)
RMS.addRecipeByMod(recipeType as string, modId as string, stage as string)
RMS.addRecipeByMod(recipeType as string, modId as string[], stage as string)

RMS.addRecipe(recipeType as RecypeType, recipeID as string, stage as string)
RMS.addRecipe(recipeType as RecypeType, recipeID as string[], stage as string)
RMS.addRecipeByMod(recipeType as RecypeType, modId as string, stage as string)
RMS.addRecipeByMod(recipeType as RecypeType, modId as string[], stage as string)
```