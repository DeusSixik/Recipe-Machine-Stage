# Recipe Machine Stage
This mod provides the ability to block recipes for mechanisms, similar to how it is implemented in Recipe Stages for the workbench.

## [CurseForge](https://www.curseforge.com/minecraft/mc-mods/recipe-machine-stages)
## Modrinth

# Supported Mods You can find it in the file *SUPPORTED MODS*

## [Discord](https://discord.gg/G6VEaBKcYt)
## [Donate](https://boosty.to/sixik)


# How to use it ?
Everything works very simply. You only need to install the (CraftTweaker and Game Stages) or KubeJS mods.


# KubeJS (Server Scripts)
```js
//kubejs/server_scripts/example.js

RecipeMachineStage.addRecipe(String recipeType, String recipeID, String stage)

RecipeMachineStage.addRecipe('create:milling', 'create:milling/fern', 'two')
RecipeMachineStage.addRecipe("minecraft:smelting", "minecraft:stone", "one")


RecipeMachineStage.addRecipe(String recipeType, String[] recipeIDs, String stage)
RecipeMachineStage.addRecipe("minecraft:smelting", ["minecraft:stone", "minecraft:iron_ingot"], "one")
```

# CraftTweaker
```ts
import mods.recipemachinestage.RecipeMachineStage;

RecipeMachineStage.addRecipe(recipeType as string, recipeID as string, stage as string)
RecipeMachineStage.addRecipe(recipeType as string, recipeID as string[], stage as string)
```

### Description
- **recipeType** - Recipe Type (In CraftTweaker `&lt;recipeType:minecraft:smelting&gt;` you need write without prefix &lt;recipeType&gt;. "minecraft:smelting")
- **recipeID** - Recipe ID ("minecraft:iron_ingot_from_blasting_iron_ore", "mekanism:processing/iron/enriched" etc.);     
- **stage** - Stage who block the recipe ("one" etc.)

### Example 
```ts
import mods.recipemachinestage.RecipeMachineStage;

RecipeMachineStage.addRecipe("minecraft:smelting", "minecraft:stone", "one");
//Botania (Mana Infusion)
RecipeMachineStage.addRecipe("botania:mana_infusion", "botania:mana_infusion/mana_diamond", "two");
//Mekanism (Metallurgic Infusing)
RecipeMachineStage.addRecipe("mekanism:metallurgic_infusing", "mekanism:processing/iron/enriched", "three");

RecipeMachineStage.addRecipe("minecraft:smelting", ["minecraft:stone", "minecraft:iron_ingot"], "one");
```