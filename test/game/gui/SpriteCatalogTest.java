package game.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests how the graphical game chooses a sprite for each visible thing, and
 * in particular that it never reveals what the console would hide.
 */
class SpriteCatalogTest {

    private final SpriteCatalog catalog = new SpriteCatalog();

    @Test
    void actorShowingItsOwnCharacterGetsItsOwnSprite() {
        SpriteCatalog.ActorLook look = catalog.actor("Nest Spider (80/80)", 's');

        assertEquals("actor.spider", look.sprite());
        assertFalse(look.disguised());
    }

    @Test
    void disguisedMimicIsDrawnAsTheItemItPretendsToBe() {
        SpriteCatalog.ActorLook look = catalog.actor("Cave Mimic (100/100)", 'G');

        assertEquals("item.geode", look.sprite());
        assertTrue(look.disguised());
    }

    @Test
    void revealedMimicIsDrawnAsAMimic() {
        SpriteCatalog.ActorLook look = catalog.actor("Cave Mimic (100/100)", 'M');

        assertEquals("actor.mimic", look.sprite());
        assertFalse(look.disguised());
    }

    @Test
    void visibleWebIsDrawnButConcealedWebIsNot() {
        assertEquals("item.web", catalog.itemOnMap("Spider Web", ':'));
        assertNull(catalog.itemOnMap("Spider Web", '.'));
    }

    @Test
    void playerIsDrawnAsTheFarmer() {
        assertEquals("actor.farmer", catalog.actor("Farmer Bob (100/100)", '@').sprite());
    }

    @Test
    void groundsAreMatchedByName() {
        assertEquals("ground.rock", catalog.ground("Rock", '#'));
        assertEquals("ground.dirt", catalog.ground("Dirt", '.'));
    }

    @Test
    void unknownThingsFallBackToTheirConsoleCharacter() {
        char unused = (char) 0x3a9;
        assertEquals(SpriteCatalog.GLYPH_PREFIX + unused, catalog.ground("Mystery Ground", unused));
        assertEquals(SpriteCatalog.GLYPH_PREFIX + unused, catalog.itemInInventory("Mystery Item", unused));
    }

    @Test
    void inventoryItemsKeepTheirOwnSprite() {
        assertEquals("item.geode", catalog.itemInInventory("Geode", 'G'));
        assertEquals("item.stone", catalog.itemInInventory("Stone", 's'));
    }
}
