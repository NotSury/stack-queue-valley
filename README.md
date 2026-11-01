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

## Assignment 2 REQ5 — Cave Mimic

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

## Assignment 2 REQ5 — Nest Spider

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

Test classes are stored in `test/`.
Test reports are generated in `target/surefire-reports/`.
The first run requires internet access to download build dependencies.

## A3 REQ 3: The Necromancer's Curse
Author: Lee Le Si (34599223)

Every monster in the Mines carries a curse that passes to whoever kills it.
Each curse spreads or changes the land in its own way, dead cursed monsters
leave remains that reshape the ground around them, and the Farmer can raise
those remains as undead minions that are immune to curses. The implemented
design (pitch, mechanics, architecture) is in `feature-proposal.md`.

### Original approved A3 REQ 3 proposal

<details>
<summary>Click to expand the proposal approved by our TA</summary>

**Pitch**

Every monster in the Mines carries a curse, and whoever kills it inherits that
curse. A Bat's Contagion spreads from actor to actor and blights the ground; a
Slime's Mire turns the land around its victim into a swallowing swamp; a Cave
Ghost's Soul Tether chains its victim to another creature and drags them
together. The Farmer can raise slain monsters as undead minions that are immune
to curses, so the smart necromancer lets the dead do the killing.

**How it plays**

1. Killing a cursed hostile passes its curse to the killer:
   - Bat → Contagion. Every turn, each actor next to the host catches
     Contagion, and the host's tile turns into Blight ground that infects
     anyone stepping on it.
   - Slime → Mire. Every 2 turns, the tiles around the host turn into Mire:
     nobody can walk onto it, and any item lying there sinks and is lost. Mire
     dries up after 4 turns.
   - Cave Ghost → Soul Tether. Every 3 turns, the host is chained to the
     nearest other actor within radius 3. Whenever that actor ends a turn more
     than 2 tiles away, it's dragged back next to the host.
2. The dead creature's remains change the land:
   - Bat: a bone burst spreads Contagion to every hostile within radius 2 and
     blights the area.
   - Slime: its body becomes a Mire pool around the remains.
   - Cave Ghost: it haunts the surrounding tiles, and anyone entering one gets
     a Soul Tether.
3. The Farmer raises the remains (costs stamina; one minion at a time):
   - Skeleton Bat: a plague aura. Every turn, adjacent hostiles catch Contagion.
   - Zombie Slime: every tile it leaves turns to Mire, building a wall that
     blocks pursuing hostiles. When it crumbles or is destroyed, its body
     becomes a Mire pool.
   - Wraith: every 3 turns it wails, pulling every hostile within radius 3 one
     tile closer and tethering them to itself so they can't escape.
4. Undead are immune to curses. A curse passed to an undead killer fizzles out,
   so minions are the safe way to finish cursed monsters.

**Architecture**

