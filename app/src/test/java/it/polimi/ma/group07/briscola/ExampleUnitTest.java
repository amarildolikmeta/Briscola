package it.polimi.ma.group07.briscola;

import org.junit.Test;

import java.util.Scanner;

import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.GameState;

import static it.polimi.ma.group07.briscola.model.GameState.*;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {

        String state="0C4G4SHC2G6S2SJS4B7B2BKG5G6G7C3C3S1G7GKS6B3BHB5C3GJG1C..4CJB2C.KC6CJC.KB1BHS5B.5S1SHG7S";
        String[] moves={"22122222202212222212220022110000"};
        String[] results={
                          "WINNER 1 70"
                         };
         Briscola b=new Briscola();
        for(int i=0;i<moves.length;i++) {
            String res=b.moveTest(state, moves[i]);
            System.out.println(res);
            assertEquals(res, results[i]);
        }

    }
   /* private void askMove(){
        System.out.print("Player"+b.getCurrentPlayer()+" move:");
        int index = sc.nextInt();
        GameState s=b.onPerformMove(index);
        if(s== WON)
        {
            System.out.println("Game finished with a winner");
        }
        else if(s==DRAW)
        {
            System.out.println("DRAW");
        }
        else
        {
            System.out.flush();
            System.out.println("Player0 hand:"+b.getPlayerHand(0)+"\t Player 0 Pile:"+b.getPlayerCardPile(0));
            System.out.println("Briscola:"+b.getBriscolaCard());
            System.out.println("Surface:"+b.getSurface());
            System.out.println("Player1 hand:"+b.getPlayerHand(1)+"\t Player 1 Pile:"+b.getPlayerCardPile(1));
            askMove();
        }

    }*/
}