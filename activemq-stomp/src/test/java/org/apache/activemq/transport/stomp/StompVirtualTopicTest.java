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
name|stomp
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
name|assertTrue
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|BrokerFactory
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|FilePendingQueueMessageStoragePolicy
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
name|policy
operator|.
name|PolicyEntry
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
name|policy
operator|.
name|PolicyMap
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|usage
operator|.
name|SystemUsage
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
name|StompVirtualTopicTest
extends|extends
name|StompTestSupport
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
name|StompVirtualTopicTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MSGS
init|=
literal|100000
decl_stmt|;
specifier|private
name|String
name|failMsg
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://()/localhost"
argument_list|)
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|File
name|testDataDir
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/StompVirtualTopicTest"
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|setDataDirectoryFile
argument_list|(
name|testDataDir
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testDataDir
argument_list|,
literal|"kahadb"
argument_list|)
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|applyMemoryLimitPolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SystemUsage
name|memoryManager
init|=
operator|new
name|SystemUsage
argument_list|()
decl_stmt|;
name|memoryManager
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|5818230784L
argument_list|)
expr_stmt|;
name|memoryManager
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|6442450944L
argument_list|)
expr_stmt|;
name|memoryManager
operator|.
name|getTempUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|3221225472L
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setSystemUsage
argument_list|(
name|memoryManager
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|policyEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setMemoryLimit
argument_list|(
literal|10485760
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|FilePendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|policyEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
specifier|final
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|policyEntries
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStompOnVirtualTopics
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running Stomp Producer"
argument_list|)
expr_stmt|;
name|StompConsumer
name|consumerWorker
init|=
operator|new
name|StompConsumer
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|Thread
name|consumer
init|=
operator|new
name|Thread
argument_list|(
name|consumerWorker
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerWorker
operator|.
name|awaitStartCompleted
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
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
name|NUM_MSGS
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|stompConnection
operator|.
name|send
argument_list|(
literal|"/topic/VirtualTopic.FOO"
argument_list|,
literal|"Hello World {"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending last packet with receipt header"
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"receipt"
argument_list|,
literal|"1234"
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|appendHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
literal|"SEND\n"
operator|+
literal|"destination:/topic/VirtualTopic.FOO\n"
operator|+
literal|"receipt: msg-1\n"
operator|+
literal|"\n\n"
operator|+
literal|"Hello World {"
operator|+
operator|(
name|NUM_MSGS
operator|-
literal|1
operator|)
operator|+
literal|"}"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|contains
argument_list|(
literal|"RECEIPT"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Does the sleep resolve the problem?
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|6000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|stompConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stomp Producer finished. Waiting for consumer to join."
argument_list|)
expr_stmt|;
comment|//wait for consumer to shut down
name|consumer
operator|.
name|join
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test finished."
argument_list|)
expr_stmt|;
comment|// check if consumer set failMsg, then let the test fail.
if|if
condition|(
literal|null
operator|!=
name|failMsg
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|failMsg
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|failMsg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Allow Consumer thread to indicate the test has failed.      * JUnits Assert.fail() does not work in threads spawned.      */
specifier|protected
name|void
name|setFail
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|this
operator|.
name|failMsg
operator|=
name|msg
expr_stmt|;
block|}
class|class
name|StompConsumer
implements|implements
name|Runnable
block|{
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StompConsumer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|StompVirtualTopicTest
name|parent
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|received
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|dups
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|StompConsumer
parameter_list|(
name|StompVirtualTopicTest
name|ref
parameter_list|)
block|{
name|parent
operator|=
name|ref
expr_stmt|;
block|}
specifier|public
name|void
name|awaitStartCompleted
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{             }
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running Stomp Consumer"
argument_list|)
expr_stmt|;
name|StompConnection
name|stompConnection
init|=
operator|new
name|StompConnection
argument_list|()
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
try|try
block|{
name|stompConnection
operator|.
name|open
argument_list|(
literal|"localhost"
argument_list|,
name|StompVirtualTopicTest
operator|.
name|this
operator|.
name|port
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|subscribe
argument_list|(
literal|"/queue/Consumer.A.VirtualTopic.FOO"
argument_list|,
literal|"auto"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|counter
operator|=
literal|0
init|;
name|counter
operator|<
name|StompVirtualTopicTest
operator|.
name|NUM_MSGS
condition|;
name|counter
operator|++
control|)
block|{
name|frame
operator|=
name|stompConnection
operator|.
name|receive
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"Received msg with content: "
operator|+
name|frame
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|received
operator|.
name|add
argument_list|(
name|frame
operator|.
name|getBody
argument_list|()
argument_list|)
condition|)
block|{
name|dups
operator|.
name|add
argument_list|(
name|frame
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// another receive should not return any more msgs
try|try
block|{
name|frame
operator|=
name|stompConnection
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Correctly received "
operator|+
name|e
operator|+
literal|" while trying to consume an additional msg."
operator|+
literal|" This is expected as the queue should be empty now."
argument_list|)
expr_stmt|;
block|}
comment|// in addition check QueueSize using JMX
name|long
name|queueSize
init|=
name|reportQueueStatistics
argument_list|()
decl_stmt|;
if|if
condition|(
name|queueSize
operator|!=
literal|0
condition|)
block|{
name|parent
operator|.
name|setFail
argument_list|(
literal|"QueueSize not 0 after test has finished."
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Stomp Consumer Received "
operator|+
name|counter
operator|+
literal|" of "
operator|+
name|StompVirtualTopicTest
operator|.
name|NUM_MSGS
operator|+
literal|" messages. Check QueueSize in JMX and try to browse the queue."
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|msg
range|:
name|dups
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received duplicate message: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|setFail
argument_list|(
literal|"Received "
operator|+
name|StompVirtualTopicTest
operator|.
name|NUM_MSGS
operator|+
literal|" messages but "
operator|+
name|dups
operator|.
name|size
argument_list|()
operator|+
literal|" were dups."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|+
literal|" after consuming "
operator|+
name|counter
operator|+
literal|" msgs."
argument_list|)
expr_stmt|;
try|try
block|{
name|reportQueueStatistics
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
name|parent
operator|.
name|setFail
argument_list|(
literal|"Stomp Consumer received "
operator|+
name|counter
operator|+
literal|" of "
operator|+
name|StompVirtualTopicTest
operator|.
name|NUM_MSGS
operator|+
literal|" messages. Check QueueSize in JMX and try to browse the queue."
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|stompConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"unexpected exception on sleep"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Test Finished."
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|reportQueueStatistics
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:Type=Queue"
operator|+
literal|",Destination=Consumer.A.VirtualTopic.FOO"
operator|+
literal|",BrokerName=localhost"
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|queue
init|=
operator|(
name|QueueViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer.A.VirtualTopic.FOO Inflight: "
operator|+
name|queue
operator|.
name|getInFlightCount
argument_list|()
operator|+
literal|", enqueueCount: "
operator|+
name|queue
operator|.
name|getEnqueueCount
argument_list|()
operator|+
literal|", dequeueCount: "
operator|+
name|queue
operator|.
name|getDequeueCount
argument_list|()
operator|+
literal|", dispatchCount: "
operator|+
name|queue
operator|.
name|getDispatchCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|queue
operator|.
name|getQueueSize
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit
