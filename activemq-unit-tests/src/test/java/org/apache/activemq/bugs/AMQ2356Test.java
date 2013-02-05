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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|VMPendingSubscriberMessageStoragePolicy
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
name|perf
operator|.
name|NumberOfDestinationsTest
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
name|KahaDBStore
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
name|java
operator|.
name|io
operator|.
name|File
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
name|Connection
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/* A AMQ2356Test We have an environment where we have a very large number of destinations.  In an effort to reduce the number of threads I have set the options -Dorg.apache.activemq.UseDedicatedTaskRunner=false  and<policyEntry queue=">" optimizedDispatch="true"/>  Unfortunately this very quickly leads to deadlocked queues.  My environment is:  ActiveMQ 5.2 Ubunty Jaunty kernel 2.6.28-14-generic #47-Ubuntu SMP (although only a single core on my system) TCP transportConnector  To reproduce the bug (which I can do 100% of the time) I connect 5 consumers (AUTO_ACK) to 5 different queues.  Then I start 5 producers and pair them up with a consumer on a queue, and they start sending PERSISTENT messages.  I've set the producer to send 100 messages and disconnect, and the consumer to receive 100 messages and disconnect.  The first pair usually gets through their 100 messages and disconnect, at which point all the other pairs have  deadlocked at less than 30 messages each.  */
end_comment

begin_class
specifier|public
class|class
name|AMQ2356Test
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|1000
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|NUMBER_OF_PAIRS
init|=
literal|10
decl_stmt|;
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
name|NumberOfDestinationsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|String
name|brokerURL
init|=
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_BROKER_BIND_URL
decl_stmt|;
specifier|protected
name|int
name|destinationCount
decl_stmt|;
specifier|public
name|void
name|testScenario
parameter_list|()
throws|throws
name|Exception
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
name|NUMBER_OF_PAIRS
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|":"
operator|+
name|i
argument_list|)
decl_stmt|;
name|ProducerConsumerPair
name|cp
init|=
operator|new
name|ProducerConsumerPair
argument_list|()
decl_stmt|;
name|cp
operator|.
name|start
argument_list|(
name|this
operator|.
name|brokerURL
argument_list|,
name|queue
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|cp
operator|.
name|testRun
argument_list|()
expr_stmt|;
name|cp
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|Destination
name|getDestination
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|destinationName
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|destinationCount
operator|++
decl_stmt|;
return|return
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|setUp
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|answer
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|dataFileDir
init|=
operator|new
name|File
argument_list|(
literal|"target/test-amq-data/bugs/AMQ2356/kahadb"
argument_list|)
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
name|dataFileDir
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Setup a destination policy where it takes only 1 message at a time.
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setOptimizedDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setEnableStatistics
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|brokerURL
argument_list|)
expr_stmt|;
block|}
specifier|static
class|class
name|ProducerConsumerPair
block|{
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|private
name|Connection
name|producerConnection
decl_stmt|;
specifier|private
name|Connection
name|consumerConnection
decl_stmt|;
specifier|private
name|int
name|numberOfMessages
decl_stmt|;
name|ProducerConsumerPair
parameter_list|()
block|{                     }
name|void
name|start
parameter_list|(
name|String
name|brokerURL
parameter_list|,
specifier|final
name|Destination
name|dest
parameter_list|,
name|int
name|msgNum
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|destination
operator|=
name|dest
expr_stmt|;
name|this
operator|.
name|numberOfMessages
operator|=
name|msgNum
expr_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
decl_stmt|;
name|this
operator|.
name|producerConnection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|this
operator|.
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|consumerConnection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|this
operator|.
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|producer
operator|=
name|createProducer
argument_list|(
name|this
operator|.
name|producerConnection
argument_list|)
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|createConsumer
argument_list|(
name|this
operator|.
name|consumerConnection
argument_list|)
expr_stmt|;
block|}
name|void
name|testRun
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
name|this
operator|.
name|producerConnection
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|msg
init|=
name|s
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|int
name|received
init|=
literal|0
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
name|this
operator|.
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|this
operator|.
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|received
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Messages received on "
operator|+
name|this
operator|.
name|destination
argument_list|,
name|this
operator|.
name|numberOfMessages
argument_list|,
name|received
argument_list|)
expr_stmt|;
block|}
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|producerConnection
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|consumerConnection
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|MessageProducer
name|createProducer
parameter_list|(
name|Connection
name|connection
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
name|MessageProducer
name|result
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|this
operator|.
name|destination
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Connection
name|connection
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
name|MessageConsumer
name|result
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|this
operator|.
name|destination
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit
