# FIT2099 Assignment (Semester 2, 2026)

```
   ,-,--.  ,--.--------.   ,---.       _,.----.  ,--.-.,-.                
 ,-.'-  _\/==/,  -   , -\.--.'  \    .' .' -   \/==/- |\  \               
/==/_ ,_.'\==\.-.  - ,-./\==\-/\ \  /==/  ,  ,-'|==|_ `/_ /,--.--------.  
\==\  \    `--`\==\- \   /==/-|_\ | |==|-   |  .|==| ,   //==/,  -   , -\ 
 \==\ -\        \==\_ \  \==\,   - \|==|_   `-' \==|-  .| \==\.-.  - ,-./ 
 _\==\ ,\       |==|- |  /==/ -   ,||==|   _  , |==| _ , \ `--`--------`  
/==/\/ _ |      |==|, | /==/-  /\ - \==\.       /==/  '\  |               
\==\ - , /      /==/ -/ \==\ _.\=\.-'`-.`.___.-'\==\ /\=\.'               
 `--`---'       `--`--`  `--`                    `--`                     
    _,.---._                     ,----.                  ,----.           
  ,-.' - ,  `.   .--.-. .-.-. ,-.--` , \ .--.-. .-.-. ,-.--` , \          
 /==/ ,    -  \ /==/ -|/=/  ||==|-  _.-`/==/ -|/=/  ||==|-  _.-`          
|==| - .=.  ,  ||==| ,||=| -||==|   `.-.|==| ,||=| -||==|   `.-.          
|==|  : ;=:  - ||==|- | =/  /==/_ ,    /|==|- | =/  /==/_ ,    /          
|==|,  '='  ,  ||==|,  \/ - |==|    .-' |==|,  \/ - |==|    .-'           
 \==\ _   -    ;|==|-   ,   /==|_  ,`-._|==|-   ,   /==|_  ,`-._          
  '.='.  ,  ; -\/==/ , _  .'/==/ ,     //==/ , _  .'/==/ ,     /          
    `--`--'' `--`--`..---'  `--`-----`` `--`..---'  `--`-----``           
        ,-.-.  ,---.                              ,----.                  
 ,--.-./=/ ,/.--.'  \       _.-.      _.-.     ,-.--` , \ ,--.-.  .-,--.  
/==/, ||=| -|\==\-/\ \    .-,.'|    .-,.'|    |==|-  _.-`/==/- / /=/_ /   
\==\,  \ / ,|/==/-|_\ |  |==|, |   |==|, |    |==|   `.-.\==\, \/=/. /    
 \==\ - ' - /\==\,   - \ |==|- |   |==|- |   /==/_ ,    / \==\  \/ -/     
  \==\ ,   | /==/ -   ,| |==|, |   |==|, |   |==|    .-'   |==|  ,_/      
  |==| -  ,//==/-  /\ - \|==|- `-._|==|- `-._|==|_  ,`-._  \==\-, /       
  \==\  _ / \==\ _.\=\.-'/==/ - , ,/==/ - , ,/==/ ,     /  /==/._/        
   `--`--'   `--`        `--`-----'`--`-----'`--`-----``   `--`-`         
