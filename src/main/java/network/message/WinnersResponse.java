package network.message;

import enumerations.MessageContent;
import model.Game;
import model.player.Player;

import java.util.ArrayList;

public class WinnersResponse extends Message {
    private ArrayList<Player> winners;

    public WinnersResponse(ArrayList<Player> winners) {
        super(Game.GOD, MessageContent.LAST_RESPONSE);

        this.winners = winners;
    }
}