/*
* This is a helper file to proj1.java. It is a class that allows a neural net to be initialized, trained, tested, saved, and loaded
* Authors: Jonathan Xakellis, Joe Cox, Augusto Escudero
* Last Modified: 3/4/2025
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class NeuralNet {
    private Boolean randomTrainingWeights;
    private Integer maximumNumberOfEpochs;
    private Double learningRate;
    private Double thresholdTheta;
    private Double thresholdForWeightChanges;

    // weights[input][output] from input neuron to output neuron
    private double[][] weights;
    // bias for each output neuron
    private double[] bias;
    // From InputOutputDataset that we train on
    private Integer inputRowDimensions;
    private Integer inputColumnDimensions;
    private Integer outputDimensions;
    private Integer numberOfPairs;
    private String[] outputIndexToClassifications;

    public NeuralNet(boolean randomTrainingWeights, int maximumNumberOfEpochs, double learningRate,
            double thresholdTheta, double thresholdForWeightChanges) {
        this.randomTrainingWeights = randomTrainingWeights;
        this.maximumNumberOfEpochs = maximumNumberOfEpochs;
        this.learningRate = learningRate;
        this.thresholdTheta = thresholdTheta;
        this.thresholdForWeightChanges = thresholdForWeightChanges;
    }

    public NeuralNet(String trainedWeightsFileName) throws IOException {
        loadTrainedNet(trainedWeightsFileName);
    }

    private boolean hasNeuralNetBeenSetup() {
        return weights != null
                && bias != null
                && inputRowDimensions != null
                && inputColumnDimensions != null
                && outputDimensions != null
                && numberOfPairs != null;
    }

    public double[][] getWeights() {
        return weights;
    }

    public double[] getBias() {
        return bias;
    }

    public Integer getTotalInputDimensions() {
        if (inputRowDimensions == null || inputColumnDimensions == null) {
            return null;
        }

        return inputRowDimensions * inputColumnDimensions;
    }

    public Integer getInputRowDimensions() {
        return inputRowDimensions;
    }

    public Integer getInputColumnDimensions() {
        return inputColumnDimensions;
    }

    public Integer getOutputDimensions() {
        return outputDimensions;
    }

    public Integer getNumberOfPairs() {
        return numberOfPairs;
    }

    public void trainAndSaveNet(InputOutputDataset dataset, String destination) throws IOException {
        weights = new double[dataset.getOutputDimensions()][dataset.getTotalInputDimensions()];
        bias = new double[dataset.getOutputDimensions()];

        inputRowDimensions = dataset.getInputRowDimensions();
        inputColumnDimensions = dataset.getInputColumnDimensions();
        outputDimensions = dataset.getInputColumnDimensions();
        numberOfPairs = dataset.getNumberOfPairs();
        outputIndexToClassifications = dataset.getOutputIndexToClassifications();

        // initialize weights and biases
        for (int od = 0; od < dataset.getOutputDimensions(); od++) {
            bias[od] = randomTrainingWeights ? (Math.random() - 0.5) : 0.0;

            for (int tid = 0; tid < dataset.getTotalInputDimensions(); tid++) {
                weights[od][tid] = randomTrainingWeights ? (Math.random() - 0.5) : 0.0;
            }
        }

        // train weights for specified number of epochs
        for (int epoch = 0; epoch < maximumNumberOfEpochs; epoch++) {
            double maxEpochWeightChange = 0.0;

            for (InputOutputPair iop : dataset.getPairs()) {
                int[] inputVector = iop.getFlattenedInputVector();
                int[] outputVector = iop.outputClassification.values;

                for (int od = 0; od < dataset.getOutputDimensions(); od++) {
                    // Net input = Y in = B(j) + W(j,i) * X(j,i)
                    double Yin = bias[od];

                    for (int tid = 0; tid < dataset.getTotalInputDimensions(); tid++) {
                        // add in each W(j,i) * X(j,i)
                        Yin += weights[od][tid] * inputVector[tid];
                    }

                    // can be -1, 0, or 1
                    int calculatedOutput = 0;
                    if (Yin > thresholdTheta) {
                        calculatedOutput = 1;
                    } else if (Yin < -thresholdTheta) {
                        calculatedOutput = -1;
                    }

                    int expectedOutput = outputVector[od];

                    // Expected didn't meet calcualted output, so update bias and weights
                    if (calculatedOutput != expectedOutput) {
                        // Update bias
                        double biasChange = learningRate * expectedOutput;
                        bias[od] += biasChange;

                        maxEpochWeightChange = Math.max(maxEpochWeightChange, Math.abs(biasChange));

                        for (int tid = 0; tid < dataset.getTotalInputDimensions(); tid++) {
                            // Uppdate weights
                            double weightChange = learningRate * expectedOutput * inputVector[tid];
                            weights[od][tid] += weightChange;

                            maxEpochWeightChange = Math.max(maxEpochWeightChange, Math.abs(weightChange));
                        }
                    }
                }
            }

            if (maxEpochWeightChange < thresholdForWeightChanges) {
                System.out.println("Network converged early at epoch " + epoch + " with a max change of "
                        + maxEpochWeightChange + " and threshold " + thresholdForWeightChanges);
                break;
            }
        }

        PrintWriter pw = new PrintWriter(new FileWriter(destination));

        pw.println(inputRowDimensions + " // row dimension of input pattern");
        pw.println(inputColumnDimensions + " // column dimension of input pattern");
        pw.println(outputDimensions + " // dimension of output pattern");
        pw.println(numberOfPairs + " // // number of testing pairs");

        pw.println(learningRate + " // learning rate");
        pw.println(thresholdTheta + " // threshold theta");
        pw.println(thresholdForWeightChanges + " // threshold for weight changes");

        pw.println();

        for (int od = 0; od < outputDimensions; od++) {
            for (int tid = 0; tid < getTotalInputDimensions(); tid++) {
                pw.print(weights[od][tid] + " ");
            }
            pw.println(" // weights for output " + od);
        }

        pw.println();

        for (int od = 0; od < outputDimensions; od++) {
            pw.println(bias[od] + " // bias for output " + od);
        }

        pw.println();

        for (int c = 0; c < outputDimensions; c++) {
            pw.print(dataset.getOutputIndexToClassifications()[c] + " ");
        }
        pw.println(" // classification readable letter for index");

        pw.close();

        System.out.println("\nSuccessfully trained and saved neural net to the file '" + destination + "'");
    }

    public void testAndSaveNet(InputOutputDataset dataset, String destination) throws IOException {
        if (hasNeuralNetBeenSetup() == false) {
            throw new IllegalStateException("The neural net has not been setup and cannot be tested.");
        }

        if (inputRowDimensions != dataset.getInputRowDimensions() 
        || inputColumnDimensions != dataset.getInputColumnDimensions()
        || outputDimensions != dataset.getOutputDimensions()) {
            throw new IllegalArgumentException("The dimensions of the trained neural net and dataset do not match.");
        }

        int correctCount = 0;
        String result = "";

        for (InputOutputPair iop : dataset.getPairs()) {
            int[] inputVector = iop.getFlattenedInputVector();
            OutputClassification actualClassification = iop.outputClassification;
            OutputClassification calculatedClassification = new OutputClassification(outputDimensions);

            for (int od = 0; od < outputDimensions; od++) {
                // Net input = Y in = B(j) + W(j,i) * X(j,i)
                double Yin = bias[od];

                for (int tid = 0; tid < dataset.getTotalInputDimensions(); tid++) {
                    // add in each W(j,i) * X(j,i)
                    Yin += weights[od][tid] * inputVector[tid];
                }

                int calculatedOutput = 0;
                if (Yin > thresholdTheta) {
                    calculatedOutput = 1;
                } else if (Yin < -thresholdTheta) {
                    calculatedOutput = -1;
                }

                calculatedClassification.values[od] = calculatedOutput;
            }

            String actual = actualClassification.getClassification(outputIndexToClassifications);
            String classified = calculatedClassification.getClassification(outputIndexToClassifications);

            if (actual.equals(classified)) {
                correctCount++;
            }

            result += "Actual Output:\n";
            result += actual + "\n";
            for (int od = 0; od < outputDimensions; od++) {
                result += iop.outputClassification.values[od] + " ";
            }
            result += "\n";

            result += "Classified Output:\n";
            result += classified + "\n";
            for (int od = 0; od < outputDimensions; od++) {
                result += calculatedClassification.values[od] + " ";
            }
            result += "\n\n";
        }

        double accuracy = 100.0 * correctCount / dataset.getNumberOfPairs();
        result += "Overall Classification Accuracy: " + accuracy + "%\n";

        PrintWriter pw = new PrintWriter(new FileWriter(destination));
        pw.print(result);

        pw.close();

        System.out.println("\nSuccessfully tested the neural net and saved the results to the file '" + destination + "'");
    }

    private String[] readLineEntries(BufferedReader br) throws IOException {
        return br.readLine().trim().split("\\s+");
    }

    private void loadTrainedNet(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        inputRowDimensions = Integer.parseInt(readLineEntries(br)[0]);
        inputColumnDimensions = Integer.parseInt(readLineEntries(br)[0]);
        outputDimensions = Integer.parseInt(readLineEntries(br)[0]);
        numberOfPairs = Integer.parseInt(readLineEntries(br)[0]);
        outputIndexToClassifications = new String[outputDimensions];

        learningRate = Double.parseDouble(readLineEntries(br)[0]);
        thresholdTheta = Double.parseDouble(readLineEntries(br)[0]);
        thresholdForWeightChanges = Double.parseDouble(readLineEntries(br)[0]);
        

        // Consume empty line
        br.readLine();

        weights = new double[outputDimensions][getTotalInputDimensions()];
        bias = new double[outputDimensions];

        // Read weights for each output neuron
        for (int od = 0; od < outputDimensions; od++) {
            String[] outputWeights = readLineEntries(br);

            for (int tid = 0; tid < getTotalInputDimensions(); tid++) {
                weights[od][tid] = Double.parseDouble(outputWeights[tid]);
            }
        }

        // Consume the empty line before biases
        br.readLine();

        // Read biases for each output neuron
        for (int od = 0; od < outputDimensions; od++) {
            bias[od] = Double.parseDouble(readLineEntries(br)[0]);
        }

        br.readLine();

        // Output index to classif array
        String[] classifications = readLineEntries(br);
        for (int oi = 0; oi < outputDimensions; oi++) {
            outputIndexToClassifications[oi] = classifications[oi];
        }

    }

}
