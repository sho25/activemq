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
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|Broker
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
name|BrokerService
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
name|ConnectionContext
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
name|command
operator|.
name|ActiveMQQueue
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
name|command
operator|.
name|ConsumerId
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
name|ConsumerInfo
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
name|RemoveSubscriptionInfo
import|;
end_import

begin_comment
comment|//import org.apache.log4j.LogManager;
end_comment

begin_comment
comment|//import org.apache.log4j.PropertyConfigurator;
end_comment

begin_class
specifier|public
class|class
name|BrokerView
implements|implements
name|BrokerViewMBean
block|{
specifier|final
name|ManagedRegionBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|sessionIdCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|BrokerView
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|ManagedRegionBroker
name|managedBroker
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|managedBroker
expr_stmt|;
block|}
specifier|public
name|ManagedRegionBroker
name|getBroker
parameter_list|()
block|{
return|return
name|broker
return|;
block|}
specifier|public
name|String
name|getBrokerId
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getBrokerId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getTotalEnqueueCount
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalDequeueCount
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalConsumerCount
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getConsumers
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalMessageCount
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalMessagesCached
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessagesCached
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMemoryPercentageUsed
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMemoryLimit
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMemoryLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|resetStatistics
parameter_list|()
block|{
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|enableStatistics
parameter_list|()
block|{
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|disableStatistics
parameter_list|()
block|{
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStatisticsEnabled
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|isEnabled
argument_list|()
return|;
block|}
specifier|public
name|void
name|terminateJVM
parameter_list|(
name|int
name|exitCode
parameter_list|)
block|{
name|System
operator|.
name|exit
argument_list|(
name|exitCode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTopics
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getTopics
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getQueues
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getQueues
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryTopics
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getTemporaryTopics
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryQueues
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getTemporaryQueues
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTopicSubscribers
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getTemporaryTopicSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getDurableTopicSubscribers
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getDurableTopicSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getQueueSubscribers
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getQueueSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryTopicSubscribers
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getTemporaryTopicSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryQueueSubscribers
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getTemporaryQueueSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getInactiveDurableTopicSubscribers
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
return|;
block|}
specifier|public
name|void
name|addTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|addDestination
argument_list|(
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|addDestination
argument_list|(
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|removeDestination
argument_list|(
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|removeDestination
argument_list|(
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ObjectName
name|createDurableSubscriber
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|,
name|String
name|topicName
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|ConsumerId
name|consumerId
init|=
operator|new
name|ConsumerId
argument_list|()
decl_stmt|;
name|consumerId
operator|.
name|setConnectionId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|consumerId
operator|.
name|setSessionId
argument_list|(
name|sessionIdCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
name|consumerId
operator|.
name|setValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|info
operator|.
name|setConsumerId
argument_list|(
name|consumerId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscriptionName
argument_list|(
name|subscriberName
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|Subscription
name|subscription
init|=
name|broker
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|broker
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|subscription
operator|!=
literal|null
condition|)
block|{
return|return
name|subscription
operator|.
name|getObjectName
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|destroyDurableSubscriber
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|Exception
block|{
name|RemoveSubscriptionInfo
name|info
init|=
operator|new
name|RemoveSubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscriptionName
argument_list|(
name|subscriberName
argument_list|)
expr_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the broker's administration connection context used for      * configuring the broker at startup      */
specifier|public
specifier|static
name|ConnectionContext
name|getConnectionContext
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|ConnectionContext
name|adminConnectionContext
init|=
name|broker
operator|.
name|getAdminConnectionContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|adminConnectionContext
operator|==
literal|null
condition|)
block|{
name|adminConnectionContext
operator|=
name|createAdminConnectionContext
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdminConnectionContext
argument_list|(
name|adminConnectionContext
argument_list|)
expr_stmt|;
block|}
return|return
name|adminConnectionContext
return|;
block|}
comment|/**      * Factory method to create the new administration connection context      * object. Note this method is here rather than inside a default broker      * implementation to ensure that the broker reference inside it is the outer      * most interceptor      */
specifier|protected
specifier|static
name|ConnectionContext
name|createAdminConnectionContext
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
comment|//  doc comment inherited from BrokerViewMBean
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*         LogManager.resetConfiguration();         ClassLoader cl = this.getClass().getClassLoader();         URL log4jprops = cl.getResource("log4j.properties");         if (log4jprops != null) {             PropertyConfigurator.configure(log4jprops);         }         */
block|}
block|}
end_class

end_unit

