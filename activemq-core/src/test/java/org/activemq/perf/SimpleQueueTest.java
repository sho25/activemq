begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *   * Copyright 2005 (C) Simula Labs Inc.  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|perf
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

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
name|Session
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|SimpleQueueTest
extends|extends
name|SimpleTopicTest
block|{
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|Session
name|s
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|s
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

