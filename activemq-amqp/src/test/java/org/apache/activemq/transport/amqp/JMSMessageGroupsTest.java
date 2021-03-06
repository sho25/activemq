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
name|transport
operator|.
name|amqp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|JMSMessageGroupsTest
extends|extends
name|JMSClientTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JMSMessageGroupsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ITERATIONS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGE_SIZE
init|=
literal|200
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|RECEIVE_TIMEOUT
init|=
literal|3000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JMSX_GROUP_ID
init|=
literal|"JmsGroupsTest"
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testGroupSeqIsNeverLost
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicInteger
name|sequenceCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
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
name|ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
block|{
name|sendMessagesToBroker
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|sequenceCounter
argument_list|)
expr_stmt|;
name|readMessagesOnBroker
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|readMessagesOnBroker
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
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
name|MESSAGE_COUNT
condition|;
operator|++
name|i
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Read message #{}: type = {}"
argument_list|,
name|i
argument_list|,
name|message
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|gid
init|=
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|)
decl_stmt|;
name|String
name|seq
init|=
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Message assigned JMSXGroupID := {}"
argument_list|,
name|gid
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Message assigned JMSXGroupSeq := {}"
argument_list|,
name|seq
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|sendMessagesToBroker
parameter_list|(
name|int
name|count
parameter_list|,
name|AtomicInteger
name|sequence
parameter_list|)
throws|throws
name|Exception
block|{
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
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
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|MESSAGE_SIZE
index|]
decl_stmt|;
for|for
control|(
name|count
operator|=
literal|0
init|;
name|count
operator|<
name|MESSAGE_SIZE
condition|;
name|count
operator|++
control|)
block|{
name|String
name|s
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|count
operator|%
literal|10
argument_list|)
decl_stmt|;
name|Character
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|value
init|=
name|c
operator|.
name|charValue
argument_list|()
decl_stmt|;
name|buffer
index|[
name|count
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending {} messages to destination: {}"
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|queue
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setJMSDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
name|JMSX_GROUP_ID
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|,
name|sequence
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
name|buffer
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
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

