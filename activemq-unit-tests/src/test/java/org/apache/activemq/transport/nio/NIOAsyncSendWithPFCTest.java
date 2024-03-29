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
name|nio
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
name|jmx
operator|.
name|DestinationView
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
name|ProducerViewMBean
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
name|QueueView
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|VMPendingQueueMessageStoragePolicy
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ExecutorService
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
name|Executors
import|;
end_import

begin_comment
comment|/*  demonstrates that with nio it does not make sense to block on the broker but thread pool  shold grow past initial corepoolsize of 10  */
end_comment

begin_class
specifier|public
class|class
name|NIOAsyncSendWithPFCTest
extends|extends
name|TestCase
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
name|NIOAsyncSendWithPFCTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|TRANSPORT_URL
init|=
literal|"nio://0.0.0.0:0"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION_ONE
init|=
literal|"testQ1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION_TWO
init|=
literal|"testQ2"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGES_TO_SEND
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
name|int
name|NUMBER_OF_PRODUCERS
init|=
literal|10
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
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
argument_list|()
decl_stmt|;
name|PolicyEntry
name|pe
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|pe
operator|.
name|setMemoryLimit
argument_list|(
literal|256000
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|VMPendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|pe
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|TRANSPORT_URL
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinations
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_ONE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|TRANSPORT_URL
operator|=
name|broker
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"nio"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
comment|/**      * Test creates 10 producer who send to a single destination using Async mode.      * Producer flow control kicks in for that destination. When producer flow control is blocking sends      * Test tries to create another JMS connection to the nio.      */
specifier|public
name|void
name|testAsyncSendPFCNewConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|NUMBER_OF_PRODUCERS
argument_list|)
decl_stmt|;
name|QueueView
name|queueView
init|=
name|getQueueView
argument_list|(
name|broker
argument_list|,
name|DESTINATION_ONE
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMBER_OF_PRODUCERS
condition|;
name|i
operator|++
control|)
block|{
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|ProducerTask
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//wait till producer follow control kicks in
name|waitForProducerFlowControl
argument_list|(
name|broker
argument_list|,
name|queueView
argument_list|)
expr_stmt|;
try|try
block|{
name|sendMessages
argument_list|(
literal|1
argument_list|,
name|DESTINATION_TWO
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Ex on send  new connection"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"*** received the following exception when creating addition producer new connection:"
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testAsyncSendPFCExistingConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
argument_list|,
name|TRANSPORT_URL
operator|+
literal|"?wireFormat.maxInactivityDuration=5000"
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|exisitngConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|NUMBER_OF_PRODUCERS
argument_list|)
decl_stmt|;
name|QueueView
name|queueView
init|=
name|getQueueView
argument_list|(
name|broker
argument_list|,
name|DESTINATION_ONE
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMBER_OF_PRODUCERS
condition|;
name|i
operator|++
control|)
block|{
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|ProducerTask
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//wait till producer follow control kicks in
name|waitForProducerFlowControl
argument_list|(
name|broker
argument_list|,
name|queueView
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Producer view blocked"
argument_list|,
name|getProducerView
argument_list|(
name|broker
argument_list|,
name|DESTINATION_ONE
argument_list|)
operator|.
name|isProducerBlocked
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Session
name|producerSession
init|=
name|exisitngConnection
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
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Ex on create session"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"*** received the following exception when creating producer session:"
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSyncSendPFCExistingConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|NUMBER_OF_PRODUCERS
argument_list|)
decl_stmt|;
name|QueueView
name|queueView
init|=
name|getQueueView
argument_list|(
name|broker
argument_list|,
name|DESTINATION_ONE
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMBER_OF_PRODUCERS
condition|;
name|i
operator|++
control|)
block|{
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|ProducerTask
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//wait till producer follow control kicks in
name|waitForProducerFlowControl
argument_list|(
name|broker
argument_list|,
name|queueView
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Producer view blocked"
argument_list|,
name|getProducerView
argument_list|(
name|broker
argument_list|,
name|DESTINATION_ONE
argument_list|)
operator|.
name|isProducerBlocked
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|waitForProducerFlowControl
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|QueueView
name|queueView
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|blockingAllSends
decl_stmt|;
do|do
block|{
name|blockingAllSends
operator|=
name|queueView
operator|.
name|getBlockedSends
argument_list|()
operator|>=
literal|10
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Blocking all sends:"
operator|+
name|queueView
operator|.
name|getBlockedSends
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|blockingAllSends
condition|)
do|;
block|}
class|class
name|ProducerTask
implements|implements
name|Runnable
block|{
name|boolean
name|sync
init|=
literal|false
decl_stmt|;
name|ProducerTask
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|ProducerTask
parameter_list|(
name|boolean
name|sync
parameter_list|)
block|{
name|this
operator|.
name|sync
operator|=
name|sync
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|//send X messages
name|sendMessages
argument_list|(
name|MESSAGES_TO_SEND
argument_list|,
name|DESTINATION_ONE
argument_list|,
name|sync
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
specifier|private
name|Long
name|sendMessages
parameter_list|(
name|int
name|messageCount
parameter_list|,
name|String
name|destination
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|numberOfMessageSent
init|=
literal|0
decl_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
argument_list|,
name|TRANSPORT_URL
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|sync
condition|)
block|{
name|connection
operator|.
name|setUseAsyncSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|connection
operator|.
name|setUseAsyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|Session
name|producerSession
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
name|jmsProducer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|producerSession
operator|.
name|createQueue
argument_list|(
name|destination
argument_list|)
argument_list|)
decl_stmt|;
name|Message
name|sendMessage
init|=
name|createTextMessage
argument_list|(
name|producerSession
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|jmsProducer
operator|.
name|send
argument_list|(
name|sendMessage
argument_list|)
expr_stmt|;
name|numberOfMessageSent
operator|++
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|" Finished after producing : "
operator|+
name|numberOfMessageSent
argument_list|)
expr_stmt|;
return|return
name|numberOfMessageSent
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exception received producing "
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignored
parameter_list|)
block|{}
block|}
block|}
return|return
name|numberOfMessageSent
return|;
block|}
specifier|private
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"1234567890"
argument_list|)
expr_stmt|;
block|}
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|QueueView
name|getQueueView
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|ObjectName
argument_list|,
name|DestinationView
argument_list|>
name|queueViews
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getQueueViews
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectName
name|key
range|:
name|queueViews
operator|.
name|keySet
argument_list|()
control|)
block|{
name|DestinationView
name|destinationView
init|=
name|queueViews
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|destinationView
operator|instanceof
name|QueueView
condition|)
block|{
name|QueueView
name|queueView
init|=
operator|(
name|QueueView
operator|)
name|destinationView
decl_stmt|;
if|if
condition|(
name|queueView
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
return|return
name|queueView
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|ProducerViewMBean
name|getProducerView
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectName
index|[]
name|qProducers
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueProducers
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectName
name|name
range|:
name|qProducers
control|)
block|{
name|ProducerViewMBean
name|proxy
init|=
operator|(
name|ProducerViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|name
argument_list|,
name|ProducerViewMBean
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
literal|""
operator|+
name|proxy
operator|.
name|getProducerId
argument_list|()
operator|+
literal|", dest: "
operator|+
name|proxy
operator|.
name|getDestinationName
argument_list|()
operator|+
literal|", blocked: "
operator|+
name|proxy
operator|.
name|isProducerBlocked
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|proxy
operator|.
name|getDestinationName
argument_list|()
operator|.
name|contains
argument_list|(
name|qName
argument_list|)
condition|)
block|{
return|return
name|proxy
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

