package game.gdx.objects.panzer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public class MyPanzer extends Panzer {
    public MyPanzer(Vector2 position, float width, float height, Texture texture, int speed) {
        this.position = position;
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
        this.texture = texture;
        textureRegion = new TextureRegion(texture);
        this.speed = speed;
    }


}
