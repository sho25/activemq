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
name|usecases
package|;
end_package

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
name|Message
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
name|Session
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
name|EmbeddedBrokerTestSupport
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
name|Queue
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
name|NonCachedMessageEvaluationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This unit test creates a fixed size queue and moves the last message in the  * queue to another queue. The test is used to very the performance of  * {@link org.apache.activemq.broker.region.Queue#moveMatchingMessagesTo(org.apache.activemq.broker.ConnectionContext, String, org.apache.activemq.command.ActiveMQDestination)}.  */
end_comment

begin_class
specifier|public
class|class
name|LargeQueueSparseDeleteTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LargeQueueSparseDeleteTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|useTopic
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**      * The test queue is filled with QUEUE_SIZE test messages, each with a      * numeric id property beginning at 0. Once the queue is filled, the last      * message (id = QUEUE_SIZE-1) is moved to another queue. The test succeeds      * if the move completes within TEST_TIMEOUT milliseconds.      *      * @throws Exception      */
specifier|public
name|void
name|testMoveMessages
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|QUEUE_SIZE
init|=
literal|30000
decl_stmt|;
specifier|final
name|String
name|MOVE_TO_DESTINATION_NAME
init|=
name|getDestinationString
argument_list|()
operator|+
literal|".dest"
decl_stmt|;
specifier|final
name|long
name|TEST_TIMEOUT
init|=
literal|10000
decl_stmt|;
comment|// Populate a test queue with uniquely-identifiable messages.
name|Connection
name|conn
init|=
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|QUEUE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Access the implementation of the test queue and move the last message
comment|// to another queue. Verify that the move occurred within the limits of
comment|// the test.
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|(
operator|new
name|NonCachedMessageEvaluationContext
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
operator|.
name|getBroker
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getMessageEvaluationContext
argument_list|()
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|long
name|startTimeMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|moveMatchingMessagesTo
argument_list|(
name|context
argument_list|,
literal|"id="
operator|+
operator|(
name|QUEUE_SIZE
operator|-
literal|1
operator|)
argument_list|,
name|createDestination
argument_list|(
name|MOVE_TO_DESTINATION_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|durationMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTimeMillis
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"It took "
operator|+
name|durationMillis
operator|+
literal|"ms to move the last message from a queue a "
operator|+
name|QUEUE_SIZE
operator|+
literal|" messages."
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Moving the message took too long: "
operator|+
name|durationMillis
operator|+
literal|"ms"
argument_list|,
name|durationMillis
operator|<
name|TEST_TIMEOUT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCopyMessages
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|QUEUE_SIZE
init|=
literal|30000
decl_stmt|;
specifier|final
name|String
name|MOVE_TO_DESTINATION_NAME
init|=
name|getDestinationString
argument_list|()
operator|+
literal|".dest"
decl_stmt|;
specifier|final
name|long
name|TEST_TIMEOUT
init|=
literal|10000
decl_stmt|;
comment|// Populate a test queue with uniquely-identifiable messages.
name|Connection
name|conn
init|=
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|QUEUE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Access the implementation of the test queue and move the last message
comment|// to another queue. Verify that the move occurred within the limits of
comment|// the test.
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|(
operator|new
name|NonCachedMessageEvaluationContext
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
operator|.
name|getBroker
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getMessageEvaluationContext
argument_list|()
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|long
name|startTimeMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|copyMatchingMessagesTo
argument_list|(
name|context
argument_list|,
literal|"id="
operator|+
operator|(
name|QUEUE_SIZE
operator|-
literal|1
operator|)
argument_list|,
name|createDestination
argument_list|(
name|MOVE_TO_DESTINATION_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|durationMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTimeMillis
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"It took "
operator|+
name|durationMillis
operator|+
literal|"ms to copy the last message from a queue a "
operator|+
name|QUEUE_SIZE
operator|+
literal|" messages."
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Copying the message took too long: "
operator|+
name|durationMillis
operator|+
literal|"ms"
argument_list|,
name|durationMillis
operator|<
name|TEST_TIMEOUT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRemoveMessages
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|QUEUE_SIZE
init|=
literal|30000
decl_stmt|;
specifier|final
name|long
name|TEST_TIMEOUT
init|=
literal|10000
decl_stmt|;
comment|// Populate a test queue with uniquely-identifiable messages.
name|Connection
name|conn
init|=
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|QUEUE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Access the implementation of the test queue and move the last message
comment|// to another queue. Verify that the move occurred within the limits of
comment|// the test.
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|(
operator|new
name|NonCachedMessageEvaluationContext
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
operator|.
name|getBroker
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getMessageEvaluationContext
argument_list|()
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|long
name|startTimeMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|removeMatchingMessages
argument_list|(
literal|"id="
operator|+
operator|(
name|QUEUE_SIZE
operator|-
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|durationMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTimeMillis
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"It took "
operator|+
name|durationMillis
operator|+
literal|"ms to remove the last message from a queue a "
operator|+
name|QUEUE_SIZE
operator|+
literal|" messages."
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Removing the message took too long: "
operator|+
name|durationMillis
operator|+
literal|"ms"
argument_list|,
name|durationMillis
operator|<
name|TEST_TIMEOUT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
