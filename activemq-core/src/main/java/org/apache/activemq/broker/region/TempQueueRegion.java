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
name|InvalidSelectorException
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
name|TempQueueRegion
extends|extends
name|AbstractTempRegion
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
name|TempQueueRegion
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BrokerService
name|brokerService
decl_stmt|;
specifier|public
name|TempQueueRegion
parameter_list|(
name|RegionBroker
name|broker
parameter_list|,
name|BrokerService
name|brokerService
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
comment|// We should allow the following to be configurable via a Destination
comment|// Policy
comment|// setAutoCreateDestinations(false);
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
specifier|protected
name|Destination
name|doCreateDestination
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
name|TempQueue
name|result
init|=
operator|new
name|TempQueue
argument_list|(
name|brokerService
argument_list|,
name|destination
argument_list|,
literal|null
argument_list|,
name|destinationStatistics
argument_list|,
name|taskRunnerFactory
argument_list|)
decl_stmt|;
name|result
operator|.
name|initialize
argument_list|()
expr_stmt|;
return|return
name|result
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
name|broker
argument_list|,
name|usageManager
argument_list|,
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
name|broker
argument_list|,
name|usageManager
argument_list|,
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

