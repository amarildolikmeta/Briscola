package it.polimi.ma.group07.briscola.model;

import android.util.Log;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Exceptions.BriscolaException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.Exceptions.NoCardInDeckException;


/**
 *Represents a game of Briscola. Its the model object
 * that is gonna be created by the application
 */

public class Briscola {
    /** Represents the total number of points a deck of cards holds
     * Used later in the game to determine the player that won
     */
    private final int TOTAL_POINTS=120;
    /**
     * Static reference to the instance of the object used in the game
     */
    private static Briscola Instance=null;
    /** Represents the players of the game
     * Each player object will hold the player hand and pile
     * of cards he has collected so far
     */
    private ArrayList<Player> players;
    /** Reference to the Deck object of the game {@link Deck}
     */
    private Deck deck;
    /** List of all the 40 cards present in the game.
     * The list will be used to validate that all cards are in a game
     * when a game is created from a string representation of it
     * as well  as to make decisions from the AI , to check which cards
     * are left in the game
     */
    private ArrayList<Card> cardList;
    /** Represents the Suit of the Trump Card
     */
    private Suit briscola;
    /** Reference to the trump card object
     * Its kept separated from the Briscola suit
     * to cover the special case when the game start from
     * a moment when there are no cards in the deck
     * so we know just the suit of the trump card and
     * not the Value
     */
    private Card briscolaCard;
    /** Rule applier for the game {@link Brain}
     */
    private Brain brain;
    /** Index of the current player to make a move whe the game
     * is in a playable state
     */
    private int currentPlayer;
    /** Array of cards thrown in the surface
     */
    private ArrayList<Card> surface;
    /** State flag :
     * true when all the players have played a card and a winner for the round
     * has to be determined
     */
    private boolean roundFinished;
    /** State flag:
     * true when the game all the cards are played and
     * the game is finished
     */
    private boolean gameFinished;
    /** State flag:
     * true when the game is in a playable state and it's the turn of
     * currentPlayer to throw a card
     */
    private boolean playableState;
    /** Index used when dealing cards to keep track which players
     * turn is to get the next card
     */
    private int dealingIndex;

    /**
     * Creates a Briscola object
     * saves the reference in the static variable Instance
     * @return  Reference to the created Briscola object
     */
    public static Briscola createInstance(){
        Instance=new Briscola();
        return Instance;
    }
    public static void  deleteInstance(){

        Instance=null;
    }
    /**
     * Creates a new game of Briscola
     * @param numPlayers number of players in the game
     * @return reference to the created object
     */
    public static Briscola createInstance(int numPlayers){
        Instance=new Briscola(numPlayers);
        return Instance;
    }

    /**
     * Creates a new game of Briscola starting starting from a configuration string
     * @param configuration String representing the state of the game
     *                      as described in the description of the project
     * @return reference to the created object
     * @throws InvalidCardDescriptionException  {@link InvalidCardDescriptionException}
     * @throws InvalidGameStateException {@link InvalidGameStateException}
     */
    public static Briscola createInstance(String configuration) throws InvalidCardDescriptionException, InvalidGameStateException {
        Instance=new Briscola(configuration);
        return Instance;
    }

    /**
     * Creates a new game of Briscola starting starting from a configuration string
     * @param configuration String representing the state of the game
     *                      as described in the description of the project
     * @param numpPlayers  number of players in the game
     * @return reference to the created object
     * @throws InvalidGameStateException {@link InvalidGameStateException}
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     */
    public static Briscola createInstance(String configuration, int numpPlayers) throws InvalidGameStateException, InvalidCardDescriptionException {
        Instance=new Briscola(configuration,numpPlayers);
        return Instance;
    }

    /**
     * Creates a new instance of the game if not already created and return the
     * reference to the game instance
     * @return Refence to the game instance
     */
    public static Briscola getInstance(){
        if(Instance==null){
            Instance=new Briscola();
        }
        return Instance;
    }

    /**
     * Default Constructor
     */
    public Briscola()  {
        this(2);

    }

