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

package eu.amidst.dataGeneration;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.utils.Utils;
import eu.amidst.flinklink.core.data.DataFlink;
import eu.amidst.flinklink.core.io.DataFlinkLoader;
import eu.amidst.flinklink.core.io.DataFlinkWriter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.Configuration;

import java.util.List;
import java.util.Random;

/**
 * Created by ana@cs.aau.dk on 23/02/16.
 */
public class AddMissingValues {

    public static void main(String[] args) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        String inputFileName = args[0];
        String outputFileName = args[1];

        DataFlink<DataInstance> dataFlink = DataFlinkLoader.loadDataFromFolder(env,inputFileName, false);

        DataSet<DataInstance> dataFlinkWithMissing = dataFlink.getDataSet().
                map(new MissValuesMap(dataFlink.getAttributes()));

        DataFlinkWriter.writeDataToARFFFolder(new DataFlink<DataInstance>() {
            @Override
            public String getName() {
                return dataFlink.getName();
            }

            @Override
            public Attributes getAttributes() {
                return dataFlink.getAttributes();
            }

            @Override
            public DataSet<DataInstance> getDataSet() {
                return dataFlinkWithMissing;
            }
        }, outputFileName);
    }

    public static void addMissingValuesToFile (String inputFileName,String outputFileName)  throws Exception {

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataFlink<DataInstance> dataFlink = DataFlinkLoader.loadDataFromFolder(env,inputFileName, false);

        DataSet<DataInstance> dataFlinkWithMissing = dataFlink.getDataSet().
                map(new MissValuesMap(dataFlink.getAttributes()));

        DataFlinkWriter.writeDataToARFFFolder(new DataFlink<DataInstance>() {
            @Override
            public String getName() {
                return dataFlink.getName();
            }

            @Override
            public Attributes getAttributes() {
                return dataFlink.getAttributes();
            }

            @Override
            public DataSet<DataInstance> getDataSet() {
                return dataFlinkWithMissing;
            }
        }, outputFileName);
    }

    public static class MissValuesMap extends RichMapFunction<DataInstance, DataInstance> {

        Random random;
        List<Attribute> attsList;

        public MissValuesMap(Attributes atts){

            attsList = atts.getListOfNonSpecialAttributes();
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            random = new Random(getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public DataInstance map(DataInstance dataInstance) throws Exception {

            attsList.stream().forEach(attribute -> {
                if(!attribute.getName().equalsIgnoreCase("Default")){
                    double probMissing = random.nextDouble();
                    if((attribute.getIndex()%2==0 && probMissing<0.3) ||
                            (attribute.getIndex()%2==1 && probMissing<0.8))
                        dataInstance.setValue(attribute, Utils.missingValue());
                }
            });
            return dataInstance;
        }
    }


}