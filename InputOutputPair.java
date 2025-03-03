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
