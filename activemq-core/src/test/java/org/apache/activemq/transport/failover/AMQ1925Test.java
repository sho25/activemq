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
name|failover
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|TransportConnector
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
name|Destination
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
name|command
operator|.
name|ActiveMQQueue
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
name|util
operator|.
name|ServiceStopper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * TestCase showing the message-destroying described in AMQ-1925  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|AMQ1925Test
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AMQ1925Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"test.amq1925"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROPERTY_MSG_NUMBER
init|=
literal|"NUMBER"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10000
decl_stmt|;
specifier|private
name|BrokerService
name|bs
decl_stmt|;
specifier|private
name|URI
name|tcpUri
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|cf
decl_stmt|;
specifier|public
name|void
name|XtestAMQ1925_TXInProgress
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
comment|// The runnable is likely to interrupt during the session#commit, since
comment|// this takes the longest
specifier|final
name|Object
name|starter
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|restarted
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
operator|new
name|Thread
argument_list|(
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
synchronized|synchronized
init|(
name|starter
init|)
block|{
name|starter
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
comment|// Simulate broker failure& restart
name|bs
operator|.
name|stop
argument_list|()
expr_stmt|;
name|bs
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|addConnector
argument_list|(
name|tcpUri
argument_list|)
expr_stmt|;
name|bs
operator|.
name|start
argument_list|()
expr_stmt|;
name|restarted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|starter
init|)
block|{
name|starter
operator|.
name|notifyAll
argument_list|()
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No Message "
operator|+
name|i
operator|+
literal|" found"
argument_list|,
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|10
condition|)
name|assertFalse
argument_list|(
literal|"Timing problem, restarted too soon"
argument_list|,
name|restarted
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|10
condition|)
block|{
synchronized|synchronized
init|(
name|starter
init|)
block|{
name|starter
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|i
operator|>
name|MESSAGE_COUNT
operator|-
literal|100
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Timing problem, restarted too late"
argument_list|,
name|restarted
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|i
argument_list|,
name|message
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_MSG_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertQueueEmpty
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|XtestAMQ1925_TXInProgress_TwoConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session1
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
name|consumer1
init|=
name|session1
operator|.
name|createConsumer
argument_list|(
name|session1
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|Session
name|session2
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
name|consumer2
init|=
name|session2
operator|.
name|createConsumer
argument_list|(
name|session2
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
comment|// The runnable is likely to interrupt during the session#commit, since
comment|// this takes the longest
specifier|final
name|Object
name|starter
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|restarted
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
operator|new
name|Thread
argument_list|(
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
synchronized|synchronized
init|(
name|starter
init|)
block|{
name|starter
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
comment|// Simulate broker failure& restart
name|bs
operator|.
name|stop
argument_list|()
expr_stmt|;
name|bs
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|addConnector
argument_list|(
name|tcpUri
argument_list|)
expr_stmt|;
name|bs
operator|.
name|start
argument_list|()
expr_stmt|;
name|restarted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|starter
init|)
block|{
name|starter
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
name|Collection
argument_list|<
name|Integer
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|MESSAGE_COUNT
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
name|i
operator|++
control|)
block|{
name|Message
name|message1
init|=
name|consumer1
operator|.
name|receive
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|Message
name|message2
init|=
name|consumer2
operator|.
name|receive
argument_list|(
literal|20
argument_list|)
decl_stmt|;
if|if
condition|(
name|message1
operator|==
literal|null
operator|&&
name|message2
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|results
operator|.
name|size
argument_list|()
operator|<
name|MESSAGE_COUNT
condition|)
block|{
name|message1
operator|=
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|message2
operator|=
name|consumer2
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
if|if
condition|(
name|message1
operator|==
literal|null
operator|&&
name|message2
operator|==
literal|null
condition|)
block|{
comment|// Missing messages
break|break;
block|}
block|}
break|break;
block|}
if|if
condition|(
name|i
operator|<
literal|10
condition|)
name|assertFalse
argument_list|(
literal|"Timing problem, restarted too soon"
argument_list|,
name|restarted
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|10
condition|)
block|{
synchronized|synchronized
init|(
name|starter
init|)
block|{
name|starter
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|i
operator|>
name|MESSAGE_COUNT
operator|-
literal|50
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Timing problem, restarted too late"
argument_list|,
name|restarted
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message1
operator|!=
literal|null
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|message1
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_MSG_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|session1
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|message2
operator|!=
literal|null
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|message2
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_MSG_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|session2
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|assertNull
argument_list|(
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer2
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|consumer1
operator|.
name|close
argument_list|()
expr_stmt|;
name|session1
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer2
operator|.
name|close
argument_list|()
expr_stmt|;
name|session2
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|foundMissingMessages
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|size
argument_list|()
operator|<
name|MESSAGE_COUNT
condition|)
block|{
name|foundMissingMessages
operator|=
name|tryToFetchMissingMessages
argument_list|()
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"Message-Nr "
operator|+
name|i
operator|+
literal|" not found ("
operator|+
name|results
operator|.
name|size
argument_list|()
operator|+
literal|" total, "
operator|+
name|foundMissingMessages
operator|+
literal|" have been found 'lingering' in the queue)"
argument_list|,
name|results
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertQueueEmpty
argument_list|()
expr_stmt|;
block|}
specifier|private
name|int
name|tryToFetchMissingMessages
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
literal|true
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
break|break;
name|log
operator|.
name|info
argument_list|(
literal|"Found \"missing\" message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|count
return|;
block|}
specifier|public
name|void
name|testAMQ1925_TXBegin
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
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
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|222
condition|)
block|{
comment|// Simulate broker failure& restart
name|bs
operator|.
name|stop
argument_list|()
expr_stmt|;
name|bs
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|addConnector
argument_list|(
name|tcpUri
argument_list|)
expr_stmt|;
name|bs
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|i
argument_list|,
name|message
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_MSG_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertQueueEmpty
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testAMQ1925_TXCommited
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
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
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|message
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_MSG_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|222
condition|)
block|{
comment|// Simulate broker failure& restart
name|bs
operator|.
name|stop
argument_list|()
expr_stmt|;
name|bs
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|addConnector
argument_list|(
name|tcpUri
argument_list|)
expr_stmt|;
name|bs
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertQueueEmpty
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|assertQueueEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertQueueLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertQueueLength
parameter_list|(
name|int
name|len
parameter_list|)
throws|throws
name|Exception
throws|,
name|IOException
block|{
name|Set
argument_list|<
name|Destination
argument_list|>
name|destinations
init|=
name|bs
operator|.
name|getBroker
argument_list|()
operator|.
name|getDestinations
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destinations
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|len
argument_list|,
name|queue
operator|.
name|getMessageStore
argument_list|()
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessagesToQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
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
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test message "
operator|+
name|i
argument_list|)
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
name|PROPERTY_MSG_NUMBER
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
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|assertQueueLength
argument_list|(
name|MESSAGE_COUNT
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
name|bs
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|bs
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|bs
operator|.
name|start
argument_list|()
expr_stmt|;
name|tcpUri
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
name|cf
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover://("
operator|+
name|tcpUri
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|sendMessagesToQueue
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|ServiceStopper
argument_list|()
operator|.
name|stop
argument_list|(
name|bs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

