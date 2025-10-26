# Create

At the moment, on 10/25/2025, all possible types of recipes have been added. (Create Version 6.0.6)

### Supported types
- create:mixing
- create:compacting
- create:filling
- create:basin
- create:crushing
- create:cutting
- create:splashing
- create:deploying
- create:milling
- create:mechanical_crafting
- create:sequenced_assembly
- create:haunting
- create:sandpaper_polishing
- create:pressing

### Support is not possible
- `create:conversion` This type is only needed for display in JEI/REI/EMI, it does not carry any recipes in itself.


## Examples

### KubeJS

```js
```

### CraftTweaker

```ts
RMS.addRecipe(<recipetype:create:pressing>, "create:pressing/brass_ingot", "create");
RMS.addRecipe(<recipetype:create:mixing>, "create:mixing/andesite_alloy", "create");
RMS.addRecipe(<recipetype:create:compacting>, "create:compacting/ice", "create");
RMS.addRecipe(<recipetype:create:filling>, "create:filling/glowstone", "create");
RMS.addRecipe(<recipetype:create:crushing>, "create:crushing/glowstone", "create");
RMS.addRecipe(<recipetype:create:haunting>, "create:haunting/nether_brick", "create");
RMS.addRecipe(<recipetype:create:sandpaper_polishing>, "create:sandpaper_polishing/rose_quartz", "create");
```