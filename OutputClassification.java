/*
* This is a helper file to proj1.java. It is a class to help track and classify output results
* Authors: Jonathan Xakellis, Joe Cox, Augusto Escudero
* Last Modified: 3/4/2025
*/

public class OutputClassification {
    public int[] values;

    public OutputClassification(int outputDimensions) {
        values = new int[outputDimensions];
    }

    public String getClassification(String[] outputIndexToClassification) {
        String classifiation = "undecided";
        Integer indexOfActivatedOutput = null;

        for (int i = 0; i < values.length; i++) {
            int value = values[i];

            // Each output neuron should be positive or negative for activation
            if (value != -1 && value != 1 && value != 0) {
                return classifiation;
            }

            if (value == 1) {
                // no other neuron has been activated before this one
                if (indexOfActivatedOutput == null) {
                    indexOfActivatedOutput = i;
                }
                else {
                    // multiple neurons activated, so undecided
                    return classifiation;
                }
            }
        }

        if (indexOfActivatedOutput == null) {
            return classifiation;
        }

        return outputIndexToClassification[indexOfActivatedOutput];
    }
}