    /**
     * Constructor for a variable number of players
     * Creates all the elements of the game .
     * Player Objects {@link Player}
     * Surface
     * Rule Applier {@link Brain}
     * @param numPlayers number of players int the game
     */
    public Briscola(int numPlayers) {
        deck=new Deck();
        brain=new Brain();
        players=new ArrayList<Player>();
        surface=new ArrayList<Card>();
        /**
         * Right after deck is created it holds all the cards in the game
         * save them in the cardList Array
         */
        cardList=new ArrayList<Card>(deck.getDeck());

        for(int i=0;i<numPlayers;i++)
            players.add(new Player("P"+(i+1)));
        for(int i=0;i<3;i++){
            for(int j=0;j<numPlayers;j++) {
                try {
                    players.get(j).addCardToHand(deck.drawCard());
                } catch (NoCardInDeckException e) {

                }
            }
        }
        Card b= null;
        try {
            b = deck.drawCard();
        } catch (NoCardInDeckException e) {

        }
        briscola=b.getSuit();
        briscolaCard=b;
        brain.setTrumpSuit(briscola);
        deck.addLastCard(b);
        currentPlayer= 0;
        roundFinished=false;
        gameFinished=false;
        playableState=true;
    }

    /**
     * Creates a new game starting from a configuration string
     * number of players is the default 2
     * @param description Configuration string of the game to start from
     * @throws InvalidGameStateException {@link InvalidGameStateException}
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     */
    public Briscola(String description) throws InvalidGameStateException, InvalidCardDescriptionException {
        this(description,2);
    }

    /**
     * Creates a new game with a specified number of players  starting from a configuration string
     * @param description Configuration string
     * @param numPlayers number of players in the game
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     * @throws InvalidGameStateException {@link InvalidGameStateException}
     */
    public Briscola(String description,int numPlayers) throws InvalidCardDescriptionException, InvalidGameStateException {
        brain=new Brain();
        setState(description, numPlayers);

    }

    /**
     * Sets the state of the game from the configuration string
     * parses the string separating the fields
     * checks if the given state is valid
     * and sets the flag fields
     * @param configuration string configuration
     * @param numPlayers number of players
     * @throws InvalidGameStateException {@link InvalidGameStateException}
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     */
    private void setState(String configuration,int numPlayers) throws InvalidGameStateException, InvalidCardDescriptionException {
        try {
            /**
             * the configuration string is parsed into a {@link State} object
             * by the helper class {@link Parser} parseState() method
             * which also performs some checks on the validity of the configuration string
             */
            State state=Parser.parseState(configuration,numPlayers);
            briscola=Suit.stringToSuit(state.trump);
            brain.setTrumpSuit(briscola);
            currentPlayer=state.currentPlayer;
            /**
             * create deck object form the parsed string
             */
            deck=new Deck(state.deck);
            /**
             * Card list will be populated incrementally from each possible
             * place cards can be on the game : deck,surface
             * player hands and piles
             */
            cardList=new ArrayList<Card>(deck.getDeck());
            if(deck.getSize()>0){
                briscolaCard=deck.getLastCard();
            }
            else
                briscolaCard=null;
            players=new ArrayList<Player>();
            surface=new ArrayList<Card>();
            /**create players and distribute card in hand and pile
            *brain implements the RuleApplier interface
            *which declares a calculatePoints(ArrayList<Card> cards) method
            *that will be called inside the constructor of a player to calculate the points
            *save cards also in the total list of cards
             */
            for(int i=0;i<state.hands.length;i++){
                players.add(new Player(state.hands[i],state.piles[i],"P"+(i+1),brain));
                for(Card c:players.get(i).getCardPile())
                    cardList.add(c);
                for(Card c:players.get(i).getHand())
                    cardList.add(c);
            }
            /**place cards in surface
             */
            for(String s:Parser.splitString(state.surface,2)){
                Card c=new Card(s);
                surface.add(c);
                cardList.add(c);
            }

            /**
             * check the round finished flag
             * a round is finished if every player has played a card
             */
            if(surface.size()==players.size())
                roundFinished=true;
            else
                roundFinished=false;
            /**
             * check correctness of the cards
             */
            

            /**
             * Assume game is finished and if any player has cards left
             * throw away the assumption
             * check also the card piles of each player
             * if they have and odd number of cards the state is invalid
             */
            gameFinished=true;
            for (Player p :players){
                if(p.getHand().size()>0)
                {
                    gameFinished=false;
                }
                if(p.getCardPile().size()%2!=0)
                    throw new InvalidGameStateException("Invalid card piles");
            }
            /**if the round is not finished we are in a playble state
             *
             */
            if(!isRoundFinished()&&!isGameFinished())
                playableState=true;
            /**
             * To conclude we check that the deck has an even number of cards
             * and that all the 40 cards are in the game
             * Note: We should also check that each card is repeated once
             * but this check is done by the Parser.parseState() method
             */
            if(getDeckSize()%2!=0)
                throw new InvalidGameStateException("Invalid Deck");
            if(cardList.size()!=Suit.values().length*Value.values().length)
                throw new InvalidGameStateException("Invalid Number Of Cards In Game");
        } catch (InvalidGameStateException | InvalidCardDescriptionException e) {

                throw e;
            }
    }

