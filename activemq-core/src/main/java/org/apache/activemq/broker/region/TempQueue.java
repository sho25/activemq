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
name|cursors
operator|.
name|VMPendingMessageCursor
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
name|thread
operator|.
name|TaskRunnerFactory
import|;
end_import

begin_comment
comment|/**  * The Queue is a List of MessageEntry objects that are dispatched to matching  * subscriptions.  *   * @version $Revision: 1.28 $  */
end_comment

begin_class
specifier|public
class|class
name|TempQueue
extends|extends
name|Queue
block|{
specifier|private
specifier|final
name|ActiveMQTempDestination
name|tempDest
decl_stmt|;
comment|/**      * @param brokerService      * @param destination      * @param store      * @param parentStats      * @param taskFactory      * @throws Exception      */
specifier|public
name|TempQueue
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|MessageStore
name|store
parameter_list|,
name|DestinationStatistics
name|parentStats
parameter_list|,
name|TaskRunnerFactory
name|taskFactory
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|brokerService
argument_list|,
name|destination
argument_list|,
name|store
argument_list|,
name|parentStats
argument_list|,
name|taskFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|tempDest
operator|=
operator|(
name|ActiveMQTempDestination
operator|)
name|destination
expr_stmt|;
block|}
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|messages
operator|=
operator|new
name|VMPendingMessageCursor
argument_list|()
expr_stmt|;
name|this
operator|.
name|systemUsage
operator|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
expr_stmt|;
name|memoryUsage
operator|.
name|setParent
argument_list|(
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskRunner
operator|=
name|taskFactory
operator|.
name|createTaskRunner
argument_list|(
name|this
argument_list|,
literal|"TempQueue:  "
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
comment|// However, we could have failed over - and we do this
comment|// check client side anyways ....
if|if
condition|(
operator|!
name|context
operator|.
name|isFaultTolerant
argument_list|()
operator|&&
operator|(
operator|!
name|context
operator|.
name|isNetworkConnection
argument_list|()
operator|&&
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
operator|)
condition|)
block|{
name|tempDest
operator|.
name|setConnectionId
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
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|" changed ownership of "
operator|+
name|this
operator|+
literal|" to "
operator|+
name|tempDest
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|wakeup
parameter_list|()
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|messages
init|)
block|{
name|result
operator|=
operator|!
name|messages
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|result
condition|)
block|{
try|try
block|{
name|pageInMessages
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to page in more queue messages "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|messagesWaitingForSpace
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|isRecoveryDispatchEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|taskRunner
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Task Runner failed to wakeup "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

