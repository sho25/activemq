begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *  * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
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
name|org
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
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|PolicyMap
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|thread
operator|.
name|TaskRunnerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
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
name|Set
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision: 1.9 $  */
end_comment

begin_class
specifier|public
class|class
name|QueueRegion
extends|extends
name|AbstractRegion
block|{
specifier|private
specifier|final
name|PolicyMap
name|policyMap
decl_stmt|;
specifier|public
name|QueueRegion
parameter_list|(
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
parameter_list|,
name|PolicyMap
name|policyMap
parameter_list|)
block|{
name|super
argument_list|(
name|destinationStatistics
argument_list|,
name|memoryManager
argument_list|,
name|taskRunnerFactory
argument_list|,
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|this
operator|.
name|policyMap
operator|=
name|policyMap
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
name|memoryManager
operator|.
name|getPercentUsage
argument_list|()
operator|+
literal|"%"
return|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Throwable
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
name|destination
argument_list|,
name|memoryManager
argument_list|,
name|store
argument_list|,
name|destinationStatistics
argument_list|,
name|taskRunnerFactory
argument_list|)
decl_stmt|;
name|configureQueue
argument_list|(
name|queue
argument_list|,
name|destination
argument_list|)
expr_stmt|;
return|return
name|queue
return|;
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
name|policyMap
operator|!=
literal|null
condition|)
block|{
name|PolicyEntry
name|entry
init|=
name|policyMap
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
argument_list|)
expr_stmt|;
block|}
block|}
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
name|InvalidSelectorException
block|{
if|if
condition|(
name|info
operator|.
name|isBrowser
argument_list|()
condition|)
block|{
return|return
operator|new
name|QueueBrowserSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|QueueSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
return|;
block|}
block|}
specifier|protected
name|Set
name|getInactiveDestinations
parameter_list|()
block|{
name|Set
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
operator|!
name|dest
operator|.
name|isQueue
argument_list|()
condition|)
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
return|return
name|inactiveDestinations
return|;
block|}
block|}
end_class

end_unit

