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
name|view
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerRegistry
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
name|region
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
name|ActiveMQTempQueue
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
name|ActiveMQTempTopic
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
name|LRUCache
import|;
end_import

begin_comment
comment|/**  * A view into the running Broker  */
end_comment

begin_class
specifier|public
class|class
name|MessageBrokerView
block|{
specifier|private
specifier|final
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|BrokerDestinationView
argument_list|>
name|destinationViewMap
init|=
operator|new
name|LRUCache
argument_list|<
name|ActiveMQDestination
argument_list|,
name|BrokerDestinationView
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Create a view of a running Broker      * @param brokerService      */
specifier|public
name|MessageBrokerView
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
if|if
condition|(
name|brokerService
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"BrokerService is null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|brokerService
operator|.
name|isStarted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"BrokerService "
operator|+
name|brokerService
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|" is not started"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Create a view of a running Broker      * @param brokerName      */
specifier|public
name|MessageBrokerView
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
if|if
condition|(
name|brokerService
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"BrokerService is null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|brokerService
operator|.
name|isStarted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"BrokerService "
operator|+
name|brokerService
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|" is not started"
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return the brokerName      */
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getBrokerName
argument_list|()
return|;
block|}
comment|/**      * @return the unique id of the Broker      */
specifier|public
name|String
name|getBrokerId
parameter_list|()
block|{
try|try
block|{
return|return
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
block|}
comment|/**      * @return the memory used by the Broker as a percentage      */
specifier|public
name|int
name|getMemoryPercentUsage
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
comment|/**      * @return  the space used by the Message Store as a percentage      */
specifier|public
name|int
name|getStorePercentUsage
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
comment|/**      * @return the space used by the store for temporary messages as a percentage      */
specifier|public
name|int
name|getTempPercentUsage
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getTempUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
comment|/**      * @return the space used by the store of scheduled messages      */
specifier|public
name|int
name|getJobSchedulerStorePercentUsage
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getJobSchedulerUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
comment|/**      * @return true if the Broker isn't using an in-memory store only for messages      */
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|isPersistent
argument_list|()
return|;
block|}
specifier|public
name|BrokerService
name|getBrokerService
parameter_list|()
block|{
return|return
name|brokerService
return|;
block|}
comment|/**      * Retrieve a set of all Destinations be used by the Broker      * @return  all Destinations      */
specifier|public
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDestinations
parameter_list|()
block|{
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|result
decl_stmt|;
try|try
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|getDestinations
argument_list|()
decl_stmt|;
name|result
operator|=
operator|new
name|HashSet
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|result
argument_list|,
name|destinations
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|result
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Retrieve a set of all Topics be used by the Broker      * @return  all Topics      */
specifier|public
name|Set
argument_list|<
name|ActiveMQTopic
argument_list|>
name|getTopics
parameter_list|()
block|{
name|Set
argument_list|<
name|ActiveMQTopic
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|ActiveMQTopic
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|getDestinations
argument_list|()
control|)
block|{
if|if
condition|(
name|destination
operator|.
name|isTopic
argument_list|()
operator|&&
operator|!
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|ActiveMQTopic
operator|)
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Retrieve a set of all Queues be used by the Broker      * @return  all Queues      */
specifier|public
name|Set
argument_list|<
name|ActiveMQQueue
argument_list|>
name|getQueues
parameter_list|()
block|{
name|Set
argument_list|<
name|ActiveMQQueue
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|ActiveMQQueue
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|getDestinations
argument_list|()
control|)
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
operator|&&
operator|!
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|ActiveMQQueue
operator|)
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Retrieve a set of all TemporaryTopics be used by the Broker      * @return  all TemporaryTopics      */
specifier|public
name|Set
argument_list|<
name|ActiveMQTempTopic
argument_list|>
name|getTempTopics
parameter_list|()
block|{
name|Set
argument_list|<
name|ActiveMQTempTopic
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|ActiveMQTempTopic
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|getDestinations
argument_list|()
control|)
block|{
if|if
condition|(
name|destination
operator|.
name|isTopic
argument_list|()
operator|&&
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|ActiveMQTempTopic
operator|)
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Retrieve a set of all TemporaryQueues be used by the Broker      * @return  all TemporaryQueues      */
specifier|public
name|Set
argument_list|<
name|ActiveMQTempQueue
argument_list|>
name|getTempQueues
parameter_list|()
block|{
name|Set
argument_list|<
name|ActiveMQTempQueue
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|ActiveMQTempQueue
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|getDestinations
argument_list|()
control|)
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
operator|&&
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|ActiveMQTempQueue
operator|)
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * It will be assumed the destinationName is prepended with topic:// or queue:// - but      * will default to a Queue      * @param destinationName      * @return the BrokerDestinationView associated with the destinationName      * @throws Exception      */
specifier|public
name|BrokerDestinationView
name|getDestinationView
parameter_list|(
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getDestinationView
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
comment|/**      * Get the BrokerDestinationView associated with the topic      * @param destinationName      * @return  BrokerDestinationView      * @throws Exception      */
specifier|public
name|BrokerDestinationView
name|getTopicDestinationView
parameter_list|(
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getDestinationView
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
return|;
block|}
comment|/**      * Get the BrokerDestinationView associated with the queue      * @param destinationName      * @return  BrokerDestinationView      * @throws Exception      */
specifier|public
name|BrokerDestinationView
name|getQueueDestinationView
parameter_list|(
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getDestinationView
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
comment|/**      * Get the BrokerDestinationView associated with destination      * @param destinationName      * @param type  expects either ActiveMQDestination.QUEUE_TYPE, ActiveMQDestination.TOPIC_TYPE etc      * @return  BrokerDestinationView      * @throws Exception      */
specifier|public
name|BrokerDestinationView
name|getDestinationView
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|byte
name|type
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|activeMQDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|type
argument_list|)
decl_stmt|;
return|return
name|getDestinationView
argument_list|(
name|activeMQDestination
argument_list|)
return|;
block|}
comment|/**      *  Get the BrokerDestinationView associated with destination      * @param activeMQDestination      * @return   BrokerDestinationView      * @throws Exception      */
specifier|public
name|BrokerDestinationView
name|getDestinationView
parameter_list|(
name|ActiveMQDestination
name|activeMQDestination
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerDestinationView
name|view
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|destinationViewMap
init|)
block|{
name|view
operator|=
name|destinationViewMap
operator|.
name|get
argument_list|(
name|activeMQDestination
argument_list|)
expr_stmt|;
if|if
condition|(
name|view
operator|==
literal|null
condition|)
block|{
comment|/**                      * If auto destinatons are allowed (on by default) - this will create a Broker Destination                      * if it doesn't exist. We could query the regionBroker first to check - but this affords more                      * flexibility - e.g. you might want to set up a query on destination statistics before any                      * messaging clients have started (and hence created the destination themselves                      */
name|Destination
name|destination
init|=
name|brokerService
operator|.
name|getDestination
argument_list|(
name|activeMQDestination
argument_list|)
decl_stmt|;
name|view
operator|=
operator|new
name|BrokerDestinationView
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|destinationViewMap
operator|.
name|put
argument_list|(
name|activeMQDestination
argument_list|,
name|view
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|view
return|;
block|}
block|}
end_class

end_unit

