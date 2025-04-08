package com.mygdx.geneticentities;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

// Class that manages the blocks that will be placed in the map

public class Block {
    float x1, y1, x2, y2;
    Color color;

    public Block(float x1, float y1, float x2, float y2, Color color) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.color = color;
    }
    public Block(float x1, float y1, float x2, float y2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.color = Color.WHITE;
    }

    public float getx1(){
        return x1/40;
    }
    public float getx2(){
        return x2/40;
    }
    public float gety1(){
        return y1/40;
    }
    public float gety2(){
        return y2/40;
    }

    // Used in the first instances of the code, no longer used
    public static float[] findClosestWalls(float x, float y, List<Block> blocks) {
        float up = Float.MAX_VALUE;
        float down = Float.MAX_VALUE;
        float left = Float.MAX_VALUE;
        float right = Float.MAX_VALUE;

        for (Block block : blocks) {
            // Vertical walls
            if (x >= block.x1 && x <= block.x2) {
                if (y < block.y1) {
                    float dist = block.y1 - y;
                    if (dist < up) up = dist;
                }
                if (y > block.y2) {
                    float dist = y - block.y2;
                    if (dist < down) down = dist;
                }
            }

            // Horizontal walls
            if (y >= block.y1 && y <= block.y2) {
                if (x < block.x1) {
                    float dist = block.x1 - x;
                    if (dist < right) right = dist;
                }
                if (x > block.x2) {
                    float dist = x - block.x2;
                    if (dist < left) left = dist;
                }
            }
        }

        return new float[]{
            up == Float.MAX_VALUE ? 1440.0f : up,    // Up
            down == Float.MAX_VALUE ? 1440.0f : down,  // Down
            left == Float.MAX_VALUE ? 1440.0f : left,  // Left
            right == Float.MAX_VALUE ? 1440.0f : right // Right
        };
    }


}
