package application;

/**
 * Defines the map data structure and tile constants.
 * This has been updated for the ZOMZOM 2.0 (10x7 grid) concept.
 */
public class Map {

    // grid dimensions 10x6
	public static final int MAP_WIDTH_TILES = 10;
    public static final int MAP_HEIGHT_TILES = 6;

    // tile size 
    public static final int TILE_WIDTH = 96;  
    public static final int TILE_HEIGHT = 96; 

    // don't remove for soldier position to be tracked pwede rin gawin for tilesheet
    public static final int SLOT_EMPTY = 0;
    public static final int SLOT_SOLDIER = 1;
    public static final int SLOT_ZOMBIE = 2;
    public static final int SLOT_FENCE = 3;

    private int[][] tileData;

    public Map() {
        this.tileData = new int[MAP_WIDTH_TILES][MAP_HEIGHT_TILES];
        // empty grid initialization
        for (int y = 0; y < MAP_HEIGHT_TILES; y++) {
            for (int x = 0; x < MAP_WIDTH_TILES; x++) {
                tileData[x][y] = SLOT_EMPTY;
            }
        }
    }

    public int getSlot(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH_TILES || y < 0 || y >= MAP_HEIGHT_TILES) {
            return -1; // if out of bounds
        }
        return tileData[x][y];
    }
    
    // method to place a soldier or anything
    public void setSlot(int x, int y, int objectType) {
        if (x >= 0 && x < MAP_WIDTH_TILES && y >= 0 && y < MAP_HEIGHT_TILES) {
            tileData[x][y] = objectType;
        }
    }
    
    public int getMapWidthTiles() { return MAP_WIDTH_TILES; }
    public int getMapHeightTiles() { return MAP_HEIGHT_TILES; }
}