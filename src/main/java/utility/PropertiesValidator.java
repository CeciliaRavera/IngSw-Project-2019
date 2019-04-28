package utility;

import enumerations.Color;
import enumerations.Direction;
import enumerations.Properties;
import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import exceptions.player.NoDirectionException;
import exceptions.player.SamePositionException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;

import java.util.*;

public class PropertiesValidator {

    private PropertiesValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Method that verifies if the startingPos can see all the other positions
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions PlayerPosition of the targets
     * @return true if the positions are visible, otherwise false
     */
    public static boolean areVisible(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        for (PlayerPosition position : targetPositions) {
            if (!shooterPosition.canSee(position)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies if the positions are concatenatedVisible with the startingPos
     *
     * @param startingPos PLayerPosition beginning of the chain
     * @param positions   the positions you want to see if are concatenatedVisible
     * @return true if all the positions are concatenated visible, otherwise false
     */
    public static boolean areConcatenatedVisible(PlayerPosition startingPos, List<PlayerPosition> positions) {
        PlayerPosition tempVisiblePos;

        if (positions.size() == 1) {
            return startingPos.canSee(positions.get(0));
        }

        tempVisiblePos = startingPos;
        for (PlayerPosition position : positions) {
            if (!tempVisiblePos.canSee(position)) {
                return false;
            }
            tempVisiblePos = position;
        }

        return true;
    }

    /**
     * Method that verifies if the targets are concatenated visible,
     * always working on the position of the target
     *
     * @param command    String containing the command
     * @param targetType TargetType to verify concatenated visibility in command
     * @return true if targets are concatenatedVisible, otherwise false
     */
    public static boolean areConcatenatedVisible(String command, TargetType targetType) {
        List<PlayerPosition> squares;

        String[] splitCommand = command.split(" ");
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getShooterPlayerID(splitCommand));

        switch (targetType) {
            case PLAYER:
                List<Player> targets = CommandUtility.getPlayersByIDs(CommandUtility.getAttributesID(splitCommand, "-t"));

                // Builds the ArrayList of targets PlayerPosition
                squares = new ArrayList<>();
                for (Player target : targets) {
                    squares.add(target.getPosition());
                }
                break;
            case SQUARE:
                squares = CommandUtility.getPositions(splitCommand, "-v");
                break;
            default:
                // Rooms can never be concatenated visible
                throw new InvalidCommandException();
        }

        return areConcatenatedVisible(shooter.getPosition(), squares);
    }

    /**
     * Method that verifies if the shooter is inLine with his targets
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions PlayerPosition of the targets
     * @return {@code true} if the shooter is inLine with the target {@code false} otherwise
     */
    private static boolean areInLine(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {

        if (targetPositions.size() == 1) {
            try {
                shooterPosition.getDirection(targetPositions.get(0));
                return true;
            } catch (NoDirectionException e) {
                return false;
            } catch (SamePositionException e) {
                return true;
            }
        }

        Direction directionChosen = null;

        for (PlayerPosition playerPosition : targetPositions) {
            try {
                if (!shooterPosition.equals(playerPosition)) {
                    if (directionChosen == null) {
                        directionChosen = shooterPosition.getDirection(playerPosition);
                    } else if (!directionChosen.equals(shooterPosition.getDirection(playerPosition))) {
                        return false;
                    }
                }
            } catch (NoDirectionException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies if the ArrayList of position passed are all the same
     *
     * @param positions ArrayList containing the positions
     * @return true if positions are coincident, otherwise false
     */
    private static boolean areInSamePosition(List<PlayerPosition> positions) {
        PlayerPosition testingPos = positions.get(0);

        for (int i = 1; i < positions.size(); ++i) {
            if (!testingPos.equals(positions.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method used to verify if a player can, in a number of moves move, reach the position specified
     *
     * @param playerID the ID of the moving player
     * @param position the position to verify her distance
     * @param move     integer representing the exact distance between the player and his moving position
     * @return true if the player can move to the position, otherwise false
     */
    private static boolean canMove(int playerID, PlayerPosition position, int move) {
        Player movingPlayer = Game.getInstance().getPlayerByID(playerID);

        return (movingPlayer.getPosition().distanceOf(position) == move);
    }

    /**
     * Method used to verify if a list of target players can move to their specified positions in a number
     * of moves move
     *
     * @param targetsID  the IDs of the targets to verify their movement
     * @param targetsPos the positions in which each target should move
     * @param move       integer representing the exact distance between a target player and his moving position
     * @return true if every target player can move to the position, otherwise false
     */
    private static boolean canMove(List<Integer> targetsID, List<PlayerPosition> targetsPos, int move) {
        if (targetsID.size() != targetsPos.size()) {
            return false;
        }

        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

        for (int i = 0; i < targetsPos.size(); ++i) {
            if (targets.get(i).getPosition().distanceOf(targetsPos.get(i)) != move) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method used to verify if a list of target players can move to their specified position in a number
     * of moves that is less than maxMove
     *
     * @param targetsID  the IDs of the targets to verify their movement
     * @param targetsPos the positions in which each target should move
     * @param maxMove    integer representing the maximum distance between a target player and his moving position
     * @return true if every target player can move to the position, otherwise false
     */
    private static boolean canMaxMove(List<Integer> targetsID, List<PlayerPosition> targetsPos, int maxMove) {
        if (targetsID.size() != targetsPos.size()) {
            return false;
        }

        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

        for (int i = 0; i < targetsPos.size(); ++i) {
            if (targets.get(i).getPosition().distanceOf(targetsPos.get(i)) > maxMove) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies that at the end of a shoot the shooter is in the
     * same position of his last target
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions PlayerPosition of the targets
     * @return true if the shooter is in the same position of the last target, otherwise false
     */
    private static boolean lastTargetPos(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        return shooterPosition.equals(targetPositions.get(targetPositions.size() - 1));
    }

    /**
     * Method that verifies if each movement of each target is done directionally from the shooter
     *
     * @param command String containing the command
     * @return true if each targetPosition identifies a direction from the shooter's one
     */
    private static boolean isMovingDirectionally(String command) {
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getShooterPlayerID(command.split(" ")));
        List<PlayerPosition> positions = CommandUtility.getPositions(command.split(" "), "-u");

        for (PlayerPosition position : positions) {
            try {

                shooter.getPosition().getDirection(position);

            } catch (NoDirectionException e) {
                return false;
            } catch (SamePositionException e) {
                // Same position is OK
            }
        }

        return true;
    }


    private static boolean isDistantEnough(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions, TargetType targetType, int distance, boolean exactDistance) {
        int tempDist;

        if (targetType == null) throw new NullPointerException();

        if (targetType == TargetType.PLAYER || targetType == TargetType.ROOM) {
            for (PlayerPosition position : targetPositions) {
                tempDist = shooterPosition.distanceOf(position);

                if ((exactDistance && tempDist != distance) ||
                        (!exactDistance && tempDist < distance)) {
                    return false;
                }
            }
        } else {
            for (PlayerPosition position : targetPositions) {
                tempDist = shooterPosition.distanceOf(position);

                if ((tempDist < distance) ||
                        (Game.getInstance().getGameMap().getSquare(shooterPosition).getColor() == Game.getInstance().getGameMap().getSquare(position).getColor())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns a sub map of the properties with only the properties of a sub effect
     *
     * @param allProperties the Map containing all properties
     * @param targetType    the starting point of the Map from which we need to remove the properties
     * @return the sub map with the properties of a sub effect
     */
    public static Map<String, String> getSubMap(Map<String, String> allProperties, TargetType targetType) {
        // here a LinkedHashMap is always passed is the iterator working right even if static type is Map?
        boolean foundTarget = false;

        Map<String, String> tempMap = new LinkedHashMap<>(allProperties);

        for (Map.Entry<String, String> entry : tempMap.entrySet()) {
            if (foundTarget && !entry.getValue().equals("stop")) {
                tempMap.remove(entry.getKey());
            }

            if (entry.getKey().equals(targetType.toString())) {
                foundTarget = true;
                tempMap.remove(entry.getKey());
            }
        }

        return tempMap;
    }

    /**
     * Checks if target moves are valid based on effect properties
     *
     * @param command    String of command
     * @param properties Map of effect properties
     * @return {@code true} if target moves are valid {@code false} otherwise
     */
    public static boolean isMoveValid(String command, Map<String, String> properties) {
        // Player move validation
        if (properties.containsKey(Properties.MOVE.getJKey())) {
            List<PlayerPosition> movingPos = CommandUtility.getPositions(command.split(" "), "-m");
            int playerID = CommandUtility.getShooterPlayerID(command.split(" "));
            int moveDistance = Integer.parseInt(properties.get(Properties.MOVE.getJKey()));

            if (movingPos.isEmpty() || !PropertiesValidator.canMove(playerID, movingPos.get(0), moveDistance)) {
                return false;
            }
        }

        // Target move validation
        if (properties.containsKey(Properties.MOVE_TARGET.getJKey())) {
            List<Integer> targetsID = CommandUtility.getAttributesID(command.split(" "), "-t");
            List<PlayerPosition> movingPos = CommandUtility.getPositions(command.split(" "), "-u");
            int moveDistance = Integer.parseInt(properties.get(Properties.MOVE_TARGET.getJKey()));

            if (movingPos.isEmpty() || !PropertiesValidator.canMove(targetsID, movingPos, moveDistance)) {
                return false;
            }
        } else if (properties.containsKey(Properties.MAX_MOVE_TARGET.getJKey())) {
            List<Integer> targetsID = CommandUtility.getAttributesID(command.split(" "), "-t");
            List<PlayerPosition> movingPos = CommandUtility.getPositions(command.split(" "), "-u");
            int moveDistance = Integer.parseInt(properties.get(Properties.MAX_MOVE_TARGET.getJKey()));

            if (movingPos.isEmpty() || !PropertiesValidator.canMaxMove(targetsID, movingPos, moveDistance)) {
                return false;
            }
        }

        // Target MoveInLine validation
        return !(properties.containsKey(Properties.MOVE_INLINE.getJKey()) && !PropertiesValidator.isMovingDirectionally(command));
    }

    public static boolean isDistanceValid(Map<String, String> properties, PlayerPosition shooterPosition, List<PlayerPosition> targetPositions, TargetType targetType) {
        // Distance validation
        int distance;
        boolean exactDistance;

        if (properties.containsKey(Properties.DISTANCE.getJKey())) { // Exact target distance
            distance = Integer.parseInt(properties.get(Properties.DISTANCE.getJKey()));
            exactDistance = true;
        } else if (properties.containsKey(Properties.MIN_DISTANCE.getJKey())) { // Minimum target distance
            distance = Integer.parseInt(properties.get(Properties.MIN_DISTANCE.getJKey()));
            exactDistance = false;
        } else {
            return true;
        }

        return isDistantEnough(shooterPosition, targetPositions, targetType, distance, exactDistance);
    }

    public static boolean isPositioningValid(Map<String, String> properties, PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        return !((properties.containsKey(Properties.INLINE.getJKey()) && !areInLine(shooterPosition, targetPositions)) || // InLine targets validation
                (properties.containsKey(Properties.MOVE_TO_LAST_TARGET.getJKey()) && !lastTargetPos(shooterPosition, targetPositions))); // Move to last target validation
    }

    public static boolean isVisibilityValid(Map<String, String> properties, PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        // Targets Same Position
        if (properties.containsKey(Properties.SAME_POSITION.getJKey()) && !areInSamePosition(targetPositions)) {
            return false;
        }

        // TODO Fatto di fretta, non sono certo che sia corretto
        if (properties.containsKey(Properties.VISIBLE.getJKey()) && (!Boolean.parseBoolean(properties.get(Properties.VISIBLE.getJKey())) && areVisible(shooterPosition, targetPositions) ||
                (Boolean.parseBoolean(properties.get(Properties.VISIBLE.getJKey())) && !areVisible(shooterPosition, targetPositions)))) {
            return false;
        }

        // TODO ConcatenatedVisibility

        return true;
    }
}