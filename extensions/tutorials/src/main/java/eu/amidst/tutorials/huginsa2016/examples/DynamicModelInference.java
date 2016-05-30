package eu.amidst.tutorials.huginsa2016.examples;



import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.distribution.UnivariateDistribution;
import eu.amidst.core.inference.ImportanceSampling;
import eu.amidst.core.inference.messagepassing.VMP;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.inference.FactoredFrontierForDBN;
import eu.amidst.dynamic.inference.InferenceEngineForDBN;
import eu.amidst.dynamic.io.DynamicDataStreamLoader;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.latentvariablemodels.dynamicmodels.HiddenMarkovModel;

/**
 * Created by rcabanas on 23/05/16.
 */
public class DynamicModelInference {

    public static void main(String[] args) {

        //Load the datastream
        String filename = "datasets/simulated/exampleDS_d0_c5.arff";
        DataStream<DynamicDataInstance> data = DynamicDataStreamLoader.loadFromFile(filename);

        //Learn the model
        HiddenMarkovModel model = new HiddenMarkovModel(data.getAttributes());
        model.setNumStatesHiddenVar(4);
        model.setWindowSize(200);
        model.updateModel(data);
        DynamicBayesianNetwork dbn = model.getModel();

        System.out.println(dbn);



        //Testing dataset
        String filenamePredict = "datasets/simulated/exampleDS_d0_c5_small.arff";
        DataStream<DynamicDataInstance> dataPredict = DynamicDataStreamLoader.loadFromFile(filenamePredict);

        //Select the inference algorithm
        FactoredFrontierForDBN FFalgorithm = new FactoredFrontierForDBN(new VMP()); // new ImportanceSampling(),  new VMP(),
        //Intitialize the inference engine
        InferenceEngineForDBN.setInferenceAlgorithmForDBN(FFalgorithm);
        InferenceEngineForDBN.setModel(dbn);

        //Variables of interest
        Variable varTarget = dbn.getDynamicVariables().getVariableByName("discreteHiddenVar");
        UnivariateDistribution posterior = null;


        //Classify each instance
        int t = 0;
        for (DynamicDataInstance instance : dataPredict) {

            InferenceEngineForDBN.addDynamicEvidence(instance);
            InferenceEngineForDBN.runInference();
            posterior = InferenceEngineForDBN.getFilteredPosterior(varTarget);

            //Display the output
            System.out.println("t="+t+", P(discreteHiddenVar | Evidence)  = " + posterior);
            t++;
        }






    }


}