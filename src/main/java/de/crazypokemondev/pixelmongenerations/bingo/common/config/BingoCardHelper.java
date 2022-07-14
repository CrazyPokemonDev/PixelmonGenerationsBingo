package de.crazypokemondev.pixelmongenerations.bingo.common.config;

import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.CatchPokemonTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.EmptyTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BingoCardHelper {

    public static Map<Integer, String> generateNewBingoCard() {
        Map<Integer, String> card = new HashMap<>();
        for (int i = 0; i < 25; i++) {
            card.put(i, getRandomTask().toString());
        }
        return card;
    }

    private static BingoTask getRandomTask() {
        List<String> types = new ArrayList<>();
        //TaskTypeSwitch
        if (PixelmonBingoConfig.Tasks.CatchPokemon.enabled)
            types.add(CatchPokemonTask.ID);
        if (types.size() < 1) {
            return new EmptyTask();
        }
        int index = ThreadLocalRandom.current().nextInt(types.size());
        //TaskTypeSwitch
        switch (types.get(index)) {
            case CatchPokemonTask.ID:
                return CatchPokemonTask.getRandomTask();
            default:
                return new EmptyTask();
        }
    }

    public static Map<Integer, BingoTask> deserializeTasks(Map<Integer, String> card) {
        Map<Integer, BingoTask> result = new HashMap<>();
        for (Map.Entry<Integer, String> entry : card.entrySet()) {
            result.put(entry.getKey(), BingoTask.fromString(entry.getValue()));
        }
        return result;
    }
}