    /**
     * Checks if the card representations in the state are correct
     *
     * @return a boolean , true if the state of the hands is correct
     */
    private boolean handSizeCorrect() {
        /**
         * special case when there are no cards in the surface
         * none of the players have played
         */
        if(surface.size()==0){
            /**
             * if there are cards in the deck each player
             * must have 3 cards in their hands
             * since the configuration string represents a playable state
             * if there are no cards on the deck
             * each player must have the same number of cards
             */
            if(deck.hasMoreCards())
            {
                for(Player p:players)
                    if(p.getHand().size()!=3)
                        return false;
            }
            else
            {
                int size=players.get(0).getHand().size();
                for(Player p:players){
                    if(p.getHand().size()!=size)
                        return false;
                }
            }
            return true;
        }
        /**
         * if the round is finished
         * each player has played so they must have at most 2 cards
         * and all the same number in the case the deck still has cards
         * or lower if the deck has no more cards
         */
        if(isRoundFinished()){
            if(deck.hasMoreCards())
            {
                for(Player p:players)
                    if(p.getHand().size()!=2)
                        return false;
            }
            else
            {
                int size=players.get(0).getHand().size();
                for(Player p:players){
                    if(p.getHand().size()!=size)
                        return false;
                }
            }
            return true;
        }
        int index=currentPlayer;

        /**
         * if none of the previous cases are true
         * the round isn't finished and cards have been played
         * in this case
         * we check that players that haven't played yet have one more cards that those that have already played
         */
        for(int i=0;i<surface.size();i++)
        {
            index--;
            if(index==-1)
                index=players.size()-1;
        }
        int currentSize=players.get(currentPlayer).getHand().size();
        if(deck.hasMoreCards())
            if(currentSize!=3)
                return false;
        else {
             int leftCards=40;
                for(int i=0;i<players.size();i++)
                    leftCards-=players.get(i).getHand().size();
             if(leftCards/players.size()!=currentSize)
                 return false;
            }
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

    /**
     *
     * @return a string representation of the current state of the game
     */
    public String surfaceToString(){
        String str="";
        for(Card c:surface){
            str+=c.toString();
        }
        return str;
    }

    /**
     *
     * @return true if the current game is finished
     */
    public boolean isRoundFinished(){
        return roundFinished;
    }

    /**
     *
     * @return true if the current game is finished
     */
    public boolean isGameFinished(){
        return gameFinished;
    }

    /**
     * The moveTest method required in the project specification
     * takes as input a playable state of the game and a string representing
     * a set of moves and return a string representing the state of the game after
     * the list of moves was performed
     * @param configuration Configuration string
     * @param moves List of moves to be performed
     * @return Final state of the game
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     * @throws InvalidGameStateException {@link InvalidGameStateException}
     */
    public String moveTest(String configuration,String moves) throws InvalidCardDescriptionException, InvalidGameStateException {
        /**
         * Create the game from the specified string
         */
        try {
            setState(configuration,2);
        } catch (InvalidGameStateException |InvalidCardDescriptionException e) {
            return "ERROR: "+e.getMessage();
        }
        /**
         * Check if the string represents a playable state or not
         */
        if(isRoundFinished()){
            if(isRoundFinished()){
                finishRound();
                dealCards();
                if(players.get(currentPlayer).getHand().size()==0)
                    gameFinished=true;
            }
        }
        /**
         * Check if the game is finshed
         * if so determine the winner
         */
        if(isGameFinished()){
            for (int i=0;i<players.size();i++)
            {
                Player p=players.get(i);
                if(p.getScore()>TOTAL_POINTS/players.size())
                {
                    return "WINNER "+i+" "+p.getScore();
                }
            }
            return "DRAW";
        }
        /**
         * apply the list of moves specified
         */
        for(int i=0;i<moves.length();i++){
            /**
             * more moves than possible were specified
             */
            if(isGameFinished())
                return "ERROR: More Moves than possible are specified";
            /**
             * perform the specified move
             * by casting the string representation of the move
             * into the index of the card to be performed
             * if it return false the move wasnt performed
             * so the sequence of moves given wasnt correct
             */
            String res= onPerformMove(moves.charAt(i)-'0');
            if(res==""){
                    return "ERROR: Incorrect Sequence of moves";
                }
            /**
             * Finish round and deal next cards
             */
            if(isRoundFinished()){
                finishRound();
                dealCards();
            }
            }
        /**
         * if the game finishes after performing the move
         * determine the winner and return the appropriate message as specified
         */
        if(isGameFinished()){
            String message="DRAW";
            for(int i=0;i<players.size();i++){
                Player p=players.get(i);
                if(p.getScore()>TOTAL_POINTS/players.size())
                {
                    message="WINNER "+i+" "+p.getScore();
                    break;
                }
            }
            return message;
        }
        /**
         * In this case the moves are finished so we just return the state
         */
        return this.toString();
    }

    /**
     * Deals one card to each player if possible
     */
    public void dealCards(){
        for (int j = 0; j < players.size(); j++) {
            try {
                players.get((currentPlayer + j) % players.size()).addCardToHand(deck.drawCard());
            } catch (NoCardInDeckException e) {
                /**
                 * if cards finish don't deal
                 */
                playableState=true;
                break;
            }
        }
        playableState=true;
    }

    /**
     * Deal one card to the next player
     * the index of the player to deal next is specified in the field
     * dealing index
     * @return String representation of dealt card ,otherwise null
     * might fail if we are in a playable state,
     * the winner of the last round is not yet determined
     * or there are no cards in the deck
     */
    public String dealCard(){
            if(isPlayableState()||roundFinished)
                return null;
            Card card=null;
            try {
                card=deck.drawCard();
                players.get((dealingIndex ) ).addCardToHand(card);
                incrementDealingIndexPlayer();
                /**
                 * when dealing index is equal to the next player to play
                 * we are back to a playable state
                 */
                if(dealingIndex==currentPlayer)
                    playableState=true;
                return card.toString();
            } catch (NoCardInDeckException e) {
                /**if cards finish don't deal
                 *
                 */
                playableState=true;
                return null;
            }
    }

    private void incrementDealingIndexPlayer() {
        dealingIndex=(dealingIndex+1)%players.size();
    }

    /**
     * Perform the move specified by the given index on the current player
     * @param index index of the card to be played from the hand of the current player
     * @return String Representation of Card that was thrown or empty string if error
     * @throws IndexOutOfBoundsException if the index specified was outside the range
     * of the hand of the current player
     */
    public String onPerformMove(int index) throws IndexOutOfBoundsException{
        /**
         * Cannot perform a move if the game is not in a playable state
         */
        if(isRoundFinished()||!isPlayableState()) {
            Log.i("Briscola","Not Playable");
            return "";
        }
        try{
            /**
             * place the specified card in the surface
             */
            Card c=players.get(currentPlayer).placeCardAtIndex(index);
            surface.add(c);
            /**
             * check if the current round is finished (all player have played)
             */
            if(surface.size()==players.size())
            {
                roundFinished=true;
                playableState=false;
            }
            incrementCurrentPlayer();
            return c.toString();
        }
        catch (IndexOutOfBoundsException e)
        {
            Log.i("Briscola","Exception");
            return "";
        }

    }

    /**
     * determines the winner
     * moves cards from surface to the player piles
     * and updates the state flags
     * @return true if the round is finished succesfuly
     */
    public boolean finishRound(){
        if(isRoundFinished())
        {
            currentPlayer=determineWinningPlayer(surface);
            int points=brain.calculatePoints(surface);
            players.get(currentPlayer).addCardsinPile(surface);
            players.get(currentPlayer).incrementScore(points);
            /**
             * clear the surface and set the dealing index to the
             * winning player index and update the round finished flag
             * (The game is not yet playable until the next batch of cards is dealed)
             */
            surface.clear();
            dealingIndex=currentPlayer;
            roundFinished=false;
            if(players.get(currentPlayer).getHand().size()==0)
                gameFinished=true;
            return true;
        }
        else
            return false;
    }

    public void incrementCurrentPlayer()
    {
        currentPlayer=(currentPlayer+1)%players.size();
    }
    /**
     * Get the index of the winner taking into account also special cases as
     * Game finishes in draw :return -1
     * Game isn't finished yet :return -2
     * @return index of winning player or {-1;-2}
     */
    public int getWinner(){
        if(!isGameFinished())
            return -2;
        return brain.determineWinningPlayer(players);
    }
    /**
     *
     * @return returns string representation of the current game
     */
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

    /**
     *
     * @param index index of the player
     * @return a string representing the hand of a player index
     */
    public String getPlayerHand(int index){
        return players.get(index).getHand().toString().replace("[","").replace(" ","").replace("]","").replace(",","");
    }

    /**
     *
     * @param index index of the player
     * @return hand of the player as an array of cards
     */
    public ArrayList<Card> getPlayerHandCards(int index){
        return players.get(index).getHand();
    }

    /**
     *
     * @return a string representing the surface
     */
    public String getSurface(){
        return surface.toString().replace("[","").replace(" ","").replace("]","").replace(",","");
    }

    /**
     *
     * @param index index of the player
     * @return a string representing the pile of a player with index
     */
    public String getPlayerCardPile(int index){
        return players.get(index).getCardPile().toString().replace("[","").replace(" ","").replace("]","").replace(",","");
    }

    /**
     *
     * @return a string representation of the Bricola card
     */
    public String getBriscolaCard(){
        //return BricolaCard if in deck
        try {
            return deck.getLastCard().toString();
        } catch (IndexOutOfBoundsException e)
        {
            /** return just the suit
             */
            return briscola.toString();
        }
    }

    /**
     * Restarts the game
     * @return reference the the current instance of the game
     */
    public Briscola restart(){
        Instance=new Briscola(players.size());
        return Instance;
    }

    /**
     * Starts game from a given configuration
     * @param configuration  configuration string
     * @return reference to the current game
     * @throws InvalidCardDescriptionException {@link InvalidCardDescriptionException}
     * @throws InvalidGameStateException {@link InvalidGameStateException}
     */
    public Briscola startFromConfiguration(String configuration) throws InvalidCardDescriptionException, InvalidGameStateException {
        Instance=new Briscola(configuration);
        return Instance;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Object that holds the information about the current state of the game
     * Serves as interface of the class  {@link StateBundle}
     * @return object representing the state of the game
     */
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

    /**
     * Returns state of the current player to play  {@link PlayerState}
     * @return state of a single player
     */
    public PlayerState getPlayerState(int playerIndex){
        ArrayList<String> hand=new ArrayList<>(Parser.splitString(getPlayerHand(playerIndex),2));
        ArrayList<String> surface=new ArrayList<>(Parser.splitString(getSurface(),2));
        ArrayList<String> ownPile=new ArrayList<>(Parser.splitString(getPlayerCardPile(playerIndex),2));
        ArrayList<ArrayList<String>> opponentPiles=new ArrayList<ArrayList<String>>();
        Card briscola=briscolaCard;
        int opponentHandSizes[]=new int[players.size()-1];
        /**
         * Visible state of a player is considered also the
         * piles of the other players since the player can
         * witness and count each card in each players piles
         * it's important to be used by an AI
         */
        int index=playerIndex;
        for(int i=1;i<players.size();i++){
            index=(index+1)%players.size();
            opponentPiles.add(new ArrayList<String>(Parser.splitString(getPlayerCardPile(index),2)));
            opponentHandSizes[i-1]=players.get(index).getHand().size();
        }

        return new PlayerState(hand,surface,ownPile,opponentPiles,briscola,
                playerIndex,deck.getSize(),opponentHandSizes,isPlayableState());
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

    /**
     * Method to create randomly states starting from the current state
     * of the game to test for invalid states
     * @return string representation of the shuffled state
     */
    public String shuffleState(){
            ArrayList<ArrayList<Card>> positions=new ArrayList<ArrayList<Card>>();
            positions.add(deck.getDeck());
            positions.add(players.get(0).getHand());
            positions.add(players.get(1).getHand());
            positions.add(players.get(0).getCardPile());
            positions.add(players.get(1).getCardPile());
        /**
         * Apply 30 random movement of cards
         * inside the game to create a potentially invalid state
         */
        for(int i=0;i<30;i++){
                ArrayList<Card> src=null;
                while(src==null){
                    src=positions.get(random(positions.size()));
                    if(src.size()==0)
                        src=null;
                }
                ArrayList<Card> dest=null;
                while(dest==null){
                    dest=positions.get(random(positions.size()));
                    if(dest.size()==0)
                        dest=null;
                }
                Card c=src.remove(random(src.size()));
                dest.add(c);
            }
            return toString();
    }

    /**
     * return a random integer from 0 to range (not inclusive)
     * @param range maximum number returned
     * @return integer included in [0:range[
     */
    private int random(int range){
        return (int)(Math.floor(Math.random()*range));
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Brain getBrain() {
        return brain;
    }

    public int getNumberPlayers() {
        return players.size();
    }

    /**
     * determines Winning player of the round if the round is finished
     * @return index of winning player or 1 if round is not finished
     */
    public int determineWinningPlayer(ArrayList<Card> surface){
        if(surface.size()==players.size()){
            /**
             * determine the index of the winning card and
             */
            int winner =brain.determineWinner(surface);
            /**
             * determine winner player from the index of the cards
             * addcards to the winner pile and increment his score
             * (winner will be first player of next round)
             */
            int winningPlayer=winner;
            winningPlayer=(currentPlayer+winningPlayer)%players.size();
            return winningPlayer;
        }
        return -1;
    }

    /**
     * calculate the reward s move gives to the current player without performing it
     * To be used by the AI
     * @param move index of card to be played by current player
     * @return points the move awards
     */
    public int getReward(int move){
        //last move of the round
        if(surface.size()+1==players.size()){
               ArrayList<Card> s=new ArrayList<>(surface);
               Card cardPlayed=players.get(currentPlayer).getHand().get(move);
               s.add(cardPlayed);
               int winningPlayer=determineWinningPlayer(s);
               if(winningPlayer==currentPlayer)
                   return brain.calculatePoints(s);
        }
            return 0;
    }

    public boolean hasMoreCards() {
        return deck.hasMoreCards();
    }

    public int[] getScores() {
        int scores[]=new int[getNumberPlayers()];
        for(int i=0;i<players.size();i++)
            scores[i]=players.get(i).getScore();
        return scores;
    }

    /**
     * Get the cards in the deck as a list of strings
     * @return list of string representations of the cards in the deck
     */
    public ArrayList<String> getDeckAsStrings() {
        ArrayList<String> cards=new ArrayList<>();
        for(Card c:deck.getDeck()){
            cards.add(c.toString());
        }
        return cards;
    }
}
