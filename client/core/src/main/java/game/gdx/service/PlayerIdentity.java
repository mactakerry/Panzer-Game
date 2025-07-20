package game.gdx.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PlayerIdentity {
    private static PlayerIdentity instance;

    private String token;
    private long id;

    private PlayerIdentity() {}

    public static synchronized PlayerIdentity getInstance() {
        if (instance == null) {
            instance = new PlayerIdentity();
            instance.load();
        }
        return instance;
    }

    private void load() {
        Preferences prefs = Gdx.app.getPreferences("AuthData");
        this.token = prefs.getString("token");
        this.id = prefs.getLong("id");
    }

    public void save() {
        Preferences prefs = Gdx.app.getPreferences("AuthData");
        prefs.putString("token", token);
        prefs.putLong("id", id);
        prefs.flush();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}

