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

begin_comment
comment|/**  * @version $Revision: 1.7 $  */
end_comment

begin_class
specifier|public
class|class
name|TempTopicRegion
extends|extends
name|AbstractRegion
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TempTopicRegion
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|TempTopicRegion
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
comment|// We should allow the following to be configurable via a Destination
comment|// Policy
comment|// setAutoCreateDestinations(false);
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
if|if
condition|(
name|info
operator|.
name|isDurable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"A durable subscription cannot be created for a temporary topic."
argument_list|)
throw|;
block|}
try|try
block|{
name|TopicSubscription
name|answer
init|=
operator|new
name|TopicSubscription
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|info
argument_list|,
name|memoryManager
argument_list|)
decl_stmt|;
comment|// lets configure the subscription depending on the destination
name|ActiveMQDestination
name|destination
init|=
name|info
operator|.
name|getDestination
argument_list|()
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
name|broker
argument_list|,
name|memoryManager
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
block|}
name|answer
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create TopicSubscription "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|JMSException
name|jmsEx
init|=
operator|new
name|JMSException
argument_list|(
literal|"Couldn't create TopicSubscription"
argument_list|)
decl_stmt|;
name|jmsEx
operator|.
name|setLinkedException
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|jmsEx
throw|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TempTopicRegion: destinations="
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
comment|// Force a timeout value so that we don't get an error that
comment|// there is still an active sub. Temp destination may be removed
comment|// while a network sub is still active which is valid.
if|if
condition|(
name|timeout
operator|==
literal|0
condition|)
block|{
name|timeout
operator|=
literal|1
expr_stmt|;
block|}
name|super
operator|.
name|removeDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

