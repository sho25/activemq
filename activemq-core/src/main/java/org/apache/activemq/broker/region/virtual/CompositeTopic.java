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
name|broker
operator|.
name|region
operator|.
name|virtual
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_comment
comment|/**  * Represents a virtual topic which forwards to a number of other destinations.  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|CompositeTopic
extends|extends
name|CompositeDestination
block|{
specifier|public
name|ActiveMQDestination
name|getVirtualDestination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

