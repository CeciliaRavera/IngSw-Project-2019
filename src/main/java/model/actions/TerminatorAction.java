package model.actions;

import exceptions.actions.InvalidActionException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import model.player.UserPlayer;

public class TerminatorAction implements Action {
    private static final int MAX_TERMINATOR_MOVE = 1;
    private static final int TERMINATOR_DAMAGE = 1;
    private static final int TERMINATOR_ADRENALINE_MARK = 1;
    private final Player terminator = Game.getInstance().getTerminator();
    private UserPlayer actingPlayer;
    private Player targetPlayer;
    private PlayerPosition movingPos;
    private TerminatorState terminatorState;


    public TerminatorAction(UserPlayer actingPlayer, Player targetPlayer, PlayerPosition movingPos) {
        this.actingPlayer = actingPlayer;
        this.targetPlayer = targetPlayer;

        if (terminator.getPosition().equals(movingPos) || movingPos == null) {
            this.movingPos = terminator.getPosition();
        } else {
            this.movingPos = movingPos;
        }

        if (terminator.getPlayerBoard().getDamageCount() < 6) {
            this.terminatorState = TerminatorState.NORMAL;
        } else {
            this.terminatorState = TerminatorState.ADRENALINE;
        }
    }

    @Override
    public boolean validate() throws InvalidActionException {
        if (actingPlayer.equals(targetPlayer)) {
            throw new InvalidActionException();
        }

        // check that the built position has a valid X coordinate
        if (movingPos.getCoordX() < 0 || movingPos.getCoordY() > 2) {
            throw new InvalidActionException();
        }

        // check that the built position has a valid Y coordinate
        if (movingPos.getCoordY() < 0 || movingPos.getCoordY() > 3) {
            throw new InvalidActionException();
        }

        int movingDistance = terminator.getPosition().distanceOf(movingPos);

        // move and Visibility validation
        if (movingDistance == 0) {
            if (targetPlayer == null) throw new InvalidActionException();

            return terminator.canSee(targetPlayer);
        } else if (movingDistance == MAX_TERMINATOR_MOVE) {
            if (targetPlayer == null) {
                if (movingPos.canSeeSomeone(actingPlayer)) {
                    throw new InvalidActionException();
                } else {
                    return true;
                }
            }

            return movingPos.canSee(targetPlayer.getPosition());
        } else {
            throw new InvalidActionException();
        }
    }

    @Override
    public void execute() {
        // first I move the terminator
        terminator.changePosition(movingPos.getCoordX(), movingPos.getCoordY());

        // if the terminator can not see anyone his action is ended
        if (targetPlayer == null) {
            return;
        }

        // then I shoot with the terminator depending on it's state
        switch (terminatorState) {
            case NORMAL:
                targetPlayer.getPlayerBoard().addDamage(terminator, TERMINATOR_DAMAGE);
                break;
            case ADRENALINE:
                targetPlayer.getPlayerBoard().addDamage(terminator, TERMINATOR_DAMAGE);
                targetPlayer.getPlayerBoard().addMark(terminator, TERMINATOR_ADRENALINE_MARK);
                break;
            default:
                throw new NullPointerException("The Terminator state can never be null!");
        }
    }

    enum TerminatorState {
        NORMAL, ADRENALINE
    }

}
