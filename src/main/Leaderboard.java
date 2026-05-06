package main;

import java.io.*;
import java.util.*;

public class Leaderboard {
    private static final String FILE_PATH = "leaderboard.txt";
    private static final int MAX_ENTRIES = 10;

    public List<String[]> getTopEntries() {
        Map<String, Integer> data = readAll();
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(data.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < Math.min(MAX_ENTRIES, sorted.size()); i++) {
            result.add(new String[]{sorted.get(i).getKey(),
                    String.valueOf(sorted.get(i).getValue())});
        }
        return result;
    }

    public void recordWin(String name) {
        Map<String, Integer> data = readAll();
        data.put(name, data.getOrDefault(name, 0) + 1);
        writeAll(data);
    }

    private Map<String, Integer> readAll() {
        Map<String, Integer> data = new LinkedHashMap<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return data;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2)
                    data.put(parts[0], Integer.parseInt(parts[1].trim()));
            }
        } catch (IOException e) { e.printStackTrace(); }
        return data;
    }

    private void writeAll(Map<String, Integer> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}