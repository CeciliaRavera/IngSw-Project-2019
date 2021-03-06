package model.cards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a deck of cards, decks contain all same kind of cards
 */
public class Deck implements Serializable {
    private static final long serialVersionUID = -3053418734560184556L;

    private ArrayList<Card> cardsDeck;
    private Boolean garbage;
    private ArrayList<Card> discard;

    /**
     * Create a new empty deck without discard
     */
    public Deck() {
        this(false);
    }

    /**
     * Create a new empty deck. If <code>garbage</code> is true, it also creates an empty discarded card deck
     *
     * @param garbage if true set the discarded card deck
     */
    public Deck(boolean garbage) {
        cardsDeck = new ArrayList<>();
        if (garbage) discard = new ArrayList<>();
        this.garbage = garbage;
    }

    public Deck(Deck other) {
        this(other, false);
    }

    public Deck(Deck other, boolean garbage) {
        cardsDeck = new ArrayList<>(other.cardsDeck);
        if (garbage) discard = new ArrayList<>(other.discard);
        this.garbage = garbage;
    }

    /**
     * Empty the decks
     */
    public void flush() {
        cardsDeck.clear();
        if (garbage) discard.clear();
    }

    /**
     * Move the discarded cards into the deck and shuffle it
     */
    public void shuffle() {
        if (garbage) {
            cardsDeck.addAll(discard);
            discard.clear();
        }
        Collections.shuffle(cardsDeck);
    }

    /**
     * Add a {@code card} to the deck
     *
     * @param card the {@code card} to add
     */
    public void addCard(Card card) {
        if (card == null) throw new NullPointerException("Card cannot be null");
        cardsDeck.add(card);
    }

    /**
     * Draw the top card from the deck, return it and remove it from the deck
     *
     * @return <code>null</code> if the deck is empty or if all the cards have been discarded, otherwise returns the top card
     */
    public Card draw() {
        try {
            return cardsDeck.remove(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * if garbage is <code>false</code> it inserts at the bottom of the deck the <code>card</code>, otherwise it inserts it on the top of <code>discard</code>
     *
     * @param card the card to be discarded
     */
    public void discardCard(Card card) {
        if (card == null) throw new NullPointerException("card cannot be null");

        if (garbage) discard.add(0, card);
        else cardsDeck.add(card);
    }

    /**
     * Return the number of cards left in the deck
     *
     * @return number of cards in the deck
     */
    public int numOfCards() {
        return cardsDeck.size();
    }

    /**
     * Return the number of cards left in the discard deck
     *
     * @return number of cards in the discard deck
     */
    public int numOfDiscards() {
        if (!garbage) throw new NullPointerException("deck of discarded cards is null");
        return discard.size();
    }

    @Override
    public String toString() {
        return "Deck{\n" +
                "cardsDeck=" + Arrays.toString(cardsDeck.toArray()) +
                ",\ngarbage=" + garbage +
                ",\ndiscard=" + (discard != null ? Arrays.toString(discard.toArray()) : "null" ) +
                "\n}";
    }

    public List<Card> toList() {
        List<Card> list = new ArrayList<>();

        list.addAll(new ArrayList<>(cardsDeck));
        if (garbage) list.addAll(new ArrayList<>(discard));

        return list;
    }
}
