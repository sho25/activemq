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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Message
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
name|MessageAck
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
name|MessageDispatchNotification
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|DestinationMap
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
name|memory
operator|.
name|UsageManager
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision: 1.14 $  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|AbstractRegion
implements|implements
name|Region
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AbstractRegion
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentHashMap
name|destinations
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|DestinationMap
name|destinationMap
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentHashMap
name|subscriptions
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|UsageManager
name|memoryManager
decl_stmt|;
specifier|protected
specifier|final
name|PersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|protected
specifier|final
name|DestinationStatistics
name|destinationStatistics
decl_stmt|;
specifier|protected
specifier|final
name|RegionBroker
name|broker
decl_stmt|;
specifier|protected
name|boolean
name|autoCreateDestinations
init|=
literal|true
decl_stmt|;
specifier|protected
specifier|final
name|TaskRunnerFactory
name|taskRunnerFactory
decl_stmt|;
specifier|protected
specifier|final
name|Object
name|destinationsMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|AbstractRegion
parameter_list|(
name|RegionBroker
name|broker
parameter_list|,
name|DestinationStatistics
name|destinationStatistics
parameter_list|,
name|UsageManager
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
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|destinationStatistics
operator|=
name|destinationStatistics
expr_stmt|;
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
name|this
operator|.
name|persistenceAdapter
operator|=
name|persistenceAdapter
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|Destination
name|addDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Adding destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|dest
operator|.
name|start
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|destinationsMutex
init|)
block|{
name|destinations
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|destinationMap
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|dest
argument_list|)
expr_stmt|;
comment|// Add all consumers that are interested in the destination.
for|for
control|(
name|Iterator
name|iter
init|=
name|subscriptions
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Subscription
name|sub
init|=
operator|(
name|Subscription
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|matches
argument_list|(
name|destination
argument_list|)
condition|)
block|{
name|dest
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dest
return|;
block|}
block|}
specifier|public
name|void
name|removeDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
comment|// The destination cannot be removed if there are any active subscriptions
for|for
control|(
name|Iterator
name|iter
init|=
name|subscriptions
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Subscription
name|sub
init|=
operator|(
name|Subscription
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|matches
argument_list|(
name|destination
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Destination still has an active subscription: "
operator|+
name|destination
argument_list|)
throw|;
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Removing destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|destinationsMutex
init|)
block|{
name|Destination
name|dest
init|=
operator|(
name|Destination
operator|)
name|destinations
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
condition|)
block|{
name|destinationMap
operator|.
name|removeAll
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|dest
operator|.
name|dispose
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|dest
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Destination doesn't exist: "
operator|+
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Map
name|getDestinationMap
parameter_list|()
block|{
synchronized|synchronized
init|(
name|destinationsMutex
init|)
block|{
return|return
operator|new
name|HashMap
argument_list|(
name|destinations
argument_list|)
return|;
block|}
block|}
specifier|public
name|Subscription
name|addConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|Subscription
name|sub
init|=
name|createSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
decl_stmt|;
comment|// We may need to add some destinations that are in persistent store but not active
comment|// in the broker.
comment|//
comment|// TODO: think about this a little more.  This is good cause destinations are not loaded into
comment|// memory until a client needs to use the queue, but a management agent viewing the
comment|// broker will not see a destination that exists in persistent store.  We may want to
comment|// eagerly load all destinations into the broker but have an inactive state for the
comment|// destination which has reduced memory usage.
comment|//
if|if
condition|(
name|persistenceAdapter
operator|!=
literal|null
condition|)
block|{
name|Set
name|inactiveDests
init|=
name|getInactiveDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|inactiveDests
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ActiveMQDestination
name|dest
init|=
operator|(
name|ActiveMQDestination
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|matches
argument_list|(
name|dest
argument_list|)
condition|)
block|{
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|subscriptions
operator|.
name|put
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|sub
argument_list|)
expr_stmt|;
comment|// Add the subscription to all the matching queues.
for|for
control|(
name|Iterator
name|iter
init|=
name|destinationMap
operator|.
name|get
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Destination
name|dest
init|=
operator|(
name|Destination
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|dest
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|isBrowser
argument_list|()
condition|)
block|{
operator|(
operator|(
name|QueueBrowserSubscription
operator|)
name|sub
operator|)
operator|.
name|browseDone
argument_list|()
expr_stmt|;
block|}
return|return
name|sub
return|;
block|}
comment|/**      * Get all the Destinations that are in storage      * @return Set of all stored destinations      */
specifier|public
name|Set
name|getDurableDestinations
parameter_list|()
block|{
return|return
name|persistenceAdapter
operator|.
name|getDestinations
argument_list|()
return|;
block|}
comment|/**      * @return all Destinations that don't have active consumers      */
specifier|protected
name|Set
name|getInactiveDestinations
parameter_list|()
block|{
name|Set
name|inactiveDests
init|=
name|persistenceAdapter
operator|.
name|getDestinations
argument_list|()
decl_stmt|;
name|inactiveDests
operator|.
name|removeAll
argument_list|(
name|destinations
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|inactiveDests
return|;
block|}
specifier|public
name|void
name|removeConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|Subscription
name|sub
init|=
operator|(
name|Subscription
operator|)
name|subscriptions
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The subscription does not exist: "
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
throw|;
comment|// remove the subscription from all the matching queues.
for|for
control|(
name|Iterator
name|iter
init|=
name|destinationMap
operator|.
name|get
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Destination
name|dest
init|=
operator|(
name|Destination
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|dest
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
name|destroySubscription
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|destroySubscription
parameter_list|(
name|Subscription
name|sub
parameter_list|)
block|{
name|sub
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|removeSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|RemoveSubscriptionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Invalid operation."
argument_list|)
throw|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|Destination
name|dest
init|=
name|lookup
argument_list|(
name|context
argument_list|,
name|messageSend
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|dest
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
name|Subscription
name|sub
init|=
operator|(
name|Subscription
operator|)
name|subscriptions
operator|.
name|get
argument_list|(
name|ack
operator|.
name|getConsumerId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The subscription does not exist: "
operator|+
name|ack
operator|.
name|getConsumerId
argument_list|()
argument_list|)
throw|;
name|sub
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Destination
name|lookup
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|destinationsMutex
init|)
block|{
name|Destination
name|dest
init|=
operator|(
name|Destination
operator|)
name|destinations
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|autoCreateDestinations
condition|)
block|{
comment|// Try to auto create the destination... re-invoke broker from the
comment|// top so that the proper security checks are performed.
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
expr_stmt|;
comment|// We should now have the dest created.
name|dest
operator|=
operator|(
name|Destination
operator|)
name|destinations
operator|.
name|get
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"The destination "
operator|+
name|destination
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
block|}
return|return
name|dest
return|;
block|}
block|}
specifier|public
name|void
name|processDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|messageDispatchNotification
parameter_list|)
throws|throws
name|Exception
block|{
name|Subscription
name|sub
init|=
operator|(
name|Subscription
operator|)
name|subscriptions
operator|.
name|get
argument_list|(
name|messageDispatchNotification
operator|.
name|getConsumerId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|processMessageDispatchNotification
argument_list|(
name|messageDispatchNotification
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|subscriptions
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Subscription
name|sub
init|=
operator|(
name|Subscription
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|sub
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|destinations
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Destination
name|dest
init|=
operator|(
name|Destination
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|dest
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|Subscription
name|createSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|abstract
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
name|boolean
name|isAutoCreateDestinations
parameter_list|()
block|{
return|return
name|autoCreateDestinations
return|;
block|}
specifier|public
name|void
name|setAutoCreateDestinations
parameter_list|(
name|boolean
name|autoCreateDestinations
parameter_list|)
block|{
name|this
operator|.
name|autoCreateDestinations
operator|=
name|autoCreateDestinations
expr_stmt|;
block|}
block|}
end_class

end_unit

