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
import com.badlogic.gdx.utils.ScreenUtils;
import game.gdx.Starter;

public class RegistrationScreen extends ScreenAdapter {
    private final Starter starter;
    private Stage stage;
    private TextField usernameField;
    private TextField passwordField;
    private TextField repeatPasswordField;
    private BitmapFont font;

    public RegistrationScreen(Starter starter) {
        this.starter = starter;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = starter.font;
        font.getData().setScale(2f);

        // Стиль для текстовых полей
        TextField.TextFieldStyle fieldStyle = createFieldStyle();

        // Поле для юзернейма
        usernameField = new TextField("", fieldStyle);
        usernameField.setMessageText("Username");
        usernameField.setPosition(100, 500);
        usernameField.setSize(400, 60);

        // Поле для пароля
        passwordField = new TextField("", fieldStyle);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('•');
        passwordField.setPosition(100, 400);
        passwordField.setSize(400, 60);

        // Поле для повторного пароля
        repeatPasswordField = new TextField("", fieldStyle);
        repeatPasswordField.setMessageText("Repeat Password");
        repeatPasswordField.setPasswordMode(true);
        repeatPasswordField.setPasswordCharacter('•');
        repeatPasswordField.setPosition(100, 300);
        repeatPasswordField.setSize(400, 60);

        // Стиль для кнопок
        TextButton.TextButtonStyle buttonStyle = createButtonStyle();

        // Кнопка регистрации
        TextButton registerButton = new TextButton("REGISTER", buttonStyle);
        registerButton.setPosition(100, 150);
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleRegistration();
            }
        });

        // Кнопка "Назад"
        TextButton backButton = new TextButton("BACK", buttonStyle);
        backButton.setPosition(320, 150);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                starter.setScreen(new AuthScreen(starter));
            }
        });

        stage.addActor(usernameField);
        stage.addActor(passwordField);
        stage.addActor(repeatPasswordField);
        stage.addActor(registerButton);
        stage.addActor(backButton);
    }

    private TextField.TextFieldStyle createFieldStyle() {
        Pixmap fieldPixmap = new Pixmap(400, 60, Pixmap.Format.RGBA8888);
        fieldPixmap.setColor(Color.LIGHT_GRAY);
        fieldPixmap.fill();
        Texture fieldTexture = new Texture(fieldPixmap);
        fieldPixmap.dispose();

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.background = new TextureRegionDrawable(fieldTexture);

        Pixmap cursorPixmap = new Pixmap(2, 30, Pixmap.Format.RGBA8888);
        cursorPixmap.setColor(Color.BLUE);
        cursorPixmap.fill();
        style.cursor = new Image(new Texture(cursorPixmap)).getDrawable();
        cursorPixmap.dispose();

        return style;
    }

    private TextButton.TextButtonStyle createButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;

        // Кнопка обычная
        Pixmap buttonUp = new Pixmap(180, 60, Pixmap.Format.RGBA8888);
        buttonUp.setColor(Color.BLUE);
        buttonUp.fill();
        style.up = new TextureRegionDrawable(new Texture(buttonUp));
        buttonUp.dispose();

        // Кнопка нажатая
        Pixmap buttonDown = new Pixmap(180, 60, Pixmap.Format.RGBA8888);
        buttonDown.setColor(Color.DARK_GRAY);
        buttonDown.fill();
        style.down = new TextureRegionDrawable(new Texture(buttonDown));
        buttonDown.dispose();

        return style;
    }

    private void handleRegistration() {

        String username = usernameField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();

        Gdx.app.log("reg", username + " " + password + " " + repeatPassword);


        if (!password.equals(repeatPassword)) {
            System.out.println("Пароли не совпадают!");
            return;
        }

        String url =  "http://localhost:8888/public/reg";
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
                    Gdx.app.log("Reg", "Success! " + response);
                    Gdx.app.postRunnable(() -> starter.setScreen(new AuthScreen(starter)));
                } else {
                    Gdx.app.error("Reg", "Error " + statusCode + ": " + response);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("Reg", "client error", t);
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Reg", "request cancelled");
            }
        });
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
