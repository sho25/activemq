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
name|group
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
name|MessageConsumer
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
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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
name|CombinationTestSupport
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
name|JmsTestSupport
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

begin_class
specifier|public
class|class
name|MessageGroupTest
extends|extends
name|JmsTestSupport
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
name|CombinationTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testGroupedMessagesDeliveredToOnlyOneConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
comment|// Setup a first connection
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
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
decl_stmt|;
name|MessageConsumer
name|consumer1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
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
comment|// Send the messages.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
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
literal|"message "
operator|+
name|i
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
literal|"TEST-GROUP"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sending message: "
operator|+
name|message
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
comment|// All the messages should have been sent down connection 1.. just get
comment|// the first 3
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|m1
init|=
operator|(
name|TextMessage
operator|)
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"m1 is null for index: "
operator|+
name|i
argument_list|,
name|m1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m1
operator|.
name|getIntProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|)
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Setup a second connection
name|Connection
name|connection1
init|=
name|factory
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|connection1
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session2
init|=
name|connection1
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer2
init|=
name|session2
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// Close the first consumer.
name|consumer1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// The last messages should now go the the second consumer.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|m1
init|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"m1 is null for index: "
operator|+
name|i
argument_list|,
name|m1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m1
operator|.
name|getIntProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|)
argument_list|,
literal|4
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
comment|//assert that there are no other messages left for the consumer 2
name|Message
name|m
init|=
name|consumer2
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"consumer 2 has some messages left"
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddingConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
comment|// Setup a first connection
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
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
comment|//MessageConsumer consumer = session.createConsumer(destination);
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"message"
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
literal|"TEST-GROUP"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sending message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
name|msg
operator|.
name|getBooleanProperty
argument_list|(
literal|"JMSXGroupFirstForConsumer"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|first
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testClosingMessageGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
comment|// Setup a first connection
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
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
decl_stmt|;
name|MessageConsumer
name|consumer1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
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
comment|// Send the messages.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
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
literal|"message "
operator|+
name|i
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
literal|"TEST-GROUP"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sending message: "
operator|+
name|message
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
comment|// All the messages should have been sent down consumer1.. just get
comment|// the first 3
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|m1
init|=
operator|(
name|TextMessage
operator|)
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"m1 is null for index: "
operator|+
name|i
argument_list|,
name|m1
argument_list|)
expr_stmt|;
block|}
comment|// Setup a second consumer
name|Connection
name|connection1
init|=
name|factory
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|connection1
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session2
init|=
name|connection1
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer2
init|=
name|session2
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|//assert that there are no messages for the consumer 2
name|Message
name|m
init|=
name|consumer2
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"consumer 2 has some messages"
argument_list|,
name|m
argument_list|)
expr_stmt|;
comment|// Close the group
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"message "
operator|+
literal|5
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
literal|"TEST-GROUP"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sending message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|//Send some more messages
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"message "
operator|+
name|i
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
literal|"TEST-GROUP"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sending message: "
operator|+
name|message
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
comment|// Receive the fourth message
name|TextMessage
name|m1
init|=
operator|(
name|TextMessage
operator|)
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"m1 is null for index: "
operator|+
literal|4
argument_list|,
name|m1
argument_list|)
expr_stmt|;
comment|// Receive the closing message
name|m1
operator|=
operator|(
name|TextMessage
operator|)
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"m1 is null for index: "
operator|+
literal|5
argument_list|,
name|m1
argument_list|)
expr_stmt|;
comment|//assert that there are no messages for the consumer 1
name|m
operator|=
name|consumer1
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"consumer 1 has some messages left"
argument_list|,
name|m
argument_list|)
expr_stmt|;
comment|// The messages should now go to the second consumer.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|m1
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"m1 is null for index: "
operator|+
name|i
argument_list|,
name|m1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

