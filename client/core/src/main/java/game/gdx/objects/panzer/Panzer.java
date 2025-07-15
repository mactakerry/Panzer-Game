package game.gdx.objects.panzer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import static game.gdx.screens.GameScreen.WORLD_CENTER;
import static game.gdx.screens.GameScreen.WORLD_RADIUS;

public abstract class Panzer {
    private long id;

    protected Vector2 position = new Vector2();
    protected Vector2 angle = new Vector2();

    private final Vector2 tmpVector = new Vector2();
    private final Vector2 tmpCenter = new Vector2();

    protected float width;
    protected float height;
    protected float halfWidth = width/2;
    protected float halfHeight = height/2;

    protected Texture texture;

    protected int speed;


    protected TextureRegion textureRegion;

    public void MoveTo(Vector2 to) {
        position.add(to);
        clampToCircle();
    }



    public void clampToCircle() {
        float tankCenterX = position.x + halfWidth;
        float tankCenterY = position.y + halfHeight;

        // Вектор от центра мира к центру танка
        tmpVector.set(tankCenterX, tankCenterY).sub(WORLD_CENTER);

        float distance = tmpVector.len();
        float tankRadius = Math.max(halfWidth, halfHeight);
        float maxDistance = WORLD_RADIUS - tankRadius;

        if (distance > maxDistance) {
            tmpVector.nor().scl(maxDistance);
            tmpCenter.set(WORLD_CENTER).add(tmpVector);

            // Обновляем позицию (левый нижний угол)
            position.set(tmpCenter.x - halfWidth, tmpCenter.y - halfHeight);
        }
    }

    public void rotateTo(Vector2 to) {
        angle.set(to).sub(position.x + halfWidth, position.y + halfHeight);
    }

    public void render(Batch batch) {
        batch.draw(textureRegion, position.x, position.y, halfWidth, halfHeight, width, height, 1, 1, angle.angleDeg() - 90);
    }

    public void dispose() {
        texture.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getAngle() {
        return angle;
    }

    public void setAngle(Vector2 angle) {
        this.angle = angle;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
