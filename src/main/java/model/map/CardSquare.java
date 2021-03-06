package model.map;

import enumerations.RoomColor;
import enumerations.SquareAdjacency;
import enumerations.SquareType;
import model.Game;
import model.cards.AmmoTile;

public class CardSquare extends Square {
    private static final long serialVersionUID = 3605209286488449112L;
    private AmmoTile ammoTile;
    private boolean hasAmmoTile;

    public CardSquare(RoomColor color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west) {
        super(color, north, east, south, west, SquareType.TILE);
        this.ammoTile = null;
        this.hasAmmoTile = false;
    }

    public CardSquare(RoomColor color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west, AmmoTile ammoTile) {
        super(color, north, east, south, west, SquareType.TILE);
        this.ammoTile = ammoTile;
        this.hasAmmoTile = true;
    }

    public boolean isAmmoTilePresent() {
        return this.hasAmmoTile;
    }

    public AmmoTile pickAmmoTile() {
        this.hasAmmoTile = false;
        Game.getInstance().getAmmoTileDeck().discardCard(ammoTile);
        return ammoTile;
    }

    public void setAmmoTile(AmmoTile ammoTile) {
        this.ammoTile = ammoTile;
        this.hasAmmoTile = true;
    }

    public AmmoTile getAmmoTile() {
        return ammoTile;
    }
}
