package game.gdx.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Starter extends Game {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;

    Texture textureMe;
    Texture textureEnemy;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        textureMe = new Texture("panzer_me.png");
        textureEnemy = new Texture("panzer_enemy.png");

        setScreen(new AuthScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();

        textureMe.dispose();
        textureEnemy.dispose();
    }
}
