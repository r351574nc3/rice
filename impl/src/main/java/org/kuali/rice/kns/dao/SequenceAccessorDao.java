/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.dao;

import org.kuali.rice.kns.bo.BusinessObject;

/**
 * This class may be used by the SequenceAccessorService implementation to get the next number for a given sequence. 
 */
public interface SequenceAccessorDao {
	public Long getNextAvailableSequenceNumber(String sequenceName, 
			Class<? extends BusinessObject> clazz);
	
    public Long getNextAvailableSequenceNumber(String sequenceName);
}
