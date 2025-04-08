package com.mygdx.geneticentities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// Class that manages the main genetic algorithm within the robots. It uses the neural network functions

public class GeneticAlgorithm {
    private static final Random random = new Random();
    private static final int GRID_SIZE = 36;

    // Function that takes a generation and returns the new evolved generation
    public static List<Robot> evolve(List<Robot> oldGeneration) {
        oldGeneration.sort(Comparator.comparingDouble(Robot::fitness).reversed());

        List<Robot> newGeneration = new ArrayList<>();
        int eliteCount = (oldGeneration.size() * 3) / 10;

        for (int i = 0; i < eliteCount; i++) {
            oldGeneration.get(i).reset();
            newGeneration.add(oldGeneration.get(i));
        }

        double totalFitness = 0;
        for (Robot robot : oldGeneration) {
            totalFitness += robot.fitness();
        }

        for (int i = 0; i < oldGeneration.size() - eliteCount; i++) {
            Robot parent1 = selectParent(oldGeneration, totalFitness);
            Robot parent2 = selectParent(oldGeneration, totalFitness);
            Robot child = crossover(parent1, parent2);
            child.brain.mutate(8,2);
            child.reset();
            child.mutateColor();
            //child.freeze();
            newGeneration.add(child);
        }

        return newGeneration;
    }

    // Function to select a robot parent from the old generation
    private static Robot selectParent(List<Robot> oldGeneration, double totalFitness) {
        double randomValue = Math.random() * totalFitness;
        double cumulativeFitness = 0;

        for (Robot robot : oldGeneration) {
            cumulativeFitness += robot.fitness();
            if (cumulativeFitness >= randomValue) {
                return robot;
            }
        }
        return oldGeneration.get(oldGeneration.size() - 1);  // Fallback in case of rounding errors
    }

    // Function to make the crossover from two robots for the children robot
    private static Robot crossover(Robot parent1, Robot parent2) {
        NeuralNetwork childBrain = NeuralNetwork.crossover(parent1.getBrain(), parent2.getBrain());
        Robot child = new Robot(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE), parent1.color);
        child.setBrain(childBrain);
        return child;
    }
}
