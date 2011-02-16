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
comment|/**  *   *   */
end_comment

begin_class
specifier|public
class|class
name|QueueRegion
extends|extends
name|AbstractRegion
block|{
specifier|public
name|QueueRegion
parameter_list|(
name|RegionBroker
name|broker
parameter_list|,
name|DestinationStatistics
name|destinationStatistics
parameter_list|,
name|SystemUsage
name|memoryManager
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|,
name|DestinationFactory
name|destinationFactory
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|destinationStatistics
argument_list|,
name|memoryManager
argument_list|,
name|taskRunnerFactory
argument_list|,
name|destinationFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"QueueRegion: destinations="
operator|+
name|destinations
operator|.
name|size
argument_list|()
operator|+
literal|", subscriptions="
operator|+
name|subscriptions
operator|.
name|size
argument_list|()
operator|+
literal|", memory="
operator|+
name|usageManager
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
operator|+
literal|"%"
return|;
block|}
specifier|protected
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
name|JMSException
block|{
name|ActiveMQDestination
name|destination
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
name|PolicyEntry
name|entry
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
operator|&&
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|entry
operator|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|destination
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
name|QueueBrowserSubscription
name|sub
init|=
operator|new
name|QueueBrowserSubscription
argument_list|(
name|broker
argument_list|,
name|usageManager
argument_list|,
name|context
argument_list|,
name|info
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
name|broker
argument_list|,
name|usageManager
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
return|return
name|sub
return|;
block|}
else|else
block|{
name|QueueSubscription
name|sub
init|=
operator|new
name|QueueSubscription
argument_list|(
name|broker
argument_list|,
name|usageManager
argument_list|,
name|context
argument_list|,
name|info
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
name|broker
argument_list|,
name|usageManager
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
return|return
name|sub
return|;
block|}
block|}
specifier|protected
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getInactiveDestinations
parameter_list|()
block|{
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|inactiveDestinations
init|=
name|super
operator|.
name|getInactiveDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ActiveMQDestination
argument_list|>
name|iter
init|=
name|inactiveDestinations
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
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dest
operator|.
name|isQueue
argument_list|()
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|inactiveDestinations
return|;
block|}
comment|/*      * For a Queue, dispatch order is imperative to match acks, so the dispatch is deferred till       * the notification to ensure that the subscription chosen by the master is used.      *       * (non-Javadoc)      * @see org.apache.activemq.broker.region.AbstractRegion#processDispatchNotification(org.apache.activemq.command.MessageDispatchNotification)      */
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
name|processDispatchNotificationViaDestination
argument_list|(
name|messageDispatchNotification
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

