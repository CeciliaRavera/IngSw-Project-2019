package controller;

import enumerations.PossibleGameState;
import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class is the One that handles the Turn Assignment to a {@link UserPlayer UserPlayer} during each phase of the
 * game, using specific parameters when the Turn Assignment does not follow the standard one performed with the method
 * {@link #nextTurn() nextTurn}
 */
class TurnManager {
    private UserPlayer turnOwner;
    private UserPlayer lastPlayer;
    private final UserPlayer lastRoundPlayer;

    private List<UserPlayer> players;
    private ArrayList<UserPlayer> damagedPlayers;
    private ArrayList<UserPlayer> deathPlayers;

    private boolean secondAction;

    private ArrayList<UserPlayer> afterFrenzy;
    private ArrayList<UserPlayer> beforeFrenzy;

    private PossibleGameState arrivingGameState;

    private int count;
    private int turnCount;

    /**
     * Creates an Instance of {@link TurnManager TurnManager} binding to it the List of {@link UserPlayer UserPlayers}
     * that have joined the {@link model.Game Game}
     *
     * @param players the List of {@link UserPlayer UserPlayers} in the {@link model.Game Game}
     */
    TurnManager(List<UserPlayer> players) {
        this.players = players;
        this.lastRoundPlayer = players.get(players.size() - 1);
        this.turnOwner = players.get(count);
        this.afterFrenzy = new ArrayList<>();
        this.beforeFrenzy = new ArrayList<>();

    }

    /**
     * @return the {@link UserPlayer UserPlayer} owning the Turn
     */
    UserPlayer getTurnOwner() {
        return turnOwner;
    }

    /**
     * Method used to set the Last {@link UserPlayer Player} of the Game, used to understand when it ends
     */
    void setLastPlayer() {
        this.lastPlayer = turnOwner;
    }

    /**
     * @return the Last {@link UserPlayer Player} of the Game
     */
    UserPlayer getLastPlayer() {
        return this.lastPlayer;
    }

    /**
     * Method that sets the ArrayList of {@link UserPlayer UserPlayers} that took damage after a damaging {@link model.actions.Action Action}
     *
     * @param damaged the ArrayList of damaged {@link UserPlayer UserPlayers}
     */
    void setDamagedPlayers(ArrayList<UserPlayer> damaged) {
        if (damaged == null) {
            this.damagedPlayers = new ArrayList<>();
        } else {
            this.damagedPlayers = damaged;
        }
    }

    /**
     * @return the ArrayList of damaged {@link UserPlayer UserPlayers}, used to verify when a TAGBACK GRANADE or TARGETING SCOPE
     * can be used
     */
    ArrayList<UserPlayer> getDamagedPlayers() {
        return this.damagedPlayers;
    }

    /**
     * Method that sets the {@link UserPlayer UserPlayers} that died during a {@link UserPlayer Player's} turn
     *
     * @param deaths the ArrayList of dead {@link UserPlayer UserPlayers}
     */
    void setDeathPlayers(ArrayList<UserPlayer> deaths) {
        if (deaths == null) {
            this.deathPlayers = new ArrayList<>();
        } else {
            this.deathPlayers = deaths;
        }
    }

    /**
     * @return an ArrayList of dead {@link UserPlayer UserPlayers}, it is used to give them the turn to Respawn
     */
    ArrayList<UserPlayer> getDeathPlayers() {
        return this.deathPlayers;
    }

    /**
     * Method used to temporary set a the parameter {@link #secondAction secondAction} used by the {@link RoundManager RoundManager}
     * to handle the next State of the {@link GameManager GameManager}
     *
     * @param secondAction Boolean that specifies if the performing action is the second
     */
    void setSecondAction(boolean secondAction) {
        this.secondAction = secondAction;
    }

    /**
     * @return a Boolean that specifies if the performing action is the second
     */
    boolean isSecondAction() {
        return this.secondAction;
    }

    /**
     * @return the ArrayList of the {@link UserPlayer UserPlayers} that are after the FrenzyActivator but before the FirstPlayer;
     * these players will have two FrenzyActions during the FinalFrenzy
     */
    ArrayList<UserPlayer> getAfterFrenzy() {
        return this.afterFrenzy;
    }

    /**
     * Method used to set the {@link PossibleGameState PossibleGameState} from which the {@link GameManager GameManager}
     * is evolving
     *
     * @param arrivingGameState the {@link PossibleGameState PossibleGameState} to be set
     */
    void setArrivingGameState(PossibleGameState arrivingGameState) {
        this.arrivingGameState = arrivingGameState;
    }

    /**
     * @return the {@link PossibleGameState PossibleGameState} of the {@link GameManager GameManager} used to restore
     * the correct State after an extemporary action
     */
    PossibleGameState getArrivingGameState() {
        return this.arrivingGameState;
    }

    /**
     * Method that resets the parameter {@link #count} used to give the Turn to each {@link UserPlayer UserPlayer} during
     * the usage of a TAGBACK GRANADE or while Respawning
     */
    void resetCount() {
        this.turnCount = 0;
    }

    /**
     * Method that increases the parameter {@link #count}
     */
    void increaseCount() {
        ++this.turnCount;
    }

    /**
     * @return the parameter {@link #count}
     */
    int getTurnCount() {
        return this.turnCount;
    }

    /**
     * Method that gives the Turn to the {@link UserPlayer UserPlayer} that comes after the TurnOwner
     */
    void nextTurn() {
        count++;
        count = count % players.size();
        turnOwner = players.get(count);
    }

    /**
     * Method used before the FinalFrenzy is starting to set the two Sets of different FrenzyPlayers as they will be
     * given the correct number and type of FrenzyActions
     */
    void setFrenzyPlayers() {
        UserPlayer frenzyActivator = turnOwner;
        boolean beforeFirst = true;

        do {
            nextTurn();

            if (turnOwner.isFirstPlayer()) {
                beforeFirst = false;
            }

            if (beforeFirst) {
                afterFrenzy.add(turnOwner);
            } else {
                beforeFrenzy.add(turnOwner);
            }

        } while (!turnOwner.equals(frenzyActivator));
    }

    /**
     * @return true if the TurnOwner is the last of a Round, otherwise false
     */
    boolean endOfRound() {
        return turnOwner.equals(lastRoundPlayer);
    }

    /**
     * Method that gives the turn to the specified {@link UserPlayer UserPlayer}
     *
     * @param damagedPlayer the {@link UserPlayer UserPlayer} that is going to become the TurnOwner
     */
    void giveTurn(UserPlayer damagedPlayer) {
        this.turnOwner = damagedPlayer;
    }
}
