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
package org.kuali.rice.kns.bo;

/**
 * This is a description of what this class does - chitra07 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface PersistableAttachment {

    public abstract byte[] getAttachmentContent();

    public abstract void setAttachmentContent(byte[] attachmentContent);

    public abstract String getFileName();

    public abstract void setFileName(String fileName);

    public abstract String getContentType();

    public abstract void setContentType(String contentType);

}
