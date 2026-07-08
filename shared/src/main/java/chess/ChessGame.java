package chess;

import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame{

    private ChessBoard board;
    private TeamColor currentTeam = TeamColor.WHITE;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(board.getPiece(startPosition) == null) {
            return null;
        }

        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moveList = piece.pieceMoves(board,startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        for(ChessMove move: moveList) {
            ChessBoard cloneBoard;
            try {
                cloneBoard = board.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            cloneBoard.addPiece(move.getEndPosition(), piece);
            cloneBoard.addPiece(startPosition, null);
            if(!checkClone(piece.getTeamColor(), cloneBoard)) {
                legalMoves.add(move);
            }


        }
        return legalMoves;

    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPos);

        if(piece == null) {
            throw new InvalidMoveException("No piece to move");
        }
        if(piece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("Not your turn");
        }
        Collection<ChessMove> moveList = validMoves(startPos);
        if(moveList == null || !moveList.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(startPos, null);

        if(move.getPromotionPiece() != null) {
            ChessPiece promotion = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotion);
        }
        if(TeamColor.WHITE == currentTeam){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor, board);
        TeamColor opponent;

        if(teamColor == TeamColor.WHITE) {
            opponent = TeamColor.BLACK;
        } else {
            opponent = TeamColor.WHITE;
        }

        for(int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if(checkHelper(piece, opponent, pos, kingPos, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && noLegalMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && noLegalMoves(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKing(TeamColor teamColor, ChessBoard boardToCheck) {
        for(int row = 1; row < 9; row++) {
            for(int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = boardToCheck.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    private boolean checkClone(TeamColor teamColor, ChessBoard clone) {
        ChessPosition kingPos = findKing(teamColor,clone);
        TeamColor opponent;

        if(teamColor == TeamColor.WHITE) {
            opponent = TeamColor.BLACK;
        } else {
            opponent = TeamColor.WHITE;
        }

        for(int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = clone.getPiece(pos);
                if(checkHelper(piece, opponent, pos, kingPos, clone)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkHelper(ChessPiece piece, TeamColor opponent, ChessPosition pos, ChessPosition kingPos, ChessBoard board) {
        if(piece != null && piece.getTeamColor() == opponent) {
            Collection<ChessMove> moveList = piece.pieceMoves(board, pos);
            for(ChessMove move : moveList) {
                if(move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean noLegalMoves(TeamColor teamColor) {
        for(int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if(moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTeam);
    }
}
