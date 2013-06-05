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
package|;
end_package

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
name|Enumeration
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
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|BaseDestination
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

begin_class
specifier|public
class|class
name|JmsQueueBrowserTest
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
name|ActiveMQXAConnectionFactoryTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|isUseCache
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|suite
argument_list|(
name|JmsQueueBrowserTest
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**      * Tests the queue browser. Browses the messages then the consumer tries to receive them. The messages should still      * be in the queue even when it was browsed.      *      * @throws Exception      */
specifier|public
name|void
name|testReceiveBrowseReceive
parameter_list|()
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
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Message
index|[]
name|outbound
init|=
operator|new
name|Message
index|[]
block|{
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Second Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Third Message"
argument_list|)
block|}
decl_stmt|;
comment|// lets consume any outstanding messages from previous test runs
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// Get the first.
name|assertEquals
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
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
name|destination
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
comment|// browse the second
name|assertTrue
argument_list|(
literal|"should have received the second message"
argument_list|,
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outbound
index|[
literal|1
index|]
argument_list|,
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
comment|// browse the third.
name|assertTrue
argument_list|(
literal|"Should have received the third message"
argument_list|,
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outbound
index|[
literal|2
index|]
argument_list|,
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
comment|// There should be no more.
name|boolean
name|tooMany
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got extra message: "
operator|+
operator|(
operator|(
name|TextMessage
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|tooMany
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|tooMany
argument_list|)
expr_stmt|;
name|browser
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Re-open the consumer.
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// Receive the second.
name|assertEquals
argument_list|(
name|outbound
index|[
literal|1
index|]
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Receive the third.
name|assertEquals
argument_list|(
name|outbound
index|[
literal|2
index|]
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestBatchSendBrowseReceive
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"isUseCache"
argument_list|,
operator|new
name|Boolean
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBatchSendBrowseReceive
parameter_list|()
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
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|TextMessage
index|[]
name|outbound
init|=
operator|new
name|TextMessage
index|[
literal|10
index|]
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|outbound
index|[
name|i
index|]
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|i
operator|+
literal|" Message"
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
comment|// lets consume any outstanding messages from previous test runs
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
name|consumer
operator|.
name|close
argument_list|()
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
name|outbound
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|destination
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outbound
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"should have a"
argument_list|,
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outbound
index|[
name|i
index|]
argument_list|,
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|browser
operator|.
name|close
argument_list|()
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
name|outbound
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// verify second batch is visible to browse
name|browser
operator|=
name|session
operator|.
name|createBrowser
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|enumeration
operator|=
name|browser
operator|.
name|getEnumeration
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|2
condition|;
name|j
operator|++
control|)
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
name|outbound
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"should have a"
argument_list|,
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"j="
operator|+
name|j
operator|+
literal|", i="
operator|+
name|i
argument_list|,
name|outbound
index|[
name|i
index|]
operator|.
name|getText
argument_list|()
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|browser
operator|.
name|close
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outbound
operator|.
name|length
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
literal|"Got message: "
operator|+
name|i
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestBatchSendJmxBrowseReceive
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"isUseCache"
argument_list|,
operator|new
name|Boolean
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBatchSendJmxBrowseReceive
parameter_list|()
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
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|TextMessage
index|[]
name|outbound
init|=
operator|new
name|TextMessage
index|[
literal|10
index|]
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|outbound
index|[
name|i
index|]
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|i
operator|+
literal|" Message"
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
comment|// lets consume any outstanding messages from previous test runs
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
name|consumer
operator|.
name|close
argument_list|()
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
name|outbound
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Create QueueView MBean..."
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|broker
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
name|long
name|concount
init|=
name|proxy
operator|.
name|getConsumerCount
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer Count :"
operator|+
name|concount
argument_list|)
expr_stmt|;
name|long
name|messcount
init|=
name|proxy
operator|.
name|getQueueSize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"current number of messages in the queue :"
operator|+
name|messcount
argument_list|)
expr_stmt|;
comment|// lets browse
name|CompositeData
index|[]
name|compdatalist
init|=
name|proxy
operator|.
name|browse
argument_list|()
decl_stmt|;
if|if
condition|(
name|compdatalist
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"There is no message in the queue:"
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|messageIDs
init|=
operator|new
name|String
index|[
name|compdatalist
operator|.
name|length
index|]
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
name|compdatalist
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|CompositeData
name|cdata
init|=
name|compdatalist
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Columns: "
operator|+
name|cdata
operator|.
name|getCompositeType
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|messageIDs
index|[
name|i
index|]
operator|=
operator|(
name|String
operator|)
name|cdata
operator|.
name|get
argument_list|(
literal|"JMSMessageID"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"message "
operator|+
name|i
operator|+
literal|" : "
operator|+
name|cdata
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|TabularData
name|table
init|=
name|proxy
operator|.
name|browseAsTable
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found tabular data: "
operator|+
name|table
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Table should not be empty!"
argument_list|,
name|table
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|outbound
operator|.
name|length
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|outbound
operator|.
name|length
argument_list|,
name|compdatalist
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|outbound
operator|.
name|length
argument_list|,
name|table
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Send another 10"
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
name|outbound
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Browse again"
argument_list|)
expr_stmt|;
name|messcount
operator|=
name|proxy
operator|.
name|getQueueSize
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"current number of messages in the queue :"
operator|+
name|messcount
argument_list|)
expr_stmt|;
name|compdatalist
operator|=
name|proxy
operator|.
name|browse
argument_list|()
expr_stmt|;
if|if
condition|(
name|compdatalist
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"There is no message in the queue:"
argument_list|)
expr_stmt|;
block|}
name|messageIDs
operator|=
operator|new
name|String
index|[
name|compdatalist
operator|.
name|length
index|]
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
name|compdatalist
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|CompositeData
name|cdata
init|=
name|compdatalist
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Columns: "
operator|+
name|cdata
operator|.
name|getCompositeType
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|messageIDs
index|[
name|i
index|]
operator|=
operator|(
name|String
operator|)
name|cdata
operator|.
name|get
argument_list|(
literal|"JMSMessageID"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"message "
operator|+
name|i
operator|+
literal|" : "
operator|+
name|cdata
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|table
operator|=
name|proxy
operator|.
name|browseAsTable
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found tabular data: "
operator|+
name|table
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Table should not be empty!"
argument_list|,
name|table
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|outbound
operator|.
name|length
operator|*
literal|2
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|outbound
operator|.
name|length
operator|*
literal|2
argument_list|,
name|compdatalist
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|outbound
operator|.
name|length
operator|*
literal|2
argument_list|,
name|table
operator|.
name|size
argument_list|()
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outbound
operator|.
name|length
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
literal|"Got message: "
operator|+
name|i
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testBrowseReceive
parameter_list|()
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
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// create consumer
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
comment|// lets consume any outstanding messages from previous test runs
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
name|Message
index|[]
name|outbound
init|=
operator|new
name|Message
index|[]
block|{
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Second Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Third Message"
argument_list|)
block|}
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
name|send
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// create browser first
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|destination
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
comment|// browse the first message
name|assertTrue
argument_list|(
literal|"should have received the first message"
argument_list|,
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|,
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
comment|// Receive the first message.
name|assertEquals
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|browser
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testLargeNumberOfMessages
parameter_list|()
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
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Message: "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|destination
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
name|assertTrue
argument_list|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numberBrowsed
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
name|browsed
init|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Browsed Message [{}]"
argument_list|,
name|browsed
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|numberBrowsed
operator|++
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Number browsed:  "
operator|+
name|numberBrowsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|numberBrowsed
argument_list|)
expr_stmt|;
name|browser
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testQueueBrowserWith2Consumers
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numMessages
init|=
literal|1000
decl_stmt|;
name|connection
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|false
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|destinationPrefetch10
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST?jms.prefetchSize=10"
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|destinationPrefetch1
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST?jms.prefetchsize=1"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnection
name|connection2
init|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|connection2
operator|.
name|start
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection2
argument_list|)
expr_stmt|;
name|Session
name|session2
init|=
name|connection2
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
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destinationPrefetch10
argument_list|)
decl_stmt|;
comment|// lets consume any outstanding messages from previous test runs
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMessages
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
literal|"Message: "
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|QueueBrowser
name|browser
init|=
name|session2
operator|.
name|createBrowser
argument_list|(
name|destinationPrefetch1
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Enumeration
argument_list|<
name|Message
argument_list|>
name|browserView
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Message
argument_list|>
name|messages
init|=
operator|new
name|ArrayList
argument_list|<
name|Message
argument_list|>
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
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|m1
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
literal|"m1 is null for index: "
operator|+
name|i
argument_list|,
name|m1
argument_list|)
expr_stmt|;
name|messages
operator|.
name|add
argument_list|(
name|m1
argument_list|)
expr_stmt|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|numMessages
operator|&&
name|browserView
operator|.
name|hasMoreElements
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|m1
init|=
name|messages
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Message
name|m2
init|=
name|browserView
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"m2 is null for index: "
operator|+
name|i
argument_list|,
name|m2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m1
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|m2
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// currently browse max page size is ignored for a queue browser consumer
comment|// only guarantee is a page size - but a snapshot of pagedinpending is
comment|// used so it is most likely more
name|assertTrue
argument_list|(
literal|"got at least our expected minimum in the browser: "
argument_list|,
name|i
operator|>
name|BaseDestination
operator|.
name|MAX_PAGE_SIZE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"nothing left in the browser"
argument_list|,
name|browserView
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"consumer finished"
argument_list|,
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBrowseClose
parameter_list|()
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
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|TextMessage
index|[]
name|outbound
init|=
operator|new
name|TextMessage
index|[]
block|{
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Second Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Third Message"
argument_list|)
block|}
decl_stmt|;
comment|// create consumer
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
comment|// lets consume any outstanding messages from previous test runs
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
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
name|send
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// create browser first
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|destination
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
comment|// browse some messages
name|assertEquals
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|,
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outbound
index|[
literal|1
index|]
argument_list|,
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertEquals(outbound[2], (Message) enumeration.nextElement());
name|browser
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Receive the first message.
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
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected "
operator|+
name|outbound
index|[
literal|0
index|]
operator|.
name|getText
argument_list|()
operator|+
literal|" but received "
operator|+
name|msg
operator|.
name|getText
argument_list|()
argument_list|,
name|outbound
index|[
literal|0
index|]
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected "
operator|+
name|outbound
index|[
literal|1
index|]
operator|.
name|getText
argument_list|()
operator|+
literal|" but received "
operator|+
name|msg
operator|.
name|getText
argument_list|()
argument_list|,
name|outbound
index|[
literal|1
index|]
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected "
operator|+
name|outbound
index|[
literal|2
index|]
operator|.
name|getText
argument_list|()
operator|+
literal|" but received "
operator|+
name|msg
operator|.
name|getText
argument_list|()
argument_list|,
name|outbound
index|[
literal|2
index|]
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setUseCache
argument_list|(
name|isUseCache
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|policyEntry
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
return|return
name|brokerService
return|;
block|}
block|}
end_class

end_unit

