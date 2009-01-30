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
name|test
operator|.
name|rollback
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
name|ConnectionFactory
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
name|EmbeddedBrokerTestSupport
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

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|core
operator|.
name|MessageCreator
import|;
end_import

begin_class
specifier|public
class|class
name|CloseRollbackRedeliveryQueueTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CloseRollbackRedeliveryQueueTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|numberOfMessagesOnQueue
init|=
literal|1
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|public
name|void
name|testVerifySessionCloseRedeliveryWithFailoverTransport
parameter_list|()
throws|throws
name|Throwable
block|{
name|Session
name|session
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
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|message
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got message "
operator|+
name|message
argument_list|)
expr_stmt|;
comment|// close will rollback the current tx
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|=
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
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivered message"
argument_list|,
name|id
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|message
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVerifyConsumerAndSessionCloseRedeliveryWithFailoverTransport
parameter_list|()
throws|throws
name|Throwable
block|{
name|Session
name|session
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
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|message
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got message "
operator|+
name|message
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|=
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
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivered message"
argument_list|,
name|id
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|message
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVerifyConsumerCloseSessionRollbackRedeliveryWithFailoverTransport
parameter_list|()
throws|throws
name|Throwable
block|{
name|Session
name|session
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
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|message
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got message "
operator|+
name|message
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivered message"
argument_list|,
name|id
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|message
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// lets fill the queue up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfMessagesOnQueue
condition|;
name|i
operator|++
control|)
block|{
name|template
operator|.
name|send
argument_list|(
name|createMessageCreator
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// failover: enables message audit - which could get in the way of redelivery
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:"
operator|+
name|bindAddress
argument_list|)
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|MessageCreator
name|createMessageCreator
parameter_list|(
specifier|final
name|int
name|i
parameter_list|)
block|{
return|return
operator|new
name|MessageCreator
argument_list|()
block|{
specifier|public
name|Message
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|TextMessage
name|answer
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Message: "
operator|+
name|i
argument_list|)
decl_stmt|;
name|answer
operator|.
name|setIntProperty
argument_list|(
literal|"Counter"
argument_list|,
name|i
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

