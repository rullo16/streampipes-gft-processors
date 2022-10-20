/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.gft.processors.timetracking;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.standalone.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.ArrayList;
import java.util.List;


public class TimeTrackingDataProcessor extends StreamPipesDataProcessor {

  private String input_value;
  private String timestamp_value;

  private static final String INPUT_VALUE = "value";
  private static final String TIMESTAMP_VALUE = "timestamp_value";

  List<Double> listSource = new ArrayList<>();
  private Double first_timestamp=0.0;


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.gft.processors.timetracking")
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .category(DataProcessorType.AGGREGATE)


            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                            Labels.withId(INPUT_VALUE), PropertyScope.NONE)
                    .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                            Labels.withId(TIMESTAMP_VALUE), PropertyScope.NONE)
                    .build())

            .outputStrategy(OutputStrategies.append(PrimitivePropertyBuilder.create(Datatypes.Double, "outputValue").build()))
            .build();
  }

  @Override
  public void onInvocation(ProcessorParams processorParams,
                            SpOutputCollector out,
                            EventProcessorRuntimeContext ctx) throws SpRuntimeException  {

    this.input_value = processorParams.extractor().mappingPropertyValue(INPUT_VALUE);
    this.timestamp_value = processorParams.extractor().mappingPropertyValue(TIMESTAMP_VALUE);

  }

  @Override
  public void onEvent(Event event,SpOutputCollector out){
    double mean = 0.0;
    double outputValue = 0.0;

    //recovery input value
    Double value = event.getFieldBySelector(this.input_value).getAsPrimitive().getAsDouble();
    System.out.println("value: " + value);

    //recovery timestamp value
    Double timestamp = event.getFieldBySelector(this.timestamp_value).getAsPrimitive().getAsDouble();
    System.out.println("timestampStr: " + timestamp);

    System.out.println("timestamp Diff: " + (timestamp - first_timestamp));

    if (first_timestamp == 0.0){

      System.out.println("******** first_timestamp == 0.0 ************");
      first_timestamp=timestamp;
      listSource.add(value);
      //outputValue= value;

    }else if (timestamp - first_timestamp >= 3600000){
      System.out.println("******** DIFF >= 3600000 ************");
      listSource.add(value);
      first_timestamp=timestamp;
      //perform mean
      mean = performMeanOperation(listSource);
      outputValue= mean;

    }else {

      System.out.println("******** ELSE ************");
      listSource.add(value);
      //outputValue= value;
    }

    System.out.println("--- mean value ---" + mean);


    System.out.println("======= OUTPUT VALUE ============" + outputValue);
    event.addField("outputValue", outputValue);
    out.collect(event);


    
  }

  @Override
  public void onDetach(){
  }

  public double performMeanOperation(List<Double> values) {
    DescriptiveStatistics ds = new DescriptiveStatistics();
    values.forEach(ds::addValue);
    double mean = ds.getMean();
    return mean;
  }

}
