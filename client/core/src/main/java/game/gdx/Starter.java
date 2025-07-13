package game.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import game.gdx.objects.panzer.EnemyPanzer;
import game.gdx.objects.panzer.MyPanzer;

import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Starter extends ApplicationAdapter {
    public static final float WORLD_RADIUS = 3000f;
    public static final Vector2 WORLD_CENTER = new Vector2(0, 0);

    private OrthographicCamera camera;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    // для графического консолья
    private GraphicalConsole graphicalConsole;
    private OrthographicCamera uiCamera;

    private final KeyboardAdapter keyboardAdapter = new KeyboardAdapter();
    private Vector3 mouseWorld = new Vector3();

    private MyPanzer me;
    private final Array<EnemyPanzer> enemies = new Array<>();

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        Gdx.input.setInputProcessor(keyboardAdapter);

        me = new MyPanzer(new Vector2(50,50), 64, 64, new Texture("panzer_me.png"), 5);

        graphicalConsole = new GraphicalConsole(500, 500);
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        graphicalConsole.updatePosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(me.getPosition().x, me.getPosition().y, 0);
        camera.update();



        for (int i = 0; i < 10; i++) {
            float x = MathUtils.random(Gdx.graphics.getWidth());
            float y = MathUtils.random(Gdx.graphics.getHeight());

            EnemyPanzer newEnemy = new EnemyPanzer(new Vector2(x,y), 64,64,new Texture("panzer_enemy.png"), 5);
            enemies.add(newEnemy);
        }
    }

    @Override
    public void render() {
        me.MoveTo(keyboardAdapter.getDirection(5));

        Vector2 mousePosition = keyboardAdapter.getMousePosition();
        mouseWorld = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0)); // преобразование в мировые координаты
        mousePosition.set(mouseWorld.x, mouseWorld.y); // используем тот же Vector2 для экономии памяти
        me.rotateTo(mousePosition);

        camera.position.set(me.getPosition().x, me.getPosition().y, 0);
        camera.update();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(
            WORLD_CENTER.x,
            WORLD_CENTER.y,
            WORLD_RADIUS
        );
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        me.render(batch);
        for (EnemyPanzer enemy : enemies) {
            enemy.rotateTo(me.getPosition());
            enemy.render(batch);
        }

        batch.end();

        if (keyboardAdapter.isShowConsole()) {
            // Переключаемся на UI-рендеринг
            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            batch.setProjectionMatrix(uiCamera.combined);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
                graphicalConsole.draw(shapeRenderer);
            shapeRenderer.end();

            batch.begin();
                font.setColor(Color.GREEN);
                graphicalConsole.drawMessages(font, batch);
            batch.end();

        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        me.dispose();
        for (EnemyPanzer enemyPanzer : enemies) {
            enemyPanzer.dispose();
        }
    }
}
