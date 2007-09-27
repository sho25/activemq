begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|virtual
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_comment
comment|/**  *  * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|MirroredQueueUsingVirtualTopicQueueTest
extends|extends
name|MirroredQueueTest
block|{
annotation|@
name|Override
specifier|protected
name|Destination
name|createConsumeDestination
parameter_list|()
block|{
name|String
name|queueName
init|=
literal|"Consumer.A.VirtualTopic.Mirror."
operator|+
name|getQueueName
argument_list|()
decl_stmt|;
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

