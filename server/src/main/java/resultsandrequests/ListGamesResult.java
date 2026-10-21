package resultsandrequests;

import model.GameData;

import java.util.ArrayList;
import java.util.Map;

public record ListGamesResult(ArrayList<GameData> games) {
}
