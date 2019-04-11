package model.cards;

import enumerations.Ammo;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.cards.weaponstates.WeaponState;
import model.player.Player;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class WeaponCard extends UsableCard {
    private final Ammo[] cost;
    private final Map<Effect, Ammo[]> secondaryEffects;     // each effect is mapped to his cost
    private WeaponState weaponState;
    public static final int CHARGED = 0;
    public static final int UNCHARGED = 1;
    public static final int SEMI_CHARGED = 2;

    public WeaponCard(String name, File image, Effect baseEffect, Ammo[] cost,
                      Map<Effect, Ammo[]> secondaryEffects, WeaponState weaponState) {
        super(name, image, baseEffect);
        this.cost = cost;
        this.secondaryEffects = secondaryEffects;
        this.weaponState = weaponState;
    }

    /**
     * @return the map of secondary effects where the key is the effect and the mapped object its cost
     */
    public Map<Effect, Ammo[]> getEffects() {
        return this.secondaryEffects;
    }

    /**
     * Return the cost to use the Weapon depending on it's state
     *
     * @return an array of Ammo which is the recharging Cost of the Weapon
     */
    public Ammo[] getRechargeCost() {
        switch (this.weaponState.status()) {
            case UNCHARGED:
                return cost;

            case SEMI_CHARGED:
                return Arrays.copyOfRange(cost, 1, cost.length);

            default:
                return new Ammo[0];
        }
    }

    /**
     * Sets the state of the Weapon
     *
     * @param status the State to put the Weapon
     */
    public void setStatus(WeaponState status) {
        this.weaponState = status;
    }

    /**
     * @return true if the Weapon is charged, otherwise false
     */
    public boolean isCharged() {
        return weaponState.charged(this);
    }


    /**
     * @return the State of the Weapon: CHARGED = 0, UNCHARGED = 1, SEMI_CHARGED = 2
     */
    public int status() {
        return weaponState.status();
    }

    /**
     * @return true if the Weapon is rechargeable, otherwise false
     */
    public boolean rechargeable() {
        return weaponState.rechargeable(this);
    }

    /**
     * Method used to recharge a Weapon
     *
     * @throws WeaponAlreadyChargedException exception thrown in case the weapon is already charged
     */
    public void recharge() throws WeaponAlreadyChargedException {
        if (this.rechargeable()) {
            setStatus(new ChargedWeapon());
        } else throw new WeaponAlreadyChargedException(this.getName());
    }

    /**
     * Method that executes the effect of the Weapon depending on it's state
     *
     * @param effect       the effect of the Weapon to be executed
     * @param target       contains informations of how and on who the effect is executed
     * @param playerDealer the Player who uses the Weapon's effect
     * @throws WeaponNotChargedException exception thrown in case the Weapon is not charged
     */
    public void use(Effect effect, Target target, Player playerDealer) throws WeaponNotChargedException {
        if (isCharged()) {
            weaponState.use(effect, target, playerDealer);
            setStatus(new UnchargedWeapon());
        } else throw new WeaponNotChargedException(this.getName());
    }
}
