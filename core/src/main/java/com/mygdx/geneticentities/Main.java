package com.mygdx.geneticentities;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Main class combining each class to make the whole algorithm

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private static final int GRID_SIZE = 36;
    private static final int CELL_SIZE = 40;
    private static final int SCREEN_WIDTH = GRID_SIZE * CELL_SIZE;
    private static final int SCREEN_HEIGHT = GRID_SIZE * CELL_SIZE;

    private List<Robot> robots;
    private List<Block> blocks;
    private Robot leader;
    private int generation = 1;
    private float generationTimer = 0;
    private static final float GENERATION_DURATION = 3;
    private Random random;

    // Initialize everything, the map, the robots, the blocks
    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        random = new Random();
        robots = initializeRobots();
        leader = new Robot();
        leader.randomize();
        for (Robot robot : robots) {
            robot.setLeader(leader);
        }
        blocks = initializeBlocks();

    }

    //Initialize the 50 robots in the right position
    private List<Robot> initializeRobots() {
        List<Robot> newRobots = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            //float x = random.nextInt(GRID_SIZE);
            //float y = random.nextInt(GRID_SIZE);
            float x = 18;
            float y = 33;
            Color color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
            newRobots.add(new Robot(x, y, color));
        }
        return newRobots;
    }

    // Initialize the blocks and their positions
    private List<Block> initializeBlocks() {
        List<Block> blocks = new ArrayList<>();

        // Borders
        blocks.add(new Block(0, 1400, 1440, 1440));
        blocks.add(new Block(0, 0, 1440, 40));
        blocks.add(new Block(0, 0, 40, 1440));
        blocks.add(new Block(1400, 0, 1440, 1440));

        //Left top part
        blocks.add(new Block(320, 1080, 600, 1360));
        blocks.add(new Block(80, 1080, 240, 1360));
        blocks.add(new Block(80, 760, 240, 960));

        blocks.add(new Block(320, 280, 360, 960));

        //Right top part
        blocks.add(new Block(760, 1080, 1120, 1360));
        blocks.add(new Block(1200, 1080, 1360, 1360));
        blocks.add(new Block(1200, 760, 1360, 960));

        blocks.add(new Block(1080, 280, 1120, 960));

        //Middle
        blocks.add(new Block(480, 760, 960, 920));
        blocks.add(new Block(680, 400, 760, 920));

        //left middle
        blocks.add(new Block(120, 540, 560, 600));

        //Right middle
        blocks.add(new Block(880, 540, 1280, 600));

        //Middle bottom
        blocks.add(new Block(480, 0, 960, 400));
        blocks.add(new Block(360, 0, 1080, 120));

        //Bottom left
        blocks.add(new Block(0, 160, 240, 400));

        //Botton right
        blocks.add(new Block(1200, 160, 1440, 400));


        return blocks;
    }

    //Render animation
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        float deltaTime = Gdx.graphics.getDeltaTime();
        generationTimer += deltaTime;
        int i = 1;
        Random random = new Random();
        leader.update(random.nextFloat(), random.nextFloat());
        boolean aux = false;

        for (Robot robot : robots) {
            robot.setBlocks(blocks);

            //float[] sensors = robot.findClosestWalls2();
            //System.out.println();
            //System.out.print("x: " + robot.x + " x1: " + blocks.get(0).x1);
            //if(robot.x < blocks.get(0).x1){
            //    System.out.println("Si");
            //}
            //else{
            //}
            //System.out.print("(" + sensors[0] + " " + sensors[1] + " " + sensors[2] + " " + sensors[3]);

            //System.out.println();
            //System.out.print("Robot number " + i);
            //robot.update(sensors[0], sensors[1], sensors[2], sensors[3]);
            robot.update2();
            robot.collition();
            i++;
        }


        if (generationTimer > GENERATION_DURATION) {
            evolve();
            generationTimer = 0;
            generation++;
        }

        drawFruit();
        drawRobots();
        drawBlocks();
    }

    //Evolution function, it uses the main evolve function from the genetic algorithm
    private void evolve() {
        robots = GeneticAlgorithm.evolve(robots);
        for (Robot robot : robots) {
            robot.setLeader(leader);
        }
        leader.set(GRID_SIZE / 2, GRID_SIZE / 2);
        //leader.brain.randomize(2, 2);
        System.out.println("Generation: " + generation);
    }

    // No longer used
    private void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (int x = 0; x <= GRID_SIZE; x++) {
            shapeRenderer.line(x * CELL_SIZE, 0, x * CELL_SIZE, SCREEN_HEIGHT);
        }
        for (int y = 0; y <= GRID_SIZE; y++) {
            shapeRenderer.line(0, y * CELL_SIZE, SCREEN_WIDTH, y * CELL_SIZE);
        }
        shapeRenderer.end();
    }

    // Function to draw each robot and their respective paths when rendering
    private void drawRobots() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw robots
        for (Robot r : robots) {
            shapeRenderer.setColor(r.color);
            shapeRenderer.circle(((r.x % GRID_SIZE) + 0.5f) * CELL_SIZE, ((r.y % GRID_SIZE) + 0.5f) * CELL_SIZE, CELL_SIZE / 4);

            // Draw path
            shapeRenderer.setColor(Color.GRAY);
            List<Vector2> pathHistory = r.getPathHistory();
            for (int i = 1; i < pathHistory.size(); i++) {
                Vector2 previousPos = pathHistory.get(i - 1);
                Vector2 currentPos = pathHistory.get(i);
                shapeRenderer.line(
                    (previousPos.x % GRID_SIZE) * CELL_SIZE + 0.5f * CELL_SIZE,
                    (previousPos.y % GRID_SIZE) * CELL_SIZE + 0.5f * CELL_SIZE,
                    (currentPos.x % GRID_SIZE) * CELL_SIZE + 0.5f * CELL_SIZE,
                    (currentPos.y % GRID_SIZE) * CELL_SIZE + 0.5f * CELL_SIZE
                );
            }
        }

        // Draw the leader robot
        // The leader robot was used by the first instance of the code, it does not have any function in the latest version
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(((leader.x % GRID_SIZE) + 0.5f) * CELL_SIZE, ((leader.y % GRID_SIZE) + 0.5f) * CELL_SIZE, CELL_SIZE / 4);

        shapeRenderer.end();
    }

    // Function to draw the blocks in the map when rendering
    private void drawBlocks() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Block b : blocks) {
            //System.out.print("(" + b.x1 + "," + b.y1 + ")");
            //System.out.print("(" + b.x2 + "," + b.y2 + ")");
            shapeRenderer.setColor(b.color);
            shapeRenderer.box(b.x1 + 20f, b.y1 + 20f, 0, b.x2 - b.x1, b.y2 - b.y1, 0);
        }
        //shapeRenderer.line(1200, 600, 1400, 600);
        shapeRenderer.end();
    }

    // Function to draw the fruit balls. They have no use but to visually locate the objective
    private void drawFruit(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        shapeRenderer.circle(120, 120, CELL_SIZE / 2);
        shapeRenderer.circle(1360, 120, CELL_SIZE / 2);

        shapeRenderer.end();
    }

    // To reset the render
    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