```

## REQ5 — Cave Mimic

The Cave Mimic is a hostile creature that disguises itself as a Geode, scatters items as bait, collects nearby loot, ambushes the Farmer and creates a decoy when escaping.

It remains an Actor in every state, retaining its health, inventory and position. Its disguised symbol is `G`, while its revealed symbol is `M`.

### Settings

| Setting | Value |
|---|---|
| Initial state | DISGUISED |
| Farmer and ordinary loot detection radius | 3 tiles |
| Quiet period before leaving the disguise | 5 consecutive turns without detecting a Farmer |
| Maximum bait scattered on Disguised entry | 3 items |
| Disguised recovery | 3 HP per ordinary turn, capped at maximum health |
| External loot range for entering Looting | 2–3 tiles |
| Ordinary collection goal per Looting visit | 2 additional items |
| Maximum items pulled on Looting entry | 2 |
| Escape health threshold | 30% of maximum health or below |
| Escape route search depth | Up to 3 walking steps |
| Decoy lifetime | 3 completed decoy turns |
| Ordinary movement | 1 tile per turn |

Distances use Chebyshev distance: horizontal, vertical and diagonal movement to an adjacent tile each count as one tile.

### States and entry effects

Entry effects execute once when the Mimic enters a different state. Remaining in the same state does not repeat them. The initial Disguised entry effect runs after the Mimic is placed on the map.

| State | Immediate entry effect | Ordinary behaviour |
|---|---|---|
| DISGUISED | Change appearance to a Geode, scatter up to three portable inventory items onto distinct eligible adjacent tiles and reset the quiet counter. | Remain stationary and recover 3 HP per turn until fully healed. |
| LOOTING | Reveal the Mimic, reclaim all portable items on adjacent tiles, then pull up to two eligible portable items from distance 2–3 one route step towards itself. Record the inventory baseline afterwards. | Find reachable portable loot, walk to its tile and collect at most one item per turn. |
| AMBUSH | Reveal the Mimic. If an adjacent Farmer carries an eligible portable item, execute that item's drop action and transfer the same item from the Farmer's tile into the Mimic's inventory. | Use AmbushBehaviour to attack an adjacent Farmer or walk towards a detected Farmer. |
| ESCAPE | Reveal the Mimic, remove its previous decoy and plan separate first escape steps for the real Mimic and a new decoy. Spawn the decoy on its selected tile and reserve the real Mimic's first movement action. | Perform the reserved first movement, then repeatedly select movement away from the Farmer. |

Items without an eligible scattering destination remain in the Mimic's inventory. Non-portable items are not scattered, reclaimed, pulled or collected.

The Looting baseline is recorded after adjacent reclamation. Reclaimed items therefore do not immediately satisfy the two-item ordinary collection goal.

### Transition conditions

Conditions are evaluated in the order shown. An eligible theft target is an adjacent, conscious Farmer carrying a portable item with an available drop action.

| Current state | Conditions and destinations |
|---|---|
| DISGUISED | An eligible theft target → AMBUSH. Otherwise, if no Farmer has been detected for at least five consecutive turns and reachable portable loot exists at distance 2–3 → LOOTING. Otherwise remain DISGUISED. Detecting any Farmer resets the quiet counter. |
| LOOTING | An eligible theft target → AMBUSH. If another Farmer is detected without a theft opportunity, remain LOOTING. With no detected Farmer, collecting two additional items or finding no reachable portable loot → DISGUISED. Otherwise remain LOOTING. |
| AMBUSH | No Farmer detected within three tiles → DISGUISED. Otherwise, health at or below 30% and a valid escape plan with separate Mimic and decoy steps → ESCAPE. Otherwise remain AMBUSH. |
| ESCAPE | An adjacent Farmer and no valid escape movement → AMBUSH. Otherwise, while a Farmer remains detected, stay in ESCAPE. With no detected Farmer, reachable portable loot within three tiles → LOOTING; otherwise → DISGUISED. |

Every state can lead to at least two other states and can remain active. State selection uses environmental checks, inventory conditions, health and route availability; it does not use random state selection.

### Item-seeking behaviour

SeekItemBehaviour searches for the nearest reachable portable-item location within three tiles, including the Mimic's current tile.

If an eligible item is already under the Mimic, it selects the item's pickup action. Otherwise, it requests the first walking step towards the selected location. Movement respects occupied tiles and impassable ground.

When no useful pickup or movement action is available, the Mimic remains stationary.

### Escape behaviour and decoy

FleeBehaviour reassesses the Farmer's position and available routes each turn. It searches routes up to three walking steps long and prefers routes that finish farther from the Farmer. If no improving route exists, it may select a route that preserves the original distance. Its first movement step never reduces its distance from the Farmer.

On Escape entry, the decoy's route excludes the real Mimic's selected first destination. This gives them different initial escape directions.

The decoy copies the Mimic's visible symbol and description. It continues moving in its assigned direction, remaining stationary when that direction is blocked. It disappears after three completed turns or when attacked, including when the attack misses. It does not attack.

### Implementation notes

The Mimic's two new ordinary behaviours are item seeking and collection in Looting, and planned fleeing in Escape.

Environmental scanning identifies conscious Farmers through ActorRole.FARMER. Transition checks and movement use fixed ordering to resolve equivalent choices.

The engine supports one Farmer, so the Mimic evaluates that Farmer's proximity, inventory and the available escape routes.

If an entry effect has no eligible item or destination, the applicable transfer or spawn is skipped. Ordinary action selection uses an idle action when no useful action is available.

## REQ5 — Nest Spider

The Nest Spider is a hostile creature that guards a fixed nest, constructs web traps, investigates triggered webs and hunts Farmers threatening its territory.

It has four states: NESTING, WEAVING, HUNTING and POUNCING. Pouncing investigates a recorded location, while Hunting pursues an engaged Farmer.

The Spider is displayed as `s`, its fixed nest as `&`, Spiderlings as `z`, and visible webs as `:`. An actor standing on the nest temporarily covers its ground symbol.

### Settings

| Setting | Value |
|---|---|
| Initial state | NESTING |
| Fixed nest location | Mines map at `(5, 3)` |
| Maximum active owned webs | 6 |
| New web lifetime | 8 map ticks |
| Maximum reinforced web lifetime | 8 map ticks |
| Ordinary web-placement radius | 4 tiles from the nest |
| Direct nest-intrusion radius | 3 tiles from the nest |
| Hunting pursuit boundary | 4 tiles from the nest |
| Pouncing jump distance | Exactly 2 tiles |
| Investigation arrival distance | Within 1 tile of the recorded alert |
| Farmer investigation radius | 2 tiles around the recorded alert |
| Low-health threshold | 40% of maximum health or below |
| Health required to begin Weaving | At least 80% of maximum health |
| Quiet period before Weaving | 3 consecutive quiet turns at home |
| Nesting recovery | 3 HP per ordinary turn while at home |
| Spiderling health | 10 HP |
| Spiderling lifetime | 8 completed Spiderling turns |

Distances use Chebyshev distance: horizontal, vertical and diagonal movement to an adjacent tile each count as one tile.

### States and entry effects

Entry effects execute once when the Spider enters a different state. Remaining in the same state does not repeat them. The initial Nesting effect runs after the Spider has been placed on the map.

| State | Immediate entry effect | Ordinary behaviour |
|---|---|---|
| NESTING | Spawn one Spiderling beside the parent when a safe, unoccupied tile exists. Maintain up to two guard webs beside the fixed nest, reinforcing existing owned guards or placing webs where needed. | Walk home using a valid route. Once home, remain stationary and recover 3 HP per turn until fully healed. |
| WEAVING | Place one eligible web beside the Spider. Reinforce active owned webs within two tiles of its position by three ticks, capped at eight. | Select strategic reachable trap sites, walk to an adjacent placement position and place webs. |
| HUNTING | Temporarily reveal all active owned webs through the engaged Farmer's next turn and increase their remaining lifespan by two ticks, capped at eight. | Use the existing AmbushBehaviour through the Spider's hunting behaviour to walk towards and attack the engaged Farmer. Hunting does not jump. |
| POUNCING | Place up to two eligible departure webs beside the Spider, preferring positions behind its direction towards the alert. | Travel towards the latest recorded web-trigger location using tactical two-tile jumps and safe walking fallback, then investigate the area. |

Nesting's guard-web effect is centred on the fixed nest even when the parent is elsewhere. Its Spiderling spawns beside the parent's current position.

### Transition conditions

Conditions are evaluated in the order shown. A pending owned-web alert takes priority over direct nest intrusion.

| Current state | Conditions and destinations |
|---|---|
| NESTING | Pending web alert → POUNCING. Otherwise, a conscious Farmer within three tiles of the nest → HUNTING. Otherwise, at least 80% health, three quiet turns at home and sufficient reachable placement work → WEAVING. Otherwise remain NESTING. |
| WEAVING | Pending web alert → POUNCING. Otherwise, nest intrusion → HUNTING. Otherwise, health at or below 40%, six active webs or no reachable placement sites → NESTING. Otherwise remain WEAVING. |
| HUNTING | Pending web alert → POUNCING. Otherwise, remain HUNTING while the engaged Farmer is conscious, on the nest's map and within four tiles of the nest. Once pursuit ends, at least 80% health and suitable weaving work → WEAVING; otherwise → NESTING. |
| POUNCING | A newer web alert redirects the destination while remaining POUNCING. After arrival, a conscious Farmer within two tiles of the alert who is also within the Hunting boundary → HUNTING. An unsuccessful or unreachable investigation → WEAVING when recovered and placement work exists; otherwise → NESTING. Otherwise remain POUNCING. |

Every state can lead to at least two other states and can remain active. State selection is deterministic and uses threats, health, web capacity, alert events and route availability.

The Nesting quiet counter resets when an alert or nest intrusion occurs, or when the Spider is away from home.

### Web traps and capacity

Webs belong to the Spider that created them. A tile can contain only one web. Placement rejects occupied, unsuitable or hazardous locations and preserves underlying ground and unrelated items.

Webs may begin hidden. Hidden webs display the item or ground beneath them. Only eligible conscious Farmers can trigger them; the Spider and Spiderlings are immune.

A successful trigger applies one pending lost turn to the Farmer and consumes the web. An already-bound Farmer cannot accumulate additional pending lost turns.

Ordinary Weaving respects the six-web cap and does not replace existing webs. Entry effects may replace the shortest-lived owned webs when valid new placement sites have been selected. Coordinates resolve equal-lifetime choices deterministically. Another creature's webs are never removed.

Before entering Weaving, the Spider requires capacity for its entry web and at least one ordinary placement, together with suitable reachable sites.

During Hunting's temporary reveal, webs remain capable of trapping Farmers and continue ageing normally. Afterwards, originally hidden webs become hidden again; originally visible webs remain visible.

### Pouncing and investigation

A triggered web records its location before disappearing. Investigation therefore does not depend on the web remaining present or the Farmer still being bound.

Each new trigger receives an increasing event version. A newer event replaces the destination, allowing the Spider to redirect towards another triggered web without leaving Pouncing or repeating its entry effect.

PounceBehaviour selects useful legal two-tile jumps towards the alert. Landings must be enterable, unoccupied and free of recognised movement hazards. When a useful jump is unavailable, it uses a safe walking route.

The Spider investigates only after reaching within one tile of the recorded location. It then checks for a Farmer within two tiles of that location. Farmers outside the Hunting pursuit boundary do not start a continued hunt.

Completed or unreachable alerts are cleared using their event version, preventing an old investigation from deleting a newer alert.

### Spiderlings

One Spiderling is created on each Nesting entry when an eligible adjacent spawn tile exists. Remaining in Nesting does not create additional Spiderlings.

Spiderlings wander independently using the existing WanderBehaviour. They have 10 HP, can be attacked, do not attack and disappear after eight completed turns. An idle turn also counts towards their lifetime.

The fixed nest tile is excluded from spawning so the new Spiderling does not immediately block its parent's return home.

### Implementation notes

The Spider's two new ordinary behaviours are strategic web construction in Weaving and alert-directed movement and investigation in Pouncing.

Strategic Weaving evaluates trap-site suitability, surrounding web coverage and reachable placement positions. Pouncing combines recorded alerts, tactical movement, redirection and destination investigation.

Walking and jumping respect terrain restrictions and actor occupancy. Safe route planning additionally avoids fire items and electrically charged ground recognised by the current implementation.

Blocked placement and spawn locations are skipped. When no useful ordinary action is available, the Spider remains stationary.