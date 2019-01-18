package de.nomagic.server.matches;

import de.nomagic.commands.MakeMoveCommand;
import de.nomagic.commands.RequestMatchDataCommand;
import de.nomagic.commands.Result;
import de.nomagic.server.user.User;

public class MampferMatch extends BaseMatch
{
    // Board Positions :
    // not on board = 0
    // 1 | 2 | 3
    //---+---+---
    // 4 | 5 | 6
    //---+---+---
    // 7 | 8 | 9
    public final static int NOT_ON_BOARD = 0;
    // the board will be filled with this Play Figures:
    public final static char BOARD_FIELD_EMPTY      = ' ';
    public final static char BOARD_FIELD_PLAYER1    = 'X';
    public final static char BOARD_FIELD_PLAYER1_m  = 'x';
    public final static char BOARD_FIELD_PLAYER1_s  = '*';
    public final static char BOARD_FIELD_PLAYER2    = 'O';
    public final static char BOARD_FIELD_PLAYER2_m  = 'o';
    public final static char BOARD_FIELD_PLAYER2_s  = '.';
    public final static int FIGURE_PLAYER1_BIG1     = 0;
    public final static int FIGURE_PLAYER1_BIG2     = 1;
    public final static int FIGURE_PLAYER2_BIG1     = 2;
    public final static int FIGURE_PLAYER2_BIG2     = 3;
    public final static int FIGURE_PLAYER1_MEDIUM1  = 4;
    public final static int FIGURE_PLAYER1_MEDIUM2  = 5;
    public final static int FIGURE_PLAYER2_MEDIUM1  = 6;
    public final static int FIGURE_PLAYER2_MEDIUM2  = 7;
    public final static int FIGURE_PLAYER1_SMALL1   = 8;
    public final static int FIGURE_PLAYER1_SMALL2   = 9;
    public final static int FIGURE_PLAYER2_SMALL1   = 10;
    public final static int FIGURE_PLAYER2_SMALL2   = 11;
    public final static int NUM_FIGURES             = 12;

    private static final long serialVersionUID = 1L;


    private int[] figuresPositions = new int[NUM_FIGURES];
    private StringBuffer gamelog = new StringBuffer();
    private int thisMove = 1;
    private String winner = "none";

    public MampferMatch()
    {
        super();
    }

    protected MampferMatch(String matchName, User user)
    {
        super(matchName, user.getName(), 2);
        for(int x = 0; x < NUM_FIGURES; x++)
        {
            figuresPositions[x] = NOT_ON_BOARD;
        }
    }

    @Override
    public Match getInstanceFor(String matchName, User user)
    {
        return new MampferMatch(matchName, user);
    }

    @Override
    public String getGameType()
    {
        return "Mampfer";
    }

    @Override
    public synchronized Object[] getData(RequestMatchDataCommand cmd, User user)
    {
        String Board;
        char[] board = new char[10];
        for(int x = 0; x < 9; x++)
        {
            board[x] = BOARD_FIELD_EMPTY;
        }
        // sequence is important !
        board[figuresPositions[FIGURE_PLAYER2_SMALL2]] = BOARD_FIELD_PLAYER2_s;
        board[figuresPositions[FIGURE_PLAYER2_SMALL1]] = BOARD_FIELD_PLAYER2_s;
        board[figuresPositions[FIGURE_PLAYER1_SMALL2]] = BOARD_FIELD_PLAYER1_s;
        board[figuresPositions[FIGURE_PLAYER1_SMALL1]] = BOARD_FIELD_PLAYER1_s;

        board[figuresPositions[FIGURE_PLAYER2_MEDIUM2]] = BOARD_FIELD_PLAYER2_m;
        board[figuresPositions[FIGURE_PLAYER2_MEDIUM1]] = BOARD_FIELD_PLAYER2_m;
        board[figuresPositions[FIGURE_PLAYER1_MEDIUM2]] = BOARD_FIELD_PLAYER1_m;
        board[figuresPositions[FIGURE_PLAYER1_MEDIUM1]] = BOARD_FIELD_PLAYER1_m;

        board[figuresPositions[FIGURE_PLAYER2_BIG2]] = BOARD_FIELD_PLAYER2;
        board[figuresPositions[FIGURE_PLAYER2_BIG1]] = BOARD_FIELD_PLAYER2;
        board[figuresPositions[FIGURE_PLAYER1_BIG2]] = BOARD_FIELD_PLAYER1;
        board[figuresPositions[FIGURE_PLAYER1_BIG1]] = BOARD_FIELD_PLAYER1;

        Board =
        "+--+---+--+"+ "\n" +
        "|" + board[1] + " | " + board[2] +" | " + board[3] + "|\n" +
        "+--+---+--+"+ "\n" +
        "|" + board[4] + " | " + board[5] +" | " + board[6] +  "|\n" +
        "+--+---+--+"+ "\n" +
        "|" + board[7] + " | " + board[8] +" | " + board[9] + "|\n" +
        "+--+---+--+";

        return new Object[] {Result.DATA,
                "Match Name : " + getName(),
                "created by : " + getOwner(),
                "Player 1 (" + BOARD_FIELD_PLAYER1 + " " + BOARD_FIELD_PLAYER1_m + " " + BOARD_FIELD_PLAYER1_s + "): " + getPlayer(0),
                "Player 2 (" + BOARD_FIELD_PLAYER2 + " " + BOARD_FIELD_PLAYER2_m + " " + BOARD_FIELD_PLAYER2_s + "): " + getPlayer(1),
                "is ongoing : " + isGameOngoing(),
                "number of moves executed : " + (thisMove -1),
                "won by : " + winner,
                "[Board]",
                Board,
                "[History]",
                gamelog.toString()
        };
    }

