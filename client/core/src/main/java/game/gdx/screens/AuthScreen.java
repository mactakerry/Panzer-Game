package game.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import game.gdx.Starter;
import game.gdx.service.PlayerIdentity;

public class AuthScreen extends ScreenAdapter {
    private final Starter starter;
    private Stage stage;
    private TextField loginField;
    private TextField passwordField;
    private TextButton loginButton;
    private TextButton registerButton;
    private BitmapFont font;

    public AuthScreen(Starter starter) {
        this.starter = starter;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = starter.font;
        font.getData().setScale(2f); // Увеличим шрифт

        // Создаем текстуры для фона полей ввода
        Pixmap fieldPixmap = new Pixmap(400, 60, Pixmap.Format.RGBA8888);
        fieldPixmap.setColor(Color.LIGHT_GRAY);
        fieldPixmap.fill();
        Texture fieldTexture = new Texture(fieldPixmap);
        fieldPixmap.dispose();

        // Стиль для текстовых полей
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = new TextureRegionDrawable(fieldTexture);

        // Создаем курсор
        Pixmap cursorPixmap = new Pixmap(2, 30, Pixmap.Format.RGBA8888);
        cursorPixmap.setColor(Color.BLUE);
        cursorPixmap.fill();
        textFieldStyle.cursor = new Image(new Texture(cursorPixmap)).getDrawable();
        cursorPixmap.dispose();

        // Поле логина
        loginField = new TextField("", textFieldStyle);
        loginField.setMessageText("login");
        loginField.setPosition(100, 400);
        loginField.setSize(400, 60);

        // Поле пароля
        passwordField = new TextField("", textFieldStyle);
        passwordField.setMessageText("password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('•');
        passwordField.setPosition(100, 300);
        passwordField.setSize(400, 60);

        // Стиль для кнопок
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;

        // Текстура для обычной кнопки
        Pixmap buttonUpPixmap = new Pixmap(180, 60, Pixmap.Format.RGBA8888);
        buttonUpPixmap.setColor(Color.GREEN);
        buttonUpPixmap.fill();
        buttonStyle.up = new TextureRegionDrawable(new Texture(buttonUpPixmap));
        buttonUpPixmap.dispose();

        // Текстура для нажатой кнопки
        Pixmap buttonDownPixmap = new Pixmap(180, 60, Pixmap.Format.RGBA8888);
        buttonDownPixmap.setColor(Color.DARK_GRAY);
        buttonDownPixmap.fill();
        buttonStyle.down = new TextureRegionDrawable(new Texture(buttonDownPixmap));
        buttonDownPixmap.dispose();

        // Кнопка входа
        loginButton = new TextButton("login", buttonStyle);
        loginButton.setPosition(100, 200);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLogin();
            }
        });

        // Кнопка регистрации
        registerButton = new TextButton("registration", buttonStyle);
        registerButton.setPosition(320, 200);
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleRegistration();
            }
        });

        // Добавляем всё на сцену
        stage.addActor(loginField);
        stage.addActor(passwordField);
        stage.addActor(loginButton);
        stage.addActor(registerButton);
    }

    private void handleLogin() {
        String username = loginField.getText();
        String password = passwordField.getText();

        Gdx.app.log("login", username + " " + password);

        String url =  "http://localhost:8888/public/login";
        String jsonBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest request = requestBuilder
            .newRequest()
            .method(Net.HttpMethods.POST)
            .url(url)
            .header("Content-Type", "application/json")
            .content(jsonBody)
            .timeout(5000)
            .build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String response = httpResponse.getResultAsString();

                if (statusCode == 200) {
                    Gdx.app.log("Login", "Success! ");
                    JsonReader reader = new JsonReader();
                    JsonValue root = reader.parse(response);

                    String token = root.getString("token");
                    long id = root.getLong("id");

                    PlayerIdentity identity = PlayerIdentity.getInstance();

                    identity.setId(id);
                    identity.setToken(token);
                    identity.save();

                    Gdx.app.postRunnable(() -> starter.setScreen(new GameScreen(starter)));
                } else {
                    Gdx.app.error("Login", "Error " + statusCode + ": " + response);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("Login", "client error: ", t);
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Login", "request cancelled");
            }
        });
    }

    private void handleRegistration() {
        starter.setScreen(new RegistrationScreen(starter));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.3f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
