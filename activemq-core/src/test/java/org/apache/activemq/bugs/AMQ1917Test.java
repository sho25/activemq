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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|CountDownLatch
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|TimeUnit
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
name|Destination
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ1917Test
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MESSAGES
init|=
literal|4000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_THREADS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REQUEST_QUEUE
init|=
literal|"mock.in.queue"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REPLY_QUEUE
init|=
literal|"mock.out.queue"
decl_stmt|;
specifier|private
name|Destination
name|requestDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|REQUEST_QUEUE
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
decl_stmt|;
specifier|private
name|Destination
name|replyDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|REPLY_QUEUE
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
decl_stmt|;
specifier|private
name|CountDownLatch
name|roundTripLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUM_MESSAGES
argument_list|)
decl_stmt|;
specifier|private
name|CountDownLatch
name|errorLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|ThreadPoolExecutor
name|tpe
decl_stmt|;
specifier|private
specifier|final
name|String
name|BROKER_URL
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
specifier|private
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|working
init|=
literal|true
decl_stmt|;
comment|// trival session/producer pool
specifier|final
name|Session
index|[]
name|sessions
init|=
operator|new
name|Session
index|[
name|NUM_THREADS
index|]
decl_stmt|;
specifier|final
name|MessageProducer
index|[]
name|producers
init|=
operator|new
name|MessageProducer
index|[
name|NUM_THREADS
index|]
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|BROKER_URL
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionUri
operator|=
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
expr_stmt|;
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|tpe
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|NUM_THREADS
argument_list|,
name|NUM_THREADS
argument_list|,
literal|60000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|ThreadFactory
name|limitedthreadFactory
init|=
operator|new
name|LimitedThreadFactory
argument_list|(
name|tpe
operator|.
name|getThreadFactory
argument_list|()
argument_list|)
decl_stmt|;
name|tpe
operator|.
name|setThreadFactory
argument_list|(
name|limitedthreadFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|tpe
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testLoadedSendRecieveWithCorrelationId
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|connectionFactory
operator|.
name|setBrokerURL
argument_list|(
name|connectionUri
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|setupReceiver
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// trival session/producer pool
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|sessions
index|[
name|i
index|]
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
name|producers
index|[
name|i
index|]
operator|=
name|sessions
index|[
name|i
index|]
operator|.
name|createProducer
argument_list|(
name|requestDestination
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_MESSAGES
condition|;
name|i
operator|++
control|)
block|{
name|MessageSenderReceiver
name|msr
init|=
operator|new
name|MessageSenderReceiver
argument_list|(
name|requestDestination
argument_list|,
name|replyDestination
argument_list|,
literal|"Test Message : "
operator|+
name|i
argument_list|)
decl_stmt|;
name|tpe
operator|.
name|execute
argument_list|(
name|msr
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|roundTripLatch
operator|.
name|await
argument_list|(
literal|4000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
if|if
condition|(
name|errorLatch
operator|.
name|await
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"there was an error, check the console for thread or thread allocation failure"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|working
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
name|void
name|setupReceiver
parameter_list|(
specifier|final
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
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
specifier|final
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|requestDestination
argument_list|)
decl_stmt|;
specifier|final
name|MessageProducer
name|sender
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|replyDestination
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|working
condition|)
block|{
comment|// wait for messages in infinitive loop
comment|// time out is set to show the client is awaiting
try|try
block|{
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|20000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|errorLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Response timed out."
operator|+
literal|" latchCount="
operator|+
name|roundTripLatch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|result
init|=
name|msg
operator|.
name|getText
argument_list|()
decl_stmt|;
comment|//System.out.println("Request:" + (i++)
comment|//        + ", msg=" + result + ", ID" + msg.getJMSMessageID());
name|TextMessage
name|response
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|response
operator|.
name|setJMSCorrelationID
argument_list|(
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setText
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
if|if
condition|(
name|working
condition|)
block|{
name|errorLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Unexpected exception:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
class|class
name|MessageSenderReceiver
implements|implements
name|Runnable
block|{
name|Destination
name|reqDest
decl_stmt|;
name|Destination
name|replyDest
decl_stmt|;
name|String
name|origMsg
decl_stmt|;
specifier|public
name|MessageSenderReceiver
parameter_list|(
name|Destination
name|reqDest
parameter_list|,
name|Destination
name|replyDest
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|replyDest
operator|=
name|replyDest
expr_stmt|;
name|this
operator|.
name|reqDest
operator|=
name|reqDest
expr_stmt|;
name|this
operator|.
name|origMsg
operator|=
name|msg
expr_stmt|;
block|}
specifier|private
name|int
name|getIndexFromCurrentThread
parameter_list|()
block|{
name|String
name|name
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|num
init|=
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|num
argument_list|)
operator|-
literal|1
decl_stmt|;
name|assertTrue
argument_list|(
literal|"idx is in range: idx="
operator|+
name|idx
argument_list|,
name|idx
operator|<
name|NUM_THREADS
argument_list|)
expr_stmt|;
return|return
name|idx
return|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// get thread session and producer from pool
name|int
name|threadIndex
init|=
name|getIndexFromCurrentThread
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|sessions
index|[
name|threadIndex
index|]
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producers
index|[
name|threadIndex
index|]
decl_stmt|;
specifier|final
name|Message
name|sendJmsMsg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|origMsg
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sendJmsMsg
argument_list|)
expr_stmt|;
name|String
name|jmsId
init|=
name|sendJmsMsg
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|String
name|selector
init|=
literal|"JMSCorrelationID='"
operator|+
name|jmsId
operator|+
literal|"'"
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|replyDest
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|Message
name|receiveJmsMsg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|receiveJmsMsg
operator|==
literal|null
condition|)
block|{
name|errorLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Unable to receive response for:"
operator|+
name|origMsg
operator|+
literal|", with selector="
operator|+
name|selector
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("received response message :"
comment|//        + ((TextMessage) receiveJmsMsg).getText()
comment|//        + " with selector : " + selector);
name|roundTripLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"unexpected exception:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
class|class
name|LimitedThreadFactory
implements|implements
name|ThreadFactory
block|{
name|int
name|threadCount
decl_stmt|;
specifier|private
name|ThreadFactory
name|factory
decl_stmt|;
specifier|public
name|LimitedThreadFactory
parameter_list|(
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|threadFactory
expr_stmt|;
block|}
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|arg0
parameter_list|)
block|{
if|if
condition|(
operator|++
name|threadCount
operator|>
name|NUM_THREADS
condition|)
block|{
name|errorLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"too many threads requested"
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
operator|.
name|newThread
argument_list|(
name|arg0
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

