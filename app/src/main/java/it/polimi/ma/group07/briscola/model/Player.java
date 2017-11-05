package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;

/**
 * Represents a player of the game
 */

public class Player {
    /**
     * name of player
     * curent score
     * List of cards in player pile and hand
     */
    private final String name;
    private int score;
    private ArrayList<Card> cardPile;
    private ArrayList<Card> hand;

    /**
     * initiates a new player with given name
     * @param name name of the player
     */
    public Player(String name){
        this.name=name;
        score=0;
        cardPile=new ArrayList<Card>();
        hand=new ArrayList<Card>();
    }

    /**
     * Creates a new player from a state of the game
     * @param hand string representing the hand of the player
     * @param pile string representing the pile of the player
     * @param name name of the player
     * @param rules referene to object implementin Rule Applier interface
     *              to calculate the points
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     */
    public Player(String hand, String pile,String name,RuleApplier rules) throws InvalidCardDescriptionException {
        this.name=name;
        this.hand=new ArrayList<Card>();
        this.cardPile=new ArrayList<Card>();
        //split hand and pile strings
        ArrayList<String> handStrings=Parser.splitString(hand,2);
        ArrayList<String> pileStrings=Parser.splitString(pile,2);
        for(int i=0;i<handStrings.size();i++)
            this.hand.add(new Card(handStrings.get(i)));
        for(int i=0;i<pileStrings.size();i++){
            this.cardPile.add(new Card(pileStrings.get(i)));
        }
        this.score=rules.calculatePoints(this.cardPile);
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score=score;
    }

    public String getName(){
        return name;
    }

    /**
     * update the pile of the player
     * @param cards list of cards to be added to the pile
     */
    public void addCardsinPile(ArrayList<Card> cards){
        for(Card card :cards)
        {
            this.cardPile.add(card);
        }
    }

    public ArrayList<Card> getCardPile(){
        return cardPile;
    }

    public ArrayList<Card> getHand(){
        return hand;
    }

    public void addCardToHand(Card card){
        hand.add(card);
    }

    /**
     * Play card at the index specified
     * @param index index of card to be played
     * @return reference to the card which was played
     * @throws IndexOutOfBoundsException thrown in case the index is out of range of the cards
     */
    public Card placeCardAtIndex(int index) throws IndexOutOfBoundsException{
        return hand.remove(index);
    }

    /**
     *
     * @return string representation of the hand of the player
     */
    public String handToString()
    {
        String str="";
        for(Card c :hand){
            str+=c.toString();
        }
        return str;
    }
    /**
     *
     * @return string representation of the pile of cards of the player
     */
    public String pileToString()
    {
        String str="";
        for(Card c :cardPile){
            str+=c.toString();
        }
        return str;
    }

    /**
     * Increment score of the player
     * @param points points to be added to the score
     */
    public void incrementScore(int points) {
        score+=points;
    }
}
