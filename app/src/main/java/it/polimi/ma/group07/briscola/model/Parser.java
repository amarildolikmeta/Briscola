package it.polimi.ma.group07.briscola.model;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.model.Exceptions.InvalidGameStateException;

/**
 * Helper class to parse strings representing the state of a game
 */

public class Parser {
    /**
     * Constants used to validate length of the string
     */
    private  static int DECK_SIZE=80;
    private static int CURRENT_PLAYER=1;
    private static int TRUMP_SUIT=1;
    private static int DECK_SURFACE_DOTS=2;
    private static int DOTS_PER_PLAYER=2;

    /**
     * Splits a string in an array of string of length size
     * used to split spring representations of diferent fields of the game
     * @param text string to be split
     * @param size length of chunks
     * @return array of chunks of length size
     */
    public static ArrayList<String> splitString(String text,int size) {

        ArrayList<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    /**
     * Parses the configuration string of the game
     * @param desc configuration string
     * @param numPlayers number of player the game represents
     * @return Object containing the parsed information
     * @throws InvalidGameStateException {@link InvalidGameStateException}
     */
    public static State parseState(String desc,int numPlayers) throws InvalidGameStateException {
        /**State variables that will be extracted from the description string
         */
        int currentPlayer;
        String trump;
        String deck;
        String surface;
        String[] hands;
        String[] piles;

        try {
            /**Check that the description has the correct length
             *0 cards*2 characters +
             *1 character for the current player +
             *1 character for the trump suit +
             *2 dots after the surface cards and deck +
             *2 dots for each players hand and pile of cards -
             *the last players pile needs no separation
             */
            if(desc.length()!=DECK_SIZE+CURRENT_PLAYER+TRUMP_SUIT+DECK_SURFACE_DOTS+DOTS_PER_PLAYER*numPlayers-1)
                throw new InvalidGameStateException("Invalid String Length");
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
            /**
             * We check that each value and each suit is repeated the right amount of time
             * this alone doesn't guarantee that each card is repeated once but
             * since later only valid cards will be created the condition is enough to guarantee
             * that each card is present only once in the game
             */
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
            /**split the string saving also empty strings between dots
             * The second parameter of split saves also the empty parts
             * like empty surface or empty piles
             */
            String[] tokens=desc.split("\\.",-1);
            /**the state should have :
             *The deck and the surface+
             *2 sections for each players : Hand and Pile
             */
            if(tokens.length!=DECK_SURFACE_DOTS+DOTS_PER_PLAYER*numPlayers)
                throw new InvalidGameStateException("Configuration Parts Are Missing");
            //initialize the counter of characters reserved for cards
            int count=0;
            //extract deck
            deck=tokens[0];

            //Check that last card has trump suit
            if(deck.length()>0)
                if((!trump.equals(deck.substring(deck.length()-1))))
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
                    throw new InvalidGameStateException("Invalid Hand State For Player "+(i+1));
                count+=hands[i].length();
            }
            //extract player piles
            for(int i=0;i<numPlayers;i++)
            {
                piles[i]=tokens[2+numPlayers+i];
                if(piles[i].length()%2!=0)
                    throw new InvalidGameStateException("Invalid Player Pile Encoding For Player "+(i+1));
                count+=piles[i].length();
            }
            /**
             * In the end 80 characters representing cards
             * must be in the state
             */
            if(count!=80)
                throw new InvalidGameStateException("Incorrect Card Encoding:Cards Are Missing");

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
        catch (NullPointerException e)
        {
            throw new InvalidGameStateException("No state specified");
        }

    }
}
