package com.mygdx.geneticentities;

import java.util.Random;

// Class that manages the small neural network from each robot, which is the State to optimize

public class NeuralNetwork {
    private float[][] weights;
    private static final Random random = new Random();

    // Random neural network initialization
    public NeuralNetwork(int inputSize, int outputSize) {
        weights = new float[inputSize][outputSize];
        //Random random = new Random();

        // Initialize random weights (-1 to 1)
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weights[i][j] = random.nextFloat() * 2 - 1;
            }
        }
    }

    // No longer used
    public void randomize(int inputSize, int outputSize){
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weights[i][j] = random.nextFloat() * 2 - 1;
            }
        }
    }

    // Function to make the linear combinations of sensors with weights for the outputs
    public float[] feedForward(float[] inputs) {
        float[] outputs = new float[weights[0].length];

        for (int j = 0; j < outputs.length; j++) {
            outputs[j] = 0;
            for (int i = 0; i < inputs.length; i++) {
                // System.out.print(" j: " + j + " i: " + i + "weights i: " + weights.length + " j: " + weights[i].length);
                outputs[j] += inputs[i] * weights[i][j];
            }
            outputs[j] = sigmoid(outputs[j]);  // Activation function
        }
        return outputs;
    }

    //Activation function, well use the sigmoid
    private float sigmoid(float x) {
        return 1 / (1 + (float) Math.exp(-x));
    }

    //Crossover function to combinate the neural networks from two parents for a children robot
    public static NeuralNetwork crossover(NeuralNetwork p1, NeuralNetwork p2) {
        NeuralNetwork child = new NeuralNetwork(8, 2);
        for (int i = 0; i < child.weights.length; i++) {
            for(int j = 0; j < child.weights[0].length; j++){
                child.weights[i][j] = (random.nextBoolean()) ? p1.weights[i][j] : p2.weights[i][j];
            }

        }
        return child;
    }

    // Function that mutates each robot weight with 5% probability
    public void mutate(int inputSize, int outputSize){
        int a;
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                a = random.nextInt(100);
                if(a <= 5){
                    weights[i][j] = random.nextFloat() * 2 - 1;
                }
            }
        }
    }
}
