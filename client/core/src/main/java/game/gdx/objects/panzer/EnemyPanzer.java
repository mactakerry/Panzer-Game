package game.gdx.objects.panzer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class EnemyPanzer extends Panzer {

    public EnemyPanzer (Vector2 position, float width, float height, Texture texture, int speed) {
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
