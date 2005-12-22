begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
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
name|ActiveMQTempDestination
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
name|thread
operator|.
name|TaskRunnerFactory
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision: 1.7 $  */
end_comment

begin_class
specifier|public
class|class
name|TempQueueRegion
extends|extends
name|AbstractRegion
block|{
specifier|public
name|TempQueueRegion
parameter_list|(
name|DestinationStatistics
name|destinationStatistics
parameter_list|,
name|UsageManager
name|memoryManager
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
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
literal|null
argument_list|)
expr_stmt|;
name|setAutoCreateDestinations
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
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
name|destination
argument_list|,
name|memoryManager
argument_list|,
literal|null
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
name|Throwable
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TempQueueRegion: destinations="
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
block|}
end_class

end_unit

