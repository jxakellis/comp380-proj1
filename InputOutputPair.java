/*
* This is a helper file to proj1.java. It is a class that allows a given input with associated output vectors to be mapped together
* Authors: Jonathan Xakellis, Joe Cox, Augusto Escudero
* Last Modified: 3/4/2025
*/

public class InputOutputPair {
    int[][] inputVector;
    OutputClassification outputClassification;

    public InputOutputPair(int inputRowDimensions, int inputColumnDimensions, int outputDimensions) {
        inputVector = new int[inputRowDimensions][inputColumnDimensions];
        outputClassification = new OutputClassification(outputDimensions);
    }

    public int[] getFlattenedInputVector() {
        if (inputVector.length == 0 || inputVector[0].length == 0) {
            return new int[0];
        }

        int totalInputs = inputVector.length * inputVector[0].length;
        int[] flattened = new int[totalInputs];
        int index = 0;
    
        for (int row = 0; row < inputVector.length; row++) {
            for (int column = 0; column < inputVector[row].length; column++) {
                flattened[index] = inputVector[row][column];
                index++;
            }
        }
    
        return flattened;
    }
}
