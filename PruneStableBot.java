import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;

public class PruneStableBot extends OthelloPlayer {

    public boolean taunted = false;
    public boolean conceded = false;
    public boolean admired = false;
    public boolean commenting = false;

    public int SEARCH_DEPTH = 4;
    public int SEARCH_MIN = 4;
    public int SEARCH_MAX = 6;

    public int turns = 0;
    public double moveNum = 0;
    public double time = 0;
    public double avg = 0;

    public PruneStableBot(Integer _color) {
        super(_color);
    }

    public int getNumStable(int color, OthelloBoard board){
        int[][] checked = new int[board.size][board.size];
        int depth = 0;
        int s = board.size;
        int stable = 0;

        while(depth <= s/2){
            int r1, r2, c1, c2;
            for(int i = 0; i<=depth; i++){
                r1 = i;
                c1 = depth - i;
                r2 = s - 1 - i;
                c2 = s - 1 - (depth-i);

                if(checked[r1][c1] == 0){
                    if(board.board[r1][c1] == color &&
                            ((r1 == 0 && c1 == 0)||
                            (r1 == 0 && (checked[r1][c1-1] == 1 || checked[r1][c1+1] == 1))||
                            (c1 == 0 && (checked[r1-1][c1] == 1 || checked[r1+1][c1] == 1))||
                            (c1 > 0 && r1 > 0 &&
                            ((checked[r1][c1-1] == 1 && checked[r1-1][c1] == 1)||
                            (checked[r1][c1-1] == 1 && checked[r1+1][c1] == 1)||
                            (checked[r1][c1+1] == 1 && checked[r1-1][c1] == 1)||
                            (checked[r1][c1+1] == 1 && checked[r1+1][c1] == 1)))
                    )){
                        checked[r1][c1] = 1;
                        stable ++;
                    }else{
                        checked[r1][c1] = -1;
                    }
                }
                if(checked[r2][c1] == 0){
                    if(board.board[r2][c1] == color &&
                            ((r2 ==  s-1&& c1 == 0)||
                            (r2 == s-1 && (checked[r2][c1-1] == 1 || checked[r2][c1+1] == 1))||
                            (c1 == 0 && (checked[r2-1][c1] == 1 || checked[r2+1][c1] == 1))||
                            (c1 > 0 && r2 < s-1 &&
                            ((checked[r2][c1-1] == 1 && checked[r2-1][c1] == 1)||
                            (checked[r2][c1-1] == 1 && checked[r2+1][c1] == 1)||
                            (checked[r2][c1+1] == 1 && checked[r2-1][c1] == 1)||
                            (checked[r2][c1+1] == 1 && checked[r2+1][c1] == 1)))
                    )){
                        checked[r2][c1] = 1;
                        stable ++;
                    }else{
                        checked[r2][c1] = -1;
                    }
                }
                if(checked[r1][c2] == 0){
                    if(board.board[r1][c2] == color &&
                            ((r1 ==  0 && c2 == s-1)||
                            (r1 == 0 && (checked[r1][c2-1] == 1 || checked[r1][c2+1] == 1))||
                            (c2 == s-1 && (checked[r1-1][c2] == 1 || checked[r1+1][c2] == 1))||
                            (c2 <s-1 && r1 > 0 &&
                            ((checked[r1][c2-1] == 1 && checked[r1-1][c2] == 1)||
                            (checked[r1][c2-1] == 1 && checked[r1+1][c2] == 1)||
                            (checked[r1][c2+1] == 1 && checked[r1-1][c2] == 1)||
                            (checked[r1][c2+1] == 1 && checked[r1+1][c2] == 1)))
                    )) {
                        checked[r1][c2] = 1;
                        stable ++;
                    }else{
                        checked[r1][c2] = -1;
                    }
                }
                if(checked[r2][c2] == 0){
                    if(board.board[r2][c2] == color &&
                            ((r2 ==  s-1 && c2 == s-1)||
                            (r2 == s-1 && (checked[r2][c2-1] == 1 || checked[r2][c2+1] == 1))||
                            (c2 == s-1 && (checked[r2-1][c2] == 1 || checked[r2+1][c2] == 1))||
                            (c2 < s-1 && r2 < s-1 &&
                            ((checked[r2][c2-1] == 1 && checked[r2-1][c2] == 1)||
                            (checked[r2][c2-1] == 1 && checked[r2+1][c2] == 1)||
                            (checked[r2][c2+1] == 1 && checked[r2-1][c2] == 1)||
                            (checked[r2][c2+1] == 1 && checked[r2+1][c2] == 1)))
                    )){
                        checked[r2][c2] = 1;
                        stable ++;
                    }else{
                        checked[r2][c2] = -1;
                    }
                }
            }
            depth ++;
        }

        return stable;
    }

