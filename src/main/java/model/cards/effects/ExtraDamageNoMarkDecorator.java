package model.cards.effects;

import model.Game;
import model.player.Player;
import network.message.EffectRequest;

import java.util.Arrays;
import java.util.List;

/**
 * This Class is used to Implement a Mark Decoration without triggering the Marks that the
 * shooter has on his target's playerboard
 */
public class ExtraDamageNoMarkDecorator extends ExtraEffectDecorator {
    private static final long serialVersionUID = -2582923298178193774L;
    private final int[] damageDistribution;

    /**
     * Builds the Damage with no marks decoration
     *
     * @param effect to be decorated
     * @param extraDamageDistribution the Array containing the damage decoration distribution
     */
    public ExtraDamageNoMarkDecorator(Effect effect, int[] extraDamageDistribution) {
        this.effect = effect;
        this.description = effect.description;
        setProperties(effect.getProperties());
        this.targets = effect.targets;
        this.cost = effect.cost;
        this.damageDistribution = extraDamageDistribution;
    }

    /**
     * Method that spreads the damages of the effect to all targets without triggering the Marks.
     * A target can be {@code PLAYER}, {@code SQUARE} or {@code ROOM} and the damage
     * distribution depends on this.
     *
     * @param request of the effect
     */
    @Override
    public void execute(EffectRequest request) {
        effect.execute(request);

        List<String> targetsUsername;
        Player shooter = Game.getInstance().getUserPlayerByUsername(request.getSenderUsername());
        targetsUsername = request.getTargetPlayersUsername();

        for (int i = 0; i < targetsUsername.size(); ++i) {
            Game.getInstance().getUserPlayerByUsername(targetsUsername.get(i)).getPlayerBoard().addDamageNoMark(shooter, damageDistribution[i]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtraDamageNoMarkDecorator that = (ExtraDamageNoMarkDecorator) o;
        return Arrays.equals(damageDistribution, that.damageDistribution);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(damageDistribution);
        return result;
    }
}
