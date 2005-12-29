begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
operator|.
name|jms
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * Converts Message from one JMS to another  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|JmsMesageConvertor
block|{
comment|/**      * Convert a foreign JMS Message to a native ActiveMQ Message      * @param message      * @return the converted message      * @throws JMSException      */
specifier|public
name|Message
name|convert
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
function_decl|;
block|}
end_interface

end_unit

