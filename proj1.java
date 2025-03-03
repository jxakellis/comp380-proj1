import java.io.*;
import java.util.Scanner;

/*
* TODO
*  1) A brief summary of the program,
2) author’s names and
3) last date modified
*/

public class proj1 {
    private final static Scanner globalScanner = new Scanner(System.in);

    // ProgramFunction: ALL
    private static Integer inputRowDimensions;
    private static Integer inputColumnDimensions;
    private static Integer outputDimensions;
    private static Integer numberOfTrainingPairs;

    // ProgramFunction: TRAIN
    private static String inputTrainingDataFileName;
    private static String outputTrainedWeightsFileName;
    private static Boolean randomTrainingWeights;
    private static Integer maximumNumberOfTrainingEpochs;
    private static Double learningRate;
    private static Double thresholdTheta;
    private static Double thresholdForWeightChanges;
    private static TrainingPair[] trainingPairs;

    // ProgramFunction: TEST
    private static String inputTrainedWeightsFileName;
    private static String inputTestingDatasetFileName;
    private static String outputTestingResultsFileName;

    public static void main(String[] args) {
        runProgram();
    }

    private static void runProgram() {
        initializeVariables();

        ProgramFunction programFunction = getProgramFunctionFromUser();

        boolean didSucessfullySetup = false;

        switch (programFunction) {
            case QUIT:
                globalScanner.close();
                System.exit(0);
                break;
            case TRAIN:
                didSucessfullySetup = getTrainingInputsFromUser();
                break;
            case TEST:
                didSucessfullySetup = getTestingInputsFromUser();
                break;
            default:
                didSucessfullySetup = false;
                break;
        }

        if (didSucessfullySetup == false) {
            System.out.println(
                    "One or more of your inputs for the program function is invalid. Restarting the program...");
            askRunProgramAgain();
            return;
        }

        if (programFunction == ProgramFunction.TRAIN) {
            boolean didSuccessfullyReadIn = readInTrainingPairs();

            if (didSuccessfullyReadIn == false) {
                System.out.println("Failed to read in dataset(s) for the program. Restarting the program...");
                askRunProgramAgain();
                return;
            }

            trainNet();
        } else if (programFunction == ProgramFunction.TEST) {
            // TODO read in data for test function
        }

        askRunProgramAgain();
    }

