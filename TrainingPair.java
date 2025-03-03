public class TrainingPair {
    int[][] trainingPattern;
    OutputClassification outputClassification;

    public TrainingPair(int trainingRows, int trainingColumns, int outputDiomension) {
        trainingPattern = new int[trainingRows][trainingColumns];
        outputClassification = new OutputClassification(outputDiomension);
    }
}