    private boolean isEaten(int figure)
    {
        if(figure < NUM_FIGURES)
        {
            int fieldPosition = figuresPositions[figure];
            if(NOT_ON_BOARD == fieldPosition)
            {
                // not yet eaten.
                return false;
            }
            // scan all possible eaters if they are on the same position
            for(int i = 0; i < figure; i++)
            {
                if(figuresPositions[i] == fieldPosition)
                {
                    // two figures on the same position
                    // -> figure has been eaten :-(
                    return true;
                }
            }
            // not yet eaten.
            return false;
        }
        else
        {
            // invalid figure
            return true;
        }
    }

    private boolean canEat(int eater, int food)
    {
        switch(eater)
        {
        case FIGURE_PLAYER1_BIG1:
            switch(food)
            {
            case FIGURE_PLAYER2_MEDIUM1: return true;
            case FIGURE_PLAYER2_MEDIUM2: return true;
            case FIGURE_PLAYER2_SMALL1: return true;
            case FIGURE_PLAYER2_SMALL2: return true;
            default: return false; // we do not eat unknown stuff
            }

        case FIGURE_PLAYER1_BIG2:
            switch(food)
            {
            case FIGURE_PLAYER2_MEDIUM1: return true;
            case FIGURE_PLAYER2_MEDIUM2: return true;
            case FIGURE_PLAYER2_SMALL1: return true;
            case FIGURE_PLAYER2_SMALL2: return true;
            default: return false; // we do not eat unknown stuff
            }

        case FIGURE_PLAYER2_BIG1:
            switch(food)
            {
            case FIGURE_PLAYER1_MEDIUM1: return true;
            case FIGURE_PLAYER1_MEDIUM2: return true;
            case FIGURE_PLAYER1_SMALL1: return true;
            case FIGURE_PLAYER1_SMALL2: return true;
            default: return false; // we do not eat unknown stuff
            }

        case FIGURE_PLAYER2_BIG2:
            switch(food)
            {
            case FIGURE_PLAYER1_MEDIUM1: return true;
            case FIGURE_PLAYER1_MEDIUM2: return true;
            case FIGURE_PLAYER1_SMALL1: return true;
            case FIGURE_PLAYER1_SMALL2: return true;
            default: return false; // we do not eat unknown stuff
            }

        case FIGURE_PLAYER1_MEDIUM1:
            switch(food)
            {
            case FIGURE_PLAYER2_SMALL1: return true;
            case FIGURE_PLAYER2_SMALL2: return true;
            default: return false; // we do not eat unknown stuff
            }

        case FIGURE_PLAYER1_MEDIUM2:
            switch(food)
            {
            case FIGURE_PLAYER2_SMALL1: return true;
            case FIGURE_PLAYER2_SMALL2: return true;
            default: return false; // we do not eat unknown stuff
            }

        case FIGURE_PLAYER2_MEDIUM1:
            switch(food)
            {
            case FIGURE_PLAYER1_SMALL1: return true;
            case FIGURE_PLAYER1_SMALL2: return true;
            default: return false; // we do not eat unknown stuff
            }

        case FIGURE_PLAYER2_MEDIUM2:
            switch(food)
            {
            case FIGURE_PLAYER1_SMALL1: return true;
            case FIGURE_PLAYER1_SMALL2: return true;
            default: return false; // we do not eat unknown stuff
            }

        case FIGURE_PLAYER1_SMALL1:
        case FIGURE_PLAYER1_SMALL2:
        case FIGURE_PLAYER2_SMALL1:
        case FIGURE_PLAYER2_SMALL2:
            // can eat nothing
            return false; // we do not eat unknown stuff

        default: return false; // we do not eat unknown stuff
        }
    }

    private int figureBelongsToPlayer(int figure)
    {
        switch(figure)
        {
        case FIGURE_PLAYER1_BIG1: return 1;
        case FIGURE_PLAYER1_BIG2: return 1;
        case FIGURE_PLAYER2_BIG1: return 2;
        case FIGURE_PLAYER2_BIG2: return 2;
        case FIGURE_PLAYER1_MEDIUM1: return 1;
        case FIGURE_PLAYER1_MEDIUM2: return 1;
        case FIGURE_PLAYER2_MEDIUM1: return 2;
        case FIGURE_PLAYER2_MEDIUM2: return 2;
        case FIGURE_PLAYER1_SMALL1: return 1;
        case FIGURE_PLAYER1_SMALL2: return 1;
        case FIGURE_PLAYER2_SMALL1: return 2;
        case FIGURE_PLAYER2_SMALL2: return 2;
        default: return 0;
        }
    }

