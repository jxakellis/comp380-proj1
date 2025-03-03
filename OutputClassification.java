public class OutputClassification {
    public int[] outputNeuronValues;

    public OutputClassification(int outputDimension) {
        outputNeuronValues = new int[outputDimension];
    }

    public String getClassification() {
        String classifiation = "undecided";
        Integer indexOfActivatedOutputNeuron = null;

        for (int i = 0; i < outputNeuronValues.length; i++) {
            int outputNeuronValue = outputNeuronValues[i];

            // Each output neuron should be positive or negative for activation
            if (outputNeuronValue != -1 && outputNeuronValue != 1) {
                return classifiation;
            }

            if (outputNeuronValue == 1) {
                // no other neuron has been activated before this one
                if (indexOfActivatedOutputNeuron == null) {
                    indexOfActivatedOutputNeuron = i;
                }
                else {
                    // multiple neurons activated, so undecided
                    return classifiation;
                }
            }
        }

        if (indexOfActivatedOutputNeuron == null) {
            return classifiation;
        }

        // Convert index to character, e.g. index 0 == a, 1 == b, 2 == c, etc...
        classifiation = (char) ('a' + indexOfActivatedOutputNeuron) + "";

        return classifiation;
    }
}
