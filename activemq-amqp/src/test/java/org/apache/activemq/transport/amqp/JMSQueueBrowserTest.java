begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|QueueBrowser
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
name|broker
operator|.
name|jmx
operator|.
name|QueueViewMBean
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
name|junit
operator|.
name|ActiveMQTestRunner
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
name|junit
operator|.
name|Repeat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|jms
operator|.
name|JmsConnectionFactory
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
comment|/**  * Tests for various QueueBrowser scenarios with an AMQP JMS client.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|ActiveMQTestRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|JMSQueueBrowserTest
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
name|JMSClientTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
annotation|@
name|Repeat
argument_list|(
name|repetitions
operator|=
literal|5
argument_list|)
specifier|public
name|void
name|testBrowseAllInQueueZeroPrefetch
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|MSG_COUNT
init|=
literal|5
decl_stmt|;
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|(
literal|"jms.prefetchPolicy.all=0"
argument_list|)
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
name|sendMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|MSG_COUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|browser
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|MSG_COUNT
operator|&&
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Recv: {}"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received all expected message, checking that hasMoreElements returns false"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|40000
argument_list|)
specifier|public
name|void
name|testCreateQueueBrowser
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|browser
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|40000
argument_list|)
specifier|public
name|void
name|testNoMessagesBrowserHasNoElements
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|browser
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testBroseOneInQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Message
name|m
init|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|m
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Browsed message {} from Queue {}"
argument_list|,
name|m
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
name|browser
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
annotation|@
name|Repeat
argument_list|(
name|repetitions
operator|=
literal|5
argument_list|)
specifier|public
name|void
name|testBrowseAllInQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
name|sendMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|browser
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Recv: {}"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
annotation|@
name|Repeat
argument_list|(
name|repetitions
operator|=
literal|5
argument_list|)
specifier|public
name|void
name|testBrowseAllInQueuePrefetchOne
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|(
literal|"jms.prefetchPolicy.all=1"
argument_list|)
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
name|sendMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|browser
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Recv: {}"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|40000
argument_list|)
specifier|public
name|void
name|testBrowseAllInQueueTxSession
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
name|sendMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|browser
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Recv: {}"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|40000
argument_list|)
specifier|public
name|void
name|testQueueBrowserInTxSessionLeavesOtherWorkUnaffected
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
name|sendMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Send some TX work but don't commit.
name|MessageProducer
name|txProducer
init|=
name|session
operator|.
name|createProducer
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
literal|5
condition|;
operator|++
name|i
control|)
block|{
name|txProducer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|browser
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Recv: {}"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|browser
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now check that all browser work did not affect the session transaction.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testBrowseAllInQueueSmallPrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsConnectionFactory
name|cf
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|(
literal|"jms.prefetchPolicy.all=5"
argument_list|)
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|int
name|MSG_COUNT
init|=
literal|30
decl_stmt|;
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
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
name|sendMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|MSG_COUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|browser
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Recv: {}"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseOpenWireConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit
