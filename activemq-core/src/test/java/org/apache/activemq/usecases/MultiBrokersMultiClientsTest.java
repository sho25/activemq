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
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|UncaughtExceptionHandler
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
name|HashMap
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
name|Map
operator|.
name|Entry
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
name|TimeUnit
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
name|MessageConsumer
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
name|JmsMultipleBrokersTestSupport
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
name|MessageIdList
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
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|MultiBrokersMultiClientsTest
extends|extends
name|JmsMultipleBrokersTestSupport
implements|implements
name|UncaughtExceptionHandler
block|{
specifier|public
specifier|static
specifier|final
name|int
name|BROKER_COUNT
init|=
literal|6
decl_stmt|;
comment|// number of brokers to network
specifier|public
specifier|static
specifier|final
name|int
name|CONSUMER_COUNT
init|=
literal|25
decl_stmt|;
comment|// consumers per broker
specifier|public
specifier|static
specifier|final
name|int
name|PRODUCER_COUNT
init|=
literal|3
decl_stmt|;
comment|// producers per broker
specifier|public
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|20
decl_stmt|;
comment|// messages per producer
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
name|MultiBrokersMultiClientsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|MessageConsumer
argument_list|>
name|consumerMap
decl_stmt|;
name|Map
argument_list|<
name|Thread
argument_list|,
name|Throwable
argument_list|>
name|unhandeledExceptions
init|=
operator|new
name|HashMap
argument_list|<
name|Thread
argument_list|,
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testTopicAllConnected
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|()
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
comment|// Setup topic destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|BROKER_COUNT
operator|*
name|PRODUCER_COUNT
operator|*
name|BROKER_COUNT
operator|*
name|CONSUMER_COUNT
operator|*
name|MESSAGE_COUNT
argument_list|)
decl_stmt|;
comment|// Setup consumers
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|CONSUMER_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|consumerMap
operator|.
name|put
argument_list|(
literal|"Consumer:"
operator|+
name|i
operator|+
literal|":"
operator|+
name|j
argument_list|,
name|createConsumer
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|,
name|latch
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for consumers to get propagated
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
comment|// all consumers on the remote brokers look like 1 consumer to the local broker.
name|assertConsumersConnect
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|,
operator|(
name|BROKER_COUNT
operator|-
literal|1
operator|)
operator|+
name|CONSUMER_COUNT
argument_list|,
literal|65000
argument_list|)
expr_stmt|;
block|}
comment|// Send messages
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|PRODUCER_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|sendMessages
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Missing "
operator|+
name|latch
operator|.
name|getCount
argument_list|()
operator|+
literal|" messages"
argument_list|,
name|latch
operator|.
name|await
argument_list|(
literal|45
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get message count
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|CONSUMER_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|MessageIdList
name|msgs
init|=
name|getConsumerMessages
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
operator|(
name|MessageConsumer
operator|)
name|consumerMap
operator|.
name|get
argument_list|(
literal|"Consumer:"
operator|+
name|i
operator|+
literal|":"
operator|+
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|BROKER_COUNT
operator|*
name|PRODUCER_COUNT
operator|*
name|MESSAGE_COUNT
argument_list|,
name|msgs
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNoUnhandeledExceptions
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|assertNoUnhandeledExceptions
parameter_list|()
block|{
for|for
control|(
name|Entry
argument_list|<
name|Thread
argument_list|,
name|Throwable
argument_list|>
name|e
range|:
name|unhandeledExceptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Thread:"
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|" Had unexpected: "
operator|+
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"There are no unhandelled exceptions, see: log for detail on: "
operator|+
name|unhandeledExceptions
argument_list|,
name|unhandeledExceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testQueueAllConnected
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|()
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|this
operator|.
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
comment|// Setup topic destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|BROKER_COUNT
operator|*
name|PRODUCER_COUNT
operator|*
name|MESSAGE_COUNT
argument_list|)
decl_stmt|;
comment|// Setup consumers
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|CONSUMER_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|consumerMap
operator|.
name|put
argument_list|(
literal|"Consumer:"
operator|+
name|i
operator|+
literal|":"
operator|+
name|j
argument_list|,
name|createConsumer
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|,
name|latch
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for consumers to get propagated
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
comment|// all consumers on the remote brokers look like 1 consumer to the local broker.
name|assertConsumersConnect
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|,
operator|(
name|BROKER_COUNT
operator|-
literal|1
operator|)
operator|+
name|CONSUMER_COUNT
argument_list|,
literal|65000
argument_list|)
expr_stmt|;
block|}
comment|// Send messages
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|PRODUCER_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|sendMessages
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Wait for messages to be delivered
name|assertTrue
argument_list|(
literal|"Missing "
operator|+
name|latch
operator|.
name|getCount
argument_list|()
operator|+
literal|" messages"
argument_list|,
name|latch
operator|.
name|await
argument_list|(
literal|45
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get message count
name|int
name|totalMsg
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|CONSUMER_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|MessageIdList
name|msgs
init|=
name|getConsumerMessages
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|consumerMap
operator|.
name|get
argument_list|(
literal|"Consumer:"
operator|+
name|i
operator|+
literal|":"
operator|+
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|totalMsg
operator|+=
name|msgs
operator|.
name|getMessageCount
argument_list|()
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|BROKER_COUNT
operator|*
name|PRODUCER_COUNT
operator|*
name|MESSAGE_COUNT
argument_list|,
name|totalMsg
argument_list|)
expr_stmt|;
name|assertNoUnhandeledExceptions
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|unhandeledExceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Setup n brokers
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()/Broker"
operator|+
name|i
operator|+
literal|"?persistent=false&useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|consumerMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MessageConsumer
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
synchronized|synchronized
init|(
name|unhandeledExceptions
init|)
block|{
name|unhandeledExceptions
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

