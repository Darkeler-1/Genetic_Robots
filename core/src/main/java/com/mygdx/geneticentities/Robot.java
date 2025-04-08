package com.mygdx.geneticentities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Class that manages the robots, with their respective parameters

public class Robot {
    private static final int GRID_SIZE = 36;
    private int targetX = 4; //No longer used
    private int targetY = 7; // No longer used
    public float x;
    public float y;
    public float speedX, speedY;
    public Color color;
    public NeuralNetwork brain;
    public float initialX;
    public float initialY;
    public double distance;
    public Robot leader; // No longer used
    public boolean freezed;
    public List<Block> blocks;
    public float orientation;
    private List<Vector2> pathHistory;

    public Robot() {
        Random random = new Random();
        this.x = random.nextInt(GRID_SIZE);
        this.y = random.nextInt(GRID_SIZE);
        this.initialX = this.x;
        this.initialY = this.y;
        this.speedX = 0;
        this.speedY = 0;
        this.brain = new NeuralNetwork(8, 2);
        this.distance = 0;
        this.color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
        this.freezed = false;
        this.orientation = 0;
        this.pathHistory = new ArrayList<>();
        this.pathHistory.add(new Vector2(x, y));
    }

    public Robot(float x, float y, Color color) {
        this.x = x;
        this.y = y;
        this.initialY = this.y;
        this.initialX = this.x;
        this.speedX = 0;
        this.speedY = 0;
        this.color = color;
        this.brain = new NeuralNetwork(8, 2);
        this.distance = 0;
        this.freezed = false;
        this.orientation = 0;
        this.pathHistory = new ArrayList<>();
        this.pathHistory.add(new Vector2(x, y));
    }

    // To make the robot start in a random position. Deprecated function
    public void randomize(){
        Random random = new Random();
        this.x = random.nextInt(GRID_SIZE);
        this.y = random.nextInt(GRID_SIZE);
        this.distance = 0;
        this.freezed = false;
    }

    // Function to trace the path traveled by the robot
    public List<Vector2> getPathHistory() {
        return pathHistory;
    }

    // Each robot has saved a list of the map blocks
    public void setBlocks(List<Block> blocks){
        this.blocks = blocks;
    }

    // to change the robot position
    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    // To freeze movement by the robots
    public void freeze(){
        this.freezed = true;
    }

    public boolean isFreezed(){
        return freezed;
    }

    // To check if a robot has collided, and freeze it
    public boolean collition() {
        for (Block block : blocks) {
            if (x >= (block.x1/40 - 0.5f) && x <= (block.x2/40 + 0.5f) &&
                y >= (block.y1/40 - 0.5f) && y <= (block.y2)/40 + 0.5f) {
                this.freezed = true;
                return true; // Robot is inside a block
            }
        }
        return false; // No collition
    }

    // Deprecated function
    public void setLeader(Robot leader){
        this.leader = leader;
    }

    // No longer used
    public static Robot leader(){
        Robot leader = new Robot();
        return leader;
    }

    // Function for the sensors
    public float[] senseBlocks() {
        float[] detections = new float[8];
        float sectorAngle = (float) (Math.PI / 4); // 45 degrees per section
        float detectionRadius = 2.5f;

        for (Block block : blocks) {
            float[][] blockCorners = {
                {block.getx1(), block.gety1()},
                {block.getx1(), block.gety2()},
                {block.getx2(), block.gety1()},
                {block.getx2(), block.gety2()}
            };

            for (float[] corner : blockCorners) {
                float bx = corner[0], by = corner[1];
                float dx = bx - x;
                float dy = by - y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance > detectionRadius) continue;

                float angleToCorner = (float) Math.atan2(dy, dx) - orientation;

                // Normalize to between 0 and 2*PI
                while (angleToCorner < 0) angleToCorner += 2 * Math.PI;
                while (angleToCorner >= 2 * Math.PI) angleToCorner -= 2 * Math.PI;

                int sectorIndex = (int) (angleToCorner / sectorAngle);
                detections[sectorIndex] = 1; // Section has a block
            }
        }

