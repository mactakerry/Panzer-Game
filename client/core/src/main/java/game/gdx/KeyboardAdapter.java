package game.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import game.gdx.dto.InputState;

public class KeyboardAdapter extends InputAdapter {

    private final Vector2 direction = new Vector2();
    private final Vector2 mousePosition = new Vector2();

    public final InputState inputState = new InputState();

    private boolean showConsole = false;



    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        inputState.firePressed = true;
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        inputState.firePressed = false;
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A) inputState.leftPressed = true;
        if (keycode == Input.Keys.D) inputState.rightPressed = true;
        if (keycode == Input.Keys.W) inputState.upPressed = true;
        if (keycode == Input.Keys.S) inputState.downPressed = true;

        if (keycode == Input.Keys.P) showConsole = !showConsole;

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) inputState.leftPressed = false;
        if (keycode == Input.Keys.D) inputState.rightPressed = false;
        if (keycode == Input.Keys.W) inputState.upPressed = false;
        if (keycode == Input.Keys.S) inputState.downPressed = false;


        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePosition.set(screenX, Gdx.graphics.getHeight() - screenY);
        return false;
    }

    public Vector2 getDirection(int speed) {
        direction.set(0,0);

        if (inputState.leftPressed) direction.add(-speed, 0);
        if (inputState.rightPressed) direction.add(speed, 0);
        if (inputState.upPressed) direction.add(0, speed);
        if (inputState.downPressed) direction.add(0, -speed);

        return direction;
    }

    public Vector2 getMousePosition() {
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
        return mousePosition;
    }

    public boolean isShowConsole() {
        return showConsole;
    }
}
