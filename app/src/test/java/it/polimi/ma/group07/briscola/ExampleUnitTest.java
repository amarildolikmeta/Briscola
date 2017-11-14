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
    //@Ignore
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
     * @throws InvalidCardDescriptionException
     */

    @Test
    public void checkDeterminedCardWinner() throws InvalidCardDescriptionException {
        Brain br = new Brain();

        String[] surfaces={"4B5B","4S4B","5GHG","6SKB","3G1G"};
        String[] suits = {"B","S","C","G","G"};
        int[] results={1,0,1,0,1};

        for(int i=0;i<surfaces.length;i++){
            ArrayList<String> cardStrings = Parser.splitString(surfaces[i],2);
            ArrayList<Card> surface = new ArrayList<Card>();

            for(String s:cardStrings){
                surface.add(new Card(s));
            }

            br.setTrumpSuit(Suit.stringToSuit(suits[i]));
            int result = br.determineWinner(surface);

            assertEquals(results[i],result);
        }
    }

    /**
     * Check of the calculatePoints() method in the Brain class
     * that calculates total points of cards on the surfaceee
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
    @Test
    public void checkPlayer() throws IndexOutOfBoundsException, InvalidCardDescriptionException {
        Brain brain=new Brain();
        String[] hands={"4B5B","4S4B","5GHG3C","6SKBJG","3G1G","KCHC","3G3S","2S3B"};
        int[] indexes= {0,1,2,2,1,0,0,0};
        int[] outBoundIndexes={2,2,3,3,2,2,-1,-2};
        String[] cards={"4B","4B","3C","JG","1G","KC","3G","2S"};

        for(int i=0;i<hands.length;i++)
        {
            Player p=new Player(hands[i],"","player",brain);
            Card c =p.placeCardAtIndex(indexes[i]);
            assertEquals(cards[i],c.toString());
        }
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
