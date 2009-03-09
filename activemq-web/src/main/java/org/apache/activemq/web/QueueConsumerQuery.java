begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|web
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
operator|.
name|SubscriptionViewMBean
import|;
end_import

begin_comment
comment|/**  * Query for Queue consumers.  *   * @version $Revision: 504235 $  */
end_comment

begin_class
specifier|public
class|class
name|QueueConsumerQuery
extends|extends
name|DestinationFacade
block|{
specifier|public
name|QueueConsumerQuery
parameter_list|(
name|BrokerFacade
name|brokerFacade
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|brokerFacade
argument_list|)
expr_stmt|;
name|setJMSDestinationType
argument_list|(
literal|"queue"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getConsumers
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getBrokerFacade
argument_list|()
operator|.
name|getQueueConsumers
argument_list|(
name|getJMSDestination
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|destroy
parameter_list|()
block|{
comment|// empty
block|}
block|}
end_class

end_unit

