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
name|BatchedMessagePriorityConsumerTest
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
name|BatchedMessagePriorityConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testBatchWithLowPriorityFirstAndClientSupport
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestBatchWithLowPriorityFirst
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBatchWithLowPriorityFirstAndClientSupportOff
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestBatchWithLowPriorityFirst
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestBatchWithLowPriorityFirst
parameter_list|(
name|boolean
name|clientPrioritySupport
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setMessagePrioritySupported
argument_list|(
name|clientPrioritySupport
argument_list|)
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
name|ActiveMQDestination
name|destination
init|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
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
name|producer
operator|.
name|setPriority
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|session
argument_list|,
name|producer
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|MessageProducer
name|producer2
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer2
operator|.
name|setPriority
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|session
argument_list|,
name|producer2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|producer2
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|connection
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
name|MessageConsumer
name|messageConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MessageID: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|consumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// should be nothing left
name|consumerSession
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
name|messageConsumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"No message left"
argument_list|,
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