    /*
     * Reinitializes all of the variables to null
     * This is important so when the program reinitializes, it has none of the data
     * from last iteration stored
     */
    private static void initializeVariables() {
        // Variables for All
        inputRowDimensions = null;
        inputColumnDimensions = null;
        outputDimensions = null;
        numberOfTrainingPairs = null;

        // Variables for TRAIN
        inputTrainingDataFileName = null;
        outputTrainedWeightsFileName = null;
        randomTrainingWeights = null;
        maximumNumberOfTrainingEpochs = null;
        learningRate = null;
        thresholdTheta = null;
        thresholdForWeightChanges = null;
        trainingPairs = null;

        // ProgramFunction: TEST
        inputTrainedWeightsFileName = null;
        inputTestingDatasetFileName = null;
        outputTestingResultsFileName = null;
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
     * Returns false if user input an invalid option
     */
    private static boolean getTrainingInputsFromUser() {
        try {
            System.out.println("Enter the training data file name (string):");
            inputTrainingDataFileName = globalScanner.nextLine();

            System.out.println(
                    "Enter 0 to initialize weights to 0, enter 1 to initialize weights to random values between -0.5 and 0.5:");
            int weightInput = Integer.parseInt(globalScanner.nextLine());
            if (weightInput != 0 && weightInput != 1) {
                throw new IllegalArgumentException(
                        "The value of " + weightInput + " is not a valid input. Input value must be 0 or 1.");
            }
            randomTrainingWeights = weightInput == 1;

            System.out.println("Enter the maximum number of training epochs [1, inf):");
            maximumNumberOfTrainingEpochs = Integer.parseInt(globalScanner.nextLine());
            if (maximumNumberOfTrainingEpochs <= 0) {
                throw new IllegalArgumentException("The value of " + maximumNumberOfTrainingEpochs
                        + " is not a valid input. Input value must be an integer greater than 0.");
            }

            System.out.println("Enter a file name to save the trained weight values (string):");
            outputTrainedWeightsFileName = globalScanner.nextLine();

            System.out.println("Enter the learning rate alpha from 0 to 1 but not including 0 (0, 1]:");
            learningRate = Double.parseDouble(globalScanner.nextLine());
            if (learningRate <= 0.0 || learningRate > 1.0) {
                throw new IllegalArgumentException("The value of " + learningRate
                        + " is not a valid input. Input value must be a double between (0, 1]");
            }

            System.out.println("Enter the threshold theta [0, inf):");
            thresholdTheta = Double.parseDouble(globalScanner.nextLine());
            if (thresholdTheta < 0) {
                throw new IllegalArgumentException("The value of " + thresholdTheta
                        + " is not a valid input. Input value must be a double greater than or equal to 0.");
            }

            System.out.println("Enter the threshold to be used for measuring weight changes (0.0, inf]:");
            thresholdForWeightChanges = Double.parseDouble(globalScanner.nextLine());
            if (thresholdForWeightChanges <= 0.0) {
                throw new IllegalArgumentException("The value of " + thresholdForWeightChanges
                        + " is not a valid input. Input value must be a double greater than 0.");
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /*
     * Prompts the user and asks them how they'd like to set up the inputs for
     * testing a net
     * Returns false if user input an invalid option
     */
    private static boolean getTestingInputsFromUser() {
        try {
            System.out.println("Enter the trained net weight file name:");
            inputTrainedWeightsFileName = globalScanner.nextLine();

            System.out.println("Enter the testing/deploying dataset file name:");
            inputTestingDatasetFileName = globalScanner.nextLine();

            System.out.println("Enter a file name to save the testing/deploying results:");
            outputTestingResultsFileName = globalScanner.nextLine();
        } catch (Exception e) {
            System.out.println("Encountered an error when setting up user input variables:" + e.getMessage());
            return false;
        }

        return true;
    }

    private static String readFirstLineEntry(BufferedReader br) throws IOException {
        return br.readLine().trim().split("\\s+")[0];
    }

    private static boolean readInTrainingPairs() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputTrainingDataFileName));

            inputRowDimensions = Integer.parseInt(readFirstLineEntry(br));
            inputColumnDimensions = Integer.parseInt(readFirstLineEntry(br));
            outputDimensions = Integer.parseInt(readFirstLineEntry(br));
            numberOfTrainingPairs = Integer.parseInt(readFirstLineEntry(br));

            trainingPairs = new TrainingPair[numberOfTrainingPairs];

            for (int trainingPairIndex = 0; trainingPairIndex < numberOfTrainingPairs; trainingPairIndex++) {
                // Consume empty line before actual data
                br.readLine();

                TrainingPair tp = new TrainingPair(inputRowDimensions, inputColumnDimensions, outputDimensions);
                trainingPairs[trainingPairIndex] = tp;

                // Extract the input pattern for the training pair
                for (int inputRow = 0; inputRow < inputRowDimensions; inputRow++) {
                    String[] inputRowValues = br.readLine().trim().split("\\s+");

                    for (int inputColumn = 0; inputColumn < inputColumnDimensions; inputColumn++) {
                        int inputValue = Integer.parseInt(inputRowValues[inputColumn]);
                        
                        if (inputValue != 1 && inputValue != -1) {
                            throw new IllegalArgumentException(
                                    "Unable to parse training dataset because it contained an illegal value of " + inputValue
                                            + " in the training set");
                        }

                        tp.inputVector[inputRow][inputColumn] = inputValue;
                    }
                }

                // Consume empty line between input & output pair
                br.readLine();

                // Extract the output pattern for the training pair
                String[] outputValues = br.readLine().trim().split("\\s+");

                for (int outputDimension = 0; outputDimension < outputDimensions; outputDimension++) {
                    int outputValue = Integer.parseInt(outputValues[outputDimension]);
                    
                    tp.outputClassification.values[outputDimension] = outputValue;
                }

                // Consume text line after output (e.g. A1, C3)
                br.readLine();
            }

            br.close();
        } catch (FileNotFoundException error) {
            System.out.println(
                    "Unable to parse training dataset because the file for inputTrainingDataFileName cannot be opened (FileNotFoundException)");
            return false;
        } catch (IOException error) {
            System.out.println(
                    "Unable to parse training dataset because the bufferReader wasn't able to read the file (IOException)");
            return false;
        } catch (NumberFormatException error) {
            System.out.println(
                    "Unable to parse training dataset because the an entry in the dataset wasn't able to be converted a number (NumberFormatException)");
            return false;
        } catch (Exception e) {
            System.out.println("Unable to parse training dataset");
            return false;
        }

        return true;
    }

