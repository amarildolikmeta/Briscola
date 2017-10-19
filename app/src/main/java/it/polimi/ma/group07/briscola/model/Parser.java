package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;

/**
 * Created by amari on 19-Oct-17.
 */

public class Parser {
    public static ArrayList<String> splitString(String text,int size) {

        ArrayList<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    public static State parseState(String desc,int numPlayers) throws InvalidGameStateException {
        //State cariables that will be extracted from the description string
        int currentPlayer;
        String trump;
        String deck;
        String surface;
        String[] hands;
        String[] piles;

        //Check that the description has the correct format
        //40 cards*2 characters +
        //1 character for the current player +
        //1 character for the trump suit +
        //2 dots after the surface cards and deck +
        //2 dots for each players hand and pile of cards -
        // the last players pile needs no separation
        if(desc.length()!=80+1+1+2+2*numPlayers-1)
            throw new InvalidGameStateException("Invalid String Length");
        try {
            hands=new String[numPlayers];
            piles=new String[numPlayers];

            //extract current player
            currentPlayer = Integer.parseInt(desc.substring(0, 1));
            if(currentPlayer<0 || currentPlayer>=numPlayers)
                throw new InvalidGameStateException("Invalid Current Player Index");
            desc=desc.substring(1);

            //extract trump suit
            trump=desc.substring(0,1);
            desc=desc.substring(1);

            // Check that the cards in the state are valid concerning the suits
            for(Suit s:Suit.values())
            {
                int lastIndex = 0;
                int count = 0;
                while(lastIndex != -1){

                    lastIndex = desc.indexOf(s.toString(),lastIndex);

                    if(lastIndex != -1){
                        count ++;
                        lastIndex += s.toString().length();
                    }
                }
                if(count!=10)
                    throw new InvalidGameStateException("Impossible Card State:Suit");
            }

            // Check that the cards in the state are valid concerning the Values
            for(Value s:Value.values())
            {
                int lastIndex = 0;
                int count = 0;
                while(lastIndex != -1){

                    lastIndex = desc.indexOf(s.toString(),lastIndex);

                    if(lastIndex != -1){
                        count ++;
                        lastIndex += s.toString().length();
                    }
                }
                if(count!=4)
                    throw new InvalidGameStateException("Impossible Card State :Value");
            }
            //split the string saving also empty strings between dots
            String[] tokens=desc.split("\\.",-1);
            //initialize the counter of characters reserved for cards
            int count=0;
            //extract deck
            deck=tokens[0];

            //Check that last card has trump suit
            if(!trump.equals(deck.substring(deck.length()-1)))
                throw new InvalidGameStateException("Invalid Trump Suit");

            if(deck.length()%2!=0)
                throw new InvalidGameStateException("Invalid Deck Encoding");
            count+=deck.length();
            //extract surface
            surface=tokens[1];
            if(surface.length()%2!=0)
                throw new InvalidGameStateException("Invalid Surface Encoding");
            count+=surface.length();
            if(surface.length()>numPlayers*2)
                throw new InvalidGameStateException("Invalid Number of Cards on the surface");
            //extract player hands
            for(int i=0;i<numPlayers;i++)
            {
                hands[i]=tokens[2+i];
                if(hands[i].length()>6 || hands[i].length()%2!=0)
                    throw new InvalidGameStateException("Invalid Hand State");
                count+=hands[i].length();
            }
            for(int i=0;i<numPlayers;i++)
            {
                piles[i]=tokens[2+numPlayers+i];
                if(piles[i].length()%2!=0)
                    throw new InvalidGameStateException("Invalid Player Pile State");
                count+=piles[i].length();
            }
            
            if(count!=80)
                throw new InvalidGameStateException("Incorrect Card Encoding");

            return new State(currentPlayer,trump,deck,surface,hands,piles);
        }
        catch (NumberFormatException e)
        {
            throw new InvalidGameStateException("Invalid String Format");
        }
        catch (InvalidGameStateException e)
        {
            throw e;
        }

    }
}
