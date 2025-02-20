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

################################
//ComigSoon
//RecipeMachineStage.addRecipeByMod(String recipeType, String modID, String stage)
//RecipeMachineStage.addRecipeByMod("minecraft:smelting", "minecraft", "one")
//
//RecipeMachineStage.addRecipeByMods(String recipeType, String[] modIDs, String stage)
//RecipeMachineStage.addRecipeByMods("minecraft:smelting", ["minecraft", "create"], "one")
################################
```

# CraftTweaker
```ts
import mods.recipemachinestage.RecipeMachineStage;

RecipeMachineStage.addRecipe(recipeType as string, recipeID as string, stage as string)
RecipeMachineStage.addRecipe(recipeType as string, recipeID as string[], stage as string)
RecipeMachineStage.addRecipeByMod(recipeType as string, modId as string, stage as string)
RecipeMachineStage.addRecipeByMod(recipeType as string, modId as string[], stage as string)

RecipeMachineStage.addRecipe(recipeType as RecypeType, recipeID as string, stage as string)
RecipeMachineStage.addRecipe(recipeType as RecypeType, recipeID as string[], stage as string)
RecipeMachineStage.addRecipeByMod(recipeType as RecypeType, modId as string, stage as string)
RecipeMachineStage.addRecipeByMod(recipeType as RecypeType, modId as string[], stage as string)
```

### Description
- **recipeType** - Recipe Type (In CraftTweaker `&lt;recipeType:minecraft:smelting&gt;` you need write without prefix &lt;recipeType&gt;. "minecraft:smelting")
- **recipeID** - Recipe ID ("minecraft:iron_ingot_from_blasting_iron_ore", "mekanism:processing/iron/enriched" etc.);     
- **stage** - Stage who block the recipe ("one" etc.)

### Example 
```ts
import mods.recipemachinestage.RecipeMachineStage;

RecipeMachineStage.addRecipe("minecraft:smelting", "minecraft:stone", "one");
RecipeMachineStage.addRecipe(<recypetype:minecraft:smelting>, "minecraft:stone", "one");
//Botania (Mana Infusion)
RecipeMachineStage.addRecipe("botania:mana_infusion", "botania:mana_infusion/mana_diamond", "two");
RecipeMachineStage.addRecipe(<recypetype:botania:mana_infusion>, "botania:mana_infusion/mana_diamond", "two");
//Mekanism (Metallurgic Infusing)
RecipeMachineStage.addRecipe("mekanism:metallurgic_infusing", "mekanism:processing/iron/enriched", "three");
RecipeMachineStage.addRecipe(<recypetype:mekanism:metallurgic_infusing>, "mekanism:processing/iron/enriched", "three");

RecipeMachineStage.addRecipe("minecraft:smelting", ["minecraft:stone", "minecraft:iron_ingot"], "one");
RecipeMachineStage.addRecipe(<recypetype:minecraft:smelting>, ["minecraft:stone", "minecraft:iron_ingot"], "one");

RecipeMachineStage.addRecipeByMod("minecraft:smelting", "minecraft", "one");
RecipeMachineStage.addRecipeByMod(<recypetype:minecraft:smelting>, "minecraft", "one");

RecipeMachineStage.addRecipeByMod("minecraft:smelting", ["minecraft", "create"], "one");
RecipeMachineStage.addRecipeByMod(<recypetype:minecraft:smelting>, ["minecraft", "create"], "one");
```
    

# For Developers

## Container

If you need to make support for a block that crafting cannot take place without a player, such as a crafting table, then you need to add a interface  **net.sdm.recipemachinestage.api.IRestrictedContainer** to the container class. [Example](https://github.com/SagaDeoMissTeam/Recipe-Machine-Stage/blob/HEAD/src/main/java/net/sdm/recipemachinestage/mixin/integration/extendedcrafting/container/AdvancedTableContainerMixin.java)

The method will be added to your class. 
```java
List<Integer> recipe_machine_stage$getOutputSlots();
```

In it, you need to transfer the IDs of the slots that have an output item when crafting.


## BlockEntity

If you need to add support for recipes that occur without the player's participation, for example a crusher for now, you need to use capability **net.sdm.recipemachinestage.SupportBlockData.BLOCK_OWNER** It stores the owner of the block

### Usage example

```java
Optional<IOwnerBlock> optionalOwnerBlock = thisBlockEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
if (optionalOwnerBlock.isPresent()) {
    IOwnerBlock ownerBlock = optionalOwnerBlock.get();

    PlayerHelper.RMSStagePlayerData playerData = PlayerHelper.getPlayerByGameProfile(thisBlockEntity.getLevel().getServer(), ownerBlock.getOwner());

    if(playerData != null) {
        if(player.hasStage("someStage")) {
            //.. your code here
        }
    }

}
```

To check if your craft recipe is limited, you need **net.sdm.recipemachinestage.stage.StageContainer** It will allow you to get the stage and some other information

```java
Recipe<?> myRecipe;

RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(myRecipe.getType(), myRecipe.getId());

if(recipeBlockType != null) {
    //.. your code here
}
```

### [FullExample](https://github.com/SagaDeoMissTeam/Recipe-Machine-Stage/blob/HEAD/src/main/java/net/sdm/recipemachinestage/mixin/integration/botania/BreweryBlockEntityMixin.java)