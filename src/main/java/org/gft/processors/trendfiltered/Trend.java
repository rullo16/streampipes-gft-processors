package org.gft.processors.trendfiltered;

import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfigBuilder;
import org.apache.streampipes.wrapper.siddhi.SiddhiQueryBuilder;
import org.apache.streampipes.wrapper.siddhi.engine.SiddhiEventEngine;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.*;
import org.apache.streampipes.wrapper.siddhi.query.expression.pattern.PatternCountOperator;

import java.util.List;

public class Trend extends SiddhiEventEngine<TrendFilteredParams> {

    public Trend(){
        super();
    }

    public Trend(SiddhiDebugCallback callback){
        super(callback);
    }

    public FromClause fromStatement(SiddhiProcessorParams<TrendFilteredParams> siddhiProcessorParams){
        TrendFilteredParams trendFilteredParams = siddhiProcessorParams.getParams();

        String inputProperty = prepareName(trendFilteredParams.getInput());
        int duration = trendFilteredParams.getDuration();
        double increase = trendFilteredParams.getIncrease();

        increase = (increase/100) +1;

        RelationalOperator operator = trendFilteredParams.getFilterOperation();
        Double threshold = trendFilteredParams.getThreshold();


        FromClause fromClause = FromClause.create();
        Expression filter = new RelationalOperatorExpression(operator,Expressions.property(inputProperty),Expressions.staticValue(threshold));

        StreamExpression exp1 = Expressions.every(Expressions.stream("e1", siddhiProcessorParams.getInputStreamNames().get(0)));
        StreamExpression exp2 = Expressions.filter(Expressions.stream("e2", siddhiProcessorParams.getInputStreamNames().get(0)),filter);

        PropertyExpressionBase mathExp = trendFilteredParams.getOperator() == TrendOperator.INCREASE ?
                Expressions.divide(Expressions.property(inputProperty),Expressions.staticValue(increase)) :
                Expressions.multiply(Expressions.property(inputProperty), Expressions.staticValue(increase));

        RelationalOperatorExpression opExp = trendFilteredParams.getOperator() == TrendOperator.INCREASE ?
                Expressions.le(Expressions.property("e1", inputProperty),mathExp) :
                Expressions.ge(Expressions.property("e1",inputProperty), mathExp);

        StreamFilterExpression filterExp = Expressions.filter(exp2, Expressions.patternCount(1, PatternCountOperator.EXACTLY_N),opExp);

        Expression sequence = (Expressions.sequence(exp1,filterExp,Expressions.within(duration, SiddhiTimeUnit.SECONDS)));
        fromClause.add(sequence);
        System.out.println(fromClause.toSiddhiEpl());
        return fromClause;
    }

    private SelectClause selectStatement(SiddhiProcessorParams<TrendFilteredParams> siddhiParams) {
        SelectClause selectClause = SelectClause.create();
        List<String> outputFieldSelectors = siddhiParams.getParams().getOutputFieldSelectors();
        outputFieldSelectors.forEach(outputFieldSelector -> selectClause.addProperty(Expressions.property("e2", outputFieldSelector, "last")));

        return selectClause;
    }

    @Override
    public SiddhiAppConfig makeStatements(SiddhiProcessorParams<TrendFilteredParams> siddhiParams,
                                          String finalInsertIntoStreamName) {

        InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName);

        return SiddhiAppConfigBuilder
                .create()
                .addQuery(SiddhiQueryBuilder
                        .create(fromStatement(siddhiParams), insertIntoClause)
                        .withSelectClause(selectStatement(siddhiParams))
                        .build())
                .build();
    }
}
