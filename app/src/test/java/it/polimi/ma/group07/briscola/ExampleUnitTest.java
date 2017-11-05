package it.polimi.ma.group07.briscola;

import org.junit.Ignore;
import org.junit.Test;

import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;
import it.polimi.ma.group07.briscola.model.StateBundle;

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
    //@Ignore
    @Test
    public void state_isCorrect() throws Exception {
        Briscola b=new Briscola();
        StateBundle state=b.getGameState();
        System.out.println("Start:Hand 1:"+state.hand1+"; Hand 2:"+state.hand2+";Surface:"+state.surface+";Briscola:"+state.briscola);
        int[] moves={0,0,0,0};
        for(int i=0;i<moves.length;i++){
            b.onPerformMove(moves[i]);
            state=b.getGameState();
            System.out.println("Move "+(i+1)+":Hand 1:"+state.hand1+"; Hand 2:"+state.hand2+";Surface:"+state.surface);
            if(b.isRoundFinished())
            {
                b.finishRound();
                state=b.getGameState();
                System.out.println("Move "+(i+1)+":Hand 1:"+state.hand1+"; Hand 2:"+state.hand2+";Surface:"+state.surface);
                b.dealCard();
                state=b.getGameState();
                System.out.println("Move "+(i+1)+":Hand 1:"+state.hand1+"; Hand 2:"+state.hand2+";Surface:"+state.surface);
                b.dealCard();
                state=b.getGameState();
                System.out.println("Move "+(i+1)+":Hand 1:"+state.hand1+"; Hand 2:"+state.hand2+";Surface:"+state.surface);
            }
        }
    }
    //@Ignore
    @Test
    public void MONKEY_TEST() throws Exception {
        Briscola b=new Briscola();
        String moves="00";
        state="0S..HS.5C.7BHB1GJGKB7C3G6C2BKC2GJB5B2C4B6S3S1C4G6B7S4C7G4S.5SKSHG2S6G5GJCHCKG1B3C3B1SJS";
        String result="DRAW";
        String res=b.moveTest(state, moves);
        System.out.println(res);
        assertEquals(result, res);
    }
    //@Ignore
    @Test
    public void invalid_configurations_test() throws Exception {
        Briscola b;
        String[] states={"1B5S4G2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B.JC.KG2B6S.1CKS3G..",
                "1B6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..KG2B4G.KS3G5S..JC1C",
                "0B6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B.3G.KG2B4G.KS5S..JC1C",
                "0B5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..KG4G6S.KS5S2C.3G2B.JC1C",
                "1B5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B.KG.4G6S.KS5S2C.3G2B.JC1C"};
        for(int i=0;i<states.length;i++) {
            try {
                b=new Briscola(states[i]);
                fail( "Failed in test number "+(i+1) );
            } catch (InvalidGameStateException expectedException) {
            }
        }
    }
    @Test
    public void randomTests() throws Exception {
        int count=0;
        for(int i=0;i<100000;i++){
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
                System.out.println("State Number "+(i+1)+" considered valid!");
                System.out.println(invalidState);
                count++;

            }
            catch (InvalidGameStateException e){

            }
        }
        System.out.println("Total valid States:"+count);
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
