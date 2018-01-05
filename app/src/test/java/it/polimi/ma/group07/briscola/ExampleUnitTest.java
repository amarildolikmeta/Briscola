package it.polimi.ma.group07.briscola;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import it.polimi.ma.group07.briscola.controller.ServerCoordinator;
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
    /**
     *Test the {@link Briscola} constructor to start from a known configuration
     * that corrects the correct game and returns the same configuration after created
     */
    @Test
    public void checkBriscolaConstructor() throws InvalidCardDescriptionException, InvalidGameStateException {
        String startingStates[]={"0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.1CKS3G..",
                "0C7G3S4SHS5G7CJSKCKS2S2C3C4B5C1CJGKGHBHG1G6BJC1B4C6S4G5BJB1S3GHCKB3B6C..2G6G7B.7S2B5S..",
                "0GKS6B3GHC5B2B7BHB5G4G6G1S1BKC1C4C3B2S4B1GJBJCKB3C6C7C4SJS7SHG5S5CHSKG..2G3S7G.6S2CJG..",
                "0S4G4SHG2SHB5B1C4B1S7S3B3C7B7CKGJS4C6C7GHC1G6BKB3GJBKS6GJC2B5GKC3S6SHS..5S5C1B.2C2GJG..",
                "0S3C3GHG4SKCJG7SHSJC7B3BHB6BKS5CJSJB1CKB2C7G2G5S2S4C4B5B4GHC6C1G7C6S3S..5GKG1B.6G1S2B..",
                "0G1SHS5GKC2B6C1CJS4SJG2G6S7GHB1G1B5S5B3S3B4B6B2CHG3C7C4C7B2SKGJC6GKS4G..7S5CKB.JBHC3G..",
                "0S4G1CJS6C2CKS4C2S2B4B2G3G5BKG3S5C5GJG4S7C1SHB1BKC6BHS7GKB1G6GJCHG3C7S..5S6S3B.JBHC7B..",
                "0G4C2G2SKC2BKBJC1B1S6G1G7S3CHS6B6SJS2C5SHB7G6C4B7CKS1CKG3G5C4G4SJGJB5G..3S3BHC.HG7B5B..",
                "0SJG1GKB7C3G6C2BKC2GJB5B2CHG2S6S4B6G3S1C4G1B6B7SHCJS4CJCKG5CHS3C3B1S4S..5G5SHB.7GKS7B..",
                "0C4G4SHC2G6S2SJS4B7B2BKG5G6G7C3C3S1G7GKS6B3BHB5C3GJG1C..4CJB2C.KC6CJC.KB1BHS5B.5S1SHG7S"};
        Briscola game;
        for(int i=0;i<startingStates.length;i++) {
            game=new Briscola(startingStates[i]);
            assertEquals(startingStates[i],game.toString());
        }

    }

    /**
     * Testing some sequences of moves and the resulting state of the game
     * the tests are taken form the project presentation slides and also from
     * real games played and include
     * games that don't finish ,
     * games that finish with a winner,
     * gmes that finish in a draw
     * @throws InvalidGameStateException
     * @throws InvalidCardDescriptionException
     */
    @Test
    public void example_moves_isCorrect() throws InvalidCardDescriptionException, InvalidGameStateException {
        String startingStates[]={"0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.1CKS3G..",
                                 "0C7G3S4SHS5G7CJSKCKS2S2C3C4B5C1CJGKGHBHG1G6BJC1B4C6S4G5BJB1S3GHCKB3B6C..2G6G7B.7S2B5S..",
                                 "0GKS6B3GHC5B2B7BHB5G4G6G1S1BKC1C4C3B2S4B1GJBJCKB3C6C7C4SJS7SHG5S5CHSKG..2G3S7G.6S2CJG..",
                                 "0S4G4SHG2SHB5B1C4B1S7S3B3C7B7CKGJS4C6C7GHC1G6BKB3GJBKS6GJC2B5GKC3S6SHS..5S5C1B.2C2GJG..",
                                 "0S3C3GHG4SKCJG7SHSJC7B3BHB6BKS5CJSJB1CKB2C7G2G5S2S4C4B5B4GHC6C1G7C6S3S..5GKG1B.6G1S2B..",
                                 "0G1SHS5GKC2B6C1CJS4SJG2G6S7GHB1G1B5S5B3S3B4B6B2CHG3C7C4C7B2SKGJC6GKS4G..7S5CKB.JBHC3G..",
                                 "0S4G1CJS6C2CKS4C2S2B4B2G3G5BKG3S5C5GJG4S7C1SHB1BKC6BHS7GKB1G6GJCHG3C7S..5S6S3B.JBHC7B..",
                                 "0G4C2G2SKC2BKBJC1B1S6G1G7S3CHS6B6SJS2C5SHB7G6C4B7CKS1CKG3G5C4G4SJGJB5G..3S3BHC.HG7B5B..",
                                 "0SJG1GKB7C3G6C2BKC2GJB5B2CHG2S6S4B6G3S1C4G1B6B7SHCJS4CJCKG5CHS3C3B1S4S..5G5SHB.7GKS7B..",
                                 "0C4G4SHC2G6S2SJS4B7B2BKG5G6G7C3C3S1G7GKS6B3BHB5C3GJG1C..4CJB2C.KC6CJC.KB1BHS5B.5S1SHG7S",
                                 "0S5G1S3SKG3GJSHBJB6GHC2S7SJGKC6BHS3B4C5C5S3C7B4BJC6C1B2G4S1CKS7C7G1G6S..4G5B2C.KB2BHG.."};
        Briscola b=new Briscola();
        String[] moves={"00110",
                        "1111",
                        "111111112102",
                        "2110",
                        "2000020001020100000201000200100002010000",
                        "22111212012022021220",
                        "0210122112220221022120212022202220220100",
                        "2022201120101212222222222022112222221100",
                        "1111111111111111112010010112211022200100",
                        "22122222202212222212220022110000",
                        "2020000021020101111222001222001111001100",
                       };
        String[] results={"1B5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B.KG.4G6S.KS5S2C.3G2B.JC1C",
                "0C5G7CJSKCKS2S2C3C4B5C1CJGKGHBHG1G6BJC1B4C6S4G5BJB1S3GHCKB3B6C..2G7G4S.7S3SHS.6G2B7B5S.",
                "0G1BKC1C4C3B2S4B1GJBJCKB3C6C7C4SJS7SHG5S5CHSKG..2GHB6G.5B5G1S.3S2C6S4G.7GJG6BKS3GHC7B2B",
                "0SHB5B1C4B1S7S3B3C7B7CKGJS4C6C7GHC1G6BKB3GJBKS6GJC2B5GKC3S6SHS..5S4GHG.JG4S2S.1B2G5C2C.",
                "WINNER 1 80",
                "0G4B6B2CHG3C7C4C7B2SKGJC6GKS4G..JG1G3S.1C5B3B.4S7S2G6SJSHB7G1B5S2B.KB3GHC5C1SKC5G6CJBHS",
                "WINNER 1 69",
                "WINNER 1 66",
                "DRAW",
                "WINNER 1 70",
                "WINNER 0 85"};
        for(int i=0;i<moves.length;i++) {
            String res=b.moveTest(startingStates[i], moves[i]);
            assertEquals(results[i],res);
        }

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
    @Ignore
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
    @Ignore
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
     * Check of the calculatePointsString() method in the Brain class
     * that calculates total points of cards on the surfaces
     * @throws InvalidCardDescriptionException
     */
    @Test
    public void checkCalculatedPointsString() throws InvalidCardDescriptionException {
        Brain br = new Brain();

        String[] surfaces={"4B5B","4S4B","5GHG","6SKB","3G1G","KCHC","3G3S","2S3B"};
        int[] results={0,0,3,4,21,7,20,10};

        for(int i=0;i<surfaces.length;i++){
            ArrayList<String> surface = Parser.splitString(surfaces[i],2);
            ArrayList<String> s=new ArrayList<String>(surface);
            surface=new ArrayList<>();
            int result = br.calculatePointsString(s);
            assertEquals(results[i],result);
        }
    }

    /**
     * Checks that the deck throws an exception when trying to draw cards
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
     * Creates decks starting from a list of cards and
     * checks that cards are drawn in that order
     * and catches an exception after the cards finish
     * @throws NoCardInDeckException
     */
    @Test
    public void checkDrawDeck() throws NoCardInDeckException, InvalidCardDescriptionException {
        String cards[]={"6B6G5S6C2S3B7G7C5GKBKC2C3C6S7S4B5CJS",
                        "KC7S2G6CJS4G5G3G4S3S3CKG6S3BKS1GHBJC",
                        "2C6GKC6CKB5CKSJB6S7C7G3S5G5B2B1B5SJG4CJS",
                        "6CKG2B3GJCHG3S1C1GJG3CHS2S7B5B7GKS4S4B3B1SJB6S2G5G",
                        "7SKG4G3GHCHS2B7CKC2C2S7BJB5BHBJG6C6B4B3B1GKB1BJSHG6G4S3C5C6S2G4C",
                        ""};
        Deck deck;
        Card c;
        for(String deckCards:cards) {
            deck = new Deck(deckCards);
            //turn the string representing the cards into an ArrayList of Strings
            ArrayList<String> cardList=Parser.splitString(deckCards,2);
            for (int i = 0; i < cardList.size(); i++) {
                c=deck.drawCard();
                assertEquals(cardList.get(i),c.toString());
            }
            try {
                deck.drawCard();
                //supposed to throw exception
                fail("Drawing card didn't fail");
            } catch (NoCardInDeckException e) {
                //Supposed to happen
            }
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
     * Method should return :
     * the index of the winner player, 0 or 1, if there is a winner
     * -1 if the game finished with a draw
     * -2 if the game is not finished yet.
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
     * Tests that the value enumeration takes the
     * right values
     *
     */
    @Test
    public void checkValue()  {
        String values[]={"1","2","3","4","5","6","7","J","H","K"};
        Value valuesReturned[]=Value.values();
        assertEquals(values.length,valuesReturned.length);
        for(int i=0;i<values.length;i++)
            assertEquals(values[i],valuesReturned[i].toString());

    }
    /**
     * Tests that the suit enumeration takes the
     * right values
     *
     */
    @Test
    public void checkSuit()  {
        String suits[]={"B","S","C","G"};
        Suit suitsReturned[]=Suit.values();
        assertEquals(suits.length,suitsReturned.length);
        for(int i=0;i<suits.length;i++)
            assertEquals(suits[i],suitsReturned[i].toString());

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
         * we know these cards are valid because it's been tested
         * in the previous method that {@link Value#values()}  and
         * {@link Suit#values()}  contain the right symbols
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

    @Test
    public void proffTest() throws InvalidGameStateException, InvalidCardDescriptionException {
        assertEquals(new Briscola().moveTest("1B5G2G3S3G1SJS6C7SJGJB4S5B2BKSHB4BKBKC2C1G1C7B5CJC7C6GHC4G6B6SHG3B.4C.1BHS.KG5S2S.3C7G.","2"), "0B3S3G1SJS6C7SJGJB4S5B2BKSHB4BKBKC2C1G1C7B5CJC7C6GHC4G6B6SHG3B..1BHS5G.KG5S2G.3C7G4C2S.");
        assertEquals(new Briscola().moveTest("1B3S3G1SJS6C7SJGJB4S5B2BKSHB4BKBKC2C1G1C7B5CJC7C6GHC4G6B6SHG3B.5G.1BHS.KG5S2G.3C7G4C2S.","1"), "0B1SJS6C7SJGJB4S5B2BKSHB4BKBKC2C1G1C7B5CJC7C6GHC4G6B6SHG3B..1BHS3S.KG2G3G.3C7G4C2S5G5S.");
        assertEquals(new Briscola().moveTest("1B1SJS6C7SJGJB4S5B2BKSHB4BKBKC2C1G1C7B5CJC7C6GHC4G6B6SHG3B.1B.HS3S.KG2G3G.3C7G4C2S5G5S.","0"), "0B6C7SJGJB4S5B2BKSHB4BKBKC2C1G1C7B5CJC7C6GHC4G6B6SHG3B..HS3S1S.2G3GJS.3C7G4C2S5G5S1BKG.");
        assertEquals(new Briscola().moveTest("0B1B2C4SKSHG5CKGHB3C2G6GHC7G3G3B6C4B1CKC6BJG2S1S3S4G5B.7B.JB5SHS.7C5G.2BJSJC4C7S1G.6SKB","0"), "0B4SKSHG5CKGHB3C2G6GHC7G3G3B6C4B1CKC6BJG2S1S3S4G5B..5SHS1B.7C5G2C.2BJSJC4C7S1G7BJB.6SKB");
        assertEquals(new Briscola().moveTest("0B1BHB7C1CJB3CJG6B.5C.KC5G6S.3B7G.1G4B7B4GJC3GHC6C4CKBKSHS2CJS.2S1S5B2B2G7S6G5SHGKG4S3S","1"), "1B7C1CJB3CJG6B..KC6SHB.3B7G1B.1G4B7B4GJC3GHC6C4CKBKSHS2CJS.2S1S5B2B2G7S6G5SHGKG4S3S5C5G");
        assertEquals(new Briscola().moveTest("0B1BHCJC5G2CJB.KS.2S4G3S.1C7C.7S6GHSJG3B7BKB4C6B5B5S3CKGHB5C4S.6SJS7G2B1G6C4BHG3G2G1SKC","0"), "1BJC5G2CJB..4G3SHC.1C7C1B.7S6GHSJG3B7BKB4C6B5B5S3CKGHB5C4S.6SJS7G2B1G6C4BHG3G2G1SKCKS2S");
        assertEquals(new Briscola().moveTest("0B1C2B2C3G6C1B.JS.HS3C2S.6GHC.JB4B5G2GKBHB6SKC1S4G4CJG7G7C4SKG5C5B.5S3B1GHG7B3SJC6BKS7S","0"), "0B2C3G6C1B..3C2S1C.6GHC2B.JB4B5G2GKBHB6SKC1S4G4CJG7G7C4SKG5C5BJSHS.5S3B1GHG7B3SJC6BKS7S");

    }
    /**
     * method used in the tests to generate a new random state
     * @return string representing a state of a new game
     */
    private String generateState(){
        Briscola b=new Briscola();
        String state=b.toString();
        return state;
    }

    /**
     * Generates a random sequence of moves of a random length
     * from 10 to 20
     * Used in the tests to generate random states of the game
     * @return a random sequence of moves of random length
     * P.S. random refers to pseudo-random of course
     */
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
