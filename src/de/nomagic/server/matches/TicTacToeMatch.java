package de.nomagic.server.matches;

import de.nomagic.commands.MakeMoveCommand;
import de.nomagic.commands.RequestMatchDataCommand;
import de.nomagic.commands.Result;
import de.nomagic.server.user.User;

public class TicTacToeMatch extends BaseMatch
{
    public final static char BOARD_FIELD_EMPTY = ' ';
    public final static char BOARD_FIELD_PLAYER_1 = 'X';
    public final static char BOARD_FIELD_PLAYER_2 = 'O';

    private static final long serialVersionUID = 1L;
    private char[][] board = new char[3][3];
    private int[][] history = new int[3][3];
    private int thisMove = 1;
    private String winner = "none";
    private int numEmptyFields = 9;

    public TicTacToeMatch()
    {
        super();
    }

    public Match getInstanceFor(String matchName, User user)
    {
        return new TicTacToeMatch(matchName, user);
    }

    public String getGameType()
    {
        return "Tic-Tac-Toe";
    }

    protected TicTacToeMatch(String matchName, User user)
    {
        super(matchName, user.getName(), 2);
        for(int x = 0; x < 3; x++)
        {
            for(int y = 0; y < 3; y++)
            {
                board[x][y] = BOARD_FIELD_EMPTY;
                history[x][y] = 0;
            }
        }
    }

    @Override
    public synchronized Object[] getData(RequestMatchDataCommand cmd, User user)
    {
        return new Object[] {Result.DATA,
                "Match Name : " + getName(),
                "created by : " + getOwner(),
                "Player 1 (" + BOARD_FIELD_PLAYER_1 + "): " + getPlayer(0),
                "Player 2 (" + BOARD_FIELD_PLAYER_2 + "): " + getPlayer(1),
                "is ongoing : " + isGameOngoing(),
                "number of moves executed : " + (thisMove -1),
                "won by : " + winner,
                "[Board]",
                "+--+---+--+"+ "\n" +
                "|" + board[0][0] + " | " + board[1][0] +" | " + board[2][0] + "|\n" +
                "+--+---+--+"+ "\n" +
                "|" + board[0][1] + " | " + board[1][1] +" | " + board[2][1] +  "|\n" +
                "+--+---+--+"+ "\n" +
                "|" + board[0][2] + " | " + board[1][2] +" | " + board[2][2] + "|\n" +
                "+--+---+--+",
                "[History]",
                "+--+---+--+"+ "\n" +
                "|" + history[0][0] + " | " + history[1][0] +" | " + history[2][0] +"|\n" +
                "+--+---+--+"+ "\n" +
                "|" + history[0][1] + " | " + history[1][1] +" | " + history[2][1] + "|\n" +
                "+--+---+--+"+ "\n" +
                "|" + history[0][2] + " | " + history[1][2] +" | " + history[2][2] + "|\n" +
                "+--+---+--+"
        };
    }

    @Override
    public synchronized Object[] makeMove(MakeMoveCommand cmd, User user)
    {
        if(false == isGameOngoing())
        {
            return new Object[] {Result.FAILED, "Game is finished! No ore moves possible !"};
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
        int y = cmd.getParameterAsInt(1);
        if((0 > x) || (0 > y))
        {
            return new Object[] {Result.FAILED, "Invalid move (" + x + "," + y + ")"};
        }

        char field = board[x][y];
        if(BOARD_FIELD_EMPTY == field)
        {
            // valid move
            if(0 == getActivePlayerIdx())
            {
                setField(x, y, BOARD_FIELD_PLAYER_1);
            }
            else
            {
                setField(x, y, BOARD_FIELD_PLAYER_2);
            }
            checkIfNowWon();
            finishedMove();

            return new Object[] {Result.OK};
        }
        else
        {
            return new Object[] {Result.FAILED, "Invalid move (" + x + "," + y + ")"};
        }
    }

    private void setField(int x, int y, char state)
    {
        if(state != BOARD_FIELD_EMPTY)
        {
            if(board[x][y] == BOARD_FIELD_EMPTY)
            {
                numEmptyFields--;
            }
        }
        else
        {
            if(board[x][y] != BOARD_FIELD_EMPTY)
            {
                numEmptyFields++;
            }
        }
        if(0 == numEmptyFields)
        {
            finishedGame();
        }
        board[x][y] = state;
        history[x][y] = thisMove;
        thisMove ++;
    }

    public synchronized void checkIfNowWon()
    {
        if(
          (   (board[0][0] == BOARD_FIELD_PLAYER_1)
           && (board[1][1] == BOARD_FIELD_PLAYER_1)
           && (board[2][2] == BOARD_FIELD_PLAYER_1))
           ||
          (   (board[0][2] == BOARD_FIELD_PLAYER_1)
           && (board[1][1] == BOARD_FIELD_PLAYER_1)
           && (board[2][0] == BOARD_FIELD_PLAYER_1))
                )
        {
            // Player 0 won diagonally
            finishedGame();
            winner = getPlayer(0);
        }

        if(
          (   (board[0][0] == BOARD_FIELD_PLAYER_2)
           && (board[1][1] == BOARD_FIELD_PLAYER_2)
           && (board[2][2] == BOARD_FIELD_PLAYER_2))
           ||
          (   (board[0][2] == BOARD_FIELD_PLAYER_2)
           && (board[1][1] == BOARD_FIELD_PLAYER_2)
           && (board[2][0] == BOARD_FIELD_PLAYER_2))
                )
        {
            // Player 1 won diagonally
            finishedGame();
            winner = getPlayer(1);
        }

        for(int i = 0; i < 3; i++)
        {
            if(   (board[i][0] == BOARD_FIELD_PLAYER_1)
               && (board[i][1] == BOARD_FIELD_PLAYER_1)
               && (board[i][2] == BOARD_FIELD_PLAYER_1))
            {
                // Player 0 won vertically
                finishedGame();
                winner = getPlayer(0);
            }
            if(   (board[i][0] == BOARD_FIELD_PLAYER_2)
               && (board[i][1] == BOARD_FIELD_PLAYER_2)
               && (board[i][2] == BOARD_FIELD_PLAYER_2))
            {
                // Player 1 won vertically
                finishedGame();
                winner = getPlayer(1);
            }

            if(   (board[0][i] == BOARD_FIELD_PLAYER_1)
               && (board[1][i] == BOARD_FIELD_PLAYER_1)
               && (board[2][i] == BOARD_FIELD_PLAYER_1))
            {
                // Player 0 won horizontally
                finishedGame();
                winner = getPlayer(0);
            }

            if(   (board[0][i] == BOARD_FIELD_PLAYER_2)
               && (board[1][i] == BOARD_FIELD_PLAYER_2)
               && (board[2][i] == BOARD_FIELD_PLAYER_2))
            {
                // Player 1 won horizontally
                finishedGame();
                winner = getPlayer(1);
            }
        }
    }
}
