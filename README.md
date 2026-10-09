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
## Contribution Log
[Contribution Log Link](https://docs.google.com/spreadsheets/d/1VfutnfXfJ8pkZfCXGZGbKtAEHKDqI-Xkg6gcwM73iw8/edit?usp=sharing)

## REQ5 — Cave Mimic

The Cave Mimic is a hostile creature that disguises itself as a Geode. It scatters items as bait, collects nearby loot, steals from the Farmer and creates a decoy when escaping.

Its disguised symbol is `G`, matching a Geode, while its revealed symbol is `M`. The disguise only changes its appearance. It remains the same Actor, keeping its health, inventory and location.

### Settings

| Setting | Value |
|---|---|
| Initial state | DISGUISED |
| Farmer detection radius | 3 tiles |
| Ordinary loot detection radius | 3 tiles, including the current tile |
| Quiet period before seeking external loot | 5 consecutive turns without detecting a Farmer |
| External loot range for leaving Disguised | 2–3 tiles |
| Maximum bait scattered on Disguised entry | 3 items |
| Disguised recovery | 3 HP per ordinary turn, capped at maximum health |
| Ordinary collection goal per Looting visit | 2 additional items |
| Maximum items pulled on Looting entry | 2 |
| Escape health threshold | 30% of maximum health or below |
| Escape route search depth | Up to 3 walking steps |
| Decoy lifetime | 3 completed decoy turns |
| Ordinary movement | 1 tile per turn |

Distances use Chebyshev distance. Moving horizontally, vertically or diagonally to an adjacent tile counts as one tile.

### States and entry effects

Entry effects happen immediately when the Mimic enters a different state, before its ordinary action. Staying in the same state does not repeat the entry effect.

The initial Disguised entry effect runs after the Mimic is placed on the map.

| State | Immediate entry effect | Ordinary behaviour |
|---|---|---|
| DISGUISED | Apply the Geode appearance, scatter up to three portable inventory items onto separate eligible adjacent tiles, and reset the quiet counter. | Remain stationary and recover 3 HP per turn until fully healed. |
| LOOTING | Reveal the Mimic, reclaim portable items from its current and adjacent tiles, then pull up to two eligible items from distance 2–3 one route step towards itself. Record the inventory baseline afterwards. | Find reachable portable loot, move towards it and collect at most one item per turn. |
| AMBUSH | Reveal the Mimic, make an adjacent eligible Farmer drop one portable item, and transfer that same item into the Mimic's inventory. | Attack an adjacent Farmer or walk towards a detected Farmer using `AmbushBehaviour`. |
| ESCAPE | Plan separate initial escape steps, reveal the Mimic, remove its previous decoy, create a new decoy and reserve the real Mimic's first movement action. | Perform the reserved first movement, then reassess the Farmer's position and continue fleeing. |

### Transition conditions

Conditions are checked in the order shown.

An **eligible theft target** is an adjacent, conscious Farmer carrying a portable item with an available drop action.

**Bait can be scattered** when the Mimic has at least one portable inventory item and an unoccupied adjacent tile that it can enter.

| Current state | Conditions and destinations |
|---|---|
| DISGUISED | An eligible theft target causes AMBUSH. Otherwise, detecting a Farmer resets the quiet counter and keeps the Mimic DISGUISED. With no detected Farmer for at least five consecutive turns, reachable portable loot at distance 2–3 causes LOOTING. Otherwise, remain DISGUISED. |
| LOOTING | An eligible theft target causes AMBUSH. A detected Farmer without a theft opportunity keeps the Mimic LOOTING. With no detected Farmer, return to DISGUISED when either the collection goal is reached or no reachable loot remains, provided bait can be scattered. Otherwise, remain LOOTING. |
| AMBUSH | With no detected Farmer, return to DISGUISED if bait can be scattered; otherwise, remain AMBUSH. With a detected Farmer, health at or below 30% and a valid plan with separate Mimic and decoy destinations cause ESCAPE. Otherwise, remain AMBUSH. |
| ESCAPE | An eligible theft target and no valid escape movement cause AMBUSH. Otherwise, remain ESCAPE while a Farmer is detected. With no detected Farmer, reachable portable loot within three tiles, including the current tile, causes LOOTING. If no loot is found, return to DISGUISED when bait can be scattered; otherwise, remain ESCAPE. |

Each state can remain active or lead to at least two other states. State selection uses environmental checks, inventory contents, health and route availability rather than random choices.

### Bait and item collection

The Mimic scatters existing items from its inventory. Each scattered item goes onto a different eligible adjacent tile. Items that cannot be scattered remain in its inventory.

On Looting entry, the Mimic reclaims all portable items from its current tile and adjacent tiles. It then pulls up to two eligible portable items from distance 2–3 along valid routes towards itself.

Reclamation includes items directly underneath the Mimic. This allows the entry effect to collect an item when Escape changes to Looting because of loot on the current tile.

The inventory baseline is recorded after entry reclamation. These reclaimed items do not count towards the goal of collecting two additional items during ordinary Looting turns.

The collection goal does not guarantee two pickups during every visit. For example, entry reclamation may take the only available item, leaving no ordinary collection work. On its next transition check, the Mimic can return to Disguised if no Farmer is detected and bait can be scattered.

Non-portable items are not scattered, reclaimed, pulled or collected.

### Item-seeking behaviour

`SeekItemBehaviour` searches for the nearest reachable location containing portable loot within three tiles, including the Mimic's current tile.

If an eligible item is already underneath the Mimic, it selects the item's pickup action. Otherwise, it selects the first walking step towards the chosen location. Movement respects occupied tiles and impassable ground.

The detection radius measures distance to the item, not the length of the walking route. A valid route may take a detour around an obstacle.

When no useful pickup or movement action is available, the Mimic remains stationary.

### Ambushing and theft

Entering Ambush requires an eligible theft opportunity. The Mimic uses the item's drop action to remove it from the Farmer's inventory, then transfers the same item from the Farmer's tile into its own inventory.

The theft happens once on entry. Remaining in Ambush does not repeatedly steal items.

During ordinary Ambush turns, the Mimic attacks an adjacent Farmer or walks towards a Farmer detected within three tiles.

### Escaping and the decoy

`FleeBehaviour` checks the Farmer's position and available routes each turn. It searches routes up to three walking steps long, considering distance from the Farmer and whether the route has an onward exit.

It prefers routes that increase separation. If no improving route exists, it may choose a route that preserves the original distance. The first step never moves closer to the Farmer.

Entering Escape requires separate valid first destinations for the real Mimic and its decoy. The decoy's route excludes the real Mimic's selected first destination, so they start in different directions.

The decoy copies the Mimic's visible symbol and description. It continues moving in its assigned direction and stays still when that direction is blocked. It does not attack.

The decoy disappears after three completed turns, including idle turns. It also disappears when attacked, even if the attack misses. Creating another decoy removes the previous one if it is still present.

### Implementation notes

The Mimic's two new ordinary behaviours are item seeking and collection in Looting, and planned fleeing in Escape.

Environmental scanning identifies conscious Farmers through `ActorRole.FARMER`. The implementation is designed around the game's single Farmer. Equivalent choices are resolved using fixed ordering.

The state framework keeps transition checks, immediate entry effects and ordinary action selection separate. Returning to Disguised requires available bait scattering, and entering Ambush requires an eligible theft target.

---

## REQ5 — Nest Spider

The Nest Spider is a hostile creature that guards a fixed nest, builds web traps and investigates triggered webs.

It has four states: NESTING, WEAVING, HUNTING and POUNCING. Hunting follows an engaged Farmer, while Pouncing travels towards the recorded location of a triggered web.

The Spider is displayed as `s`, its nest as `&`, Spiderlings as `z`, and visible webs as `:`. An Actor standing on the nest temporarily covers the nest's ground symbol.

### Settings

| Setting | Value |
|---|---|
| Initial state | NESTING |
| Fixed nest location | Mines map at `(5, 3)` |
| Maximum active owned webs | 6 |
| New web lifetime | 8 map ticks |
| Maximum reinforced web lifetime | 8 map ticks |
| Chance that a new web starts hidden | 20% |
| Ordinary web-placement radius | 4 tiles from the nest |
| Direct nest-intrusion radius | 1 tile from the nest |
| Hunting pursuit boundary | 4 tiles from the nest |
| Pouncing jump distance | Exactly 2 tiles horizontally, vertically or diagonally |
| Investigation arrival distance | Within 1 tile of the recorded alert |
| Farmer investigation radius | 2 tiles around the recorded alert |
| Low-health threshold | 40% of maximum health or below |
| Health required to begin Weaving | At least 80% of maximum health |
| Quiet period before Weaving from Nesting | 3 consecutive quiet turns at home |
| Nesting recovery | 3 HP per ordinary turn while at home |
| Spiderling health | 10 HP |
| Spiderling lifetime | 8 completed Spiderling turns |

Distances use Chebyshev distance. An adjacent horizontal, vertical or diagonal tile is one tile away.

Direct nest intrusion uses a radius of one tile to leave more room for the Farmer to move around the map. Once Hunting begins, the pursuit boundary remains four tiles from the nest. Triggered webs can also lead to Pouncing and then Hunting outside the direct intrusion radius.

### States and entry effects

Entry effects happen once when the Spider enters a different state, before its ordinary action. Remaining in the same state does not repeat them.

The initial Nesting entry effect runs after the Spider is placed on the map.

| State | Immediate entry effect | Ordinary behaviour |
|---|---|---|
| NESTING | Spawn one Spiderling beside the parent when a safe, unoccupied tile exists. Reinforce existing guard webs around the fixed nest and attempt to provide at least two guards. | Walk home using a safe route. At home, remain stationary and recover 3 HP per turn until fully healed. |
| WEAVING | Place one eligible adjacent web and reinforce active owned webs within two tiles by three ticks, capped at eight. | Select suitable reachable trap sites, walk to an adjacent placement position and place webs. |
| HUNTING | Temporarily reveal all active owned webs and increase their remaining lifetime by two ticks, capped at eight. If no webs remain, use the defensive fallback described below. | Use `SpiderHuntBehaviour` to walk towards the engaged Farmer and attack when adjacent. Hunting does not jump. |
| POUNCING | Place up to two eligible departure webs beside the Spider, preferring tiles behind its direction towards the alert. | Approach the latest recorded alert using useful two-tile jumps or safe walking, then investigate the area. |

Nesting maintains guard webs around the fixed nest even when the Spider is elsewhere. The Spiderling spawns beside the parent's current position.

Existing guard webs are preserved, including when more than two already exist.

### Transition conditions

A pending owned-web alert takes priority over other transition conditions in Nesting, Weaving and Hunting.

Before entering Pouncing, the Spider checks whether at least one adjacent departure web can be placed. This includes checking whether an eligible existing web can be replaced when the network is full.

If departure-web placement is blocked, the Spider retains the alert and its current state, performs that state's ordinary behaviour, and retries on its next turn. Lower-priority transitions are not evaluated while that alert remains pending.

| Current state | Conditions and destinations |
|---|---|
| NESTING | Apply the pending-alert rule first. With no alert, a conscious Farmer within one tile of the nest causes HUNTING. Otherwise, at least 80% health, three quiet turns at home and sufficient reachable placement work cause WEAVING. Otherwise, remain NESTING. |
| WEAVING | Apply the pending-alert rule first. With no alert, nest intrusion causes HUNTING. Otherwise, health at or below 40%, no free web capacity or no reachable placement sites causes NESTING. Otherwise, remain WEAVING. |
| HUNTING | Apply the pending-alert rule first. With no alert, remain HUNTING while the engaged Farmer is conscious, on the nest's map and within four tiles of the nest. Once pursuit ends, enter WEAVING if health is at least 80% and suitable placement work exists; otherwise, enter NESTING. |
| POUNCING | A newer alert updates the destination while the Spider remains POUNCING. After arrival, a conscious Farmer within two tiles of the alert starts HUNTING only if that Farmer is also inside the pursuit boundary. If the investigation finishes without a valid target, becomes unreachable or has no remaining alert, enter WEAVING when sufficiently recovered and placement work exists; otherwise, enter NESTING. While still approaching an unchanged reachable alert, remain POUNCING. |

Each state can remain active or lead to at least two other states. Transitions use threats, health, web capacity, recorded alerts and route availability.

The Nesting quiet counter resets when an alert or nest intrusion occurs, or when the Spider is away from home.

### Web placement and capacity

Each web belongs to the Spider that created it. A tile can contain only one web.

Placement requires an unoccupied, safe and suitable tile on the nest's map. The nest tile itself is excluded. Placing a web preserves the underlying ground and unrelated items.

Ordinary Weaving respects the six-web limit and does not replace existing webs.

Entry effects may replace the shortest-lived active owned web that is not a guard web. The new site is checked before removing an existing web. Equal remaining lifetimes are resolved by row, then column. Guard webs and another creature's webs are never removed to make room.

Before entering Weaving, the Spider requires:

- An eligible adjacent site for its entry web.
- A different eligible site with a safely reachable placement position for ordinary weaving.
- Capacity for both the entry web and at least one ordinary web.

These checks prevent Weaving from starting when its entry effect would use the only available placement opportunity.

### Strategic weaving

`WeaveWebBehaviour` uses the placement planner to select trap sites within four tiles of the nest.

Sites are ranked using narrow passages, surrounding web coverage and the walking distance to an adjacent placement position. Fixed coordinate ordering resolves equal choices.

The Spider performs one movement or placement action per ordinary turn. It keeps its selected site while approaching it and chooses another if the site becomes occupied, unsafe or otherwise unavailable.

When there is no useful action, the Spider remains stationary.

### Hidden webs and trapping

Each new web has a 20% chance of starting hidden. This randomness affects appearance only; it does not choose the Spider's next state or change placement eligibility.

A hidden web displays the item or ground beneath it. A visible web uses `:`.

Webs check for an occupying Actor during map ticks. Only eligible conscious Farmers can trigger them. The Spider and Spiderlings are immune.

A successful trigger gives the Farmer one pending lost turn and consumes the web. An already-bound Farmer cannot accumulate additional pending lost turns.

The binding is consumed when the Farmer's turn begins. The Farmer spends that turn escaping the web before normal action selection resumes on a later turn.

Unused webs age each map tick and expire when their lifetime reaches zero. They also become inactive if their owner dies or leaves their map.

### Hunting and defensive webs

`SpiderHuntBehaviour` follows the engaged Farmer using safe walking and the existing `AttackAction`.

The Spider keeps tracking that Farmer while they remain conscious, on the nest's map and within the four-tile pursuit boundary. A temporarily blocked route does not clear the engagement; the Spider waits and tries again.

On Hunting entry, owned webs are revealed for two map ticks, covering the Farmer's next turn. Their remaining lifetime increases by two ticks, up to eight. They continue ageing and remain able to trap the Farmer during the reveal.

After the temporary reveal ends, originally hidden webs become hidden again. Originally visible webs remain visible.

If no active owned webs remain, the Hunting entry effect first attempts to establish two guard webs around the nest. If that produces no webs, it attempts to place webs around the engaged Farmer.

If neither placement attempt succeeds, the Spider releases a defensive silk pulse around the Farmer's tile and its adjacent tiles. This uses the existing binding rules: only eligible conscious Farmers can be bound, immunity is respected, and pending lost turns do not stack.

### Pouncing and investigation

A successfully triggered web records its location before disappearing. The Spider can therefore investigate even after the web has been consumed and the Farmer has moved away.

Each trigger receives an increasing event version. A newer trigger replaces the pending alert.

While Pouncing, the Spider follows the latest alert. A newer alert redirects it without leaving the state or repeating the departure-web entry effect.

`PounceBehaviour` prefers a useful legal two-tile jump. The landing must be enterable, unoccupied and free of recognised movement hazards. A jump can pass over the middle tile.

A useful jump improves the number of actions needed to approach the alert or provides an approach where walking alone is unavailable. If no useful jump exists, the Spider uses safe walking.

The Spider investigates after reaching within one tile of the recorded location. It then searches for a Farmer within two tiles of that location.

A detected Farmer starts Hunting only if they are conscious, on the nest's map and within four tiles of the nest. A Farmer outside that pursuit boundary does not start a continued hunt.

Completed or unreachable investigations clear only the alert version they were investigating, so an older investigation cannot erase a newer alert.

### Spiderlings

One Spiderling is created on each Nesting entry when a safe adjacent spawn tile is available. Remaining in Nesting does not create additional Spiderlings.

Spiderlings have 10 HP and wander independently using the existing `WanderBehaviour`. They can be attacked but do not attack.

They disappear after eight completed turns. Idle turns also count towards their lifetime.

The nest tile is excluded from spawning so a newly created Spiderling does not immediately block its parent's return home.

### Implementation notes

The Spider's two new ordinary behaviours are strategic web construction in Weaving and alert-directed movement and investigation in Pouncing.

`SpiderTargetTracker` shares the targeting and environmental checks used by the states. `WebPlacement` provides ordinary placement and capacity operations, while `WebNetwork` also provides maintenance, entry effects and alert access.

`WebAlertSource` supplies recorded alerts to Pouncing movement, and `WebTriggerRecorder` allows a web to report a successful trigger without depending on the full network interface.

Walking and jumping respect terrain restrictions and Actor occupancy. Safe route planning also avoids recognised hazards, including fire items and electrically charged ground.

Placement and spawning use eligible locations. If no ordinary action is available, the Spider remains stationary.