/**
 * Copyright (c) 2008, SnakeYAML
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.yaml.snakeyaml.representer;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.*;

import java.util.*;

/**
 * Represent basic YAML structures: scalar, sequence, mapping
 */
public abstract class BaseRepresenter
{
	
	/**
	 * represent the class without its subclasses
	 */
	protected final Map<Class<?>, Represent> representers = new HashMap<Class<?>, Represent>();
	/**
	 * in Java 'null' is not a type. So we have to keep the null representer separately otherwise it
	 * will coincide with the default representer which is stored with the key null.
	 */
	protected Represent nullRepresenter;
	// the order is important (map can be also a sequence of key-values)
	/**
	 * represent class and its children with common code
	 */
	protected final Map<Class<?>, Represent> multiRepresenters = new LinkedHashMap<Class<?>, Represent>();
	/**
	 * default scalar style is not defined
	 */
	protected DumperOptions.ScalarStyle defaultScalarStyle = null; // not explicitly defined
	/**
	 * flow style to use if not redefined.
	 */
	protected FlowStyle defaultFlowStyle = FlowStyle.AUTO;
	/**
	 * Keep references of already represented instances
	 */
	protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>()
	{
		private static final long serialVersionUID = -5576159264232131854L;
		
		public Node put(Object key, Node value)
		{
			return super.put(key, new AnchorNode(value));
		}
	};
	
	/**
	 * object to create the Node for
	 */
	protected Object objectToRepresent;
	private PropertyUtils propertyUtils;
	private boolean explicitPropertyUtils = false;
	
	public Node represent(Object data)
	{
		Node node = representData(data);
		representedObjects.clear();
		objectToRepresent = null;
		return node;
	}
	
	protected final Node representData(Object data)
	{
		objectToRepresent = data;
		// check for identity
		if (representedObjects.containsKey(objectToRepresent))
		{
			Node node = representedObjects.get(objectToRepresent);
			return node;
		}
		// }
		// check for null first
		if (data == null)
		{
			Node node = nullRepresenter.representData(null);
			return node;
		}
		// check the same class
		Node node;
		Class<?> clazz = data.getClass();
		if (representers.containsKey(clazz))
		{
			Represent representer = representers.get(clazz);
			node = representer.representData(data);
		}
		else
		{
			// check the parents
			for (Class<?> repr : multiRepresenters.keySet())
			{
				if (repr != null && repr.isInstance(data))
				{
					Represent representer = multiRepresenters.get(repr);
					node = representer.representData(data);
					return node;
				}
			}
			
			// check defaults
			if (multiRepresenters.containsKey(null))
			{
				Represent representer = multiRepresenters.get(null);
				node = representer.representData(data);
			}
			else
			{
				Represent representer = representers.get(null);
				node = representer.representData(data);
			}
		}
		return node;
	}
	
	protected Node representScalar(Tag tag, String value, DumperOptions.ScalarStyle style)
	{
		if (style == null)
		{
			style = this.defaultScalarStyle;
		}
		Node node = new ScalarNode(tag, value, null, null, style);
		return node;
	}
	
	protected Node representScalar(Tag tag, String value)
	{
		return representScalar(tag, value, null);
	}
	
	protected Node representSequence(Tag tag, Iterable<?> sequence, DumperOptions.FlowStyle flowStyle)
	{
		int size = 10;// default for ArrayList
		if (sequence instanceof List<?>)
		{
			size = ((List<?>) sequence).size();
		}
		List<Node> value = new ArrayList<Node>(size);
		SequenceNode node = new SequenceNode(tag, value, flowStyle);
		representedObjects.put(objectToRepresent, node);
		DumperOptions.FlowStyle bestStyle = FlowStyle.FLOW;
		for (Object item : sequence)
		{
			Node nodeItem = representData(item);
			if (!(nodeItem instanceof ScalarNode && ((ScalarNode) nodeItem).isPlain()))
			{
				bestStyle = FlowStyle.BLOCK;
			}
			value.add(nodeItem);
		}
		if (flowStyle == FlowStyle.AUTO)
		{
			if (defaultFlowStyle != FlowStyle.AUTO)
			{
				node.setFlowStyle(defaultFlowStyle);
			}
			else
			{
				node.setFlowStyle(bestStyle);
			}
		}
		return node;
	}
	
	protected Node representMapping(Tag tag, Map<?, ?> mapping, DumperOptions.FlowStyle flowStyle)
	{
		List<NodeTuple> value = new ArrayList<NodeTuple>(mapping.size());
		MappingNode node = new MappingNode(tag, value, flowStyle);
		representedObjects.put(objectToRepresent, node);
		DumperOptions.FlowStyle bestStyle = FlowStyle.FLOW;
		for (Map.Entry<?, ?> entry : mapping.entrySet())
		{
			Node nodeKey = representData(entry.getKey());
			Node nodeValue = representData(entry.getValue());
			if (!(nodeKey instanceof ScalarNode && ((ScalarNode) nodeKey).isPlain()))
			{
				bestStyle = FlowStyle.BLOCK;
			}
			if (!(nodeValue instanceof ScalarNode && ((ScalarNode) nodeValue).isPlain()))
			{
				bestStyle = FlowStyle.BLOCK;
			}
			value.add(new NodeTuple(nodeKey, nodeValue));
		}
		if (flowStyle == FlowStyle.AUTO)
		{
			if (defaultFlowStyle != FlowStyle.AUTO)
			{
				node.setFlowStyle(defaultFlowStyle);
			}
			else
			{
				node.setFlowStyle(bestStyle);
			}
		}
		return node;
	}
	
	public void setDefaultScalarStyle(ScalarStyle defaultStyle)
	{
		this.defaultScalarStyle = defaultStyle;
	}
	
	/**
	 * getter
	 *
	 * @return scala style
	 */
	public ScalarStyle getDefaultScalarStyle()
	{
		if (defaultScalarStyle == null)
		{
			return ScalarStyle.PLAIN;
		}
		return defaultScalarStyle;
	}
	
	public void setDefaultFlowStyle(FlowStyle defaultFlowStyle)
	{
		this.defaultFlowStyle = defaultFlowStyle;
	}
	
	/**
	 * getter
	 *
	 * @return current flow style
	 */
	public FlowStyle getDefaultFlowStyle()
	{
		return this.defaultFlowStyle;
	}
	
	public void setPropertyUtils(PropertyUtils propertyUtils)
	{
		this.propertyUtils = propertyUtils;
		this.explicitPropertyUtils = true;
	}
	
	/**
	 * getter
	 *
	 * @return utils or create if null
	 */
	public final PropertyUtils getPropertyUtils()
	{
		if (propertyUtils == null)
		{
			propertyUtils = new PropertyUtils();
		}
		return propertyUtils;
	}
	
	public final boolean isExplicitPropertyUtils()
	{
		return explicitPropertyUtils;
	}
}
