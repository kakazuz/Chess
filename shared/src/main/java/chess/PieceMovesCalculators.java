package chess;

import java.util.ArrayList;
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
            ChessGame.TeamColor peiceType = piece.getTeamColor();
            List<ChessMove> moveList = new ArrayList<>();

            int[][] moves = { {1,1}, {1,-1}, {-1,-1}, {-1,1} };

            for (int i = 0; i < 4; i++) {
                for(int mul = 1; mul < 8; mul++) {
                    int newRow = myPosition.getRow() + moves[i][0] * mul;
                    int newCol = myPosition.getColumn() + moves[i][1] * mul;

                    if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8 ) {
                        break;
                    }
                    ChessPiece newPiece = board.getPiece(new ChessPosition(newRow,newCol));
                    if (newPiece == null ) {
                        moveList.add(new ChessMove(myPosition, new ChessPosition(newRow,newCol), null));
                    }
                    else {
                        if (piece.getTeamColor() != newPiece.getTeamColor()) {
                            moveList.add(new ChessMove(myPosition, new ChessPosition(newRow,newCol), null));
                        }
                        break;
                    }

                }
            }
            return moveList;
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
