package it.polimi.ma.group07.briscola;

import android.util.Log;

import org.junit.Test;

import java.net.URLEncoder;
import java.util.Scanner;

import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.GameState;
import it.polimi.ma.group07.briscola.model.StateBundle;
import it.polimi.ma.group07.briscola.model.helper.HttpRequest;

import static it.polimi.ma.group07.briscola.model.GameState.*;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    String state;
    String res;
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


    }
