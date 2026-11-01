# Feature Proposals – MA_AppliedSession4_C

## A3 REQ 3: The Necromancer's Curse
Author: Lee Le Si (34599223)

> This describes the implemented version. Minor changes from the TA-approved
> proposal (Soul Tether, Wraith, Contagion) are listed in README.md under
> "Changes from the approved A3 REQ 3 proposal".

### Pitch
Every monster in the Mines carries a curse, and whoever kills it inherits that
curse. A Bat's Contagion spreads from actor to actor and blights the ground; a
Slime's Mire turns the land around its victim into a swallowing swamp; a Cave
Ghost's Soul Tether binds its victim to another creature and shares every curse
between them. The Farmer can raise slain monsters as undead minions that are
immune to curses, so the smart necromancer lets the dead do the killing.

### Mechanics
- **Killing a cursed hostile passes its curse to the killer.**
    - **Bat → Contagion:** every turn, each actor next to the host catches
      Contagion, and the host's tile turns into Blight, which infects anyone
      standing on it. Contagion weakens each time it spreads, so every outbreak
      eventually dies out.
    - **Slime → Mire:** every 2 turns, the empty tiles around the host turn into
      Mire. Nobody can walk onto Mire, and any item lying there sinks and is lost.
      Mire dries up after 4 turns.
    - **Cave Ghost → Soul Tether:** every 3 turns, the host is linked to the
      closest actor within 3 tiles, and every other curse the host carries is
      copied onto that actor. If the host has no other curse, that actor's tile
      becomes Haunted Ground instead.
- **The dead creature's remains change the land.**
    - Bat: its bones burst, giving Contagion to every hostile within 2 tiles and
      blighting the tiles around the remains.
    - Slime: its body melts into a Mire pool around the remains.
    - Cave Ghost: the tiles around the remains become Haunted Ground; anyone
      standing on it gets a Soul Tether.
- **The Farmer raises the remains** as an undead minion (costs 5 stamina; one
  minion at a time; a minion lasts 15 turns, then crumbles):
    - **Skeleton Bat:** plague aura – every turn, adjacent hostiles catch Contagion.
    - **Zombie Slime:** every tile it leaves turns into Mire, walling off pursuing
      hostiles. When it crumbles or is destroyed, it melts into a Mire pool.
    - **Wraith:** every 3 turns it wails, lifting every curse off the Farmer and
      pushing each one onto a random hostile within 3 tiles; a curse with no
      hostile in range fizzles out. Every tile it leaves becomes Haunted Ground.
- **Minions fight for the Farmer:** they attack adjacent hostiles, hunt hostiles
  within 4 tiles, and otherwise stay near the Farmer. Hostiles attack minions too.
- **Undead are immune to curses.** A curse passed to an undead killer fizzles
  out, so minions are the safe way to finish cursed monsters.
- Cursed ground (Blight, Mire, Haunted Ground) and Remains are never covered by
  more cursed ground, and every cursed ground restores the original ground when
  it wears off.

### Architecture

| Abstraction | Concrete class | New or retrofitted |
|---|---|---|
| `Affliction` (abstract class, implements engine `Status`) | `Contagion` | New |
| | `Mire` | New |
| | `SoulTether` | New |
| `Cursed` (interface) | `Bat` | Retrofitted |
| | `Slime` | Retrofitted |
| | `CaveGhost` | Retrofitted |
| `Undead` (abstract class, extends engine `Actor`) | `SkeletonBat` | New |
| | `ZombieSlime` | New |
| | `Wraith` | New |

**Supporting new classes:** `Remains`, `RaiseDeadAction`, `MinionBehaviour`,
`TemporaryGround` (abstract), `Blight`, `MireGround`, `HauntedGround`,
`CurseApplier`, `CurseType`, `NearbyActors`, `NecromancyCapability`
(`HAS_MINION`, `UNDEAD`, `CURSED_GROUND`).

