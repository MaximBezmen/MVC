
import java.util.*;

public class Model {//будет содержать игровую логику и хранить игровое поле.

    private final static int FIELD_WIDTH = 4;// размер поля
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];// игровое поле
    int score;//текущий счет
    int maxTile;//максимальный вес плитки

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public void setGameTiles(Tile[][] gameTiles) {
        this.gameTiles = gameTiles;
    }
    private Stack<Integer> previousScores = new Stack<>();
    private Stack<Tile[][]> previousStates = new Stack<>();

    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
        this.score = 0;
        this.maxTile = 2;
    }



    public void resetGameTiles() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                this.gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    public boolean canMove() {//проверить возможно сделать ход
        if (!getEmptyTiles().isEmpty()) return true;

        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 1; j < gameTiles.length; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j - 1].value)
                    return true;
            }
        }
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 1; j < gameTiles.length; j++) {
                if (gameTiles[j][i].value == gameTiles[j - 1][i].value) return true;
            }
        }
        return false;
    }

    private List<Tile> getEmptyTiles() {//получение свободных плиток в массиве gameTiles
        List<Tile> newEmpti = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    newEmpti.add( gameTiles[i][j] );
                }
            }
        }
        return newEmpti;
    }


    private void addTile() {//смотреть какие плитки пустуют и, если такие имеются, менять вес одной
        List<Tile> list = getEmptyTiles();
        if (list != null && list.size() != 0) {
            list.get((int) (list.size() * Math.random())).setValue(Math.random() < 0.9 ? 2 : 4);
        }
    }


    private boolean compressTiles(Tile[] tiles) {//Сжатие плиток
        boolean chage = false;
        for (int i = tiles.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (tiles[j].value == 0 && tiles[j + 1].value != 0) {
                    int tmp = tiles[j].value;
                    tiles[j].value = tiles[j + 1].value;
                    tiles[j + 1].value = tmp;

                    chage = true;
                }
            }
        }
        return chage;
    }

    private boolean mergeTiles(Tile[] tiles) {//Слияние плиток
        boolean chage = false;
        for (int i = 1; i < tiles.length; i++) {
            if ((tiles[i - 1].value == tiles[i].value) && (tiles[i - 1].value != 0) && (tiles[i].value != 0)) {
                tiles[i - 1].value *= 2;
                tiles[i].value = 0;

                if (tiles[i - 1].value > maxTile)
                    maxTile = tiles[i - 1].value;
                score += tiles[i - 1].value;
                chage = true;
            }
        }
        compressTiles( tiles );
        return chage;
    }

    public void left() {//для каждой строки массива gameTiles вызывать методы compressTiles и mergeTiles и добавлять
        // одну плитку с помощью метода addTile в том случае, если это необходимо.
        if (isSaveNeeded){
            saveState( gameTiles );
        }
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }
        if (isChanged) {
            addTile();
            isSaveNeeded = true;
        }

    }
//    2. В методе left организуем проверку того, вызывался ли уже метод saveState. За это у нас отвечает флаг isSaveNeeded
//    , соответственно, если он равен true, выполняем сохранение. После выполнения сдвига влево устанавливаем флаг isSaveNeeded равным true.

    public static Tile[][] turn(Tile[][] mass) { // поворачивает массив на 90
        Tile[][] result = new Tile[mass.length][mass.length];
        for (int i = 0; i < mass.length; i++) {
            for (int j = 0; j < mass.length; j++) {
                result[i][j] = mass[mass.length - 1 - j][i];
            }
        }
        return result;
    }

    public void right() {//перемещать элементы массива gameTiles вправо
        saveState( gameTiles );
        gameTiles = turn( gameTiles );
        gameTiles = turn( gameTiles );
        left();
        gameTiles = turn( gameTiles );
        gameTiles = turn( gameTiles );
    }

    public void up() {//перемещать элементы массива gameTiles вверх
        saveState( gameTiles );
        gameTiles = turn( gameTiles );
        gameTiles = turn( gameTiles );
        gameTiles = turn( gameTiles );
        left();
        gameTiles = turn( gameTiles );
    }


    public void down() {//перемещать элементы массива gameTiles вниз
        saveState( gameTiles );
        gameTiles = turn( gameTiles );
        left();
        gameTiles = turn( gameTiles );
        gameTiles = turn( gameTiles );
        gameTiles = turn( gameTiles );
    }

    private void saveState(Tile[][] tiles) {//сохранять текущее игровое состояние и счет в стеки
        Tile[][] fieldToSave = new Tile[tiles.length][tiles[0].length];
        isSaveNeeded = false;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                fieldToSave[i][j] = new Tile(tiles[i][j].getValue());
            }
        }
        previousStates.push(fieldToSave);
        int scoreToSave = score;
        previousScores.push(scoreToSave);
    }

    public void rollback(){ // возврат хода
        if(!previousScores.isEmpty()&&!previousStates.isEmpty()) {
            gameTiles = previousStates.pop();
            score=previousScores.pop();
        }
    }


    public void randomMove(){ //вызывать один из методов движения случайным образом
        int n = ((int) (Math.random() * 100)) % 4;
        if (n==0){
            left();
        }else if (n==1){
            right();
        }else if (n==2){
            up();
        }else if (n==3){
            down();
        }
    }

    public boolean hasBoardChanged(){ //вес плиток отличается
        Tile[][] lastBoard = previousStates.peek();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (lastBoard[i][j].value != gameTiles[i][j].value) {
                    return true;
                }
            }
        }

        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move){//описывающий эффективность переданного хода
        move.move();
        if (!hasBoardChanged()) {
            rollback();
            return new MoveEfficiency(-1, 0, move);
        }

        int emptyTilesCount = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    emptyTilesCount++;
                }
            }
        }

        MoveEfficiency moveEfficiency = new MoveEfficiency(emptyTilesCount, score, move);
        rollback();

        return moveEfficiency;
    }

    public void autoMove(){
        PriorityQueue <MoveEfficiency> priorityQueue = new PriorityQueue( 4, Collections.reverseOrder() );
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.peek().getMove().move();
//        1) Создадим локальную PriorityQueue с параметром Collections.reverseOrder()
//        (для того, чтобы вверху очереди всегда был максимальный элемент) и размером равным четырем.
//        2) Заполним PriorityQueue четырьмя объектами типа MoveEfficiency (по одному на каждый вариант хода).
//        3) Возьмем верхний элемент и выполним ход связанный с ним.
    }
}

