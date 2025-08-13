package game.gdx.dto;

public class GetPanzerState {
    public long playerId;
    public float x;
    public float y;
    public float angle;

    @Override
    public String toString() {
        return "GetPanzerState{" +
            "playerId=" + playerId +
            ", x=" + x +
            ", y=" + y +
            ", angle=" + angle +
            '}';
    }
}
