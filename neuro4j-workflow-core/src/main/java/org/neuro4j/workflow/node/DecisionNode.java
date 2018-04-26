/**
 * Copyright (c) 2013-2016, Neuro4j
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.neuro4j.workflow.node;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.neuro4j.workflow.FlowContext;
import org.neuro4j.workflow.WorkflowRequest;
import org.neuro4j.workflow.common.ExpressionException;
import org.neuro4j.workflow.common.FlowExecutionException;
import org.neuro4j.workflow.enums.DecisionCompTypes;
import org.neuro4j.workflow.enums.DecisionOperators;
import org.neuro4j.workflow.loader.f4j.SWFConstants;

public class DecisionNode extends WorkflowNode {

    private static final String NEXT_EXIT_RELATION = SWFConstants.NEXT_RELATION_NAME;
    private static final String FALSE_EXIT_RELATION = "FALSE";

    private static final String _FALSE = "false";
    private static final String _TRUE = "true";

    private DecisionOperators operator = null;
    private DecisionCompTypes compTypes = null;
    private String decisionKey = null;
    //maybe bool number or "String" or ${a.b}
    private String comparisonKey = null;

    /**
     * 
     */
    private Transition trueExit = null;
    /**
     * 
     */
    private Transition falseExit = null;

    public DecisionNode(String name, String uuid)
    {
        super(name, uuid);
    }

    public DecisionOperators getOperator() {
        return operator;
    }

    public void setOperator(DecisionOperators operator) {
        this.operator = operator;
    }

    public DecisionCompTypes getCompTypes() {
        return compTypes;
    }

    public void setCompTypes(DecisionCompTypes compTypes) {
        this.compTypes = compTypes;
    }

    public String getDecisionKey() {
        return decisionKey;
    }

    public void setDecisionKey(String decisionKey) {
        this.decisionKey = decisionKey;
    }

    public String getComparisonKey() {
        return comparisonKey;
    }

    public void setComparisonKey(String comparisonKey) {
        this.comparisonKey = comparisonKey;
    }

    @Override
    public final Transition execute(final WorkflowProcessor perocessor, final WorkflowRequest request) throws FlowExecutionException {
        FlowContext fctx = request.getLogicContext();
        switch (operator) {
            case DEFINED:
                Object decisionValue = fctx.get(decisionKey);
                if (decisionValue == null)
                {
                    request.setNextRelation(falseExit);
                } else {
                    request.setNextRelation(trueExit);
                }
                break;
            case UNDEFINED:
                decisionValue = fctx.get(decisionKey);
                if (decisionValue == null)
                {
                    request.setNextRelation(trueExit);
                } else {
                    request.setNextRelation(falseExit);
                }
                break;
            case EMPTY_STR:
                decisionValue = fctx.get(decisionKey);
                if (decisionValue != null && decisionValue instanceof String && "".equals(decisionValue.toString().trim()))
                {
                    request.setNextRelation(trueExit);
                } else {
                    request.setNextRelation(falseExit);
                }
                break;
            case EQ_STR:
                decisionValue = fctx.get(decisionKey);

                Object compValue = getComparisonValue(fctx);

                if (Objects.equals(decisionValue, compValue))
                {
                    request.setNextRelation(trueExit);
                } else {
                    request.setNextRelation(falseExit);
                }
                break;
            case NEQ_STR:
                decisionValue = fctx.get(decisionKey);
                compValue = getComparisonValue(fctx);

                if (Objects.equals(decisionValue, compValue))
                {
                    request.setNextRelation(falseExit);
                } else {
                    request.setNextRelation(trueExit);
                }
                break;
            case NEQ:// for bool or number
                decisionValue = fctx.get(decisionKey);
                compValue = getComparisonValue(fctx);

                if (compValue == null || decisionValue == null) {
                    request.setNextRelation(falseExit);
                    break;
                }

                if (compValue instanceof Boolean || isBoolStr(compValue)) {
                    if (!Boolean.valueOf(compValue.toString()).equals(decisionValue)) {
                        request.setNextRelation(trueExit);
                    } else {
                        request.setNextRelation(falseExit);
                    }
                    break;
                }
                double numberValue = new Double(decisionValue.toString()).doubleValue();
                double numberCompareValue = new Double(compValue.toString()).doubleValue();

                if (numberValue != numberCompareValue)
                {
                    request.setNextRelation(trueExit);
                } else {
                    request.setNextRelation(falseExit);
                }
                break;
            case LESS:
                decisionValue = fctx.get(decisionKey);
                compValue = getComparisonValue(fctx);

                if (compValue == null || decisionValue == null) {
                    request.setNextRelation(falseExit);
                    break;
                }

                 numberValue = new Double(decisionValue.toString()).doubleValue();
                 numberCompareValue = new Double(compValue.toString()).doubleValue();

                if (numberValue < numberCompareValue)
                {
                    request.setNextRelation(trueExit);
                } else {
                    request.setNextRelation(falseExit);
                }
                break;
            case GREATER:
                decisionValue = fctx.get(decisionKey);
                compValue = getComparisonValue(fctx);
                
                if (compValue == null || decisionValue == null) {
                    request.setNextRelation(falseExit);
                    break;
                }
                numberValue = new Double(decisionValue.toString()).doubleValue();
                numberCompareValue = new Double(compValue.toString()).doubleValue();

                if (numberValue > numberCompareValue)
                {
                    request.setNextRelation(trueExit);
                } else {
                    request.setNextRelation(falseExit);
                }
                break;
            case EQ:
                decisionValue = fctx.get(decisionKey);

                compValue = getComparisonValue(fctx);//compValue maybe boolean or number
                
                if (compValue == null || decisionValue == null) {
                    request.setNextRelation(falseExit);
                    break;
                }

                if (compValue instanceof Boolean || isBoolStr(compValue)) {
                    if (Boolean.valueOf(compValue.toString()).equals(decisionValue)) {
                        request.setNextRelation(trueExit);
                    } else {
                        request.setNextRelation(falseExit);
                    }
                }else{
                    numberValue = new Double(decisionValue.toString()).doubleValue();
                    numberCompareValue = new Double(compValue.toString()).doubleValue();

                    if (numberValue == numberCompareValue)
                    {
                        request.setNextRelation(trueExit);
                    } else {
                        request.setNextRelation(falseExit);
                    }
                }
                break;
            case HAS_EL:

                Object value = fctx.get(decisionKey);
                boolean result = false;
                if (value != null)
                {
                    if ((value instanceof Iterator))
                    {
                        result = ((Iterator) value).hasNext();
                    }
                    else if ((value instanceof Enumeration))
                    {
                        result = ((Enumeration) value).hasMoreElements();
                    }
                    else if ((value instanceof Collection))
                    {
                        result = !((Collection) value).isEmpty();
                    }
                    else if ((value instanceof Object[]))
                    {
                        result = ((Object[]) value).length > 0;
                    }
                }

                if (result)
                {
                    request.setNextRelation(trueExit);
                } else {
                    request.setNextRelation(falseExit);
                }
                break;
            default:
                throw new FlowExecutionException("Decision node: Wrong configuration");
        }
        return request.getNextWorkflowNode();
    }

    private boolean isBoolStr(Object compValue) {
        if (compValue == null) {
            return false;
        }
        if (_FALSE.equalsIgnoreCase(compValue.toString()) || _TRUE.equalsIgnoreCase(compValue.toString())) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * @param fctx
     *        flow context
     * @return object from context
     */
    private Object getComparisonValue(FlowContext fctx)
    {
        Object compValue = null;
        switch (compTypes) {
            case context:
                if (comparisonKey.startsWith(SWFConstants.GR_VARIABLE_SYMBOL_START)
                        && comparisonKey.endsWith(SWFConstants.GR_VARIABLE_SYMBOL_END)) {
                    comparisonKey = comparisonKey.replace(SWFConstants.GR_VARIABLE_SYMBOL_START,"").replace(SWFConstants.GR_VARIABLE_SYMBOL_END,"");
                    compValue = fctx.get(comparisonKey);
                } else {
                    //not allowed
                    compValue = fctx.get(comparisonKey);
                    //throw new RuntimeException("decision node's comparisonKey must like ${xxx} when get valve from context");
                }
                break;
            default:
                if (comparisonKey.startsWith(SWFConstants.QUOTES_SYMBOL)
                        && comparisonKey.endsWith(SWFConstants.QUOTES_SYMBOL)) {
                    compValue = comparisonKey.substring(1,comparisonKey.length() - 1);
                } else {
                    compValue = comparisonKey;
                }
                break;
        }
        return compValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neuro4j.workflow.node.LogicBlock#load(org.neuro4j.workflow.xml.WorkflowNode)
     */
    public final void init() throws FlowExecutionException
    {
        trueExit = getExitByName(NEXT_EXIT_RELATION);
        falseExit = getExitByName(FALSE_EXIT_RELATION);

    }

    @Override
    public void validate(final WorkflowProcessor processor, final FlowContext ctx) throws FlowExecutionException {
        if (operator == null) {
            throw new FlowExecutionException(
                    "Decision node: Operator not defined");
        }
        if (compTypes == null) {
            throw new FlowExecutionException(
                    "Decision node: CompTypes not defined");
        }
        if (trueExit == null && falseExit == null)
        {
            throw new FlowExecutionException("Decision node: Connector not defined.");
        }

        switch (operator) {
            case DEFINED:
            case UNDEFINED:
            case HAS_EL:
            case EMPTY_STR:

                if (decisionKey == null)
                {
                    throw new FlowExecutionException("Decision node: decisionKey is not defined");
                }
                break;

            case EQ_STR:
            case EQ:
            case LESS:
            case GREATER:
            case NEQ:
            case NEQ_STR:
                if (decisionKey == null || comparisonKey == null)
                {
                    throw new FlowExecutionException("Decision node: decisionKey and comparisonKey are mandatory");
                }
                break;
            default:

        }
    }
}
