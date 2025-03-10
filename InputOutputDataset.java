/*
* This is a helper file to proj1.java. It is class that helps read in and manipulate datasets
* Authors: Jonathan Xakellis, Joe Cox, Augusto Escudero
* Last Modified: 3/4/2025
*/

import java.io.*;
public class InputOutputDataset {
    private Integer inputRowDimensions = null;
    private Integer inputColumnDimensions = null;
    private Integer outputDimensions = null;
    private Integer numberOfPairs = null;
    private InputOutputPair[] pairs = null;
    private String[] outputIndexToClassifications = null;

    public InputOutputDataset(String fileName) throws FileNotFoundException, IOException, NumberFormatException {
        readInDataPairs(fileName);
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

    public InputOutputPair[] getPairs() {
        return pairs;
    }

    public String[] getOutputIndexToClassifications() {
        return outputIndexToClassifications;
    }

    private String[] readLineEntries(BufferedReader br) throws IOException {
        return br.readLine().trim().split("\\s+");
    }

    private void readInDataPairs(String fileName) throws FileNotFoundException, IOException, NumberFormatException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        inputRowDimensions = Integer.parseInt(readLineEntries(br)[0]);
        inputColumnDimensions = Integer.parseInt(readLineEntries(br)[0]);
        outputDimensions = Integer.parseInt(readLineEntries(br)[0]);
        numberOfPairs = Integer.parseInt(readLineEntries(br)[0]);
        outputIndexToClassifications = new String[outputDimensions];

        pairs = new InputOutputPair[numberOfPairs];

        for (int pi = 0; pi < numberOfPairs; pi++) {
            // Consume empty line before actual data
            br.readLine();

            InputOutputPair pair = new InputOutputPair(inputRowDimensions, inputColumnDimensions, outputDimensions);
            pairs[pi] = pair;

            // Extract the input pattern for the training pair
            for (int ir = 0; ir < inputRowDimensions; ir++) {
                String[] inputRowValues = readLineEntries(br);

                for (int ic = 0; ic < inputColumnDimensions; ic++) {
                    int inputValue = Integer.parseInt(inputRowValues[ic]);

                    if (inputValue != 1 && inputValue != -1) {
                        throw new IllegalArgumentException(
                                "Unable to parse training dataset because it contained an illegal value of "
                                        + inputValue
                                        + " in the training set");
                    }

                    pair.inputVector[ir][ic] = inputValue;
                }
            }

            // Consume empty line between input & output pair
            br.readLine();

            // Extract the output pattern for the training pair
            String[] outputValues = readLineEntries(br);
            String classificationLetter = readLineEntries(br)[0].replaceAll("\\d","");
            Integer classificationIndex = null;

            for (int od = 0; od < outputDimensions; od++) {
                int outputValue = Integer.parseInt(outputValues[od]);

                if (outputValue == 1 && classificationIndex == null) {
                    classificationIndex = od;
                }

                pair.outputClassification.values[od] = outputValue;
            }

            outputIndexToClassifications[classificationIndex] = classificationLetter;

            // Consume text line after output (e.g. A1, C3)
        }

        br.close();

    }
}
