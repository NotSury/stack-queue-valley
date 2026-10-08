package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.WebAbility;
import game.interfaces.WebNetwork;

import java.util.*;

/**
 * Ranks web sites by choke points, existing web coverage and walking distance.
 * Uses safe breath-first routes and fixed coordinate tie-breakers.
 *
* @author Surya Muni Peragas 35651628
* @author Lee Le Si 34599223
* @version 1.1.0
 */
public class MapWebPlanner implements WebPlanner {

    private final int placementRadius;

    /**
     * Creates a planner for web sites within the supplied nest radius.
     *
     * @param placementRadius the maximum web-site distance from the nest
     */
    public MapWebPlanner(int placementRadius) {
        this.placementRadius = placementRadius;
    }

    /**
     * Selects the best eligible web site within the nest's placement radius.
     * Prioritises choke points, fewer neighbouring webs and shorter walking
     * distance, then uses coordinates to resolve ties deterministically.
     *
     * @param creature the Spider placing webs
     * @param origin the Spider's current location
     * @param network the owning Spider's web network
     * @return the selected site, or null when no eligible reachable site exists
     */
    @Override
    public Location findPlacementTarget(Actor creature, Location origin, WebNetwork network) {
        Location nest = network.getNestLocation();

        if (origin.map() != nest.map()) {
            return null;
        }

        Routes routes = buildRoutes(creature, origin);

        Comparator<Location> ranking = Comparator
                .comparingInt((Location site) -> chokeRank(creature, site))
                .thenComparingInt(this::countNeighbouringWebTiles)
                .thenComparingInt(site -> routes.distances.get(
                        closestApproach(site, routes)))
                .thenComparingInt(Location::y)
                .thenComparingInt(Location::x);

        Location best = null;

        for (Location site : nest.getNearbyLocations(placementRadius)) {
            if (!isEligible(site, origin, network)
                    || closestApproach(site, routes) == null) {
                continue;
            }

            if (best == null || ranking.compare(site, best) < 0) {
                best = site;
            }
        }

        return best;
    }

    /**
     * Checks whether a proposed web site remains eligible and has a safely
     * reachable adjacent position from which the Spider can place the web.
     *
     * @param creature the Spider placing webs
     * @param origin the Spider's current location
     * @param target the proposed web site, or null
     * @param network the owning Spider's web network
     * @return true when the site remains eligible and approachable
     */
    @Override
    public boolean isPlacementTargetValid(
            Actor creature,
            Location origin,
            Location target,
            WebNetwork network
    ) {
        return isEligible(target, origin, network)
                && closestApproach(target, buildRoutes(creature, origin)) != null;
    }

    /**
     * Finds the first safe walking step towards the nearest reachable
     * position adjacent to the intended web site.
     *
     * @param creature the moving Spider
     * @param origin the Spider's current location
     * @param target the intended web site
     * @return the first step, or null when already adjacent or unreachable
     */
    @Override
    public Exit findStepToPlacement(
            Actor creature,
            Location origin,
            Location target
    ) {
        if (target == null || origin.map() != target.map()) {
            return null;
        }

        Routes routes = buildRoutes(creature, origin);
        Location approach = closestApproach(target, routes);

        return routes.firstSteps.get(approach);
    }

    /**
     * Finds the first safe walking step towards a destination.
     * For an occupied destination, selects a reachable adjacent position.
     *
     * @param creature the moving Spider
     * @param origin the Spider's current location
     * @param target the destination or occupied target
     * @return the first step, or null when no movement is needed or possible
     */
    @Override
    public Exit findStepTowards(
            Actor creature,
            Location origin,
            Location target
    ) {
        if (target == null
                || origin.map() != target.map()
                || origin == target) {
            return null;
        }

        Routes routes = buildRoutes(creature, origin);

        Location destination = target.map().isAnActorAt(target)
                ? closestApproach(target, routes)
                : target;

        return routes.firstSteps.get(destination);
    }

    private boolean isEligible(
            Location site,
            Location origin,
            WebNetwork network
    ) {
        Location nest = network.getNestLocation();

        return site != null
                && origin.map() == nest.map()
                && site.map() == nest.map()
                && site != nest
                && GridDistance.between(site, nest) <= placementRadius
                && network.canPlaceAt(site);
    }

