import java.io.*;
import java.util.Scanner;

/*
 * TODO
 *  1) A brief summary of the program,
    2) author’s names and
    3) last date modified
 */

enum ProgramFunction {
    TRAIN,
    TEST,
    QUIT
}

public class proj1 {
    private final static Scanner globalScanner = new Scanner(System.in);

    // ProgramFunction: ALL
    private static Integer rowDimensionOfInput;
    private static Integer columnDimensionOfInput;
    private static Integer dimensionOfOutput;
    private static Integer numberOfTrainingPairs;

    // ProgramFunction: TRAIN
    private static String inputTrainingDataFileName;
    private static String outputTrainedWeightsFileName;
    private static Boolean randomTrainingWeights;
    private static Integer maximumNumberOfTrainingEpochs;
    private static Double learningRate;
    private static Double thresholdTheta;
    private static Double thresholdForWeightChanges;

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
            case TEST:
                didSucessfullySetup = getTestingInputsFromUser();
            default:
                didSucessfullySetup = false;
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
        }
        else if (programFunction == ProgramFunction.TEST) {
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
        rowDimensionOfInput = null;
        columnDimensionOfInput = null;
        dimensionOfOutput = null;
        numberOfTrainingPairs = null;

        // Variables for TRAIN
        inputTrainingDataFileName = null;
        outputTrainedWeightsFileName = null;
        randomTrainingWeights = null;
        maximumNumberOfTrainingEpochs = null;
        learningRate = null;
        thresholdTheta = null;
        thresholdForWeightChanges = null;

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
            int input = globalScanner.nextInt();
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
            System.out.println("Enter the training data file name:");
            inputTrainingDataFileName = globalScanner.nextLine();

            System.out.println(
                    "Enter 0 to initialize weights to 0, enter 1 to initialize weights to random values between -0.5 and 0.5:");
            int weightInput = globalScanner.nextInt();
            if (weightInput != 0 && weightInput != 1) {
                throw new IllegalArgumentException(
                        "The value of" + weightInput + "is not a valid input. Input value must be 0 or 1.");
            }
            randomTrainingWeights = weightInput == 0;

            System.out.println("Enter the maximum number of training epochs:");
            maximumNumberOfTrainingEpochs = globalScanner.nextInt();
            if (maximumNumberOfTrainingEpochs <= 0) {
                throw new IllegalArgumentException("The value of" + maximumNumberOfTrainingEpochs
                        + "is not a valid input. Input value must be an integer greater than 0.");
            }

            System.out.println("Enter a file name to save the trained weight values:");
            outputTrainedWeightsFileName = globalScanner.nextLine();

            System.out.println("Enter the learning rate alpha from 0 to 1 but not including 0:");
            learningRate = globalScanner.nextDouble();
            if (learningRate <= 0.0 || learningRate > 1.0) {
                throw new IllegalArgumentException("The value of" + learningRate
                        + "is not a valid input. Input value must be a double between (0, 1]");
            }

            System.out.println("Enter the threshold theta:");
            thresholdTheta = globalScanner.nextDouble();

            System.out.println("Enter the threshold to be used for measuring weight changes:");
            thresholdForWeightChanges = globalScanner.nextDouble();
            if (thresholdForWeightChanges <= 0.0) {
                throw new IllegalArgumentException("The value of" + thresholdForWeightChanges
                        + "is not a valid input. Input value must be a double greater than 0.");
            }

        } catch (Exception e) {
            System.out.println("Encountered an error when setting up user input variables:" + e.getMessage());
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

    private static boolean readInTrainingPairs() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputTrainingDataFileName));

            rowDimensionOfInput = Integer.parseInt(br.readLine());
            columnDimensionOfInput = Integer.parseInt(br.readLine());
            dimensionOfOutput = Integer.parseInt(br.readLine());
            numberOfTrainingPairs = Integer.parseInt(br.readLine());

            for (int i = 0; i < numberOfTrainingPairs; i++) {
                // Should be empty line before actual data
                br.readLine();
                TrainingPair tp = new TrainingPair(rowDimensionOfInput, columnDimensionOfInput, dimensionOfOutput);

                // Extract the input pattern for the training pair
                for (int inputRow = 0; inputRow < rowDimensionOfInput; inputRow++) {
                    String[] inputRowValues = br.readLine().split("\\s+");

                    for (int inputColumn = 0; inputColumn < columnDimensionOfInput; inputColumn++) {
                        int inputValue = Integer.parseInt(inputRowValues[inputColumn]);

                        if (inputValue != 1 && inputValue != -1) {
                            throw new IllegalArgumentException("Unable to parse dataset because it contained an illegal value of " + inputValue + " in the training set");
                        }

                        tp.trainingPattern[inputRow][inputColumn] = inputValue;
                    }
                }

                // Should be an empty line between input & output pair
                br.readLine();

                // Extract the output pattern for the training pair
                String[] outputValues = br.readLine().split("\\s+");
                for (int outputDimension = 0; outputDimension < dimensionOfOutput; outputDimension++) {
                    int outputValue = Integer.parseInt(outputValues[outputDimension]);

                    tp.outputClassification.outputNeuronValues[outputDimension] = outputValue;
                }

                // Skip over text line from input (e.g. A1, C3, etc...)
                br.readLine();
            }

            br.close();
        } catch (FileNotFoundException error) {
            System.out.println(
                    "Unable to parse dataset because the file for inputTrainingDataFileName cannot be opened (FileNotFoundException)");
            return false;
        } catch (IOException error) {
            System.out.println(
                    "Unable to parse dataset because the bufferReader wasn't able to read the file (IOException)");
            return false;
        } catch (NumberFormatException error) {
            System.out.println(
                    "Unable to parse dataset because the an entry in the dataset wasn't able to be converted a number (NumberFormatException)");
            return false;
        } catch (Exception e) {
            System.out.println("Unable to parse dataset");
            return false;
        }

        return true;
    }

    private static boolean trainNet() {

    }
    /*
     * Prompts the user and asks if they want to run the program again.
     * If they say y or yes, repeats program
     * If they say n or no, terminates program
     * Else reprompts the user until they give a valid answer
     */
    private static void askRunProgramAgain() {
        System.out.println("Do you want to run the program again (y for yes and n for no)?:");

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