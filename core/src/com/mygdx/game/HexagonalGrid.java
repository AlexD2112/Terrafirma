package com.mygdx.game;

import java.util.ArrayList;

public class HexagonalGrid {
    private ArrayList<HexagonTile> tiles = new ArrayList<>();

    public HexagonalGrid(int width, int height) {
        for (int q = 0; q < width; q++) {
            int q_offset = (int)Math.floor(q / 2); // Offset for odd columns
            for (int r = 0; r < height - q_offset; r++) {
                tiles.add(new HexagonTile(q, r + q_offset));
            }
        }
    }

    public ArrayList<HexagonTile> getTiles() {
        return tiles;
    }
}
