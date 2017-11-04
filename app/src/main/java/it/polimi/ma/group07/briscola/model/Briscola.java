package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.Exceptions.NoCardInDeckException;


/**
 * Created by amari on 18-Oct-17.
 */

public class Briscola {

    private static Briscola Instance=null;

    private ArrayList<Player> players;
    private Deck deck;
    //will hold all the cards no matter where they are
    private ArrayList<Card> cardList;
    private int round=0;
    private Suit briscola;
    private Card briscolaCard;
    private Brain brain;
    private int currentPlayer;
    private ArrayList<Card> surface;
    private int briscolaPlayed;
    private StateBundle gameState;
    private boolean roundFinished;
    private boolean gameFinished;
    private boolean playableState;   //true if in the current state moves can be made
    private int dealingIndex;       //index used when dealing cards
    //if number of players not specified

    public static Briscola createInstance(){
        Instance=new Briscola();
        return Instance;
    }

    public static Briscola createInstance(int numPlayers){
        Instance=new Briscola(numPlayers);
        return Instance;
    }

    public static Briscola createInstance(String configuration) throws InvalidCardDescriptionException, InvalidGameStateException {
        Instance=new Briscola(configuration);
        return Instance;
    }

    public static Briscola createInstance(String configuration, int numpPlayers) throws InvalidGameStateException, InvalidCardDescriptionException {
        Instance=new Briscola(configuration,numpPlayers);
        return Instance;
    }

    public static Briscola getInstance(){
        if(Instance==null){
            Instance=new Briscola();
        }
        return Instance;
    }

    public Briscola(){
        this(2);
    }

