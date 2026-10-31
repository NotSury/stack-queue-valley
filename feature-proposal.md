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

> This describes the implemented version and planned features of Magical Farm Companions.
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
    while the companion still has charges. The companion's area effects stay fixed where 
    they were cast, even if the companion wanders away.
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
- **PrismFox:** *to be added.* 
- **SanctuaryDeer:** *to be added.* 

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

**Supporting new classes (not counted among the six):** `CompanionAction` (abstract,
shared charge and adjacency rules), `ActivateMagicAction`, `ActivateOwnAbilityAction`,
`TimedAreaEffect` (abstract), `TemporalFreezeEffect`, `AreaLocationHelper`, and the 
interfaces `GrowthAcceleratable` and `Freezable`.

**Modified existing classes (REQ1):** 
- `Plant` implements `GrowthAcceleratable` and `Freezable`, and registers growth
   through `addGrowBehaviour`.
- `GrowBehaviour` implements `GrowthAcceleratable`, so the forced transition reuses
   the plant's own `PlantFactory`.
- `StrawberrySeedling`, `StrawberrySprout`, `MelonSprout` and `CauliflowerSprout`
   register their growth with `addGrowBehaviour`.

**Higher-level classes that depend only on the abstractions (DIP):**
- `ActivateMagicAction` uses only `FarmMagic`.
- `GrowthMagic` uses only `GrowthAcceleratable`.
- `TemporalFreezeEffect` uses only `Freezable`.
- `MagicalCompanion` uses only `TimedAreaEffect`.