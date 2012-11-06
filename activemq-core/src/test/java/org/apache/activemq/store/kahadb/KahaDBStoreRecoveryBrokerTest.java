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
name|store
operator|.
name|kahadb
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|RecoveryBrokerTest
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
name|StubConnection
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Used to verify that recovery works correctly against   *   *   */
end_comment

begin_class
specifier|public
class|class
name|KahaDBStoreRecoveryBrokerTest
extends|extends
name|RecoveryBrokerTest
block|{
enum|enum
name|CorruptionType
block|{
name|None
block|,
name|FailToLoad
block|,
name|LoadInvalid
block|,
name|LoadCorrupt
block|}
empty_stmt|;
specifier|public
name|CorruptionType
name|failTest
init|=
name|CorruptionType
operator|.
name|None
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb"
argument_list|)
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|BrokerService
name|createRestartedBroker
parameter_list|()
throws|throws
name|Exception
block|{
comment|// corrupting index
name|File
name|index
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb/db.data"
argument_list|)
decl_stmt|;
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|index
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|failTest
condition|)
block|{
case|case
name|FailToLoad
case|:
name|index
operator|.
name|delete
argument_list|()
expr_stmt|;
name|raf
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|index
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|raf
operator|.
name|seek
argument_list|(
name|index
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|raf
operator|.
name|writeBytes
argument_list|(
literal|"corrupt"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LoadInvalid
case|:
comment|// page size 0
name|raf
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|raf
operator|.
name|writeBytes
argument_list|(
literal|"corrupt and cannot load metadata"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LoadCorrupt
case|:
comment|// loadable but invalid metadata
comment|// location of order index low priority index for first destination...
name|raf
operator|.
name|seek
argument_list|(
literal|8
operator|*
literal|1024
operator|+
literal|57
argument_list|)
expr_stmt|;
name|raf
operator|.
name|writeLong
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|10
argument_list|)
expr_stmt|;
break|break;
default|default:
block|}
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// starting broker
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
comment|// uncomment if you want to test archiving
comment|//kaha.setArchiveCorruptedIndex(true);
name|kaha
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|KahaDBStoreRecoveryBrokerTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestLargeQueuePersistentMessagesNotLostOnRestart
parameter_list|()
block|{
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"failTest"
argument_list|,
operator|new
name|CorruptionType
index|[]
block|{
name|CorruptionType
operator|.
name|FailToLoad
block|,
name|CorruptionType
operator|.
name|LoadInvalid
block|,
name|CorruptionType
operator|.
name|LoadCorrupt
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLargeQueuePersistentMessagesNotLostOnRestart
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
comment|// Setup the producer and send the message.
name|StubConnection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo
argument_list|)
decl_stmt|;
name|ProducerInfo
name|producerInfo
init|=
name|createProducerInfo
argument_list|(
name|sessionInfo
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|producerInfo
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|MESSAGE_COUNT
init|=
literal|10000
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|request
argument_list|(
name|closeConnectionInfo
argument_list|(
name|connectionInfo
argument_list|)
argument_list|)
expr_stmt|;
comment|// restart the broker.
name|restartBroker
argument_list|()
expr_stmt|;
comment|// Setup the consumer and receive the message.
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connectionInfo
operator|=
name|createConnectionInfo
argument_list|()
expr_stmt|;
name|sessionInfo
operator|=
name|createSessionInfo
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
name|producerInfo
operator|=
name|createProducerInfo
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|producerInfo
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MESSAGE_COUNT
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|m
init|=
name|receiveMessage
argument_list|(
name|connection
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received message "
operator|+
name|expected
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|" by now!"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|,
name|m
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|MessageAck
name|ack
init|=
name|createAck
argument_list|(
name|consumerInfo
argument_list|,
name|m
argument_list|,
literal|1
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|request
argument_list|(
name|closeConnectionInfo
argument_list|(
name|connectionInfo
argument_list|)
argument_list|)
expr_stmt|;
comment|// restart the broker.
name|restartBroker
argument_list|()
expr_stmt|;
comment|// Setup the consumer and receive the message.
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connectionInfo
operator|=
name|createConnectionInfo
argument_list|()
expr_stmt|;
name|sessionInfo
operator|=
name|createSessionInfo
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|consumerInfo
operator|=
name|createConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MESSAGE_COUNT
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|m
init|=
name|receiveMessage
argument_list|(
name|connection
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received message "
operator|+
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|+
literal|" by now!"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|m
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|MessageAck
name|ack
init|=
name|createAck
argument_list|(
name|consumerInfo
argument_list|,
name|m
argument_list|,
literal|1
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|request
argument_list|(
name|closeConnectionInfo
argument_list|(
name|connectionInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

