public class OutputClassification {
    public int[] values;

    public OutputClassification(int outputDimensions) {
        values = new int[outputDimensions];
    }

    public String getClassification() {
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

        // Convert index to character, e.g. index 0 == a, 1 == b, 2 == c, etc...
        classifiation = (char) ('a' + indexOfActivatedOutput) + "";

        return classifiation;
    }
}
