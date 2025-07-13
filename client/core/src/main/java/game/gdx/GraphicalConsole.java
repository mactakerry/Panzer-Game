package game.gdx;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.time.LocalDateTime;

public class GraphicalConsole {
    private Vector2 position = new Vector2();
    private int width;
    private int height;

    private Array<String> messages = new Array<>();

    public GraphicalConsole(int width, int height) {
        this.width = width;
        this.height = height;
        messages.add("Start");
        messages.add(LocalDateTime.now().toString());
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(position.x, position.y, width, height);
    }

    public void drawMessages(BitmapFont font, Batch batch) {
        float x = position.x;
        float y = position.y + height;
        for (String message : messages) {
            font.draw(batch, message, x, y);
            y = y - 20;
        }
    }

    public void addMessage(String message) {
        messages.add(message);

        while (20 * messages.size > height) {
            messages.removeIndex(0);
        }
    }

    public void updatePosition(float screenWidth, float screenHeight) {
        position.set(10, screenHeight - height - 10);
    }
}
