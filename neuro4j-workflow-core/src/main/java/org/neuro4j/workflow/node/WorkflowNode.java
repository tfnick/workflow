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

import java.util.*;

import org.apache.commons.beanutils.ConstructorUtils;
import org.neuro4j.workflow.FlowContext;
import org.neuro4j.workflow.WorkflowRequest;
import org.neuro4j.workflow.common.ExpressionException;
import org.neuro4j.workflow.common.FlowExecutionException;
import org.neuro4j.workflow.loader.f4j.SWFConstants;
import org.neuro4j.workflow.utils.JsonUtil;
import org.neuro4j.workflow.utils.RegexUtil;
import org.neuro4j.workflow.utils.ScriptEvalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.neuro4j.workflow.loader.f4j.SWFConstants.QUOTES_SYMBOL;

/**
 * Base class for executable nodes
 *
 */
public class WorkflowNode {

	private static final Logger logger = LoggerFactory.getLogger(WorkflowNode.class);

	final private Map<String, String> parameters = new HashMap<String, String>(4);

	final Map<String, Transition> exits = new HashMap<String, Transition>(3);

	private final NodeInfo nodeInfo;

	public WorkflowNode(String name, String uuid) {
		nodeInfo = new NodeInfo(uuid, name);
	}

	public Set<String> getParameterNames() {
		return parameters.keySet();
	}

	public void addParameter(String key, String value) {
		this.parameters.put(key, value);
	}

	public String getParameter(String key) {

		return this.parameters.get(key);
	}

	public String getName() {
		return nodeInfo.getName();
	}

	public String getUuid() {
		return nodeInfo.getUuid();
	}

	public NodeInfo getNodeInfo() {
		return nodeInfo;
	}

	public Transition getExitByName(String relationName) {
		return exits.get(relationName);
	}

	public void registerExit(Transition con) {
		con.setFromNode(this);
		exits.put(con.getName(), con);

	}

	public Collection<Transition> getExits() {
		return exits.values();
	}

	/**
	 * Validates if current node can be executed
	 * 
	 * @param ctx
	 *            current context
	 * @param processor
	 *            current processor
	 * @param ctx
	 *            current context
	 * @throws FlowExecutionException
	 *             in case of error
	 */
	public void validate(final WorkflowProcessor processor, final FlowContext ctx) throws FlowExecutionException {
		return;
	}

	/**
	 * Executes current node.
	 * 
	 * @param processor
	 *            workflow processor
	 * @param request
	 *            current request
	 * @return next transition
	 * @throws FlowExecutionException
	 *             in case of error
	 */
	protected Transition execute(final WorkflowProcessor processor, final WorkflowRequest request)
			throws FlowExecutionException {
		return null;
	}

	public void init() throws FlowExecutionException {

	}

