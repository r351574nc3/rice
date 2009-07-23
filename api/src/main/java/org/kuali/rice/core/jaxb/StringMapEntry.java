/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Single String-String key-value pair for 
 * marshalling/unmarshalling. Need this rather than
 * general Map.Entry<String, String> to specify
 * cardinality in resulting wsdl's.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class StringMapEntry {
	
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute
	String key;
	
	@XmlElement(required=true) // maxoccurs == minoccurs == 1
	String value;
	
	/**
	 *
	 */
	public StringMapEntry() {
	    super();
	}
	
	/**
	 * @param name
	 * @param value
	 */
	public StringMapEntry(String key, String value) {
	    super();
	    
	    this.key = key;
	    this.value = value;
	}
}
