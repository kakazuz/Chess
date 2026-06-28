package chess;

import java.util.Collection;
import java.util.List;

public class PieceMovesCalculators {

    public interface PieceMovesCalculator {
        Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
    }

    public static class BishopMovesCalc implements  PieceMovesCalculator {
        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            }
            return List.of();
        }

    }

    public static class KingMovesCalc implements  PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            }
            return List.of();
        }

    }


    public static class QueenMovesCalc implements  PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            }
            return List.of();
        }

    }

    public static class RookMovesCalc implements  PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            }
            return List.of();
        }

    }

    public static class PawnMovesCalc implements  PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            }
            return List.of();
        }

    }

    public static class KnightMovesCalc implements  PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            }
            return List.of();
        }

    }

}
