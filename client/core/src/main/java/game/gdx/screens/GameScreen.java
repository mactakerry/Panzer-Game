package game.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.WebSockets;
import game.gdx.GraphicalConsole;
import game.gdx.KeyboardAdapter;
import game.gdx.Starter;
import game.gdx.dto.GetPanzerState;
import game.gdx.dto.SendPanzerState;
import game.gdx.network.NetworkMessage;
import game.gdx.objects.panzer.EnemyPanzer;
import game.gdx.objects.panzer.MyPanzer;
import game.gdx.service.PlayerIdentity;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameScreen extends ScreenAdapter {
    private final Starter starter;
    private WebSocket socket;

    public static final float WORLD_RADIUS = 3000f;
    public static final Vector2 WORLD_CENTER = new Vector2(0, 0);

    private Texture textureEnemy;

    private float stateUpdateTimer = 0;
    private static final float STATE_UPDATE_INTERVAL = 0.005f;

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
    private SendPanzerState sendPanzerState;
    private final Array<EnemyPanzer> enemies = new Array<>();

    public GameScreen(Starter starter) {
        this.starter = starter;
    }

    @Override
    public void show() {
        batch = starter.batch;
        shapeRenderer = starter.shapeRenderer;
        font = starter.font;
        font.getData().setScale(1f);

        textureEnemy = starter.textureEnemy;

        Gdx.input.setInputProcessor(keyboardAdapter);

        me = new MyPanzer(new Vector2(50,50), 64, 64, starter.textureMe, 5);
        me.setId(PlayerIdentity.getInstance().getId());
        sendPanzerState = new SendPanzerState();

        graphicalConsole = new GraphicalConsole(Gdx.graphics.getWidth() - 20, 500);
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        graphicalConsole.updatePosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(me.getPosition().x, me.getPosition().y, 0);
        camera.update();

        connectToServer();
    }

    @Override
    public void render(float delta) {

        Vector2 mousePosition = keyboardAdapter.getMousePosition();
        mouseWorld = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0)); // преобразование в мировые координаты
        mousePosition.set(mouseWorld.x, mouseWorld.y); // используем тот же Vector2 для экономии памяти

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
        for (int i = 0; i < enemies.size; i++) {
            EnemyPanzer enemy = enemies.get(i);
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

        // Отправляем состояние с интервалом
        stateUpdateTimer += delta;
        if (stateUpdateTimer >= STATE_UPDATE_INTERVAL) {
            sendState();
            stateUpdateTimer = 0;
        }
    }

    @Override
    public void dispose() {
        if (socket != null) {
            socket.close();
        }
    }

    private void connectToServer() {
        String token = PlayerIdentity.getInstance().getToken();

        socket = WebSockets.newSocket("ws://localhost:8888/panzer-ws?token=" + token);
        socket.setSendGracefully(true);

        socket.addListener(new WebSocketAdapter() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                graphicalConsole.addMessage("Connected to server!");

                Json json = new Json();
                NetworkMessage message = new NetworkMessage();
                message.type = "CONSOLE";
                message.payload = "Hello server";
                socket.send(json.toJson(message));
                return false;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String packet) {
                Json json = new Json();
                NetworkMessage message = json.fromJson(NetworkMessage.class, packet);
                switch (message.type) {
                    case "CONSOLE":
                        graphicalConsole.addMessage(packet);
                        break;
                    case "STATE_UPDATE":
                        GetPanzerState state = json.fromJson(GetPanzerState.class, message.payload);
                        updateTanks(state);
                        break;
                }

                return false;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                graphicalConsole.addMessage("ERROR: " + error.getMessage());
                return false;
            }

            @Override
            public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
                graphicalConsole.addMessage("CLOSE");
                return false;
            }
        });

        socket.connect();
    }

    private void sendState() {
        if (socket != null && socket.isOpen()) {
            Json json = new Json();

            sendPanzerState.playerId = me.getId();
            sendPanzerState.angle = me.getAngle().angleDeg();
            sendPanzerState.inputState = keyboardAdapter.inputState;

            NetworkMessage message = new NetworkMessage();
            message.type = "STATE_UPDATE";
            message.payload = json.toJson(sendPanzerState);

            socket.send(json.toJson(message));
        }
    }

    private void updateTanks(GetPanzerState state) {
        System.out.println(state.toString());
        if (state.playerId == me.getId()) {
            me.setPosition(state.x, state.y);
            me.setAngleDeg(state.angle);
            return;
        }

        for (EnemyPanzer enemy : enemies) {
            if (enemy.getId() == state.playerId) {
                enemy.setPosition(state.x, state.y);
                enemy.setAngleDeg(state.angle);
                return;
            }
        }

        // Если танк с таким ID не найден - создаем нового
        Vector2 position = new Vector2(state.x, state.y);
        EnemyPanzer newEnemy = new EnemyPanzer(
            position,
            64, 64,
            textureEnemy,
            5
        );
        newEnemy.setId(state.playerId);
        enemies.add(newEnemy);
    }
}
