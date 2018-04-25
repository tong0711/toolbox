/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package simulatedData;

import eu.amidst.core.distribution.Normal;
import eu.amidst.core.learning.parametric.bayesian.*;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.utils.BayesianNetworkSampler;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;

import static simulatedData.StaticMethods.*;

/**
 * Created by andresmasegosa on 10/11/16.
 */
public class NormalChangeMean {

    public static void main(String[] args) {


        Variables variables = new Variables();

        Variable normal = variables.newGaussianVariable("N");

        BayesianNetwork bn = new BayesianNetwork(new DAG(variables));

        BayesianNetworkSampler sampler = new BayesianNetworkSampler(bn);

        BayesianParameterLearningAlgorithm svb = initDrift();

        svb.setDAG(bn.getDAG());

        svb.setOutput(false);

        svb.initLearning();

        svb.randomInitialize();

        double total = 0;

        Normal normalDist = bn.getConditionalDistribution(normal);
        normalDist.setMean(-1);
        normalDist.setVariance(1);

        System.out.println(bn);

        System.out.println("LogLikelihood\t RealParameter \t Learnt Parameter \t [Lambda(s)]");

        for (int i = 0; i < totalITER; i++) {
            sampler.setSeed(i);

            normalDist = bn.getConditionalDistribution(normal);


            if (i>=totalITER/3){
                normalDist.setMean(1);
            } if (i>=totalITER*2/3) {
                normalDist.setMean(-1);
            }


            if (svb.getClass().getName().compareTo("eu.amidst.core.learning.parametric.bayesian.DriftSVB")==0){
                ((DriftSVB)svb).updateModelWithConceptDrift(sampler.sampleToDataStream(sampleSize).toDataOnMemory());

                sampler.setSeed(10*i);

                double log=svb.predictedLogLikelihood(sampler.sampleToDataStream(sampleSize).toDataOnMemory());

                System.out.println(log+"\t"+normalDist.getMean()+"\t"+svb.getLearntBayesianNetwork().getConditionalDistribution(normal).getParameters()[0] +"\t"+((DriftSVB)svb).getLambdaMomentParameter());
                //System.out.println(((DriftSVB)svb).getPlateuStructure().getPlateauNaturalParameterPrior().getVectorByPosition(0).get(3)*2);


                total+=log;

            }else if (svb.getClass().getName().compareTo("eu.amidst.core.learning.parametric.bayesian.MultiDriftSVB")==0){
                ((MultiDriftSVB)svb).updateModelWithConceptDrift(sampler.sampleToDataStream(sampleSize).toDataOnMemory());

                sampler.setSeed(10*i);

                double log=svb.predictedLogLikelihood(sampler.sampleToDataStream(sampleSize).toDataOnMemory());

                System.out.print(log+"\t"+normalDist.getMean()+"\t"+svb.getLearntBayesianNetwork().getConditionalDistribution(normal).getParameters()[0] +"\t");

                double[] labmdaValues = ((MultiDriftSVB)svb).getLambdaMomentParameters();
                for (int j = 0; j < labmdaValues.length; j++) {
                    System.out.print(labmdaValues[j]+"\t");
                }

                System.out.println();
                total+=log;

            }else{
                svb.updateModel(sampler.sampleToDataStream(sampleSize).toDataOnMemory());

                sampler.setSeed(10*i);

                double log=svb.predictedLogLikelihood(sampler.sampleToDataStream(sampleSize).toDataOnMemory());

                System.out.println(log+"\t"+normalDist.getMean()+"\t"+svb.getLearntBayesianNetwork().getConditionalDistribution(normal).getParameters()[0]);
                total+=log;
            }

        }

        System.out.println(total);

    }


}
