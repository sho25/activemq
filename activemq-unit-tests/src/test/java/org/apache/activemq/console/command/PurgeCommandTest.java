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
name|console
operator|.
name|command
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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|Arrays
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
name|JMSException
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
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueRequestor
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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
name|MBeanServerConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerInvocationHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectInstance
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
name|console
operator|.
name|CommandContext
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
name|console
operator|.
name|formatter
operator|.
name|CommandShellOutputFormatter
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
name|console
operator|.
name|util
operator|.
name|JmxMBeansUtil
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
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|ClassPathXmlApplicationContext
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

begin_class
specifier|public
class|class
name|PurgeCommandTest
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
name|PurgeCommandTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|PROPERTY_NAME
init|=
literal|"XTestProperty"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|PROPERTY_VALUE
init|=
literal|"1:1"
decl_stmt|;
comment|// check for existence of property
specifier|protected
specifier|static
specifier|final
name|String
name|MSG_SEL_WITH_PROPERTY
init|=
name|PROPERTY_NAME
operator|+
literal|" is not null"
decl_stmt|;
comment|// check for non-existence of property
specifier|protected
specifier|static
specifier|final
name|String
name|MSG_SEL_WITHOUT_PROPERTY
init|=
name|PROPERTY_NAME
operator|+
literal|" is null"
decl_stmt|;
comment|// complex message selector query using XTestProperty and JMSPriority
specifier|protected
specifier|static
specifier|final
name|String
name|MSG_SEL_COMPLEX
init|=
name|PROPERTY_NAME
operator|+
literal|"='"
operator|+
literal|"1:1"
operator|+
literal|"' AND JMSPriority>3"
decl_stmt|;
comment|// complex message selector query using XTestProperty AND JMSPriority
comment|// but in SQL-92 syntax
specifier|protected
specifier|static
specifier|final
name|String
name|MSG_SEL_COMPLEX_SQL_AND
init|=
literal|"("
operator|+
name|PROPERTY_NAME
operator|+
literal|"='"
operator|+
literal|"1:1"
operator|+
literal|"') AND (JMSPriority>3)"
decl_stmt|;
comment|// complex message selector query using XTestProperty OR JMSPriority
comment|// but in SQL-92 syntax
specifier|protected
specifier|static
specifier|final
name|String
name|MSG_SEL_COMPLEX_SQL_OR
init|=
literal|"("
operator|+
name|PROPERTY_NAME
operator|+
literal|"='"
operator|+
literal|"1:1"
operator|+
literal|"') OR (JMSPriority>3)"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"org.apache.activemq.network.jms.QueueBridgeTest"
decl_stmt|;
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
specifier|protected
name|QueueConnection
name|localConnection
decl_stmt|;
specifier|protected
name|QueueRequestor
name|requestor
decl_stmt|;
specifier|protected
name|QueueSession
name|requestServerSession
decl_stmt|;
specifier|protected
name|MessageConsumer
name|requestServerConsumer
decl_stmt|;
specifier|protected
name|MessageProducer
name|requestServerProducer
decl_stmt|;
specifier|protected
name|Queue
name|theQueue
decl_stmt|;
annotation|@
name|Override
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
name|context
operator|=
name|createApplicationContext
argument_list|()
expr_stmt|;
name|createConnections
argument_list|()
expr_stmt|;
name|requestServerSession
operator|=
name|localConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|theQueue
operator|=
name|requestServerSession
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
expr_stmt|;
name|requestServerConsumer
operator|=
name|requestServerSession
operator|.
name|createConsumer
argument_list|(
name|theQueue
argument_list|)
expr_stmt|;
name|requestServerProducer
operator|=
name|requestServerSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|QueueSession
name|session
init|=
name|localConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|requestor
operator|=
operator|new
name|QueueRequestor
argument_list|(
name|session
argument_list|,
name|theQueue
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|createConnections
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|fac
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"localFactory"
argument_list|)
decl_stmt|;
name|localConnection
operator|=
name|fac
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
name|localConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|AbstractApplicationContext
name|createApplicationContext
parameter_list|()
block|{
return|return
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
literal|"org/apache/activemq/console/command/activemq.xml"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|localConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|BrokerService
name|broker
init|=
operator|(
name|BrokerService
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"localbroker"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
operator|(
name|BrokerService
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getMessageCount
parameter_list|(
name|QueueBrowser
name|browser
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|JMSException
block|{
name|Enumeration
argument_list|<
name|?
argument_list|>
name|e
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
name|int
name|with
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Object
name|o
init|=
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|o
argument_list|)
expr_stmt|;
name|with
operator|++
expr_stmt|;
block|}
return|return
name|with
return|;
block|}
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|JMSException
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
name|MESSAGE_COUNT
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|requestServerConsumer
operator|.
name|receive
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|MBeanServerConnection
name|createJmxConnection
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|purgeAllMessages
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|List
argument_list|<
name|ObjectInstance
argument_list|>
name|queueList
init|=
name|JmxMBeansUtil
operator|.
name|queryMBeans
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|,
literal|"type=Broker,brokerName=localbroker,destinationType=Queue,destinationName=*"
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectInstance
name|oi
range|:
name|queueList
control|)
block|{
name|ObjectName
name|queueName
init|=
name|oi
operator|.
name|getObjectName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Purging all messages in queue: "
operator|+
name|queueName
operator|.
name|getKeyProperty
argument_list|(
literal|"Destination"
argument_list|)
argument_list|)
expr_stmt|;
name|createJmxConnection
argument_list|()
operator|.
name|invoke
argument_list|(
name|queueName
argument_list|,
literal|"purge"
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addMessages
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
comment|// first clean out any messages that may exist.
name|purgeAllMessages
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|requestServerSession
operator|.
name|createTextMessage
argument_list|(
literal|"test msg: "
operator|+
name|i
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setStringProperty
argument_list|(
name|PROPERTY_NAME
argument_list|,
name|PROPERTY_VALUE
argument_list|)
expr_stmt|;
name|requestServerProducer
operator|.
name|send
argument_list|(
name|theQueue
argument_list|,
name|msg
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|requestServerSession
operator|.
name|createTextMessage
argument_list|(
literal|"test msg: "
operator|+
name|i
argument_list|)
decl_stmt|;
name|requestServerProducer
operator|.
name|send
argument_list|(
name|theQueue
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|validateCounts
parameter_list|(
name|int
name|expectedWithCount
parameter_list|,
name|int
name|expectedWithoutCount
parameter_list|,
name|int
name|expectedAllCount
parameter_list|)
throws|throws
name|JMSException
block|{
name|QueueBrowser
name|withPropertyBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|,
name|MSG_SEL_WITH_PROPERTY
argument_list|)
decl_stmt|;
name|QueueBrowser
name|withoutPropertyBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|,
name|MSG_SEL_WITHOUT_PROPERTY
argument_list|)
decl_stmt|;
name|QueueBrowser
name|allBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|)
decl_stmt|;
name|int
name|withCount
init|=
name|getMessageCount
argument_list|(
name|withPropertyBrowser
argument_list|,
literal|"withProperty "
argument_list|)
decl_stmt|;
name|int
name|withoutCount
init|=
name|getMessageCount
argument_list|(
name|withoutPropertyBrowser
argument_list|,
literal|"withoutProperty "
argument_list|)
decl_stmt|;
name|int
name|allCount
init|=
name|getMessageCount
argument_list|(
name|allBrowser
argument_list|,
literal|"allMessages "
argument_list|)
decl_stmt|;
name|withPropertyBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|withoutPropertyBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|allBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected withCount to be "
operator|+
name|expectedWithCount
operator|+
literal|" was "
operator|+
name|withCount
argument_list|,
name|expectedWithCount
argument_list|,
name|withCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected withoutCount to be "
operator|+
name|expectedWithoutCount
operator|+
literal|" was "
operator|+
name|withoutCount
argument_list|,
name|expectedWithoutCount
argument_list|,
name|withoutCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected allCount to be "
operator|+
name|expectedAllCount
operator|+
literal|" was "
operator|+
name|allCount
argument_list|,
name|expectedAllCount
argument_list|,
name|allCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"withCount = "
operator|+
name|withCount
operator|+
literal|"\n withoutCount = "
operator|+
name|withoutCount
operator|+
literal|"\n allCount = "
operator|+
name|allCount
operator|+
literal|"\n  = "
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
comment|/**      * This test ensures that the queueViewMbean will work.      *      * @throws Exception      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|testQueueViewMbean
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|addMessages
argument_list|()
expr_stmt|;
name|validateCounts
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"*"
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|token
range|:
name|tokens
control|)
block|{
name|List
argument_list|<
name|ObjectInstance
argument_list|>
name|queueList
init|=
name|JmxMBeansUtil
operator|.
name|queryMBeans
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|,
literal|"type=Broker,brokerName=localbroker,destinationType=Queue,destinationName="
operator|+
name|token
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectInstance
name|queue
range|:
name|queueList
control|)
block|{
name|ObjectName
name|queueName
init|=
name|queue
operator|.
name|getObjectName
argument_list|()
decl_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|,
name|queueName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|removed
init|=
name|proxy
operator|.
name|removeMatchingMessages
argument_list|(
name|MSG_SEL_WITH_PROPERTY
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed: "
operator|+
name|removed
argument_list|)
expr_stmt|;
block|}
block|}
name|validateCounts
argument_list|(
literal|0
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|purgeAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testPurgeCommandSimpleSelector
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|PurgeCommand
name|purgeCommand
init|=
operator|new
name|PurgeCommand
argument_list|()
decl_stmt|;
name|CommandContext
name|context
init|=
operator|new
name|CommandContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setFormatter
argument_list|(
operator|new
name|CommandShellOutputFormatter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setCommandContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setJmxUseLocal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
literal|"--msgsel"
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|MSG_SEL_WITH_PROPERTY
argument_list|)
expr_stmt|;
name|addMessages
argument_list|()
expr_stmt|;
name|validateCounts
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|validateCounts
argument_list|(
literal|0
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|purgeAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testPurgeCommandComplexSelector
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|PurgeCommand
name|purgeCommand
init|=
operator|new
name|PurgeCommand
argument_list|()
decl_stmt|;
name|CommandContext
name|context
init|=
operator|new
name|CommandContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setFormatter
argument_list|(
operator|new
name|CommandShellOutputFormatter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setCommandContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setJmxUseLocal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
literal|"--msgsel"
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|MSG_SEL_COMPLEX
argument_list|)
expr_stmt|;
name|addMessages
argument_list|()
expr_stmt|;
name|validateCounts
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|QueueBrowser
name|withPropertyBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|,
name|MSG_SEL_COMPLEX
argument_list|)
decl_stmt|;
name|QueueBrowser
name|allBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|)
decl_stmt|;
name|int
name|withCount
init|=
name|getMessageCount
argument_list|(
name|withPropertyBrowser
argument_list|,
literal|"withProperty "
argument_list|)
decl_stmt|;
name|int
name|allCount
init|=
name|getMessageCount
argument_list|(
name|allBrowser
argument_list|,
literal|"allMessages "
argument_list|)
decl_stmt|;
name|withPropertyBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|allBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected withCount to be "
operator|+
literal|"0"
operator|+
literal|" was "
operator|+
name|withCount
argument_list|,
literal|0
argument_list|,
name|withCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected allCount to be "
operator|+
name|MESSAGE_COUNT
operator|+
literal|" was "
operator|+
name|allCount
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|allCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"withCount = "
operator|+
name|withCount
operator|+
literal|"\n allCount = "
operator|+
name|allCount
operator|+
literal|"\n  = "
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|purgeAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testPurgeCommandComplexSQLSelector_AND
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|one
init|=
literal|"ID:mac.fritz.box:1213242.3231.1:1:1:100"
decl_stmt|;
name|String
name|two
init|=
literal|"\\*:100"
decl_stmt|;
try|try
block|{
if|if
condition|(
name|one
operator|.
name|matches
argument_list|(
name|two
argument_list|)
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"String matches."
argument_list|)
expr_stmt|;
else|else
name|LOG
operator|.
name|info
argument_list|(
literal|"string does not match."
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
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PurgeCommand
name|purgeCommand
init|=
operator|new
name|PurgeCommand
argument_list|()
decl_stmt|;
name|CommandContext
name|context
init|=
operator|new
name|CommandContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setFormatter
argument_list|(
operator|new
name|CommandShellOutputFormatter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setCommandContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setJmxUseLocal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
literal|"--msgsel"
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|MSG_SEL_COMPLEX_SQL_AND
argument_list|)
expr_stmt|;
name|addMessages
argument_list|()
expr_stmt|;
name|validateCounts
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|QueueBrowser
name|withPropertyBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|,
name|MSG_SEL_COMPLEX_SQL_AND
argument_list|)
decl_stmt|;
name|QueueBrowser
name|allBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|)
decl_stmt|;
name|int
name|withCount
init|=
name|getMessageCount
argument_list|(
name|withPropertyBrowser
argument_list|,
literal|"withProperty "
argument_list|)
decl_stmt|;
name|int
name|allCount
init|=
name|getMessageCount
argument_list|(
name|allBrowser
argument_list|,
literal|"allMessages "
argument_list|)
decl_stmt|;
name|withPropertyBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|allBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected withCount to be "
operator|+
literal|"0"
operator|+
literal|" was "
operator|+
name|withCount
argument_list|,
literal|0
argument_list|,
name|withCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected allCount to be "
operator|+
name|MESSAGE_COUNT
operator|+
literal|" was "
operator|+
name|allCount
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|allCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"withCount = "
operator|+
name|withCount
operator|+
literal|"\n allCount = "
operator|+
name|allCount
operator|+
literal|"\n  = "
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|purgeAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testPurgeCommandComplexSQLSelector_OR
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|PurgeCommand
name|purgeCommand
init|=
operator|new
name|PurgeCommand
argument_list|()
decl_stmt|;
name|CommandContext
name|context
init|=
operator|new
name|CommandContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setFormatter
argument_list|(
operator|new
name|CommandShellOutputFormatter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setCommandContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setJmxUseLocal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
literal|"--msgsel"
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|MSG_SEL_COMPLEX_SQL_OR
argument_list|)
expr_stmt|;
name|addMessages
argument_list|()
expr_stmt|;
name|validateCounts
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|QueueBrowser
name|withPropertyBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|,
name|MSG_SEL_COMPLEX_SQL_OR
argument_list|)
decl_stmt|;
name|QueueBrowser
name|allBrowser
init|=
name|requestServerSession
operator|.
name|createBrowser
argument_list|(
name|theQueue
argument_list|)
decl_stmt|;
name|int
name|withCount
init|=
name|getMessageCount
argument_list|(
name|withPropertyBrowser
argument_list|,
literal|"withProperty "
argument_list|)
decl_stmt|;
name|int
name|allCount
init|=
name|getMessageCount
argument_list|(
name|allBrowser
argument_list|,
literal|"allMessages "
argument_list|)
decl_stmt|;
name|withPropertyBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|allBrowser
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected withCount to be 0 but was "
operator|+
name|withCount
argument_list|,
literal|0
argument_list|,
name|withCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected allCount to be 0 but was "
operator|+
name|allCount
argument_list|,
literal|0
argument_list|,
name|allCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"withCount = "
operator|+
name|withCount
operator|+
literal|"\n allCount = "
operator|+
name|allCount
operator|+
literal|"\n  = "
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|purgeAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testDummy
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|one
init|=
literal|"ID:mac.fritz.box:1213242.3231.1:1:1:100"
decl_stmt|;
name|String
name|two
init|=
literal|"ID*:100"
decl_stmt|;
try|try
block|{
if|if
condition|(
name|one
operator|.
name|matches
argument_list|(
name|two
argument_list|)
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"String matches."
argument_list|)
expr_stmt|;
else|else
name|LOG
operator|.
name|info
argument_list|(
literal|"string does not match."
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
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PurgeCommand
name|purgeCommand
init|=
operator|new
name|PurgeCommand
argument_list|()
decl_stmt|;
name|CommandContext
name|context
init|=
operator|new
name|CommandContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setFormatter
argument_list|(
operator|new
name|CommandShellOutputFormatter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setCommandContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|purgeCommand
operator|.
name|setJmxUseLocal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
literal|"--msgsel"
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
literal|"(XTestProperty LIKE '1:*') AND (JMSPriority>3)"
argument_list|)
expr_stmt|;
name|addMessages
argument_list|()
expr_stmt|;
name|purgeCommand
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
comment|/*             QueueBrowser withPropertyBrowser = requestServerSession.createBrowser(                     theQueue, MSG_SEL_COMPLEX_SQL_AND);             QueueBrowser allBrowser = requestServerSession.createBrowser(theQueue);              int withCount = getMessageCount(withPropertyBrowser, "withProperty ");             int allCount = getMessageCount(allBrowser, "allMessages ");              withPropertyBrowser.close();             allBrowser.close();              assertEquals("Expected withCount to be " + "0" + " was "                     + withCount, 0, withCount);             assertEquals("Expected allCount to be " + MESSAGE_COUNT + " was "                     + allCount, MESSAGE_COUNT, allCount);             LOG.info("withCount = " + withCount + "\n allCount = " +                     allCount + "\n  = " + "\n");             */
block|}
finally|finally
block|{
name|purgeAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

