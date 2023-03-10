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
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

/**
 * Marks the beginning of a sequence node.
 * <p>
 * This event is followed by the elements contained in the sequence, and a {@link SequenceEndEvent}.
 * </p>
 *
 * @see SequenceEndEvent
 */
public final class SequenceStartEvent extends CollectionStartEvent
{
	
	public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle)
	{
		super(anchor, tag, implicit, startMark, endMark, flowStyle);
	}
	
	/*
	 * Existed in older versions but replaced with {@link DumperOptions.SequenceStyle}-based
	 * constructor. Restored in v1.22 for backwards compatibility.
	 *
	 * @deprecated Since restored in v1.22. Use {@link SequenceStartEvent#SequenceStartEvent(String,
	 * String, boolean, Mark, Mark, org.yaml.snakeyaml.DumperOptions.FlowStyle) }.
	 */
	@Deprecated
	public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, Boolean flowStyle)
	{
		this(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.fromBoolean(flowStyle));
	}
	
	@Override
	public Event.ID getEventId()
	{
		return ID.SequenceStart;
	}
}
