# DinoBriks Aging Mechanics

Aging transforms raw, distilled alcohol into mature, high-quality brews. In the DinoBriks Rum datapack, aging is handled exclusively through a custom "Aging Barrel" block.

## How Aging Works
1. Place a raw, distilled beverage (e.g., `Unaged Gnome Rum`) into the exact inventory slot of the Aging Barrel.
2. The internal ticker of the datapack continuously updates the integer `aged_days` stored natively on the beverage item every in-game day it sits in the barrel.
3. Once the item's `aged_days` value reaches specific thresholds, the barrel mechanically updates the item's visual Lore and Name, establishing its new mature state.

## Aging Thresholds

Unlike base Vanilla brewing, different alcohols require completely different durations to mature!

- **Beers**: Pale Beer and Stout Beer reach their mature forms remarkably quickly in just **4 Days**.
- **Wines**: Wine progresses through two stages of aging! After **8 Days**, you receive a `"Half Well Aged"` Wine. Leaving it in the barrel for the full **16 Days** upgrades it into `"Well Aged"` Wine.
- **Rums, Tequilas, Absinthes**: These heavy spirits demand precise aging. They often require the entire **16 Days** duration to transition from an `Unaged` product to their `Final`, highly-potent mature state. Extracting them early yields an unaged beverage.
- **Dwarven & Golden specific rums/whiskeys**: These rely on exactly **16 Days** and typically interact perfectly inside a specific wood-type barrel (e.g., `oak` vs `cherry` vs `mangrove`) to yield extremely specialized variants! Always check your barrel choice.
