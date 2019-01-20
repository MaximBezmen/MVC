public class MoveEfficiency implements Comparable<MoveEfficiency>{
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        if (this.numberOfEmptyTiles != o.numberOfEmptyTiles) {
            return Integer.compare(this.numberOfEmptyTiles, o.numberOfEmptyTiles);
        } else {
            return Integer.compare(this.score, o.score);
        }
    }
}
//    В методе compareTo первым делом сравни количество пустых плиток (numberOfEmptyTiles), потом счет (score),
//        если количество пустых плиток равное. Если и счет окажется равным, будем считать эффективность ходов равной и вернем ноль.