    public int getValue(OthelloBoard board, OthelloMove m, int depth, int min, int max){

        Integer value = null;

        OthelloBoard newBoard = new OthelloBoard(board.size, false);
        for(int r = 0; r < board.size; r++){
            for(int c = 0; c<board.size;c++){
                newBoard.board[r][c] = board.board[r][c];
            }
        }
        newBoard.addPiece(m);

        int nextPlayer = playerColor;
        if(m.player == playerColor) nextPlayer = getOpponentColor();

        ArrayList<OthelloMove> moves = newBoard.legalMoves(nextPlayer);

        if(moves.size() == 0 || depth <= 1){
            value = board.size*9*(getNumStable(playerColor, newBoard) - getNumStable(getOpponentColor(), newBoard));
            if(playerColor == 1) value += newBoard.getBoardScore();
            else value -= newBoard.getBoardScore();
            for(int i = 0; i<board.size; i++){
                value += (newBoard.board[0][i] == playerColor)?3:0;
                value -= (newBoard.board[0][i] == getOpponentColor())?3:0;
                value += (newBoard.board[board.size-1][i] == playerColor)?3:0;
                value -= (newBoard.board[board.size-1][i] == getOpponentColor())?3:0;
                value += (newBoard.board[i][0] == playerColor)?3:0;
                value -= (newBoard.board[i][0] == getOpponentColor())?3:0;
                value += (newBoard.board[i][board.size-1] == playerColor)?3:0;
                value -= (newBoard.board[i][board.size-1] == getOpponentColor())?3:0;
            }
        }else{
            int moveValue;
            for (int i = 0; i < moves.size(); i++){
                moveValue = getValue(newBoard, moves.get(i), depth - 1, min, max);
                if(value == null) value = moveValue;
                if((m.player == getOpponentColor()) && moveValue > value)
								{
									value = moveValue;
									max = Math.max(max, value);
									if (min <= max)
										break;
								}
                else if((m.player == playerColor) && moveValue < value)
								{
									value = moveValue;
									min = Math.min(min, value);
									if (min <= max)
										break;
								}
            }
        }

        return value;
    }

    public OthelloMove makeMove(OthelloBoard board) {

        double start = (double)System.currentTimeMillis();

        ArrayList<OthelloMove> moves = board.legalMoves(playerColor);
        int chosenMoveIndex = 0;
        Integer bestMoveValue = null;

        for (int i = 0; i < moves.size(); i++){
            int moveValue = getValue(board, moves.get(i), SEARCH_DEPTH, Integer.MAX_VALUE, Integer.MIN_VALUE);
            if(bestMoveValue == null) bestMoveValue = moveValue;
            else if(moveValue > bestMoveValue){
                chosenMoveIndex = i;
                bestMoveValue = moveValue;
            }
        }

        if(commenting){

            if(!taunted && !conceded){
                if(bestMoveValue <=-500){
                    System.out.println("It appears I have been bested");
                    conceded = true;
                }
                if (bestMoveValue >=500){
                    System.out.println("GG");
                    taunted = true;
                }
                if (!admired && turns > (board.size*board.size/2)){
                    System.out.println("This is a close game!");
                    admired = true;
                }
            }else if(!taunted && bestMoveValue >=500){
                System.out.println("WHAT A PLAY!");
                taunted = true;
            }else if(!conceded && bestMoveValue <=-500){
                System.out.println("NOOOOOOOOOOO!");
                conceded = true;
            }
        }

        moveNum ++;
        time += ((double)System.currentTimeMillis()-start);
        avg = (time/moveNum)/1000.0;

        if(avg > 1 && SEARCH_DEPTH> SEARCH_MIN) SEARCH_DEPTH --;
        else if(avg <=.8 && SEARCH_DEPTH < SEARCH_MAX) SEARCH_DEPTH ++;

        //System.out.println(avg + " " + SEARCH_DEPTH);
        //System.out.println(bestMoveValue);

        return moves.get(chosenMoveIndex);
    }
}
