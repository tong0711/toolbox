/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package eu.amidst.core.datastream;

import eu.amidst.core.utils.Utils;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.Variable;

import java.util.Set;

/**
 *
 *
 *
 * Created by ana@cs.aau.dk on 10/11/14.
 */
public interface DataInstance extends Assignment {

    @Override
    default double getValue(Variable var) {
        if (var.getAttribute()==null)
            return Utils.missingValue();
        else
            return this.getValue(var.getAttribute());
    }

    @Override
    default void setValue(Variable var, double value) {
        if (var.getAttribute()!=null)
            this.setValue(var.getAttribute(), value);
    }

    @Override
    default Set<Variable> getVariables(){
        return null;
    }

    Attributes getAttributes();

    double getValue(Attribute att);

    void setValue(Attribute att, double val);
}