    private static void trainNet() {
        int totalInputDimensions = inputRowDimensions * inputColumnDimensions;
        // weights[input][output] from input i to output neuron j
        double[][] weights = new double[outputDimensions][totalInputDimensions];
        double[] bias = new double[outputDimensions];

        // initialize weights and biases
        for (int outputDimension = 0; outputDimension < outputDimensions; outputDimension++) {
            bias[outputDimension] = randomTrainingWeights ? (Math.random() - 0.5) : 0.0;

            for (int totalInputDimension = 0; totalInputDimension < totalInputDimensions; totalInputDimension++) {
                weights[outputDimension][totalInputDimension] = randomTrainingWeights ? (Math.random() - 0.5) : 0.0;
            }
        }

        // train weights for specified number of epochs
        for (int epoch = 0; epoch < maximumNumberOfTrainingEpochs; epoch++) {
            double maxEpochWeightChange = 0.0;

            for (TrainingPair tp : trainingPairs) {
                int[] inputVector = tp.getFlattenedInputVector();
                int[] outputVector = tp.outputClassification.values;

                System.out.println("New training pair");
                for (int outputDimension = 0; outputDimension < outputDimensions; outputDimension++) {
                    // Net input = Y in = B(j) + W(j,i) * X(j,i)
                    double Yin = bias[outputDimension];

                    for (int totalInputDimension = 0; totalInputDimension < totalInputDimensions; totalInputDimension++) {
                        // add in each W(j,i) * X(j,i)
                        Yin += weights[outputDimension][totalInputDimension] * inputVector[totalInputDimension];
                    }

                    // can be -1, 0, or 1
                    int calculatedOutput = 0;
                    if (Yin > thresholdTheta) {
                        calculatedOutput = 1;
                    } else if (Yin < -thresholdTheta) {
                        calculatedOutput = -1;
                    }

                    int expectedOutput = outputVector[outputDimension];

                    System.out.println("\tExpected " + expectedOutput + " got " + calculatedOutput);

                    // Expected didn't meet calcualted output, so update bias and weights
                    if (calculatedOutput != expectedOutput) {
                        // Update bias
                        double biasChange = learningRate * expectedOutput;
                        bias[outputDimension] += biasChange;

                        maxEpochWeightChange = Math.max(maxEpochWeightChange, Math.abs(biasChange));

                        for (int totalInputDimension = 0; totalInputDimension < totalInputDimensions; totalInputDimension++) {
                            // Uppdate weights
                            double weightChange = learningRate * expectedOutput * inputVector[totalInputDimension];
                            weights[outputDimension][totalInputDimension] += weightChange;

                            maxEpochWeightChange = Math.max(maxEpochWeightChange, Math.abs(weightChange));
                        }
                    }
                }
            }

            if (maxEpochWeightChange < thresholdForWeightChanges) {
                System.out.println("Network converged early at epoch " + epoch + " with a max change of " + maxEpochWeightChange + " and threshold " + thresholdForWeightChanges);
                break;
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(outputTrainedWeightsFileName))) {

            pw.println(inputRowDimensions + " // row dimension of input pattern");
            pw.println(inputColumnDimensions + " // column dimension of input pattern");
            pw.println(outputDimensions + " // dimension of output pattern");
            pw.println(numberOfTrainingPairs + " // // number of testing pairs");

            pw.println(learningRate + " // learning rate");
            pw.println(thresholdTheta + " // threshold theta");
            pw.println(thresholdForWeightChanges + " // threshold for weight changes");

            pw.println();

            for (int outputDimension = 0; outputDimension < outputDimensions; outputDimension++) {
                for (int totalInputDimension = 0; totalInputDimension < totalInputDimensions; totalInputDimension++) {
                    pw.print(weights[outputDimension][totalInputDimension] + " ");
                }
                pw.println(" // weights for output " + outputDimension);
            }

            pw.println();

            for (int outputDimension = 0; outputDimension < outputDimensions; outputDimension++) {
                pw.println(bias[outputDimension] + " // bias for output " + outputDimension);
            }

            pw.close();
        } catch (IOException error) {
            System.out.println(
                    "Unable to write trained weights to file because the fileWriter wasn't able to write the file (IOException)");
        } catch (Exception e) {
            System.out.println("Unable to write trained weights to file");
        }
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

    /*
     * For training, the system should use the input and output data dimensions
     * specified in its
     * training set (see the attached sample set and page 72 of your textbook). When
     * creating
     * additional datasets for testing and experiments, they MUST adhere to the
     * format
     * specified in the attached sample datasets. Additionally, the system MUST be
     * able to
     * handle pattern classification problems with any data dimensions, provided the
     * training
     * and testing datasets have matching dimensions.
     * For application, you will experiment with the system by deploying it on
     * multiple datasets
     * with comparable dimensions (see page 75 of the textbook) and save the
     * classification
     * results in files with the following format:
     * Actual Output:
     * A
     * 1 -1 -1 -1 -1 -1 -1
     * Classified Output:
     * A
     * 1 -1 -1 -1 -1 -1 -1
     * The output files should also include the overall classification accuracy for
     * the testing set.
     * Notes:
     * 1) If a network classified output lacks a single +1 or contains multiple +
     * values, the
     * corresponding testing letter should be marked as “undecided”.
     * 2) The system should use a user-specified threshold to determine whether
     * weight
     * changes occur during training.
     * 3) To facilitate a meaningful testing or application, the system should save
     * some net
     * training parameter values (e.g., the theta) in its associated weight file.
     * Task 2: Experimental Analysis
     * You will conduct various experiments and document your findings in detail in
     * your
     * project writeup:
     * 1 System Initial Testing
     * Train your system using a training data set (e.g., the provided sample
     * training
     * set) and test it on the same dataset.
     * Evaluate whether the system correctly classifies all training samples.
     * 2 Effect of Learning Rate and Threshold on Convergence
     * Use a fixed testing set and evaluate the system’s convergence speed (i.e.,
     * the
     * numbers of epochs required to converge)
     * Test different values for the learning rate alpha (0.25, 0.50, 0.75 and 1.00)
     * Test different values for the threshold theta (0.00, 0.25, 0.50, 1.00, 5.00,
     * 10.00,
     * and 50.00).
     * 
     */
}