	/**
	 * source is constant or variable expression
	 * @param source
	 * @param target
	 * @param ctx
	 */
	public final void evaluateParameterValue(String source, String target, FlowContext ctx) {
		Object obj = null;

		// 1) if null
		if (SWFConstants.NULL_VALUE.equalsIgnoreCase(source)) {
			ctx.put(target, null);
			return;

			// 2) if create new class expression
		} else if (source.startsWith(SWFConstants.NEW_CLASS_SYMBOL_START)
				&& source.endsWith(SWFConstants.NEW_CLASS_SYMBOL_END)) {

			source = source.replace(SWFConstants.QUOTES_SYMBOL, "").replace("(", "").replace(")", "");

			obj = createNewInstance(source);

			ctx.put(target, obj);
			return;
			// 3) GR extend ${v1.v2}
		} else if(source.startsWith(SWFConstants.GR_VARIABLE_SYMBOL_START)
				&& source.endsWith(SWFConstants.GR_VARIABLE_SYMBOL_END)){
			//${v1.v2} -> v1.v2
			source = source.replace(SWFConstants.GR_VARIABLE_SYMBOL_START,"").replace(SWFConstants.GR_VARIABLE_SYMBOL_END,"");
			obj = ctx.get(source);
			ctx.put(target, obj);
			return;
			// 4) GR extend #{a:1,b:2}
		} else if (source.startsWith(SWFConstants.GR_MAP_SYMBOL_START) && source.endsWith(SWFConstants.GR_MAP_SYMBOL_END)) {

			source = source.replace(SWFConstants.GR_MAP_SYMBOL_START_REGEX, "");
			List<String> variables = RegexUtil.getStringList(source,SWFConstants.GR_VARIABLE_SYMBOL_REGEX, null);

			if (variables != null) {
				for (String var : variables) {
					String rVar = var.replaceFirst(SWFConstants.GR_VARIABLE_SYMBOL_START_REGEX,"").replace(SWFConstants.GR_VARIABLE_SYMBOL_END_REGEX,"");
					Object val = ctx.get(rVar);
					if (val == null) {
						source = source.replace(var, "null");
					} else {
						if (val instanceof String) {
							//source = source.replace(var, "\"" + val + "\"");
							source = source.replace(var, val.toString());
						} else {
							source = source.replace(var, val.toString());
						}
					}

				}
			}

			Map<String,Object> objm = JsonUtil.jsonToObject(source, HashMap.class);
			for (Map.Entry<String,Object> entry : objm.entrySet()) {
				//String key = entry.getKey();
				Object val = entry.getValue();

				if (val instanceof String) {
					String valExpression = val.toString();
					if(valExpression.startsWith(SWFConstants.GR_VARIABLE_SYMBOL_START)
							&& valExpression.endsWith(SWFConstants.GR_VARIABLE_SYMBOL_END)){
						valExpression = valExpression.replaceFirst(SWFConstants.GR_VARIABLE_SYMBOL_START,"").replace(SWFConstants.GR_VARIABLE_SYMBOL_END,"");
						Object objt = ctx.get(valExpression);

						entry.setValue(objt);
					}
				}
			}
			ctx.put(target,objm);
			return;
			// 5) %{${a.b} == 1 && ${age} > 19}
		} else if(source.startsWith(SWFConstants.GR_EXPRESSION_SYMBOL_START) && source.endsWith(SWFConstants.GR_EXPRESSION_SYMBOL_END)){
			source = source.replaceFirst(SWFConstants.GR_EXPRESSION_SYMBOL_START_REGEX,"");
			source = source.substring(0, source.length() - 1);

			List<String> variables = RegexUtil.getStringList(source,SWFConstants.GR_VARIABLE_SYMBOL_REGEX, null);

			if (variables != null) {
				for (String var : variables) {
					String rVar = var.replaceFirst(SWFConstants.GR_VARIABLE_SYMBOL_START_REGEX,"").replace(SWFConstants.GR_VARIABLE_SYMBOL_END_REGEX,"");
					Object val = ctx.get(rVar);
					if (val == null) {
						source = source.replace(var, "null");
					} else {
						if (val instanceof String) {
							source = source.replace(var, "\"" + val + "\"");
						} else if (val instanceof Map) {
							source = source.replace(var, "\"" + val + "\"");
						} else {//like Integer Boolean Double Float etc
							source = source.replace(var, val.toString());
						}
					}
				}
			}

			try {
				obj = ScriptEvalUtil.eval(source);
			} catch (ExpressionException e) {
				logger.error(e.getMessage(), e);
			}

			ctx.put(target, obj);
			// 6) constants like 1 or "year" etc
		} else {
			if (source != null && source instanceof String) {
				source = source.trim();
			}
			obj = source;
			// 7) string constants remove "
			if (source != null && source.startsWith(QUOTES_SYMBOL) && source.endsWith(QUOTES_SYMBOL) && source.length() > 1) {
				obj = source.substring(1, source.length() - 1);
			}
			ctx.put(target,obj);
			return;
		}
		/*
		//TODO remove following code
		String[] parts = source.split("\\+");

		// if concatenated string
		if (parts.length > 1) {
			String stringValue = "";

			for (String src : parts) {
				stringValue += (String) ctx.get(src);
			}
			obj = stringValue;

		} else {
			//use v1.v2.v3 expression to get value from context
			obj = ctx.get(source);
		}
		ctx.put(target, obj);
		*/
	}

	private Object createNewInstance(String clazzName) {
		Class<?> beanClass = null;
		Object beanInstance = null;
		try {
			beanClass = getClass().getClassLoader().loadClass(clazzName);
			beanInstance = ConstructorUtils.invokeConstructor(beanClass, null);
		} catch (Exception e) {
			logger.error("Error during creating class" + clazzName, e);
		}

		return beanInstance;

	}

	/**
	 * Provides immutable information about node
	 *
	 */
	static public class NodeInfo {
		private final String uuid;
		private final String name;

		public NodeInfo(String uuid, String name) {
			super();
			this.uuid = uuid;
			this.name = name;
		}

		public String getUuid() {
			return uuid;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "NodeInfo [uuid=" + uuid + ", name=" + name + "]";
		}

	}

}
