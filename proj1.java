import java.io.*;
import java.util.Scanner;

/*
* This program allows a user to input bipolar datasets and configure a perceptron neural net to recognize patterns within the data
* Authors: Jonathan Xakellis, Joe Cox, Augusto Escudero
* Last Modified: 3/4/2025
*/

public class proj1 {
    private final static Scanner globalScanner = new Scanner(System.in);

    private static String inputOutputDataFile;
    private static String resultsDestinationFile;

    public static void main(String[] args) {
        runProgram();
    }

    private static void runProgram() {
        inputOutputDataFile = null;
        resultsDestinationFile = null;

        ProgramFunction programFunction = getProgramFunctionFromUser();

        NeuralNet neuralNet = null;

        try {
            switch (programFunction) {
                case QUIT:
                    globalScanner.close();
                    System.exit(0);
                    break;
                case TRAIN:
                    neuralNet = getTrainingNeuralNet();
                    break;
                case TEST:
                    neuralNet = getTestingNeuralNet();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println(
                    "One or more of your inputs for the program function is invalid. Restarting the program..." + e.getMessage());
            askRunProgramAgain();
            return;
        }

        if (neuralNet == null) {
            System.out.println(
                    "One or more of your inputs for the program function is invalid. Restarting the program...");
            askRunProgramAgain();
            return;
        }

        if (programFunction == ProgramFunction.TRAIN) {
            InputOutputDataset trainingDataset = null;

            try {
                trainingDataset = new InputOutputDataset(inputOutputDataFile);
            } catch (Exception e) {
                System.out.println("Failed to read in training dataset for the program. Restarting the program..." + e.getMessage());
                askRunProgramAgain();
                return;
            }

            try {
                neuralNet.trainAndSaveNet(trainingDataset, resultsDestinationFile);
            } catch (Exception e) {
                System.out.println("Failed to train and save neural net. Restarting the program..." + e.getMessage());
                askRunProgramAgain();
                return;
            }
        } else if (programFunction == ProgramFunction.TEST) {
            InputOutputDataset testingDataset = null;

            try {
                testingDataset = new InputOutputDataset(inputOutputDataFile);
            } catch (Exception e) {
                System.out.println("Failed to read in testing dataset for the program. Restarting the program..." + e.getMessage());
                askRunProgramAgain();
                return;
            }

            try {
                neuralNet.testAndSaveNet(testingDataset, resultsDestinationFile);
            } catch (Exception e) {
                System.out.println("Failed to test neural net and results. Restarting the program..." + e.getMessage());
                askRunProgramAgain();
                return;
            }
        }

        askRunProgramAgain();
    }

    /*
     * Prompts the user and asks them how they'd like to utilize the program
     * Returns null if user input an invalid option
     */
    private static ProgramFunction getProgramFunctionFromUser() {
        System.out.println("Welcome to our first neural network – A Perceptron Net!");
        System.out.println("1) Enter 1 to train the net on a data file");
        System.out.println("2) Enter 2 to test the net on a data file");
        System.out.println("3) Enter 3 to quit");

        try {
            int input = Integer.parseInt(globalScanner.nextLine());
            if (input == 1) {
                return ProgramFunction.TRAIN;
            } else if (input == 2) {
                return ProgramFunction.TEST;
            } else if (input == 3) {
                return ProgramFunction.QUIT;
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    /*
     * Prompts the user and asks them how they'd like to set up the inputs for the
     * training net
     * Returns neural net from the input parameters or throws an error
     */
    private static NeuralNet getTrainingNeuralNet() throws IllegalArgumentException, Exception {
        System.out.println("Enter the training data file name (string):");
        inputOutputDataFile = globalScanner.nextLine();

        System.out.println(
                "Enter 0 to initialize weights to 0, enter 1 to initialize weights to random values between -0.5 and 0.5:");
        int weightInput = Integer.parseInt(globalScanner.nextLine());
        if (weightInput != 0 && weightInput != 1) {
            throw new IllegalArgumentException(
                    "The value of " + weightInput + " is not a valid input. Input value must be 0 or 1.");
        }
        boolean randomTrainingWeights = weightInput == 1;

        System.out.println("Enter the maximum number of training epochs [1, inf):");
        int maximumNumberOfEpochs = Integer.parseInt(globalScanner.nextLine());
        if (maximumNumberOfEpochs <= 0) {
            throw new IllegalArgumentException("The value of " + maximumNumberOfEpochs
                    + " is not a valid input. Input value must be an integer greater than 0.");
        }

        System.out.println("Enter a file name to save the trained weight values (string):");
        resultsDestinationFile = globalScanner.nextLine();

        System.out.println("Enter the learning rate alpha from 0 to 1 but not including 0 (0, 1]:");
        double learningRate = Double.parseDouble(globalScanner.nextLine());
        if (learningRate <= 0.0 || learningRate > 1.0) {
            throw new IllegalArgumentException("The value of " + learningRate
                    + " is not a valid input. Input value must be a double between (0, 1]");
        }

        System.out.println("Enter the threshold theta [0, inf):");
        double thresholdTheta = Double.parseDouble(globalScanner.nextLine());
        if (thresholdTheta < 0) {
            throw new IllegalArgumentException("The value of " + thresholdTheta
                    + " is not a valid input. Input value must be a double greater than or equal to 0.");
        }

        System.out.println("Enter the threshold to be used for measuring weight changes (0.0, inf]:");
        double thresholdForWeightChanges = Double.parseDouble(globalScanner.nextLine());
        if (thresholdForWeightChanges <= 0.0) {
            throw new IllegalArgumentException("The value of " + thresholdForWeightChanges
                    + " is not a valid input. Input value must be a double greater than 0.");
        }

        return new NeuralNet(randomTrainingWeights, maximumNumberOfEpochs, learningRate, thresholdTheta,
                thresholdForWeightChanges);
    }

    /*
     * Prompts the user and asks them how they'd like to set up the inputs for
     * testing a net
     * Returns NeuralNet from the specified file name, or throws an error
     */
    private static NeuralNet getTestingNeuralNet() throws IOException {
        System.out.println("Enter the trained net weight file name:");
        String trainedWeightsFileName = globalScanner.nextLine();

        System.out.println("Enter the testing/deploying dataset file name:");
        inputOutputDataFile = globalScanner.nextLine();

        System.out.println("Enter a file name to save the testing/deploying results:");
        resultsDestinationFile = globalScanner.nextLine();

        return new NeuralNet(trainedWeightsFileName);
    }

    /*
     * Prompts the user and asks if they want to run the program again.
     * If they say y or yes, repeats program
     * If they say n or no, terminates program
     * Else reprompts the user until they give a valid answer
     */
    private static void askRunProgramAgain() {
        System.out.println("\nDo you want to run the program again (y for yes and n for no)?:");

        String answer = globalScanner.nextLine().trim();

        if (answer.toLowerCase().equals("y") || answer.toLowerCase().equals("yes")) {
            runProgram();
            return;
        }

        if (answer.toLowerCase().equals("n") || answer.toLowerCase().equals("no")) {
            // We are terminating the program so we can finally close the globalScanner
            globalScanner.close();
            System.exit(0);
            return;
        }

        System.out.println("Input not recognized. Please input y or n");
        askRunProgramAgain();
    }
}