    /**
     * Checks for the exact adjacent entry site selected by the network
     * and a distinct reachable ordinary site remaining afterwards.
     * New webs do not block the Spider's walking routes.
     *
     * @param creature the Spider placing webs
     * @param origin the Spider's current location
     * @param network the owning web network
     * @return true when entry and ordinary placement both have usable sites
     */
    @Override
    public boolean hasReachableWeavingWork(
            Actor creature,
            Location origin,
            WebNetwork network
    ) {
        if (origin.map() != network.getNestLocation().map()
                || !network.hasCapacityFor(2)) {
            return false;
        }

        List<Location> adjacent = new ArrayList<>(
                origin.getNearbyLocations(1)
        );

        adjacent.sort(
                Comparator.comparingInt(Location::y)
                        .thenComparingInt(Location::x)
        );

        Location entrySite = null;

        for (Location site : adjacent) {
            if (network.canPlaceAt(site)) {
                entrySite = site;
                break;
            }
        }

        if (entrySite == null) {
            return false;
        }

        Routes routes = buildRoutes(creature, origin);

        for (Location site : network.getNestLocation()
                .getNearbyLocations(placementRadius)) {
            if (site != entrySite
                    && isEligible(site, origin, network)
                    && closestApproach(site, routes) != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks for free slots for all entry webs plus one ordinary web.
     *
     * @param network the Spider's web network
     * @param entryWebs the number of entry webs Weaving places on entry
     * @return true if there is enough capacity
     */
   @Override
   public boolean hasCapacityForWeaving(WebNetwork network, int entryWebs){
       return network.getRemainingCapacity() >= Math.max(0, entryWebs) + 1;
   }

    private Routes buildRoutes(Actor creature, Location origin) {
        Routes routes = new Routes();
        Queue<Location> queue = new ArrayDeque<>();

        routes.distances.put(origin, 0);
        queue.add(origin);

        while (!queue.isEmpty()) {
            Location current = queue.remove();

            List<Exit> exits = new ArrayList<>(current.getExits());

            exits.sort(Comparator
                    .comparingInt((Exit exit) -> exit.getDestination().y())
                    .thenComparingInt(exit -> exit.getDestination().x())
                    .thenComparing(Exit::getName));

            for (Exit exit : exits) {
                Location next = exit.getDestination();

                if (next.map() != origin.map()
                        || GridDistance.between(current, next) != 1
                        || routes.distances.containsKey(next)
                        || !next.canActorEnter(creature)
                        || !MovementSafety.isSafe(next)) {
                    continue;
                }

                routes.distances.put(
                        next,
                        routes.distances.get(current) + 1
                );

                routes.firstSteps.put(
                        next,
                        current == origin
                                ? exit
                                : routes.firstSteps.get(current)
                );

                queue.add(next);
            }
        }

        return routes;
    }

    private Location closestApproach(Location target, Routes routes) {
        Location best = null;

        Comparator<Location> ranking = Comparator
                .comparingInt((Location tile) -> routes.distances.get(tile))
                .thenComparingInt(Location::y)
                .thenComparingInt(Location::x);

        for (Location tile : target.getNearbyLocations(1)) {
            if (routes.distances.containsKey(tile)
                    && (best == null || ranking.compare(tile, best) < 0)) {
                best = tile;
            }
        }

        return best;
    }

    private int chokeRank(Actor creature, Location site) {
        int openExits = 0;

        for (Exit exit : site.getExits()) {
            Location neighbour = exit.getDestination();

            if (neighbour.map() == site.map()
                    && GridDistance.between(site, neighbour) == 1
                    && neighbour.getGround().canActorEnter(creature)
                    && MovementSafety.isSafe(neighbour)) {
                openExits++;
            }
        }

        return openExits >= 2 && openExits <= 3 ? 0 : 1;
    }

    private int countNeighbouringWebTiles(Location site) {
        int count = 0;

        for (Location neighbour : site.getNearbyLocations(1)) {
            boolean containsWeb = neighbour.getItems().stream()
                    .anyMatch(item -> item.hasAbility(WebAbility.WEB_TRAP));

            if (containsWeb) {
                count++;
            }
        }

        return count;
    }

    private static class Routes {
        private final Map<Location, Integer> distances = new HashMap<>();
        private final Map<Location, Exit> firstSteps = new HashMap<>();
    }

}
