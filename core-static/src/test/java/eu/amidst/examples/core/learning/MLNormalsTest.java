package eu.amidst.examples.core.learning;

import eu.amidst.examples.core.datastream.DataInstance;
import eu.amidst.examples.core.datastream.DataStream;
import eu.amidst.examples.core.exponentialfamily.EF_BayesianNetwork;
import eu.amidst.examples.core.io.BayesianNetworkLoader;
import eu.amidst.examples.core.models.BayesianNetwork;
import eu.amidst.examples.core.utils.BayesianNetworkSampler;
import eu.amidst.examples.core.variables.Variable;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by ana@cs.aau.dk on 22/01/15.
 *
 */
public class MLNormalsTest {

    //TODO: Test more than 1 parent

    @Test
    public void testingML_NormalNormal1Parent() throws IOException, ClassNotFoundException  {


        BayesianNetwork testnet = BayesianNetworkLoader.loadFromFile("networks/Normal_1NormalParents.bn");
        System.out.println("\nNormal_withOneNormalParent network \n ");

        //Sampling
        BayesianNetworkSampler sampler = new BayesianNetworkSampler(testnet);
        sampler.setSeed(0);
        DataStream<DataInstance> data = sampler.sampleToDataStream(10000);


        //Parameter Learning
        MaximumLikelihoodForBN.setBatchSize(1000);
        MaximumLikelihoodForBN.setParallelMode(true);
        BayesianNetwork bnet = MaximumLikelihoodForBN.learnParametersStaticModel(testnet.getDAG(), data);

        EF_BayesianNetwork ef_testnet = new EF_BayesianNetwork(testnet);

        EF_BayesianNetwork ef_bnet = new EF_BayesianNetwork(bnet);

        assertTrue(ef_bnet.equal_efBN(ef_testnet,0.05));
    }

    @Test
    public void testingML_GaussiansTwoParents() throws  IOException, ClassNotFoundException {

        BayesianNetwork testnet = BayesianNetworkLoader.loadFromFile("networks/Normal_NormalParents.bn");
        System.out.println("\nNormal_withTwoNormalParents network \n ");

        //Sampling
        BayesianNetworkSampler sampler = new BayesianNetworkSampler(testnet);
        sampler.setSeed(0);
        DataStream<DataInstance> data = sampler.sampleToDataStream(10000);


        //Parameter Learning
        MaximumLikelihoodForBN.setBatchSize(1000);
        MaximumLikelihoodForBN.setParallelMode(true);
        BayesianNetwork bnet = MaximumLikelihoodForBN.learnParametersStaticModel(testnet.getDAG(), data);

        //Check the probability distributions of each node
        for (Variable var : testnet.getStaticVariables()) {
            System.out.println("\n------ Variable " + var.getName() + " ------");
            System.out.println("\nTrue distribution:\n"+ testnet.getConditionalDistribution(var));
            System.out.println("\nLearned distribution:\n"+ bnet.getConditionalDistribution(var));
            assertTrue(bnet.getConditionalDistribution(var).equalDist(testnet.getConditionalDistribution(var), 0.05));
        }

        //Or check directly if the true and learned networks are equals
        assertTrue(bnet.equalBNs(testnet,0.05));
    }

}