    @Override
    public synchronized Object[] makeMove(MakeMoveCommand cmd, User user)
    {
        if(false == isGameOngoing())
        {
            return new Object[] {Result.FAILED, "Game is finished! No more moves possible !"};
        }
        String playerName = user.getName();
        if(true == playerName.equals(getPlayer(getActivePlayerIdx())))
        {
            // ok
        }
        else
        {
            return new Object[] {Result.FAILED, "It is not your turn"};
        }
        int x = cmd.getParameterAsInt(0);
        int figure = cmd.getParameterAsInt(1);

        if((1 > x) || (9 < x))
        {
            return new Object[] {Result.FAILED, "Invalid field (" + x + ") (valid = 1..9)"};
        }

        if(0 == getActivePlayerIdx())
        {
            // player 1 may move only these figures:
            if( 1 == figureBelongsToPlayer(figure))
            {
                // OK
            }
            else
            {
                return new Object[] {Result.FAILED, "That is not your figure"};
            }
        }
        else
        {
            // player2 may only move these figures:
            if( 2 == figureBelongsToPlayer(figure))
            {
                // OK
            }
            else
            {
                return new Object[] {Result.FAILED, "That is not your figure"};
            }
        }

        if(true == isEaten(figure))
        {
            return new Object[] {Result.FAILED, "You can only move figures that are not eaten"};
        }

        for(int i = 0; i < NUM_FIGURES; i++)
        {
            if(figuresPositions[i] == x)
            {
                // position already taken
                if(true == canEat(figure, i))
                {
                    // OK
                }
                else
                {
                    return new Object[] {Result.FAILED, "That field is already taken and you can not eat the figure!"};
                }
            }
        }

        // valid move
        figuresPositions[figure] = x;
        checkIfNowWon();
        finishedMove();

        return new Object[] {Result.OK};
    }

    private synchronized void checkIfNowWon()
    {
        int BOARD_FIELD_PLAYER_1 = 1;
        int BOARD_FIELD_PLAYER_2 = 2;

        int[] board = new int[10];

        // clear board
        for(int x = 0; x < 10; x++)
                board[x] = 0;

        // add all figures
        for(int i = 0; i < NUM_FIGURES; i++)
        {
            int f = NUM_FIGURES - (i + 1);
            board[figuresPositions[f]] = figureBelongsToPlayer(f);
        }

        // Board Positions :
        // not on board = 0
        // 1 | 2 | 3
        //---+---+---
        // 4 | 5 | 6
        //---+---+---
        // 7 | 8 | 9

        if(
          (   (board[1] == BOARD_FIELD_PLAYER_1)
           && (board[5] == BOARD_FIELD_PLAYER_1)
           && (board[9] == BOARD_FIELD_PLAYER_1))
           ||
          (   (board[7] == BOARD_FIELD_PLAYER_1)
           && (board[5] == BOARD_FIELD_PLAYER_1)
           && (board[3] == BOARD_FIELD_PLAYER_1))
                )
        {
            // Player 0 won diagonally
            finishedGame();
            winner = getPlayer(0);
        }

        if(
          (   (board[1] == BOARD_FIELD_PLAYER_2)
           && (board[5] == BOARD_FIELD_PLAYER_2)
           && (board[9] == BOARD_FIELD_PLAYER_2))
           ||
          (   (board[7] == BOARD_FIELD_PLAYER_2)
           && (board[5] == BOARD_FIELD_PLAYER_2)
           && (board[3] == BOARD_FIELD_PLAYER_2))
                )
        {
            // Player 1 won diagonally
            finishedGame();
            winner = getPlayer(1);
        }

        for(int i = 0; i < 3; i++)
        {
            if(   (board[1 + i] == BOARD_FIELD_PLAYER_1)
               && (board[4 + i] == BOARD_FIELD_PLAYER_1)
               && (board[7 + i] == BOARD_FIELD_PLAYER_1))
            {
                // Player 0 won vertically
                finishedGame();
                winner = getPlayer(0);
            }
            if(   (board[1 + i] == BOARD_FIELD_PLAYER_2)
               && (board[4 + i] == BOARD_FIELD_PLAYER_2)
               && (board[7 + 1] == BOARD_FIELD_PLAYER_2))
            {
                // Player 1 won vertically
                finishedGame();
                winner = getPlayer(1);
            }

            if(   (board[1 + (i*3)] == BOARD_FIELD_PLAYER_1)
               && (board[2 + (i*3)] == BOARD_FIELD_PLAYER_1)
               && (board[3 + (i*3)] == BOARD_FIELD_PLAYER_1))
            {
                // Player 0 won horizontally
                finishedGame();
                winner = getPlayer(0);
            }

            if(   (board[1 + (i*3)] == BOARD_FIELD_PLAYER_2)
               && (board[2 + (i*3)] == BOARD_FIELD_PLAYER_2)
               && (board[3 + (i*3)] == BOARD_FIELD_PLAYER_2))
            {
                // Player 1 won horizontally
                finishedGame();
                winner = getPlayer(1);
            }
        }
    }

}
