
import java.util.ArrayList;

class BoardNode
{
  OthelloBoard board;
  BoardNode parent;
  int score;
  OthelloMove move;
  public BoardNode(OthelloBoard b, int s)
  {
    board = b;
    score = s;
  }
  // public void setParent(BoardNode p)
  // {
  //   parent = p;
  // }
  // public void setScore(int s)
  // {
  //   score = s;
  // }
  // public BoardNode getParent()
  // {
  //   return parent;
  // }
  public int getScore()
  {
    return score;
  }
  public OthelloBoard getBoard()
  {
    return board;
  }
  public void setMove(OthelloMove m)
  {
    move = m;
  }
  public OthelloMove getMove()
  {
    return move;
  }
}

public class CheaterBot extends OthelloPlayer {
    int DEPTH = 4;
    public CheaterBot(Integer _color) {
        super(_color);
    }
    public OthelloBoard cloneBoard(OthelloBoard b){
       OthelloBoard board = new OthelloBoard(b.size, false);
       board.board = b.board.clone();
       return board;
   }
    public BoardNode makeNode(OthelloBoard b, OthelloMove move, int depth)
    {
      OthelloBoard newBoard = cloneBoard(b);
      newBoard.addPiece(move);
      BoardNode myNode = miniMax(newBoard, depth - 1);  //recursion
      //if (depth > DEPTH-2)
        myNode.setMove(move);
      //System.out.println("my move is " + move.toString());
      return myNode;
    }
    public BoardNode miniMax(OthelloBoard b, int depth)
    {
      ArrayList<OthelloMove> moves = b.legalMoves(playerColor);
      if (depth < 1 || moves.size()<1)  //if at end of tree
      {
        System.out.println("end of tree!");
        int score = b.getBoardScore();
        BoardNode myNode = new BoardNode(b, score);
        return myNode; //
      }
      System.out.println("not end of tree!");
      BoardNode bestNode = makeNode(b, moves.get(0), depth);
      int maxScore = bestNode.getScore();
      //for (OthelloMove move : moves)
      for (int i = 1; i < moves.size(); i++)
      {
        BoardNode myNode = makeNode(b, moves.get(i), depth);  //recursion
        int myScore = myNode.getScore();
        if (myScore>maxScore)
        {
          maxScore = myScore;
          bestNode = myNode;
        }
      }
      return bestNode;
    }
    public int getMoveScore(OthelloBoard b, OthelloMove m) {
        if (!b.isLegalMove(m)) {
            return -1;
        }
        else {
            int score = 0;
            score += scoreUp(b, m);
            score += scoreDown(b, m);
            score += scoreLeft(b, m);
            score += scoreRight(b, m);
            score += scoreUpLeft(b, m);
            score += scoreUpRight(b, m);
            score += scoreDownLeft(b, m);
            score += scoreDownRight(b, m);

            return score;
        }
    }

    public OthelloMove makeMove(OthelloBoard b) {

        //System.out.println(b);
        BoardNode node = miniMax(b, DEPTH);
        System.out.println("my node is " + node);
        OthelloMove move = node.getMove();
        System.out.println("my move is " + move);
        return move;

    }

    public int scoreUp(OthelloBoard b, OthelloMove m) {
        int currRow = m.row - 1;
        while (currRow >= 0 && b.board[currRow][m.col] != m.player && b.board[currRow][m.col] != 0) {
            currRow--;
        }
        if (currRow >= 0 && b.board[currRow][m.col] == m.player) {
            return m.row - currRow - 1;
        }
        return 0;
    }


    public int scoreDown(OthelloBoard b, OthelloMove m) {
        int currRow = m.row + 1;
        while (currRow < b.size && b.board[currRow][m.col] != m.player && b.board[currRow][m.col] != 0) {
            currRow++;
        }
        if (currRow < b.size && b.board[currRow][m.col] == m.player) {
            return currRow - m.row - 1;
        }
        return 0;
    }

    public int scoreRight(OthelloBoard b, OthelloMove m) {
        int currCol = m.col + 1;
        while (currCol < b.size && b.board[m.row][currCol] != m.player && b.board[m.row][currCol] != 0) {
            currCol++;
        }
        if (currCol < b.size && b.board[m.row][currCol] == m.player) {
            return currCol - m.col - 1;
        }
        return 0;
    }


    public int scoreLeft(OthelloBoard b, OthelloMove m) {
        int currCol = m.col - 1;
        while (currCol >= 0 && b.board[m.row][currCol] != m.player && b.board[m.row][currCol] != 0) {
            currCol--;
        }
        if (currCol >= 0 && b.board[m.row][currCol] == m.player) {
            return m.col-currCol - 1;
        }
        return 0;
    }

    public int scoreUpRight(OthelloBoard b, OthelloMove m) {
        int currRow = m.row - 1;
        int currCol = m.col + 1;
        while (currCol < b.size && currRow >= 0 && b.board[currRow][currCol] != m.player && b.board[currRow][currCol] != 0) {
            currCol++;
            currRow--;
        }
        if (currCol < b.size && currRow >= 0 && b.board[currRow][currCol] == m.player) {
            return currCol - m.col - 1;
        }
        return 0;
    }


    public int scoreUpLeft(OthelloBoard b, OthelloMove m) {
        int currRow = m.row - 1;
        int currCol = m.col - 1;
        while (currCol >= 0 && currRow >= 0 && b.board[currRow][currCol] != m.player && b.board[currRow][currCol] != 0) {
            currCol--;
            currRow--;
        }
        if (currCol >= 0 && currRow >= 0 && b.board[currRow][currCol] == m.player) {
            return m.row - currRow - 1;
        }
        return 0;
    }


    public int scoreDownRight(OthelloBoard b, OthelloMove m) {
        int currRow = m.row + 1;
        int currCol = m.col + 1;
        while (currCol < b.size && currRow < b.size && b.board[currRow][currCol] != m.player && b.board[currRow][currCol] != 0) {
            currCol++;
            currRow++;
        }
        if (currCol < b.size && currRow < b.size && b.board[currRow][currCol] == m.player) {
            return currRow - m.row - 1;
        }
        return 0;
    }


    public int scoreDownLeft(OthelloBoard b, OthelloMove m) {
        int currRow = m.row + 1;
        int currCol = m.col - 1;
        while (currCol >= 0 && currRow < b.size && b.board[currRow][currCol] != m.player && b.board[currRow][currCol] != 0) {
            currCol--;
            currRow++;
        }
        if (currCol >= 0 && currRow < b.size && b.board[currRow][currCol] == m.player) {
            return currRow - m.row - 1;
        }
        return 0;
    }
}
