package it.polimi.ma.group07.briscola;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import it.polimi.ma.group07.briscola.model.Brain;
import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Card;
import it.polimi.ma.group07.briscola.model.Deck;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidCardDescriptionException;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.Exceptions.NoCardInDeckException;
import it.polimi.ma.group07.briscola.model.Parser;
import it.polimi.ma.group07.briscola.model.Player;
import it.polimi.ma.group07.briscola.model.State;
import it.polimi.ma.group07.briscola.model.StateBundle;
import it.polimi.ma.group07.briscola.model.Suit;
import it.polimi.ma.group07.briscola.model.Value;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    String state;
    String res;

    /**
     * Testing some sequences of moves and the resulting state of the game
     * the tests are taken form the project presentation slides
     * @throws Exception
     */
    @Test
    public void example_moves_isCorrect() throws Exception {
        String state="0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.1CKS3G..";
        Briscola b=new Briscola(state);
        assertEquals(state,b.toString());
        String[] moves={"0","00","001","0011","00110"};
        String[] results={"1B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B.JC.KG2B.1CKS3G..",
                "1B6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..KG2B4G.KS3G5S..JC1C",
                "0B6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B.3G.KG2B4G.KS5S..JC1C",
                "0B5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..KG4G6S.KS5S2C.3G2B.JC1C",
                "1B5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B.KG.4G6S.KS5S2C.3G2B.JC1C"};
        for(int i=0;i<moves.length;i++) {
            String res=b.moveTest(state, moves[i]);
            System.out.println(res);
            assertEquals(res, results[i]);
        }
        state="0C4G4SHC2G6S2SJS4B7B2BKG5G6G7C3C3S1G7GKS6B3BHB5C3GJG1C..4CJB2C.KC6CJC.KB1BHS5B.5S1SHG7S";
        String move="22122222202212222212220022110000";
        String result="WINNER 1 70";
        String res=b.moveTest(state, move);
        System.out.println(res);
        assertEquals(res, result);
    }

    /**
     * Test of invalid configurations to start the game
     * the invalid configurations are wrong not sintatically in the form of the configuration string
     * but are states that can never be achieved logically like:
     * states where the surface has a card but the current player has less cards than the previous player
     * states when both players have less than 2 cards but the deck still has cards
     * states with more than 6 cards in play (hands and surface)
     * @throws Exception
     */
    @Test
    public void invalid_configurations_test() throws Exception {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("invalidConfigurations");
        BufferedReader file=new BufferedReader(new InputStreamReader(in));
        String state;
        Briscola b;

        while((state=file.readLine())!=null)
        {
            try{
                b=new Briscola(state);
                fail("Failed recognizing invalid state");
            }catch (InvalidGameStateException e){

            }

        }

    }

    /**
     * This test automatically tests invalid configurations by creating a game
     * playing some random moves and shuffling the cards of the game with the
     * {@link Briscola#shuffleState()} method . Since this is done randomly there is a possibility that
     * by chance the shuffle state is still valid , but this should occur in a small percentage of cases
     * For this reason we count the number of times this occurs and check manually all these states in the end
     * to see if indeed they are valid or no
     * @throws Exception
     */
    @Test
    public void randomInvalidConfigurationTests() throws Exception {
        int count=0;
        for(int i=0;i<10000;i++){
            Briscola game=new Briscola();
            String startState=game.toString();
            String moves=generateMoves();
            try{
                game.moveTest(startState,moves);
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("Error in test number:"+(i+1));
                System.out.println("Start Configuration:"+startState);
                System.out.println("Moves:"+moves);
                fail("Error in applying moves");
            }
            String invalidState=game.shuffleState();
            try{
                game=new Briscola(invalidState);
                count++;

            }
            catch (InvalidGameStateException e){
            }
        }
        System.out.println("Total valid States:"+count);
    }

    /**
     * Check of the determineWinner() method in the Brain class
     * that determines the winning card on the surface
     * A number of cases are tested including
     * cases when both cards in surface are of Briscola suit
     * cases when just one of the cards is of Briscola Suit
     * cases when none of the cards are Briscola Suit but they have same suit
     * cases when none of the cards are Briscola Suit but different suit
     * @throws InvalidCardDescriptionException
     */

    @Test
    public void checkDeterminedCardWinner() throws InvalidCardDescriptionException {
        Brain br = new Brain();
        String[] suits = {"B","S","C","G","G"};
        String[] BothBriscolasurfaces={"4B5B","4SKS","JC1C","KG7G","1G2G"};
        String[] OneBriscolasurfaces={"4B7C","4SKG","5GHC","6SKG","3G1C"};
        String[] NoneBriscolasurfacesSame={"7SKS","4G5G","5GHG","6S2S","KG2G"};
        String[] NoneBothBriscolasurfacesDifferent={"2S7G","4B4C","5GHB","6SKB","3SKB"};
        int[] resultsBothBriscola={1,1,1,0,0};
        int[] resultsOneBriscola={0,0,1,1,0};
        int[] resultsNoneBriscolaSame={1,1,1,0,0};
        int[] resultsNoneBriscolaDifferent={0,0,0,0,0};
        //Test Both Briscola Case
        for(int i=0;i<BothBriscolasurfaces.length;i++){
            ArrayList<String> cardStrings = Parser.splitString(BothBriscolasurfaces[i],2);
            ArrayList<Card> surface = new ArrayList<Card>();

            for(String s:cardStrings){
                surface.add(new Card(s));
            }

            br.setTrumpSuit(Suit.stringToSuit(suits[i]));
            int result = br.determineWinner(surface);

            assertEquals(resultsBothBriscola[i],result);
        }
        //Test One Briscola Case
        for(int i=0;i<OneBriscolasurfaces.length;i++){
            ArrayList<String> cardStrings = Parser.splitString(OneBriscolasurfaces[i],2);
            ArrayList<Card> surface = new ArrayList<Card>();

            for(String s:cardStrings){
                surface.add(new Card(s));
            }

            br.setTrumpSuit(Suit.stringToSuit(suits[i]));
            int result = br.determineWinner(surface);

            assertEquals(resultsOneBriscola[i],result);
        }
        //Test None Briscola Cases but same suit
        for(int i=0;i<NoneBriscolasurfacesSame.length;i++){
            ArrayList<String> cardStrings = Parser.splitString(NoneBriscolasurfacesSame[i],2);
            ArrayList<Card> surface = new ArrayList<Card>();

            for(String s:cardStrings){
                surface.add(new Card(s));
            }

            br.setTrumpSuit(Suit.stringToSuit(suits[i]));
            int result = br.determineWinner(surface);

            assertEquals(resultsNoneBriscolaSame[i],result);
        }
        //Test None Briscola Case but different suits
        for(int i=0;i<NoneBothBriscolasurfacesDifferent.length;i++){
            ArrayList<String> cardStrings = Parser.splitString(NoneBothBriscolasurfacesDifferent[i],2);
            ArrayList<Card> surface = new ArrayList<Card>();

            for(String s:cardStrings){
                surface.add(new Card(s));
            }

            br.setTrumpSuit(Suit.stringToSuit(suits[i]));
            int result = br.determineWinner(surface);

            assertEquals(resultsNoneBriscolaDifferent[i],result);
        }
    }

    /**
     * Check of the calculatePoints() method in the Brain class
     * that calculates total points of cards on the surfaces
     * @throws InvalidCardDescriptionException
     */
    @Test
    public void checkCalculatedPoints() throws InvalidCardDescriptionException {
        Brain br = new Brain();

        String[] surfaces={"4B5B","4S4B","5GHG","6SKB","3G1G","KCHC","3G3S","2S3B"};
        int[] results={0,0,3,4,21,7,20,10};

        for(int i=0;i<surfaces.length;i++){
            ArrayList<String> cardStrings = Parser.splitString(surfaces[i],2);
            ArrayList<Card> surface = new ArrayList<Card>();

            for(String s:cardStrings){
                surface.add(new Card(s));
            }

            int result = br.calculatePoints(surface);

            assertEquals(results[i],result);
        }
    }

    /**
     * Checks that the deck thorws an exception when trying to draw cards
     * when there are none to draw
     * @throws NoCardInDeckException
     */
    @Test
    public void checkDeck() throws NoCardInDeckException {
        Deck deck=new Deck();
        for(int i =0;i<40;i++)
        {
            deck.drawCard();
        }
        try{
            deck.drawCard();
            //supposed to throw exception
            fail("Drawing card didn't fail");
        }
        catch (NoCardInDeckException e)
        {
            //Supposed to happen
        }
    }

    /**
     * This test method checks that the {@link Player#placeCardAtIndex(int)} method
     * works properly including
     * returns the correct card thrown
     * throws exception when the index of the card to play is outside bounds
     * @throws IndexOutOfBoundsException
     * @throws InvalidCardDescriptionException
     */
    @Test
    public void checkPlayCard() throws IndexOutOfBoundsException, InvalidCardDescriptionException {
        Brain brain=new Brain();
        String[] hands={"4B5B","4S4B","5GHG3C","6SKBJG","3G1G","KCHC","3G3S","2S3B"};
        int[] indexes= {0,1,2,2,1,0,0,0};
        int[] outBoundIndexes={2,2,3,3,2,2,-1,-2};
        String[] cards={"4B","4B","3C","JG","1G","KC","3G","2S"};
        //tests correctness of the played card
        for(int i=0;i<hands.length;i++)
        {
            Player p=new Player(hands[i],"","player",brain);
            Card c =p.placeCardAtIndex(indexes[i]);
            assertEquals(cards[i],c.toString());
        }
        //tests if an exception is thrown
        for(int i=0;i<hands.length;i++)
        {
            Player p=new Player(hands[i],"","player",brain);
            try{
                Card c =p.placeCardAtIndex(outBoundIndexes[i]);
                //code shouldn't reach here
                fail("Should have failed");
            }
            catch (IndexOutOfBoundsException e){
                //supposed to happen
            }

        }
    }
    /**
     * This test checks that the {@link Brain#determineWinner(ArrayList)}  method
     * works properly
     * To check it we will start the game  from a final configuration
     * whose result is known  and test the method
     * by comparing the expected and actual result
     */
    @Test
    public void checkDetermineWinner() throws InvalidCardDescriptionException, InvalidGameStateException {
        String[] states={"0S....7BHB1GJGKB7C3G6C2BKC2GJB5B2C4B6S3S1C4G6B7S4C7G4SHS5C.5SKSHG2S6G5GJCHCKG1B3C3B1SJS",
                         "0S....1B6G2B4S7SHS3CJCKB2S2C7G6S3S.5G1SKG3GHGKCJG7BHB3BKS1CJS5CJB6B4C5S4B2G4G7CHC6C1G5B",
                         "0G....4C2G2S7B3BKCJC5B5C4G1S3G.HCHG2BKB6G7S1GHS6B6SJS2C5SHB7G6C4B3SKS1C3C7CJB5G1BKGJG4S",
                         "0B....KS4G7C5C4C2BHCHG1S2G2C3BKG4B6G4S5SJG2SKB6SKC6C3SHB5G7B6B1G3GJBJC3C1B.7G5BJS1C7SHS",
                         "0C5G7CJSKCKS2S2C3C4B5C1CJGKGHBHG1G6BJC1B4C6S4G5BJB1S3GHCKB3B6C..2G7G4S.7S3SHS.6G2B7B5S."};
        int[] results={-1,1,1,0,-2};
        Briscola b;
        Brain brain=new Brain();
        for(int i=0;i<states.length;i++){
           b=new Briscola(states[i]);
           int winner=brain.determineWinningPlayer(b.getPlayers());
           assertEquals(results[i],winner);
        }
    }

    /**
     * Check the Card constructor in the Card class
     * by providing it a set of all possible Invalid pairs of characters for card creation
     * @throws Exception
     */
    @Test
    public void checkCard() throws Exception {
        Brain br = new Brain();
        Character alpha = 'A';
        ArrayList<String> cards = new ArrayList<String>();
        ArrayList<String> valid=new ArrayList<String>();

        /**
         * Create a pile of valid cards
         */
        for(Suit s:Suit.values())
            for(Value v:Value.values())
                valid.add(v.toString()+s.toString());

        /**
         * Create all possible Numerical/Alphabetical+Alphabetical combinations
         * and clean from valid pairs to leave only the Invalid ones
         */
        for (int j=0; j < 25; j++) {
            Character beta = 'A';
            Character number = '1';

            for (int k=0; k < 10; k++) {
                String s=""+number + alpha;
                if(!valid.contains(s))
                    cards.add(s);
                number++;
            }

            for (int y=0; y < 25; y++) {
                String s = "" + beta + alpha;
                if(!valid.contains(s))
                    cards.add(s);
                beta++;
            }
            alpha++;
        }

        for (String c:cards){
            try{
                Card card = new Card(c);
                fail("Failed to create a Card number " + c);
            }
            catch(Exception e){}
        }
    }

    /**
     * Check drawCard() method from the Deck class
     * and pass an Empty Deck to it and check if you can draw a Card from it.
     * @throws NoCardInDeckException
     */
    @Test
    public void checkDrawCard() throws NoCardInDeckException, InvalidCardDescriptionException {
        Deck d = new Deck("");
        ArrayList<Card> cards = new ArrayList<Card>();

        try{
            d.drawCard();
            fail("Drew a card from an empty Deck");
        }
        catch(Exception e){}
    }

    private String generateState(){
        Briscola b=new Briscola();
        state=b.toString();
        return state;
    }
    private String generateMoves(){
        int moveLength= (int) (Math.floor(Math.random()*10)+10);
        String moves="";
        for(int i=0;i<moveLength;i++){
            if(i<=38)
            {
                moves+=(int)(Math.floor(Math.random()*3));
            }
            else
                if(i==39)
                    moves+=(int)(Math.floor(Math.random()*2));
                else
                    moves+=0;
        }
        return moves;
    }
}
