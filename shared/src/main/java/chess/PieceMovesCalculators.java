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
            ChessGame.TeamColor peiceType = piece.getTeamColor();
            List<ChessMove> moveList = new ArrayList<>();

            int[][] moves = { {1,1}, {1,-1}, {-1,-1}, {-1,1}, {1,0}, {-1,0}, {0,-1}, {0,1} };

            for (int i = 0; i < 8; i++) {
                int newRow = myPosition.getRow() + moves[i][0];
                int newCol = myPosition.getColumn() + moves[i][1];

                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8 ) {
                    continue;
                }
                ChessPiece newPiece = board.getPiece(new ChessPosition(newRow,newCol));
                if (newPiece == null ) {
                    moveList.add(new ChessMove(myPosition, new ChessPosition(newRow,newCol), null));
                }
                else {
                    if (piece.getTeamColor() != newPiece.getTeamColor()) {
                        moveList.add(new ChessMove(myPosition, new ChessPosition(newRow,newCol), null));
                    }
                    continue;
                }


            }
            return moveList;
        }

    }


    public static class QueenMovesCalc implements  PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            ChessGame.TeamColor peiceType = piece.getTeamColor();
            List<ChessMove> moveList = new ArrayList<>();

            int[][] moves = { {1,1}, {1,-1}, {-1,-1}, {-1,1}, {1,0}, {-1,0}, {0,-1}, {0,1} };

            for (int i = 0; i < 8; i++) {
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

    public static class RookMovesCalc implements  PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            ChessGame.TeamColor peiceType = piece.getTeamColor();
            List<ChessMove> moveList = new ArrayList<>();

            int[][] moves = { {1,0}, {-1,0}, {0,-1}, {0,1} };

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

    public static class PawnMovesCalc implements  PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            ChessGame.TeamColor peiceType = piece.getTeamColor();
            int currentRow = myPosition.getRow();
            int currentCol = myPosition.getColumn();
            List<ChessMove> moveList = new ArrayList<>();

            int[][] moves = { {1,0}, {2,0}, {1,1}, {1,-1} };
            int direction;

            if (peiceType == ChessGame.TeamColor.WHITE) {
                direction = 1;
            } else {
                direction = -1;
            }

            int onefront = currentRow + 1;
            int twofront = currentRow + 2;
            boolean frontEmpty = board.getPiece(new ChessPosition(onefront, currentCol)) == null;

            if (direction == 1 && frontEmpty && onefront <= 8 ) {
                if (onefront == 8) {
                    moveList.add(new ChessMove(myPosition, new ChessPosition(onefront,currentCol), ChessPiece.PieceType.PAWN));
                } else {
                    moveList.add(new ChessMove(myPosition, new ChessPosition(onefront,currentCol), null));
                }
            }
            if (direction == 1 && currentRow == 2 && frontEmpty) {
                boolean twofrontEmpty = board.getPiece(new ChessPosition(twofront, currentCol)) == null;
                if (twofrontEmpty) {
                    moveList.add(new ChessMove(myPosition, new ChessPosition(twofront, currentCol), null));
                }
            }

            int oneright = currentCol + 1;
            int oneleft = currentCol - 1;

            if (direction == 1) {
                if (oneright <= 8) {
                    ChessPiece rightEmpty = board.getPiece(new ChessPosition(onefront, oneright));
                    if (rightEmpty != null && rightEmpty.getTeamColor() != ChessGame.TeamColor.WHITE) {
                        if (onefront == 8) {
                            moveList.add(new ChessMove(myPosition, new ChessPosition(onefront, oneright), ChessPiece.PieceType.PAWN));
                        } else {
                            moveList.add(new ChessMove(myPosition, new ChessPosition(onefront, oneright), null));
                        }
                    }

                }
                if (oneleft >= 1) {
                    ChessPiece leftEmpty = board.getPiece(new ChessPosition(onefront, oneleft));
                    if (leftEmpty != null && leftEmpty.getTeamColor() != ChessGame.TeamColor.WHITE) {
                        if (onefront == 8) {
                            moveList.add(new ChessMove(myPosition, new ChessPosition(onefront, oneleft), ChessPiece.PieceType.PAWN));
                        } else {
                            moveList.add(new ChessMove(myPosition, new ChessPosition(onefront, oneleft), null));
                        };
                    }
                }
            }


// this is the start of black

            int onebelow = currentRow - 1;
            int twobelow = currentRow - 2;
            boolean belowEmpty = board.getPiece(new ChessPosition(onebelow, currentCol)) == null;

            if (direction == -1 && belowEmpty && onebelow >= 1 ) {
                if (onebelow == 1) {
                    moveList.add(new ChessMove(myPosition, new ChessPosition(onebelow,currentCol), ChessPiece.PieceType.PAWN));
                } else {
                    moveList.add(new ChessMove(myPosition, new ChessPosition(onebelow,currentCol), null));

                }
            }
            if (direction == -1 && currentRow == 7 && belowEmpty) {
                boolean twobelowEmpty = board.getPiece(new ChessPosition(twobelow, currentCol)) == null;
                if (twobelowEmpty) {
                    moveList.add(new ChessMove(myPosition, new ChessPosition(twobelow, currentCol), null));
                }
            }

            int oneBelowright = currentCol - 1;
            int oneBelowleft = currentCol + 1;

            if (direction == -1) {
                if (oneBelowleft <= 8) {
                    ChessPiece rightbelowEmpty = board.getPiece(new ChessPosition(onebelow, oneBelowleft));
                        if (rightbelowEmpty != null && rightbelowEmpty.getTeamColor() != ChessGame.TeamColor.BLACK) {
                            if (onebelow == 1) {
                                moveList.add(new ChessMove(myPosition, new ChessPosition(onebelow, oneBelowleft), ChessPiece.PieceType.PAWN));
                            } else {
                                moveList.add(new ChessMove(myPosition, new ChessPosition(onebelow, oneBelowleft), null));
                            }
                        }

                }
                if (oneBelowright >= 1) {
                    ChessPiece leftbelowEmpty = board.getPiece(new ChessPosition(onebelow, oneBelowright));
                    if (leftbelowEmpty != null && leftbelowEmpty.getTeamColor() != ChessGame.TeamColor.BLACK) {
                        if (onebelow == 1) {
                            moveList.add(new ChessMove(myPosition, new ChessPosition(onebelow, oneBelowright), ChessPiece.PieceType.PAWN));
                        } else {
                            moveList.add(new ChessMove(myPosition, new ChessPosition(onebelow, oneBelowright), null));
                        }
                    }
                }
            }


            return moveList;
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
