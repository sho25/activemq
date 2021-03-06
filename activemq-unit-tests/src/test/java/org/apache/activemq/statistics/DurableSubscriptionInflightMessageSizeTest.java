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
name|statistics
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
name|MessageConsumer
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
name|region
operator|.
name|Subscription
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
name|region
operator|.
name|Topic
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|SubscriptionKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_comment
comment|/**  * This test shows Inflight Message sizes are correct for various acknowledgement modes  * using a DurableSubscription  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|DurableSubscriptionInflightMessageSizeTest
extends|extends
name|AbstractInflightMessageSizeTest
block|{
specifier|public
name|DurableSubscriptionInflightMessageSizeTest
parameter_list|(
name|int
name|ackType
parameter_list|,
name|boolean
name|optimizeAcknowledge
parameter_list|,
name|boolean
name|useTopicSubscriptionInflightStats
parameter_list|)
block|{
name|super
argument_list|(
name|ackType
argument_list|,
name|optimizeAcknowledge
argument_list|,
name|useTopicSubscriptionInflightStats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|MessageConsumer
name|getMessageConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|javax
operator|.
name|jms
operator|.
name|Topic
operator|)
name|dest
argument_list|,
literal|"sub1"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Subscription
name|getSubscription
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Topic
operator|)
name|amqDestination
operator|)
operator|.
name|getDurableTopicSubs
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|SubscriptionKey
argument_list|(
literal|"client1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|javax
operator|.
name|jms
operator|.
name|Topic
name|getDestination
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTopic
argument_list|(
name|destName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|destName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