**Modified existing class:** `HostileCreature` (its death hook passes the curse
to the killer, applies the creature's death effect and leaves `Remains`).

**Higher-level classes that depend only on the abstractions (DIP):**
- `HostileCreature` uses only `Cursed` and `Affliction` when a creature dies.
- `RaiseDeadAction` uses only `Cursed` and `Undead`.
- `MinionBehaviour` uses only `Hostile` (through `NearbyActors`) and the master `Actor`.

## A3 REQ 4: Magical Farm Companions
Author: Vanessa Liau Jia Yee (34599398)

> This describes the implemented version of Magical Farm Companions.
> Changes from the TA-approved proposal are documented in README.md.

### Pitch
Magical Farm Companions introduces three wandering magical animals(TimeRabbit,
PrismFox and SanctuaryDeer) that assist the Farmer through time manipulation, 
automated resource collection, environmental restoration and defensive magic.
Each companion has two abilities that the Farmer can activate while standing next
to it. The two abilities share three activation charges, cost no stamina and use 
one Farmer turn.

### Mechanics
- **Activating:** standing next to a companion adds two actions to the Farmer's menu
    while the companion still has charges. Each companion has three shared activation
    charges across both abilities. Successful activations consume one charge, while failed
    activations still use the Farmer's turn without consuming a charge. Area effects remain
    fixed at their activation locations even if the companion wanders away.
- **TimeRabbit** (3x3 area centred on the rabbit)
    - **Growth Magic:** every plant in the area with a next growth stage advances
        by exactly one stage, ignoring its age, interval and growth probability.
        The new stage starts at age 0 and follows normal REQ1 rules. Mature crops
        (Tomato, Parsnip, any crop with no next stage) are unaffected, and no spreading
        or produce happens. Plants held by Temporal Freeze cannot be accelerated. If no plant
        grows, the Farmer's turn is used but no charge is spent.
    - **Temporal Freeze:** a field lasting three effect ticks that stops plants in the area
        from ageing, growing, spreading and producing. The field is re-applied every tick, so 
        plants that appear inside it later are frozen too. Watering and harvesting still work.
        When the field expires the plants resume from their preserved age, and Growth Magic works
        on them again. A TimeRabbit cannot activate another Temporal Freeze while its existing
        field is active. Failed activation uses the Farmer's turn but no charge.
- **PrismFox:** (3x3 trail area, 5x5 storm area, both centred on the fox)
    - **Magical Harvest Trail:** a collection area lasting three effect ticks. Each tick it moves
        fresh produce lying on the ground in the area into the activating Farmer's inventory,
        including produce dropped there after activation. It collects at most five items in total
        and ends early once the fifth is collected. Rotten produce, tools, weapons and other non-produce
        items stay on the ground, and planted crops are never harvested or removed.
        The area stays where it was cast, even if the fox or Farmer moves away. If there is no produce at
        first, the trail still activates.
    - **Rainbow Storm:** a storm lasting three effect ticks. Each tick, every hostile creature inside the area
        takes 5 damage and is stunned, so it cannot move or attack. Hostile creatures that enter the area later
        are affected too. The Farmer, farm animals and allied undead are never affected. A creature killed by the
        storm dies through its normal death handling, so its REQ3 curse and remains still apply. Other status effects
        on the creature are neither removed nor paused.
    - **Overlap:** the two abilities may run at the same time, even over the same tiles. A second cast of the same
        ability is refused while one is active. A refused cast uses the Farmer's turn but no charge.
- **SanctuaryDeer:** (3x3 restoration area centred on the deer for three effect ticks)
    - **Living Soil:** On each tick, Dirt tiles in the area become Soil. The first tick restores the ground without
        growing plants. On later ticks, each tile newly restored
        from Dirt (for example, left behind after a crop is harvested) gets one
        independent 50% chance to regrow a random Strawberry Seedling, Cauliflower
        Sprout or Melon Sprout (Nature's Regeneration). Existing crops, Soil and
        special ground are never overwritten. A second cast is refused while active.
    - **Nature's Sanctuary:** immediately restores 2 HP and 2 stamina to the
        Farmer and creates a fixed 3x3 shield centred on the Farmer. Hostiles inside
        the shield are pushed along a single axis to the nearest free tile outside it
        (search distance 1 to 5, ties resolved up, right, down, left). If none is
        found they stay. Hostiles within the 5x5 detection area are forbidden from
        entering the shield on their next turn; the ban is cleared at the start of
        that creature's following turn, so it does not depend on actor order. The
        push checks the destination only, so it can pass over obstacles. This is a
        re-entry ban, not attack immunity. Recasting is allowed while charges remain.
        Movement and jump planners consult the restriction.
    - **Restriction expiry:** each affected hostile creature retains its forbidden tiles while selecting and executing 
        its next action. The restriction is cleared at the start of that creature's following turn, avoiding dependence 
        on SanctuaryDeer's position in the engine's actor order. This is a per-creature turn rule, not a global round-long
        shield.
  
### Relationship with REQ3
Rainbow Storm affects hostile creatures through the `Hostile` and `Stunnable` capabilities without removing their
existing curses or other status effects. When a hostile dies, its normal death handling remains
responsible for curses and remains. Nature's Sanctuary affects only `Repellable` creatures, leaving allied
undead unaffected. Living Soil transforms only ground supporting SoilConvertible, ensuring cursed ground
and remains are preserved.

### Architecture
| Abstraction                                                 | Concrete class  | New or retrofitted | Complex effect |
|-------------------------------------------------------------|-----------------|---|---|
| `MagicalCompanion` (abstract class, extends engine 'Actor') | `TimeRabbit`    | New | Temporal Freeze: 3x3 lifecycle suspension for three ticks |
|                                                             | `PrismFox`      | New | Magical Harvest Trail: persistent 3x3 fresh-produce collection |
|                                                             | `SanctuaryDeer` | New | Living Soil and Nature's Regeneration |
| `FarmMagic` (interface)                                     | `GrowthMagic`   | New | Guaranteed 3x3 plant growth |
|                                                             | `StormMagic`    | New | Rainbow Storm: persistent 5x5 hostile stun and damage |
|                                                             | `ShieldMagic`   | New | Nature's Sanctuary: healing, repulsion and re-entry block |

**Class-to-ability mapping:** `TimeRabbit` owns Temporal Freeze, `PrismFox` owns
Magical Harvest Trail, `SanctuaryDeer` owns Living Soil, and the three
`FarmMagic` classes implement Growth Magic, Rainbow Storm and Nature's Sanctuary.
Only the three `FarmMagic` classes implement that interface; the companion-owned abilities
are TimedAreaEffects started by the companion.

**Supporting new classes (not counted among the six):** `CompanionAction` (abstract class
providing shared charge and adjacency rules), `ActivateMagicAction`, `ActivateOwnAbilityAction`,
`TimedAreaEffect` (abstract class), `TemporalFreezeEffect`, `HarvestTrailEffect`, `RainbowStormEffect`,
`LivingSoilEffect`, `NatureSanctuaryEffect`, `AreaLocationHelper`, `MovementRestrictions`and the 
capability interfaces `GrowthAcceleratable`, `Freezable`, `FreshProduce`, `Stunnable`, 
`Repellable` and `SoilConvertible`.

**Modified existing classes:** 
- REQ 1: `Plant` (implements `GrowthAcceleratable`, `Freezable`; `addGrowBehaviour`;
  freeze holds), `GrowBehaviour` (implements `GrowthAcceleratable`),
  `StrawberrySeedling`, `StrawberrySprout`, `MelonSprout`, `CauliflowerSprout`
  (register growth through `addGrowBehaviour`), `Dirt` (implements `SoilConvertible`).
- Items: `Strawberry`, `Tomato`, `Parsnip` (implement `FreshProduce`).
- REQ 3 / REQ 5: `HostileCreature` and `StatefulCreature` (implement `Stunnable`
  and `Repellable`), `WanderBehaviour`, `MapMovementPlanner`, `MapJumpPlanner`
  (consult `MovementRestrictions`).
- No engine classes are modified.

**Higher-level classes that depend only on the abstractions (DIP):**
- `ActivateMagicAction` uses only `FarmMagic`.
- `GrowthMagic` uses only `GrowthAcceleratable`.
- `TemporalFreezeEffect` uses only `Freezable`.
- `HarvestTrailEffect` uses only `FreshProduce`.
- `RainbowStormEffect` uses only `Hostile` and `Stunnable`.
- `LivingSoilEffect` uses only `SoilConvertible` and `PlantFactory`.
- `NatureSanctuaryEffect` uses only `Repellable`.
- `MagicalCompanion` uses only `TimedAreaEffect`.

## A3 REQ 5: Starfall: Celestial Forge
Author: Chin Kai Xuan (35730374)

> This describes the implemented version of Starfall: Celestial Forge.

### Pitch

Starfall introduces a NASA-powered celestial event system where the Farmer activates an Observatory to retrieve real asteroid information. Depending on the asteroid's diameter, velocity and hazardous classification, meteor impacts can transform terrain, mutate crops, generate resources, summon Astral Guardians and create temporary environmental fields. These events fundamentally alter the game world and introduce new environmental hazards, resources and gameplay interactions.

### Mechanics

- **Activating the Observatory:** The Observatory (`O`) is located at (1, 3) on the Inherited Farm. The Farmer must stand adjacent to it and spend 3 stamina to perform a successful scan.
    - The Observatory is impassable, preventing actors from occupying it.
    - Another scan cannot begin while an environmental field remains active.
    - Failed scans do not consume stamina or increase the completed scan count.

- **Dynamic NASA scanning:** `ObservatoryScanPlanner` generates a two-day request window based on the Farmer's current stamina, map coordinates and number of completed scans. `NasaAsteroidFeed` retrieves and parses the response into `AsteroidData`, which influences the resulting event.

- **Three celestial events:**
    - **Meteor Shower:** small, non-hazardous asteroids generate multiple impacts, Meteorite Ore and an Astral Blessing field.
    - **Asteroid Fragment:** large non-hazardous or lower-speed hazardous asteroids generate a concentrated impact, resources, possible guardian spawning and Gravitational Distortion.
    - **Celestial Storm:** fast, hazardous asteroids generate stronger multiple impacts, guardians and Cosmic Radiation.

- **Meteor impact reactions:** each event creates an `ImpactProfile` containing impact count, radius, damage, ore count, guardian count and field duration.
    - **Crop Mutation:** eligible crops may transform into Cosmic Crops or be destroyed.
    - **Terrain Transformation:** impactable ground becomes an Impact Crater.
    - **Impact Damage:** actors within the impact area receive damage, with each actor damaged at most once per celestial event.
    - **Resource Generation:** Meteorite Ore is generated on suitable impact sites, including neighbouring craters.
    - **Guardian Spawning:** Astral Guardians appear dynamically at safe impact locations, fight as hostile creatures and drop Meteorite Ore when defeated.
    - Existing Impact Craters can be targeted by later scans, while protected terrain is preserved.

- **Three environmental fields:** one field is created per successful event and operates automatically during game ticks.
    - **Astral Blessing (`!`):** restores 4 HP and 2 stamina per tick. On expiration, it transforms affected Impact Craters into Starlite Soil and refines eligible Meteorite Ore into Starlite Ore.
    - **Gravitational Distortion (`Q`):** drains 3 stamina per tick and pulls loose celestial resources towards its centre. Absorbed resources can extend its duration by up to 4 additional turns.
    - **Cosmic Radiation (`X`):** damages unprotected actors and can generate temporary Fire on eligible terrain. Radiation immunity prevents this damage.

- **Cosmic Crops:** meteor impacts can produce five harvestable Cosmic Crop varieties.
    - **Cosmic Parsnip:** restores up to 30 HP.
    - **Cosmic Tomato:** restores up to 8 stamina.
    - **Cosmic Cauliflower:** restores 10 HP per turn for 2 turns.
    - **Cosmic Melon:** triggers a delayed 12-damage explosion against adjacent actors, including their normal unconscious and death effects.
    - **Cosmic Strawberry:** protects against the next 2 Cosmic Radiation field ticks.

- **Failure handling:** missing API keys, unavailable NASA responses, interrupted requests, invalid responses and empty asteroid feeds are handled without spending the Farmer's stamina.

### Architecture

| Abstraction | Concrete class | New or retrofitted | Complex effect |
|---|---|---|---|
| `CelestialReaction` (interface) | `CropMutationReaction` | New | Area-based crop mutation and terrain destruction |
| | `ResourceReaction` | New | Dynamic Meteorite Ore generation and placement around impact sites |
| | `GuardianReaction` | New | Safe spawning of hostile Astral Guardians with combat and resource drops |
| `EnvironmentalField` (abstract class, extends engine `Item`) | `AstralBlessing` | New | Persistent area recovery, terrain restoration and resource refinement |
| | `GravitationalDistortion` | New | Resource attraction, stamina drain and duration extension through absorption |
| | `CosmicRadiation` | New | Area damage, immunity interaction and temporary Fire generation |

**Additional concrete reaction classes (new):** `TerrainReaction` and `ImpactDamageReaction`, which also implement `CelestialReaction`.

**Additional new abstractions:**
- `CelestialEvent` (interface): implemented by `MeteorShower`, `AsteroidFragment` and `CelestialStorm` (all new). Each class classifies asteroid data, calculates an impact profile and creates its corresponding environmental field.
- `AsteroidFeed` (interface): implemented by `NasaAsteroidFeed` to separate external data retrieval from gameplay processing.
- `CelestialScanService` (interface): implemented by `CelestialEventManager` to provide scanning operations without exposing the concrete manager.
- `CosmicMutable` (interface): implemented by existing `ParsnipCrop`, `TomatoCrop`, `CauliflowerCrop`, `MelonCrop` and `StrawberryCrop` classes (retrofitted). Each crop identifies its corresponding Cosmic Crop variety.

**Supporting new classes:** `AsteroidData`, `NasaApiConfig`, `NasaAsteroidClient`, `NasaAsteroidParser`, `NasaAsteroidFeed`, `ObservatoryScanPlanner`, `ImpactProfile`, `CelestialEventManager`, `Observatory`, `ActivateObservatoryAction`, `CosmicCrop`, `ImpactCrater`, `StarliteSoil`, `MeteoriteOre`, `StarliteOre`, `AstralGuardian`, `CosmicParsnip`, `CosmicTomato`, `CosmicCauliflower`, `CosmicMelon`, `CosmicStrawberry`, `MelonBurstStatus` and `RadiationImmunityStatus`.

**Reused existing classes:** `AreaLocationHelper` for circular-area and distance calculations, `MovementSafety` for safe spawning and resource movement, `Explosion` for Cosmic Melon, and `HealthOverTimeStatus` for temporary healing.

**Modified existing classes:**
- `StackQueueValleyWorld` integrates the Observatory, NASA dependencies, event strategies and ordered reactions into the game.
- `ParsnipCrop`, `TomatoCrop`, `CauliflowerCrop`, `MelonCrop` and `StrawberryCrop` implement `CosmicMutable`.

**Higher-level classes that depend on abstractions (DIP):**
- `CelestialEventManager` uses `AsteroidFeed`, `CelestialEvent` and `CelestialReaction` for asteroid retrieval, event selection and impact processing.
- `Observatory` depends on `CelestialScanService` to determine scan availability and offer the activation action.
- `ActivateObservatoryAction` depends on `CelestialScanService` to execute scans without knowing the concrete manager.

### Request

NASA's Near-Earth Object Web Service (NeoWs) receives a dynamic request generated from the current game state.

`ObservatoryScanPlanner` calculates the date range using the Farmer's stamina, X coordinate, Y coordinate and number of completed scans:

```text
signature = 3 × stamina + 5 × playerX + 11 × playerY + 7 × completedScans
offsetDays = signature mod 60
endDate = currentDate - (1 + offsetDays) days
startDate = endDate - 1 day
```

For example, assuming the current UTC date is 31 October 2026, the Farmer has 20 stamina, is at (0, 3), and has completed 0 scans:

```text
signature = 3(20) + 5(0) + 11(3) + 7(0) = 93
offsetDays = 93 mod 60 = 33

startDate = 2026-09-26
endDate = 2026-09-27
```

**Example dynamic API request:**

```text
https://api.nasa.gov/neo/rest/v1/feed?start_date=2026-09-26&end_date=2026-09-27&api_key=YOUR_NASA_API_KEY
```

Changing the Farmer's stamina, position or completed scan count can change the dates sent to NASA. The actual asteroid data returned influences the event type and gameplay effects, rather than merely producing console output.

**Security:** `NasaApiConfig` reads the key from the `NASA_API_KEY` environment variable using `System.getenv()`. The real API key is not stored in the source code or proposal and must not be committed to Git.

### Schema

`NasaAsteroidParser` expects a JSON response containing a `near_earth_objects` object, with asteroid arrays grouped by approach date.

**Example expected JSON structure:**

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

The parser extracts the asteroid's ID, name, average estimated diameter, velocity, miss distance, hazardous classification and approach date into `AsteroidData`.

`CelestialEvent` implementations use these measurements to calculate an `ImpactProfile`, which determines meteor impacts, terrain changes, resource generation, guardian spawning and environmental effects.

The celestial events are fictional gameplay mechanics and are not NASA impact predictions.