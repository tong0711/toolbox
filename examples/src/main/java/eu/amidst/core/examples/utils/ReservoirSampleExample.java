/*
 *
 *
 *    Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.
 *    See the NOTICE file distributed with this work for additional information regarding copyright ownership.
 *    The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use
 *    this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software distributed under the License is
 *    distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and limitations under the License.
 *
 *
 */
package eu.amidst.core.examples.utils;


import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.io.DataStreamWriter;
import eu.amidst.core.utils.ReservoirSampling;

/**
 *
 *  This example shows how to use the RervourSampling class which implements an algorithm to uniformly subsample N data points
 *  for a data stream of unknown size using the algorithm given in
 *
 *  Vitter, J. S. (1985). Random sampling with a reservoir. ACM Transactions on Mathematical Software (TOMS), 11(1), 37-57.
 *
 *
 * Created by andresmasegosa on 18/6/15.
 */
public class ReservoirSampleExample {

    public static void main(String[] args) throws Exception {

        //We can open the data stream using the static class DataStreamLoader
        DataStream<DataInstance> data = DataStreamLoader.open("datasets/simulated/syntheticData.arff");

        //ReservoirSampling allows to create a DataOnMemory object containing a unfiorm subsample of the data stream
        DataOnMemory<DataInstance> dataOnMemory = ReservoirSampling.samplingNumberOfSamples(100, data);

        //We can save this data set to a new file using the static class DataStreamWriter
        DataStreamWriter.writeDataToFile(data, "datasets/simulated/subsample.arff");
    }
}
