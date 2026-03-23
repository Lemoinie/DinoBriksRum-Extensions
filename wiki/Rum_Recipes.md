# DinoBriks Rum Recipes

This guide documents the known brewing and distillation recipes for Rum as found in the DinoBriks Rum datapack.

## Base Fermentation
All rum derivations start with fermenting **Sugar Cane** in a brewing barrel. You then toss the required ingredients near the barrel to mutate the brew during fermentation.

### 1. Gnome Rum
- **Base Ingredient:** Sugar Cane
- **Catalyst Items (Tossed nearby):** 4x Red Mushroom, 4x Brown Mushroom

### 2. Creeper Cider
- **Base Ingredient:** Sugar Cane
- **Catalyst Items (Tossed nearby):** 1x Creeper Head

### 3. Flyneberry Rum
- **Base Ingredient:** Sugar Cane
- **Catalyst Items (Tossed nearby):** 1x Player Head (Profile: `Flyrr_`)

### 4. Basic Rum
- **Base Ingredient:** Sugar Cane
- **Catalyst Items:** None (Just let the Sugar Cane ferment normally)

## Distillation
After the fermentation finishes, you will receive a fermented variant (e.g., `fermented_sugar_cane`, `gnome_rum_tag`). 
You must take this fermented brew to a Distillation Setup. Once distilled, you will receive the unaged raw alcohol (e.g., `just_rum`, `black_midnight unaged`, `gnome_rum final`, `creeper_cider final`).

## Aging
Place the raw distilled alcohol into an **Aging Barrel**. Over time, the custom `aged_days` component will increase. The rum's qualities and textures update appropriately as it reaches thresholds (0, 4, 8, 12, 16 days).
