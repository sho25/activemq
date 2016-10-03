begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|junit
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|ActiveMQConnectionFactory
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
name|ActiveMQDestination
import|;
end_import

begin_class
specifier|public
class|class
name|ActiveMQTopicSubscriberResource
extends|extends
name|AbstractActiveMQConsumerResource
block|{
specifier|public
name|ActiveMQTopicSubscriberResource
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|super
argument_list|(
name|destinationName
argument_list|,
name|connectionFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQTopicSubscriberResource
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|URI
name|brokerURI
parameter_list|)
block|{
name|super
argument_list|(
name|destinationName
argument_list|,
name|brokerURI
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQTopicSubscriberResource
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|EmbeddedActiveMQBroker
name|embeddedActiveMQBroker
parameter_list|)
block|{
name|super
argument_list|(
name|destinationName
argument_list|,
name|embeddedActiveMQBroker
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQTopicSubscriberResource
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|URI
name|brokerURI
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|super
argument_list|(
name|destinationName
argument_list|,
name|brokerURI
argument_list|,
name|userName
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|getDestinationType
parameter_list|()
block|{
return|return
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
return|;
block|}
block|}
end_class

end_unit

