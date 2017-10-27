package it.polimi.ma.group07.briscola;

import android.util.Log;

import org.junit.Test;

import java.net.URLEncoder;
import java.util.Scanner;

import it.polimi.ma.group07.briscola.model.Briscola;
import it.polimi.ma.group07.briscola.model.GameState;
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
    public void addition_isCorrect() throws Exception {

        String state="0C4G4SHC2G6S2SJS4B7B2BKG5G6G7C3C3S1G7GKS6B3BHB5C3GJG1C..4CJB2C.KC6CJC.KB1BHS5B.5S1SHG7S";
        String[] moves={"22122222202212222212220022110000"};
        String[] results={
                          "WINNER 1 70"
                         };
        //state="0C..7S5CJG.KB3G1C.5B1B6CJB6G7C7G1G6BKS3BHB.4CKC1S5SHGHS2CJC4S4GHC2G6S2SJS4B7B2BKG5G3S3C";
        Briscola b=new Briscola(state);

    }
}