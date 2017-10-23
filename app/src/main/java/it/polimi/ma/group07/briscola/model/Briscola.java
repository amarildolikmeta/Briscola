package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.Exceptions.NoCardInDeckException;

import static it.polimi.ma.group07.briscola.model.GameState.WON;

/**
 * Created by amari on 18-Oct-17.
 */

public class Briscola {
    private ArrayList<Player> players;
    private Deck deck;
    private int round=0;
    private Suit briscola;
    private Brain brain;
    private int currentPlayer;
    private ArrayList<Card> surface;
    private int briscolaPlayed;
    private StateBundle gameState;
    //if number of players not specified
    public Briscola(){
        this(2);
    }

    public Briscola(int numPlayers)
    {
        deck=new Deck();
        brain=new Brain();
        players=new ArrayList<Player>();
        surface=new ArrayList<Card>();
        for(int i=0;i<numPlayers;i++)
            players.add(new Player("P"+(i+1)));
        round=1;
        for(int i=0;i<3;i++){
            for(int j=0;j<numPlayers;j++) {
                try {
                    players.get(j).addCardToHand(deck.drawCard());
                } catch (NoCardInDeckException e) {
                    e.printStackTrace();
                }
            }
        }
        Card b= null;
        try {
            b = deck.drawCard();
        } catch (NoCardInDeckException e) {
            e.printStackTrace();
        }
        briscola=b.getSuit();
        brain.setTrumpSuit(briscola);
        deck.addLastCard(b);
        currentPlayer= 0;
        briscolaPlayed=0;
    }

    public Briscola(String description) throws InvalidGameStateException, InvalidCardDescriptionException {
        this(description,2);
    }

    public Briscola(String description,int numPlayers) throws InvalidCardDescriptionException, InvalidGameStateException {
        brain=new Brain();
        setState(description, numPlayers);

    }

    private void setState(String configuration,int numPlayers) throws InvalidGameStateException, InvalidCardDescriptionException {
        try {
            State state=Parser.parseState(configuration,numPlayers);
            briscola=Suit.stringToSuit(state.trump);
            brain.setTrumpSuit(briscola);
            currentPlayer=state.currentPlayer;
            deck=new Deck(state.deck);
            players=new ArrayList<Player>();
            surface=new ArrayList<Card>();
            //count briscola cards present in surface
            briscolaPlayed=0;
            for(Card c:surface){
                if(c.getSuit().toString().equals(briscola.toString()))
                    briscolaPlayed++;
            }
            //create players and distribute card in hand and pile
            //brain implements the RuleApplier interface
            //which declares a calculatePoints(ArrayList<Card> cards) method
            //that will be called inside the constructor of a player to calculate the points
            for(int i=0;i<state.hands.length;i++){
                players.add(new Player(state.hands[i],state.piles[i],"P"+(i+1),brain));
            }
            //place cards in surface
            for(String s:Parser.splitString(state.surface,2)){
                surface.add(new Card(s));
            }

        } catch (InvalidGameStateException | InvalidCardDescriptionException e) {
                System.out.println(e.getMessage());
                throw e;
            }
    }

    public String surfaceToString(){
        String str="";
        for(Card c:surface){
            str+=c.toString();
        }
        return str;
    }

    public void resetGame(){
        deck=new Deck();
        surface.clear();
        round=1;
        for(int i=0;i<players.size();i++)
            players.get(i).reset();
    }

