package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    public final PieceMovesCalculators.PieceMovesCalculator calculator;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.calculator = createCalculator(type);
    }

    private PieceMovesCalculators.PieceMovesCalculator createCalculator(PieceType type) {
        if (type == PieceType.BISHOP) {
            return new PieceMovesCalculators.BishopMovesCalc();
        }
        if (type == PieceType.KING) {
            return new PieceMovesCalculators.KingMovesCalc();
        }
        if (type == PieceType.QUEEN) {
            return new PieceMovesCalculators.QueenMovesCalc();
        }
        if (type == PieceType.ROOK) {
            return new PieceMovesCalculators.RookMovesCalc();
        }
        if (type == PieceType.PAWN) {
            return new PieceMovesCalculators.PawnMovesCalc();
        }
        if (type == PieceType.KNIGHT) {
            return new PieceMovesCalculators.KnightMovesCalc();
        }
        else {
            return null;
        }
    }




    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return calculator.pieceMoves(board, myPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