| Abstraction | Concrete class | New or retrofitted | When | What happens |
|---|---|---|---|---|
| Affliction (abstract class implementing the engine's Status) | Contagion | New | Every turn | The curse jumps to everyone standing next to the host including the Farmer. The host's tile turns into Blight, and anyone who steps on Blight catches the curse too. |
| | Mire | New | Every 2 turns | The tiles around the host turn into swamp (the host's own tile stays solid). Nobody can walk onto them, and any item lying there sinks and disappears. The swamp dries up after 4 turns. |
| | SoulTether | New | Every 3 turns | The host is chained to the closest actor within 3 tiles. If that actor moves more than 2 tiles away, it gets dragged back next to the host. |
| Cursed (interface) | Bat | Retrofitted | When it dies | Its killer catches Contagion. Its bones burst, giving Contagion to every hostile within 2 tiles and turning the nearby ground into Blight. It can be raised as a SkeletonBat. |
| | Slime | Retrofitted | When it dies | Its killer catches Mire. Its body melts into a swamp around the remains. It can be raised as a ZombieSlime. |
| | CaveGhost | Retrofitted | When it dies | Its killer gets a Soul Tether. The nearby tiles become haunted, and anyone who walks in gets a Soul Tether too. It can be raised as a Wraith. |
| Undead (abstract class extending the engine's Actor) | SkeletonBat | New | Every turn | Every hostile standing next to it catches Contagion, so the curse spreads through groups of enemies. |
| | ZombieSlime | New | Every turn, and when it ends | Every tile it leaves turns into swamp, building a wall behind it that hostiles can't cross. When it crumbles or is destroyed, its body becomes a swamp pool. |
| | Wraith | New | Every 3 turns | It wails: every hostile within 3 tiles is pulled 1 tile closer and chained to the Wraith, so they can't run away. |

Supporting new classes: Remains, RaiseDeadAction, MinionBehaviour,
TemporaryGround (abstract), Blight, MireGround, HauntedGround,
NecromancyCapability (HAS_MINION, UNDEAD).

Modified existing class: HostileCreature (passes the curse to the killer and
leaves Remains in its death hook).

Higher-level classes that depend only on the abstractions (DIP):
- HostileCreature uses only Cursed and Affliction when a creature dies.
- RaiseDeadAction uses only Cursed and Undead.
- MinionBehaviour uses only Undead and Hostile.

</details>

### Changes from the approved A3 REQ 3 proposal
To ensure changes are allowed for A3 REQ 3 and REQ 4 proposal for preventing overlapping features, an Ed forum question was posted
. From the teaching teams' reply, the TA/lecturer confirmed that minor changes are fine if documented here. All three
abstractions (Affliction, Cursed, Undead) and all nine concrete classes are
unchanged; only the effects below differ.

| # | Class | Approved proposal | Implemented | Reason |
|---|-------|-------------------|-------------|--------|
| 1 | SoulTether | Every 3 turns, the host is chained to the closest actor within 3 tiles. If that actor moves more than 2 tiles away, it's dragged back next to the host. | Every 3 turns, the host is linked to the closest actor within 3 tiles, and every other curse the host carries is copied onto that actor. If the host has no other curse, the linked actor's tile becomes Haunted Ground instead. | Removes forced movement, which used the same mechanism as A3 REQ 4's Nature's Sanctuary push (and restricted movement like Rainbow Storm's stun). |
| 2 | Wraith | Every 3 turns, it wails: every hostile within 3 tiles is pulled 1 tile closer and chained to the Wraith. | Every 3 turns, it wails: every curse on the Farmer is lifted and pushed onto a random hostile within 3 tiles; if none can take it, the curse fizzles out. Every tile it leaves becomes Haunted Ground. | Same as above: no A3 REQ 3 effect moves actors any more. |
| 3 | Pitch | "…a Cave Ghost's Soul Tether chains its victim to another creature and drags them together." | "…a Cave Ghost's Soul Tether binds its victim to another creature and shares every curse between them." | Matches change 1. |
| 4 | Contagion | Every turn, the curse jumps to everyone next to the host and the host's tile turns into Blight. | Same, but Contagion weakens as it spreads: each new infection is one generation further from the source, and the third generation can no longer spread or blight. | Without this, an actor standing on its own Blight was re-infected forever, so an outbreak never ended. |
| 5 | Supporting classes | – | Added `CurseApplier`, `CurseType`, `NearbyActors`, and the `CURSED_GROUND` capability. | Shared helpers so curse rules, area searches and the "don't cover cursed ground" rule are written once (DRY). |

### Implementation details
- **Values not stated in the proposal:** raising the dead costs 5 stamina;
  minions last 15 turns; the Farmer can have one minion at a time.
- **Minion behaviour:** attack an adjacent hostile; otherwise move towards the
  closest hostile within 4 tiles; otherwise return to the Farmer if more than 2
  tiles away; otherwise wander. Hostiles attack minions too.
- **Curse durations:** Contagion 6 turns, Mire 6 turns, Soul Tether 9 turns.
  Cursed ground lasts Blight 4, Mire 4, Haunted Ground 5 turns, then the
  original ground returns. Remains stay until raised.
- **Display characters:** Blight `;` (`:` is already used by Web), Mire `=`,
  Haunted Ground `?`, Remains `%`, Skeleton Bat `B`, Zombie Slime `Z`,
  Wraith `V`.

### Running the A3 REQ 3 tests
Tests: `AfflictionTest`, `RaiseDeadActionTest`, `UndeadTest` (17 cases:
normal, edge and invalid). All are deterministic: curses use no randomness, and
the Wraith is given a seeded `Random`.

- IntelliJ: Ctrl+click the three test classes → right-click → Run.
- Maven: `mvn test -Dtest=AfflictionTest,RaiseDeadActionTest,UndeadTest`

## A3 REQ 4: Magical Farm Companions
Author: Vanessa Liau Jia Yee (34599398)

Magical Farm Companions introduces three wandering animals: TimeRabbit, PrismFox
and SanctuaryDeer. Each companion provides two Farmer-activated abilities
sharing three magical charges. Activating an ability requires adjacency, consumes
one Farmer turn and costs no stamina. Charges are consumed only when an activation
succeeds.

The implemented mechanics and class architecture are documented in `Feature Proposals.md`.

### Original approved A3 REQ 4 proposal
| Abstraction                                                 | Concrete class  | New or retrofitted | Complex effect |
|-------------------------------------------------------------|-----------------|---|---|
| `MagicalCompanion` (abstract class, extends engine 'Actor') | `TimeRabbit`    | New | Temporal Freeze: 3x3 lifecycle suspension for three ticks |
|                                                             | `PrismFox`      | New | Magical Harvest Trail: persistent 3x3 fresh-produce collection |
|                                                             | `SanctuaryDeer` | New | Living Soil and Nature's Regeneration |
| `FarmMagic` (interface)                                     | `GrowthMagic`   | New | Guaranteed 3x3 plant growth |
|                                                             | `StormMagic`    | New | Rainbow Storm: persistent 5x5 hostile stun and damage |
|                                                             | `ShieldMagic`   | New | Nature's Sanctuary: healing, repulsion and re-entry block |

Supporting classes: `ActivateMagicAction`, `TemporalFreezeEffect`, `HarvestTrailEffect`, `RainbowStormEffect`,
`LivingSoilEffect`, `NatureSanctuaryEffect`, `GrowthAcceleratable`.

### Changes from the approved A3 REQ 4 proposal
The two abstractions, six concrete classes and six complex effects are unchanged.
The Ed forum answer was recorded under REQ 3 (minor changes are fine if documented) also applies here.

### Implementation details
- **Menu:** `MagicalCompanion.allowableActions` offers two action to an actor with `ActorRole.FARMER` while
charges remain. `ActivateMagicAction` depends only on `FarmMagic`. `ActivateOwnAbilityAction`
runs the companion's own ability. Both extend `CompanionAction`.
- **Charges:** owned by `MagicalCompanion`. A charge is spent only if the ability took effect.
- **Timing:** effects are registered on activation and ticked in companion's `playTurn`.
In play-testing the order each round was the Farmer, then the other actors in shuffled order,
then ground ticks. A hostile that acts before the fox escapes that round's stun.
- **Known limitations:** pushes check only the destination; a companion's effects stop ticking if it
leaves the map.
- **Modified teammate files:** listed in `feature-proposal.md`, each agreed with its author.

### Running the A3 REQ 4 tests
Tests: `GrowhtAndFreezeTest`, `MagicalCompanionChargesTest`, `RainbowStormTest`, `MagicalCompanionsTest`. 
They are deterministic: effects are ticked directly and the tests use fixed random seeds when necessary to
ensure reproducible results.

- IntelliJ: right-click the four test classes, then Run.
- Maven: `mvn test -Dtest=GrowhtAndFreezeTest, MagicalCompanionChargesTest, RainbowStormTest, MagicalCompanionsTest`

## Assignment 3 REQ5 — Starfall: Celestial Forge

Starfall is a celestial-event system that integrates NASA's Near-Earth Object Web Service (NeoWs) API into the game. The Farmer can activate an Observatory to retrieve asteroid data, which is used to generate meteor impacts, transform terrain, mutate crops, create resources, summon hostile creatures and activate environmental effects.

The Observatory is displayed as `O` and is located beside the Farmer's starting position on the Inherited Farm.

### Settings

| Setting | Value |
|---|---|
| External API | NASA Near-Earth Object Web Service (NeoWs) |
| API endpoint | `https://api.nasa.gov/neo/rest/v1/feed` |
| Observatory symbol | `O` |
| Observatory location | Inherited Farm, (1, 3) |
| Scan stamina cost | 3 stamina |
| Scan requirement | Farmer must be on or adjacent to the Observatory |
| API request range | 2 calendar days |
| Event types | Meteor Shower, Asteroid Fragment, Celestial Storm |
| Environmental fields | Astral Blessing, Gravitational Distortion, Cosmic Radiation |
| Impact reactions | Crop mutation, terrain transformation, actor damage, resource generation and guardian spawning |
| Maximum field radius | 2 tiles |
| Maximum initial field duration | 4 turns |
| Maximum gravity extension | 4 additional turns |
| Active scan limit | One environmental field at a time |

A successful Observatory scan deducts stamina and activates a celestial event. Another scan cannot begin until the previous environmental field expires.

### Dynamic API requests

The NASA request is generated using the current game state rather than a fixed URL.

`ObservatoryScanPlanner` calculates the request dates using the Farmer's current stamina, X and Y coordinates, the number of completed Observatory scans, and the current date.

The calculation is:

```text
signature = 3 × stamina + 5 × playerX + 11 × playerY + 7 × completedScans
offsetDays = signature mod 60
endDate = currentDate - (1 + offsetDays) days
startDate = endDate - 1 day
```

For example, on 31 October 2026, a Farmer with 20 stamina at (0, 3) and no completed scans produces the following request:

```text
https://api.nasa.gov/neo/rest/v1/feed?start_date=2026-09-26&end_date=2026-09-27&api_key=YOUR_NASA_API_KEY
```

Changing the Farmer's stamina, position or completed scan count can change the requested dates. This ensures the API request is dynamically influenced by gameplay.

### Expected API response

`NasaAsteroidParser` expects a NASA JSON response containing `near_earth_objects`, grouped by approach date.

An example of the expected JSON structure is:

```json
{
  "near_earth_objects": {
    "2026-09-26": [
      {
        "id": "123456",
        "name": "Example Asteroid",
        "is_potentially_hazardous_asteroid": false,
        "estimated_diameter": {
          "meters": {
            "estimated_diameter_min": 30.0,
            "estimated_diameter_max": 38.0
          }
        },
        "close_approach_data": [
          {
            "close_approach_date": "2026-09-26",
            "relative_velocity": {
              "kilometers_per_second": "16.0"
            },
            "miss_distance": {
              "kilometers": "500000.0"
            }
          }
        ]
      }
    ]
  }
}
```

The parser extracts the asteroid's ID, name, average estimated diameter, velocity, miss distance, hazardous classification and approach date.

These values are stored in `AsteroidData` and used to determine the celestial event and its gameplay effects. The resulting events are fictional game mechanics, not NASA impact predictions.

### Celestial events

| Event | Trigger condition | Gameplay effect |
|---|---|---|
| Meteor Shower | Non-hazardous asteroid with diameter below 80 metres | Multiple small impacts, resources and Astral Blessing |
| Asteroid Fragment | Non-hazardous asteroid with diameter at least 80 metres, or hazardous asteroid travelling below 20 km/s | Concentrated impact, resources, possible guardian and Gravitational Distortion |
| Celestial Storm | Hazardous asteroid travelling at least 20 km/s | Stronger multiple impacts, guardian spawning and Cosmic Radiation |

Each event calculates an `ImpactProfile` using NASA asteroid measurements. The profile determines impact count, radius, damage, ore count, guardian count and environmental field duration.

The event manager selects suitable impact locations near the Observatory and applies the reactions in the following order:

1. Crop Mutation
2. Terrain Transformation
3. Impact Damage
4. Resource Generation
5. Guardian Spawning

Impacts can create `ImpactCrater` ground and `MeteoriteOre` items. Actors within an impact radius receive damage, while protected ground and structures are not transformed.

An actor can receive direct impact damage only once per celestial event, even if multiple impact areas overlap. Guardian spawning is also limited by the event's total guardian allocation.

### Cosmic crops and consumables

Meteor impacts can transform eligible crops into Cosmic Crops or destroy them, depending on the impact damage and mutation probability.

Five Cosmic Crop varieties are supported.

| Cosmic item | Effect |
|---|---|
| Cosmic Parsnip | Restores up to 30 HP |
| Cosmic Tomato | Restores up to 8 stamina |
| Cosmic Cauliflower | Restores 10 HP per turn for 2 turns |
| Cosmic Melon | Causes a delayed 12-damage explosion against adjacent actors |
| Cosmic Strawberry | Protects against 2 subsequent Cosmic Radiation field ticks |

Cosmic Crops can be harvested to obtain their corresponding consumable items.

### Environmental fields

Each successful celestial event creates one temporary environmental field at its main impact location.

| Field | Effect |
|---|---|
| Astral Blessing | Restores 4 HP and 2 stamina per turn to nearby eligible actors. When it expires, affected Impact Craters become Starlite Soil and eligible Meteorite Ore is refined into Starlite Ore. |
| Gravitational Distortion | Reduces nearby actors' stamina by 3 per turn and attracts loose celestial resources towards its centre. Absorbed resources can extend the field's duration by up to 4 additional turns. |
| Cosmic Radiation | Damages nearby unprotected actors based on the impact profile and can generate temporary Fire on eligible terrain. Cosmic Strawberry provides temporary immunity. |

Environmental fields operate automatically during game ticks and disappear when their durations expire.

Astral Guardians can also appear during hazardous events. They behave as hostile creatures and drop Meteorite Ore when defeated.

### NASA API key configuration

The NASA API requires an API key for live Observatory scans.

1. Visit `https://api.nasa.gov/` and request an API key.
2. Open Windows **Environment Variables**.
3. Under **User variables**, create a new variable named `NASA_API_KEY`.
4. Set its value to the API key obtained from NASA.
5. Restart IntelliJ IDEA so the updated environment variable is available.
6. Open the Maven project and run `game.Application`.
7. Activate the Observatory in the game to perform a NASA scan.

`NasaApiConfig` reads the key using `System.getenv("NASA_API_KEY")`.

The API key must not be hardcoded, committed to GitLab or included in submitted project files. Local IDE configuration files containing credentials should also be excluded from shared ZIP files.

An internet connection is required for live Observatory scans, but not for the automated unit tests.

### Failure handling

The Observatory handles unavailable NASA responses, invalid data, interrupted requests and empty asteroid feeds without deducting stamina.

Scanning is also rejected when the Farmer has insufficient stamina, is too far away, an environmental field is still active, or no suitable impactable terrain is available.

Failed scans do not increase the completed scan count or create environmental fields.

### Implementation notes

The implementation separates API communication, JSON parsing, event calculations and gameplay effects into independent classes.

`NasaAsteroidClient` performs HTTP requests, while `NasaAsteroidParser` converts responses into `AsteroidData`. `ObservatoryScanPlanner` generates dynamic date parameters based on the current game state.

The feature introduces three main abstractions:

| Abstraction | Concrete implementations |
|---|---|
| `CelestialEvent` | `MeteorShower`, `AsteroidFragment`, `CelestialStorm` |
| `CelestialReaction` | `CropMutationReaction`, `TerrainReaction`, `ImpactDamageReaction`, `ResourceReaction`, `GuardianReaction` |
| `EnvironmentalField` | `AstralBlessing`, `GravitationalDistortion`, `CosmicRadiation` |

`CelestialEventManager` depends on the `CelestialEvent`, `CelestialReaction` and `AsteroidFeed` interfaces rather than their concrete implementations.

`Observatory` and `ActivateObservatoryAction` depend on the `CelestialScanService` interface.

This supports the Single Responsibility Principle by separating responsibilities, the Open-Closed Principle by allowing new events and reactions to be added through existing abstractions, and the Dependency Inversion Principle by making higher-level classes depend on abstractions.

The API feature is tested using deterministic asteroid data, fixed clocks, controlled randomness and a fake NASA feed, allowing tests to run without external network access.

---

## Running unit tests

Requirements: JDK 17 or later and Apache Maven.

The project uses JUnit 5 for automated tests, Mockito where appropriate, and Maven Surefire for test execution and reporting.

From the project root, run:

```shell
mvn clean test
```

Alternatively, use IntelliJ IDEA's Maven panel and run **Lifecycle → clean**, followed by **Lifecycle → test**.

Test classes are stored in `test/`. Test reports are generated in `target/surefire-reports/`.

To run an individual test class:

```shell
mvn -Dtest=ObservatoryIntegrationTest test
```

On Windows PowerShell, a test report can be viewed using:

```powershell
Get-Content .\target\surefire-reports\game.events.ObservatoryIntegrationTest.txt
```

### REQ5 test coverage

| Test class | Tested functionality |
|---|---|
| `ObservatoryScanPlannerTest` | Dynamic API dates, game-state changes and invalid inputs |
| `CelestialEventTest` | Event classification, impact calculations and field creation |
| `CosmicCropMutationTest` | Crop mutation, destruction and harvesting |
| `CosmicConsumableTest` | Five Cosmic Item effects and duration boundaries |
| `EnvironmentalFieldTest` | Environmental effects, duration, terrain changes and resource movement |
| `CelestialReactionTest` | Impact damage, resource generation, protected terrain and guardian spawning |
| `ObservatoryIntegrationTest` | Complete scan workflow, API failures, cooldowns and gameplay integration |

REQ5 tests use fixed dates, deterministic random values and simulated NASA responses to avoid depending on live network access, API keys or external state.

The automated REQ5 suite has recorded 77 passing test executions with no failures or errors.

The full assignment test suite must also include meaningful tests for REQ1–REQ4, covering at least three distinct cases per functional requirement.

The first Maven build may require internet access to download dependencies.