        return detections;
    }

    // No longer used
    public float[] findClosestWalls2(){
        float up = 36-this.y;
        float down = this.y;
        float left = this.x;
        float right = 36-this.x;
        float[] output = new float[4];

        for(Block block : blocks){
            if(x > block.getx1() || x < block.getx2()){
                if(y < block.gety1()){
                    up = Math.min(up, block.gety1() - y);
                }
                if(y > block.gety2()){
                    down = Math.min(down, y - block.gety2());
                }
            }
            if (y > block.gety1() && y <= block.gety2()) {
                if (x < block.getx1()) {
                    float dist = block.getx1() - x;
                    if (dist < right) right = dist;
                }
                if (x > block.getx2()) {
                    float dist = x - block.getx2();
                    if (dist < left) left = dist;
                }
            }
        }

        output[0] = up;
        output[1] = down;
        output[2] = left;
        output[3] = right;

        return output;

    }

    // No longer used
    public float[] findClosestWalls() {
        float up = Float.MAX_VALUE;
        float down = Float.MAX_VALUE;
        float left = Float.MAX_VALUE;
        float right = Float.MAX_VALUE;
        float x = this.x * 40;
        float y = this.y * 40;

        for (Block block : blocks) {
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

    // Resets the robot to initial position and stats
    public void reset(){
        this.x = 18;
        this.y = 34;
        this.freezed = false;
        this.orientation = 0;
        this.pathHistory = new ArrayList<>();
        this.pathHistory.add(new Vector2(x, y));
    }

    // No longer used
    public void update(float sensor1, float sensor2, float sensor3, float sensor4){
        float[] inputs = {sensor1/40, sensor2/40, sensor3/40, sensor4/40};
        float[] outputs = brain.feedForward(inputs);

        if(!isFreezed()) {
            speedX = outputs[0] * 2 - 1;
            speedY = outputs[1] * 2 - 1;
        }
        else{
            speedX = 0;
            speedY = 0;
        }

        x += (speedX)*0.30;
        y += speedY*0.30;

        if(x<0 || x>36){
            this.freeze();
        }
        if(y<0 || y>36){
            this.freeze();
        }

        double distance = Math.sqrt((speedX*speedX*.3*.3) + (speedY*speedY*.3*.3));
        this.distance += distance;
    }

    // Function that takes the output from the sensors and updates the robot position and angle
    public void update2() {
        float[] detections = senseBlocks();
        float[] outputs = brain.feedForward(detections);

        float turn = (outputs[0] * 2 - 1) * (float) Math.PI / 4;
        float forwardSpeed = outputs[1] * 2 - 1; // Normalize

        // If there is nothing around, minimal movement
        if (Math.abs(forwardSpeed) < 0.5f) {
            forwardSpeed = 0.5f;
        }


        if(isFreezed()){
            forwardSpeed = 0;
        }

        orientation += turn;

        while (orientation < 0) orientation += 2 * Math.PI;
        while (orientation >= 2 * Math.PI) orientation -= 2 * Math.PI;

        x += Math.sin(orientation) * forwardSpeed * 0.7;
        y -= Math.cos(orientation) * forwardSpeed * 0.7;

        //x += Math.cos(orientation) * 0.5;
        //y += Math.sin(orientation) * 0.5;

        collition();

        if(x<0 || x>36){
            this.freeze();
        }
        if(y<0 || y>36){
            this.freeze();
        }

        pathHistory.add(new Vector2(x, y));

    }

    // No longer used
    public void update(float sensor1, float sensor2) {
        float[] inputs = {sensor1, sensor2};  // Sensor readings
        float[] outputs = brain.feedForward(inputs);  // Get motor outputs

        speedX = outputs[0] * 2 - 1;  // Map output (-1 to 1)
        speedY = outputs[1] * 2 - 1;

        x += (speedX)*0.10;
        y += speedY*0.10;

        double distance = Math.sqrt((speedX*speedX*.3*.3) + (speedY*speedY*.3*.3));

    }

    // For easy crossover
    public NeuralNetwork getBrain(){
        return this.brain;
    }

    // For easy creation of robot children
    public void setBrain(NeuralNetwork newBrain) {
        this.brain = newBrain;
    }

    // Deprecated fitness
    public double fitness5(){
        return distance;
    }

    // Deprecated fitness
    public double fitness6(){
        if(x<7 && y<7){
            return 1;
        }
        else{
            return 0;
        }
    }

    // Actual fitness function
    public double fitness(){
        double a = 0;
        double res;

        if(!isFreezed()){
            a = 100;
        }
        //res = Math.sqrt(((this.x-18)*(this.x-18)) + ((this.y-36)*(this.y-36)));
        //return Math.sqrt((this.x*this.x) + (this.y*this.y));
        //res = 36-this.y;
        //res = Math.sqrt((18-this.x)*(18-this.x) + (36-this.y)*(36-this.y));
        res = (18-this.x)*(18-this.x) + (36-this.y)*(36-this.y);

        if(res<5){
            return 0;
        }
        //res = res*(36-this.y);
        //res = res*res;
        return res + a;
        //return 18 - Math.abs(x-18);
    }

    // Deprecated function
    public float fitness3(){
        if(this.x > GRID_SIZE/2){
            return GRID_SIZE/2-this.x;
        }
        else{
            return this.x;
        }
    }

    // Deprecated function
    public float fitness2(){
        if(this.x <= GRID_SIZE/2){
            return this.x/(GRID_SIZE/2);
        }
        else{
            return 1;
        }
    }

    // Just to avoid color convergence
    public void mutateColor(){
        Random random = new Random();
        int a = random.nextInt(100);
        if(a<20)
            this.color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
    }

    public float getX() { return x; }

    public float getY() { return y; }

}