    public Briscola(int numPlayers)
    {
        deck=new Deck();
        brain=new Brain();
        players=new ArrayList<Player>();
        surface=new ArrayList<Card>();
        //duplicate the pointers now the deck holds all the 40 cards
        cardList=new ArrayList<Card>(deck.getDeck());

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
        briscolaCard=b;
        brain.setTrumpSuit(briscola);
        deck.addLastCard(b);
        currentPlayer= 0;
        briscolaPlayed=0;
        roundFinished=false;
        gameFinished=false;
        playableState=true;
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
            cardList=new ArrayList<Card>(deck.getDeck());
            if(deck.getSize()>0){
                briscolaCard=deck.getLastCard();
            }
            else
                briscolaCard=null;
            players=new ArrayList<Player>();
            surface=new ArrayList<Card>();
            //count briscola cards present in surface
            briscolaPlayed=0;
            //create players and distribute card in hand and pile
            //brain implements the RuleApplier interface
            //which declares a calculatePoints(ArrayList<Card> cards) method
            //that will be called inside the constructor of a player to calculate the points
            //savecards also in the total list of cards
            for(int i=0;i<state.hands.length;i++){
                players.add(new Player(state.hands[i],state.piles[i],"P"+(i+1),brain));
                for(Card c:players.get(i).getCardPile())
                    cardList.add(c);
                for(Card c:players.get(i).getHand())
                    cardList.add(c);
            }
            //place cards in surface
            for(String s:Parser.splitString(state.surface,2)){
                Card c=new Card(s);
                surface.add(c);
                cardList.add(c);
            }
            for(Card c:surface){
                if(c.getSuit().toString().equals(briscola.toString()))
                    briscolaPlayed++;
            }
            //check correctness of hands
            if(!handSizeCorrect()){
                System.out.println(toString());
                throw new InvalidGameStateException("Hand sizes don't reflect state of game");
            }
            if(surface.size()==players.size())
                roundFinished=true;
            else
                roundFinished=false;
            gameFinished=true;
            for (Player p :players){
                if(p.getHand().size()>0)
                {
                    gameFinished=false;
                }
                if(p.getCardPile().size()%2!=0)
                    throw new InvalidGameStateException("Invalid card piles");
            }
            if(getDeckSize()%2!=0)
                throw new InvalidGameStateException("Invalid Deck");
            //check that there are right number of cards
            if(cardList.size()!=Suit.values().length*Value.values().length)
                throw new InvalidGameStateException("Invalid Number Of Cards In Game");
        } catch (InvalidGameStateException | InvalidCardDescriptionException e) {
                System.out.println(e.getMessage());
                throw e;
            }
    }

    private boolean handSizeCorrect() {
        int index=currentPlayer;

        //return to starting index of the round
        for(int i=0;i<surface.size();i++)
        {
            index--;
            if(index==-1)
                index=players.size()-1;
        }
        int currentSize=players.get(currentPlayer).getHand().size();
        for(int i=0;i<surface.size();i++)
        {
            int handSize=players.get(index).getHand().size();
            if(handSize>2||handSize!=currentSize-1)
                return false;
            index++;
            if(index==players.size())
                index=0;

        }
        return true;
    }

    public String surfaceToString(){
        String str="";
        for(Card c:surface){
            str+=c.toString();
        }
        return str;
    }

    public boolean isRoundFinished(){
        return roundFinished;
    }
    public boolean isGameFinished(){
        return gameFinished;
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
            if(isGameFinished())
                return "ERROR: More Moves than possible are specified";
            boolean res= onPerformMove(moves.charAt(i)-'0');
            if(!res){
                    return "ERROR: Wrong Sequence of moves";
                }
            if(isRoundFinished()){
                finishRound();
                dealCards();
            }
            }
        if(isGameFinished()){
            String message="DRAW";
            for(int i=0;i<players.size();i++){
                Player p=players.get(i);
                if(p.getScore()>60)
                {
                    message="WINNER "+i+" "+p.getScore();
                    break;
                }
            }
            return message;
        }
        //In this case the moves are finished so we just return the state
        return this.toString();
    }

    public void dealCards(){
        for (int j = 0; j < players.size(); j++) {
            try {
                players.get((currentPlayer + j) % players.size()).addCardToHand(deck.drawCard());
            } catch (NoCardInDeckException e) {
                //if cards finish don't deal
                break;
            }
        }
    }
    //returns true if card was dealed
    public boolean dealCard(){
            if(isPlayableState()||roundFinished)
                return false;
            try {
                players.get((dealingIndex ) ).addCardToHand(deck.drawCard());
                incrementDealingIndexPlayer();
                if(dealingIndex==currentPlayer)
                    playableState=true;
                return true;
            } catch (NoCardInDeckException e) {
                //if cards finish don't deal
                return false;
            }
    }

    private void incrementDealingIndexPlayer() {
        dealingIndex=(dealingIndex+1)%players.size();
    }

    //return true if move performed
    public boolean onPerformMove(int index) throws ArrayIndexOutOfBoundsException{
        if(roundFinished)
            return false;
        try{
            Card c=players.get(currentPlayer).placeCardAtIndex(index);
            surface.add(c);
            if (c.getSuit().toString().equals(briscola.toString()))
                briscolaPlayed++;
            if(surface.size()==players.size())
            {
                roundFinished=true;
                playableState=false;
                if(players.get(currentPlayer).getHand().size()==0)
                    gameFinished=true;
            }
            incrementCurrentPlayer();
            return true;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return false;
        }

    }
    public boolean finishRound(){
        if(roundFinished)
        {
            //determine winning card
            int winner =brain.determineWinner(surface,briscolaPlayed);
            int points=brain.calculatePoints(surface);
            //addcards to the winner pile (winner will be first player of next round)
            currentPlayer+=winner;
            currentPlayer=currentPlayer%players.size();
            players.get(currentPlayer).addCardsinPile(surface);
            surface.clear();
            dealingIndex=currentPlayer;
            players.get(currentPlayer).incrementScore(points);
            roundFinished=false;
            briscolaPlayed=0;
            return true;
        }
        else
            return false;
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

    public Briscola restart(){
        Instance=new Briscola(players.size());
        return Instance;
    }
    public Briscola startFromConfiguration(String configuration) throws InvalidCardDescriptionException, InvalidGameStateException {
        Instance=new Briscola(configuration);
        return Instance;
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
        int deckSize=getDeckSize();
        boolean playableState=isPlayableState();
        return new StateBundle(hand1,hand2,surface,briscola,currentPlayer,pile1,pile2,score1,score2,deckSize,playableState);
    }

    public PlayerState getCurrentPlayerState(){
        ArrayList<Card> hand=getPlayerHandCards(currentPlayer);
        ArrayList<Card> surface=getSurfaceCards();
        ArrayList<Card> ownPile=getPlayerCardsPile(currentPlayer);
        ArrayList<ArrayList<Card>> opponentPiles=new ArrayList<ArrayList<Card>>();
        Card briscola=briscolaCard;

        int index=currentPlayer;
        for(int i=1;i<players.size();i++){
            index=(index+1)%players.size();
            opponentPiles.add(getPlayerCardsPile(index));
        }

        return new PlayerState(hand,surface,ownPile,opponentPiles,briscola);
    }
    public int getDeckSize(){
        return deck.getSize();
    }
    public boolean isPlayableState(){
        return playableState;
    }

    public ArrayList<Card> getSurfaceCards() {
        return surface;
    }

    public ArrayList<Card> getPlayerCardsPile(int index) {
        return players.get(index).getCardPile();
    }

    public ArrayList<Card> getCardList() {
        return cardList;
    }
}
