
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {// будет следить за нажатием клавиш во время игры.
    private Model model;
    private View view;
    private static final int WINNING_TILE = 2048;//определять вес плитки при достижении которого игра будет считаться выигранной

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public View getView() {
        return view;
    }

    public int getScore() {
        return model.score;
    }

    public Controller(Model model) {
        this.model = model;
        this.view = new View( this );
//        Для начала нам понадобится конструктор, он будет принимать один параметр типа Model, инициализировать поле model,
//        а также сохранять в поле view новый объект типа View с текущим контроллером(this) в качестве параметра конструктора.
    }

    public void resetGame() {
        model.score = 0;
        view.isGameLost = false;
        view.isGameWon = false;
        model.resetGameTiles();
        //    Далее, нам нужен метод resetGame, который позволит вернуть игровое поле в начальное состояние.
//    Необходимо обнулить счет, установить флаги isGameWon и isGameLost у представления в false и вызывать метод resetGameTiles у модели.
//    Примечание: устанавливай значение полей напрямую, без использования сеттеров.
    }

    @Override
    public void keyPressed(KeyEvent e) {//обрабатывать пользовательский ввод
        if (e.getKeyCode()==KeyEvent.VK_R) {
            model.randomMove();
        }
        if (e.getKeyCode()==KeyEvent.VK_A) {
            model.autoMove();
        }
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
            resetGame();
        }
        if (e.getKeyCode()==KeyEvent.VK_Z){
            model.rollback();
        }
        if (model.canMove() == false) {
            view.isGameLost = true;
        }
        if (view.isGameLost == false && view.isGameWon==false){
            if (e.getKeyCode()==KeyEvent.VK_LEFT){
                model.left();
            }else if (e.getKeyCode()==KeyEvent.VK_RIGHT){
                model.right();
            }else if (e.getKeyCode()==KeyEvent.VK_UP){
                model.up();
            }else if (e.getKeyCode()==KeyEvent.VK_DOWN){
                model.down();
            }
        }
        if (model.maxTile==WINNING_TILE){
            view.isGameWon = true;
        }
        view.repaint(  );
    }
}

