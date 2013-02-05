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
name|bugs
package|;
end_package

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
name|ConnectionFactory
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
name|JMSException
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
name|ResourceAllocationException
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
name|ActiveMQConnection
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
name|transport
operator|.
name|RequestTimedOutIOException
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
name|JmsTimeoutTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JmsTimeoutTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|messageSize
init|=
literal|1024
operator|*
literal|64
decl_stmt|;
specifier|private
specifier|final
name|int
name|messageCount
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|exceptionCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**      * Test the case where the broker is blocked due to a memory limit      * and a producer timeout is set on the connection.      * @throws Exception      */
specifier|public
name|void
name|testBlockedProducerConnectionTimeout
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnection
name|cx
init|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
decl_stmt|;
specifier|final
name|ActiveMQDestination
name|queue
init|=
name|createDestination
argument_list|(
literal|"testqueue"
argument_list|)
decl_stmt|;
comment|// we should not take longer than 10 seconds to return from send
name|cx
operator|.
name|setSendTimeout
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|Runnable
name|r
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sender thread starting"
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|cx
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
literal|1
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
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|createMessageText
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|messageCount
condition|;
name|count
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Done sending.."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|RequestTimedOutIOException
condition|)
block|{
name|exceptionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
block|}
block|}
decl_stmt|;
name|cx
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|producerThread
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|producerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerThread
operator|.
name|join
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|cx
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// We should have a few timeout exceptions as memory store will fill up
name|assertTrue
argument_list|(
literal|"No exception from the broker"
argument_list|,
name|exceptionCount
operator|.
name|get
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test the case where the broker is blocked due to a memory limit      * with a fail timeout      * @throws Exception      */
specifier|public
name|void
name|testBlockedProducerUsageSendFailTimeout
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnection
name|cx
init|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
decl_stmt|;
specifier|final
name|ActiveMQDestination
name|queue
init|=
name|createDestination
argument_list|(
literal|"testqueue"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|setSendFailIfNoSpaceAfterTimeout
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|Runnable
name|r
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sender thread starting"
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|cx
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
literal|1
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
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|createMessageText
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|messageCount
condition|;
name|count
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Done sending.."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ResourceAllocationException
operator|||
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|RequestTimedOutIOException
condition|)
block|{
name|exceptionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
block|}
block|}
decl_stmt|;
name|cx
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|producerThread
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|producerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerThread
operator|.
name|join
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|cx
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// We should have a few timeout exceptions as memory store will fill up
name|assertTrue
argument_list|(
literal|"No exception from the broker"
argument_list|,
name|exceptionCount
operator|.
name|get
argument_list|()
operator|>
literal|0
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
name|exceptionCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bindAddress
operator|=
literal|"tcp://localhost:0"
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|5
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|createMessageText
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"<filler>"
argument_list|)
expr_stmt|;
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
literal|'X'
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"</filler>"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