    public String moveTest(String configuration,String moves) throws InvalidCardDescriptionException, InvalidGameStateException {
        try {
            setState(configuration,2);
        } catch (InvalidGameStateException |InvalidCardDescriptionException e) {
            return "ERROR: "+e.getMessage();
        }
        for(int i=0;i<moves.length();i++){

            GameState s= onPerformMove(moves.charAt(i)-'0');
            //OnPerform move return 1 if >60 points are reached
            switch(s){
                case WON:
                {
                    return "WINNER "+currentPlayer+" "+players.get(currentPlayer).getScore();
                }
                case DRAW:
                {
                    if(i<players.size()-1)
                        return "ERROR: More moves than possible are specified";
                        return "DRAW";
                }
                default:
                {

                }
            }
        }
        //In this case the moves are finished so we just return the state
        return this.toString();
    }
    private void dealCards(){
        for (int j = 0; j < players.size(); j++) {
            try {
                players.get((currentPlayer + j) % players.size()).addCardToHand(deck.drawCard());
            } catch (NoCardInDeckException e) {
                //if cards finish don't deal
                break;
            }
        }
    }
    //return true if the game finishes
    public GameState onPerformMove(int index) throws ArrayIndexOutOfBoundsException{
        Card c=players.get(currentPlayer).placeCardAtIndex(index);
        surface.add(c);
        if (c.getSuit().toString().equals(briscola.toString()))
            briscolaPlayed++;
        incrementCurrentPlayer();
        if(surface.size()==players.size())
        {
            //determine winning card
            int winner =brain.determineWinner(surface,briscolaPlayed);
            int points=brain.calculatePoints(surface);
            //addcards to the winner pile (winner will be first player of next round)
            currentPlayer+=winner;
            currentPlayer=currentPlayer%players.size();
            players.get(currentPlayer).addCardsinPile(surface);
            surface.clear();
            players.get(currentPlayer).incrementScore(points);
            //check if game is Finished
            if(players.get(currentPlayer).getScore()>60){
                return WON;
            }
            //empty surface
            briscolaPlayed=0;
            //deal next batch of cards
            if (deck.hasMoreCards()) {
                dealCards();
            }
            else{
                if(players.get(currentPlayer).getHand().size()==0)
                    return GameState.DRAW;
            }
        }
        return GameState.CONTINUE;
    }
    public void incrementCurrentPlayer()
    {
        currentPlayer=(currentPlayer+1)%players.size();
    }
    @Override
    public String toString(){
        String str="";
        //add Current player to play
        str+=currentPlayer;
        //add the trump suit
        str+=briscola;
        //add the deck string representation
        str+=deck.toString()+".";
        //add the surface cards
        str+=surfaceToString()+".";
        //add player hands
        for(Player p:players){
            str+=p.handToString()+".";
        }
        //add Player piles
        for(Player p:players){
            str+=p.pileToString()+".";
        }
        str=str.substring(0,str.length()-1);
        return str;
    }

    //return a string representing the hand of a player with index
    public String getPlayerHand(int index){
        return players.get(index).getHand().toString().replace("[","").replace(" ","").replace("]","").replace(",","");
    }
    public ArrayList<Card> getPlayerHandCards(int index){
        return players.get(index).getHand();
    }
    //return a string representing the surface
    public String getSurface(){
        return surface.toString().replace("[","").replace(" ","").replace("]","").replace(",","");
    }

    //return a string representing the pile of a player with index
    public String getPlayerCardPile(int index){
        return players.get(index).getCardPile().toString().replace("[","").replace(" ","").replace("]","").replace(",","");
    }

    //get a string representation of the Bricola card
    public String getBriscolaCard(){
        //return BricolaCard if in deck
        try {
            return deck.getLastCard().toString();
        } catch (ArrayIndexOutOfBoundsException e)
        {
            return briscola.toString();
        }
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public StateBundle getGameState(){
        ArrayList<String> hand1=Parser.splitString(getPlayerHand(0),2);
        ArrayList<String> hand2=Parser.splitString(getPlayerHand(1),2);
        ArrayList<String> pile1=Parser.splitString(getPlayerCardPile(0),2);
        ArrayList<String> pile2=Parser.splitString(getPlayerCardPile(1),2);
        ArrayList<String> surface=Parser.splitString(getSurface(),2);
        int score1=players.get(0).getScore();
        int score2=players.get(1).getScore();
        String briscola=getBriscolaCard();
        return new StateBundle(hand1,hand2,surface,briscola,currentPlayer,pile1,pile2,score1,score2);
    }
}
