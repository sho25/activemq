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
name|region
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|advisory
operator|.
name|AdvisorySupport
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
name|policy
operator|.
name|PolicyEntry
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
name|ActiveMQTempDestination
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
name|SubscriptionInfo
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
name|store
operator|.
name|MessageStore
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
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|TopicMessageStore
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
name|thread
operator|.
name|TaskRunnerFactory
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_comment
comment|/**  * Creates standard ActiveMQ implementations of  * {@link org.apache.activemq.broker.region.Destination}.  *   * @author fateev@amazon.com  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DestinationFactoryImpl
extends|extends
name|DestinationFactory
block|{
specifier|protected
specifier|final
name|SystemUsage
name|memoryManager
decl_stmt|;
specifier|protected
specifier|final
name|TaskRunnerFactory
name|taskRunnerFactory
decl_stmt|;
specifier|protected
specifier|final
name|PersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|protected
name|RegionBroker
name|broker
decl_stmt|;
specifier|public
name|DestinationFactoryImpl
parameter_list|(
name|SystemUsage
name|memoryManager
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|,
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|)
block|{
name|this
operator|.
name|memoryManager
operator|=
name|memoryManager
expr_stmt|;
name|this
operator|.
name|taskRunnerFactory
operator|=
name|taskRunnerFactory
expr_stmt|;
if|if
condition|(
name|persistenceAdapter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null persistenceAdapter"
argument_list|)
throw|;
block|}
name|this
operator|.
name|persistenceAdapter
operator|=
name|persistenceAdapter
expr_stmt|;
block|}
specifier|public
name|void
name|setRegionBroker
parameter_list|(
name|RegionBroker
name|broker
parameter_list|)
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null broker"
argument_list|)
throw|;
block|}
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDestinations
parameter_list|()
block|{
return|return
name|persistenceAdapter
operator|.
name|getDestinations
argument_list|()
return|;
block|}
comment|/**      * @return instance of {@link Queue} or {@link Topic}      */
specifier|public
name|Destination
name|createDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|DestinationStatistics
name|destinationStatistics
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
condition|)
block|{
if|if
condition|(
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
specifier|final
name|ActiveMQTempDestination
name|tempDest
init|=
operator|(
name|ActiveMQTempDestination
operator|)
name|destination
decl_stmt|;
return|return
operator|new
name|Queue
argument_list|(
name|broker
operator|.
name|getRoot
argument_list|()
argument_list|,
name|destination
argument_list|,
name|memoryManager
argument_list|,
literal|null
argument_list|,
name|destinationStatistics
argument_list|,
name|taskRunnerFactory
argument_list|,
name|broker
operator|.
name|getTempDataStore
argument_list|()
argument_list|)
block|{
specifier|public
name|void
name|addSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Only consumers on the same connection can consume
comment|// from
comment|// the temporary destination
if|if
condition|(
operator|!
name|tempDest
operator|.
name|getConnectionId
argument_list|()
operator|.
name|equals
argument_list|(
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Cannot subscribe to remote temporary destination: "
operator|+
name|tempDest
argument_list|)
throw|;
block|}
name|super
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
return|;
block|}
else|else
block|{
name|MessageStore
name|store
init|=
name|persistenceAdapter
operator|.
name|createQueueMessageStore
argument_list|(
operator|(
name|ActiveMQQueue
operator|)
name|destination
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
operator|new
name|Queue
argument_list|(
name|broker
operator|.
name|getRoot
argument_list|()
argument_list|,
name|destination
argument_list|,
name|memoryManager
argument_list|,
name|store
argument_list|,
name|destinationStatistics
argument_list|,
name|taskRunnerFactory
argument_list|,
name|broker
operator|.
name|getTempDataStore
argument_list|()
argument_list|)
decl_stmt|;
name|configureQueue
argument_list|(
name|queue
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|queue
operator|.
name|initialize
argument_list|()
expr_stmt|;
return|return
name|queue
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
specifier|final
name|ActiveMQTempDestination
name|tempDest
init|=
operator|(
name|ActiveMQTempDestination
operator|)
name|destination
decl_stmt|;
return|return
operator|new
name|Topic
argument_list|(
name|broker
operator|.
name|getRoot
argument_list|()
argument_list|,
name|destination
argument_list|,
literal|null
argument_list|,
name|memoryManager
argument_list|,
name|destinationStatistics
argument_list|,
name|taskRunnerFactory
argument_list|)
block|{
specifier|public
name|void
name|addSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Only consumers on the same connection can consume from
comment|// the temporary destination
if|if
condition|(
operator|!
name|tempDest
operator|.
name|getConnectionId
argument_list|()
operator|.
name|equals
argument_list|(
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Cannot subscribe to remote temporary destination: "
operator|+
name|tempDest
argument_list|)
throw|;
block|}
name|super
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
return|;
block|}
else|else
block|{
name|TopicMessageStore
name|store
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|destination
argument_list|)
condition|)
block|{
name|store
operator|=
name|persistenceAdapter
operator|.
name|createTopicMessageStore
argument_list|(
operator|(
name|ActiveMQTopic
operator|)
name|destination
argument_list|)
expr_stmt|;
block|}
name|Topic
name|topic
init|=
operator|new
name|Topic
argument_list|(
name|broker
operator|.
name|getRoot
argument_list|()
argument_list|,
name|destination
argument_list|,
name|store
argument_list|,
name|memoryManager
argument_list|,
name|destinationStatistics
argument_list|,
name|taskRunnerFactory
argument_list|)
decl_stmt|;
name|configureTopic
argument_list|(
name|topic
argument_list|,
name|destination
argument_list|)
expr_stmt|;
return|return
name|topic
return|;
block|}
block|}
specifier|protected
name|void
name|configureQueue
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"broker property is not set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PolicyEntry
name|entry
init|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|configure
argument_list|(
name|queue
argument_list|,
name|broker
operator|.
name|getTempDataStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|configureTopic
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"broker property is not set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PolicyEntry
name|entry
init|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|configure
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|long
name|getLastMessageBrokerSequenceId
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|persistenceAdapter
operator|.
name|getLastMessageBrokerSequenceId
argument_list|()
return|;
block|}
specifier|public
name|PersistenceAdapter
name|getPersistenceAdapter
parameter_list|()
block|{
return|return
name|persistenceAdapter
return|;
block|}
specifier|public
name|SubscriptionInfo
index|[]
name|getAllDurableSubscriptions
parameter_list|(
name|ActiveMQTopic
name|topic
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|persistenceAdapter
operator|.
name|createTopicMessageStore
argument_list|(
name|topic
argument_list|)
operator|.
name|getAllSubscriptions
argument_list|()
return|;
block|}
block|}
end_class

end_unit

