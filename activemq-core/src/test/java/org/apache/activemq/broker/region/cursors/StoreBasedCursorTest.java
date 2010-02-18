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
operator|.
name|cursors
package|;
end_package

begin_comment
comment|/**  * A StoreBasedCursorTest  *  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ActiveMQConnectionFactory
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
name|apache
operator|.
name|activemq
operator|.
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_class
specifier|public
class|class
name|StoreBasedCursorTest
extends|extends
name|TestCase
block|{
specifier|protected
name|String
name|bindAddress
init|=
literal|"tcp://localhost:60706"
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|Queue
name|queue
decl_stmt|;
name|int
name|messageSize
init|=
literal|1024
decl_stmt|;
comment|// actual message is messageSize*2, and 4*MessageSize would allow 2 messages be delivered, but the flush of the cache is async so the flush
comment|// triggered on 2nd message maxing out the usage may not be in effect for the 3rd message to succeed. Making the memory usage more lenient
comment|// gives the usageChange listener in the cursor an opportunity to kick in.
name|int
name|memoryLimit
init|=
literal|12
operator|*
name|messageSize
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?jms.alwaysSyncSend=true"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|queue
operator|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"QUEUE."
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|long
name|memoryLimit
parameter_list|,
name|long
name|systemLimit
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SystemUsage
name|systemUsage
init|=
name|broker
operator|.
name|getSystemUsage
argument_list|()
decl_stmt|;
name|systemUsage
operator|.
name|setSendFailIfNoSpace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|systemLimit
argument_list|)
expr_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setProducerFlowControl
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setUseCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|createMessageText
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|messageSize
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Message: "
operator|+
name|index
operator|+
literal|" sent at: "
operator|+
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
name|messageSize
condition|)
block|{
return|return
name|buffer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|messageSize
argument_list|)
return|;
block|}
for|for
control|(
name|int
name|i
init|=
name|buffer
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|messageSize
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|int
name|deliveryMode
parameter_list|)
throws|throws
name|Exception
block|{
name|start
argument_list|()
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|createMessageText
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|ResourceAllocationException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" num msgs = "
operator|+
name|i
operator|+
literal|". percentUsage = "
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// use QueueStorePrefetch
specifier|public
name|void
name|testTwoUsageEqualPersistent
parameter_list|()
throws|throws
name|Exception
block|{
name|configureBroker
argument_list|(
name|memoryLimit
argument_list|,
name|memoryLimit
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUseCachePersistent
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|limit
init|=
name|memoryLimit
operator|/
literal|2
decl_stmt|;
name|configureBroker
argument_list|(
name|limit
argument_list|,
name|memoryLimit
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMemoryUsageLowPersistent
parameter_list|()
throws|throws
name|Exception
block|{
name|configureBroker
argument_list|(
name|memoryLimit
argument_list|,
literal|10
operator|*
name|memoryLimit
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
comment|// use FilePendingMessageCursor
specifier|public
name|void
name|testTwoUsageEqualNonPersistent
parameter_list|()
throws|throws
name|Exception
block|{
name|configureBroker
argument_list|(
name|memoryLimit
argument_list|,
name|memoryLimit
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMemoryUsageLowNonPersistent
parameter_list|()
throws|throws
name|Exception
block|{
name|configureBroker
argument_list|(
name|memoryLimit
argument_list|,
literal|10
operator|*
name|memoryLimit
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

