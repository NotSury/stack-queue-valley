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