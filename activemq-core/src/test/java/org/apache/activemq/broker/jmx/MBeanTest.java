begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
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
name|broker
operator|.
name|BrokerService
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
name|Message
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
name|management
operator|.
name|MBeanServer
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
name|MalformedObjectNameException
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
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_comment
comment|/**  * A test case of the various MBeans in ActiveMQ.  * If you want to look at the various MBeans after the test has been run then  * run this test case as a command line application.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MBeanTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|private
specifier|static
name|boolean
name|waitForKeyPress
decl_stmt|;
specifier|protected
name|MBeanServer
name|mbeanServer
decl_stmt|;
specifier|protected
name|String
name|domain
init|=
literal|"org.apache.activemq"
decl_stmt|;
specifier|protected
name|String
name|clientID
init|=
literal|"foo"
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|boolean
name|transacted
decl_stmt|;
specifier|protected
name|int
name|authMode
init|=
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
decl_stmt|;
specifier|protected
name|int
name|messageCount
init|=
literal|10
decl_stmt|;
comment|/**      * When you run this test case from the command line it will pause before terminating      * so that you can look at the MBeans state for debugging purposes.      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|waitForKeyPress
operator|=
literal|true
expr_stmt|;
name|TestRunner
operator|.
name|run
argument_list|(
name|MBeanTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMBeans
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|useConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
comment|// test all the various MBeans now we have a producer, consumer and
comment|// messages on a queue
name|assertQueueBrowseWorks
argument_list|()
expr_stmt|;
name|assertCreateAndDestroyDurableSubscriptions
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testMoveMessagesBySelector
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|useConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|ObjectName
name|queueViewMBeanName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Queue,Destination="
operator|+
name|getDestinationString
argument_list|()
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
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|newDestination
init|=
literal|"test.new.destination."
operator|+
name|getClass
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
decl_stmt|;
name|queue
operator|.
name|moveMatchingMessagesTo
argument_list|(
literal|"counter> 2"
argument_list|,
name|newDestination
argument_list|)
expr_stmt|;
name|queueViewMBeanName
operator|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Queue,Destination="
operator|+
name|newDestination
operator|+
literal|",BrokerName=localhost"
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|(
name|QueueViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have at least one message in the queue: "
operator|+
name|queueViewMBeanName
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// now lets remove them by selector
name|queue
operator|.
name|removeMatchingMessages
argument_list|(
literal|"counter> 2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have no more messages in the queue: "
operator|+
name|queueViewMBeanName
argument_list|,
literal|0
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCopyMessagesBySelector
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|useConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|ObjectName
name|queueViewMBeanName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Queue,Destination="
operator|+
name|getDestinationString
argument_list|()
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
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|newDestination
init|=
literal|"test.new.destination."
operator|+
name|getClass
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
decl_stmt|;
name|long
name|queueSize
init|=
name|queue
operator|.
name|getQueueSize
argument_list|()
decl_stmt|;
name|queue
operator|.
name|copyMatchingMessagesTo
argument_list|(
literal|"counter> 2"
argument_list|,
name|newDestination
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have same number of messages in the queue: "
operator|+
name|queueViewMBeanName
argument_list|,
name|queueSize
argument_list|,
name|queueSize
argument_list|)
expr_stmt|;
name|queueViewMBeanName
operator|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Queue,Destination="
operator|+
name|newDestination
operator|+
literal|",BrokerName=localhost"
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|(
name|QueueViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Queue: "
operator|+
name|queueViewMBeanName
operator|+
literal|" now has: "
operator|+
name|queue
operator|.
name|getQueueSize
argument_list|()
operator|+
literal|" message(s)"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have at least one message in the queue: "
operator|+
name|queueViewMBeanName
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// now lets remove them by selector
name|queue
operator|.
name|removeMatchingMessages
argument_list|(
literal|"counter> 2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have no more messages in the queue: "
operator|+
name|queueViewMBeanName
argument_list|,
literal|0
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertQueueBrowseWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|Integer
name|mbeancnt
init|=
name|mbeanServer
operator|.
name|getMBeanCount
argument_list|()
decl_stmt|;
name|echo
argument_list|(
literal|"Mbean count :"
operator|+
name|mbeancnt
argument_list|)
expr_stmt|;
name|ObjectName
name|queueViewMBeanName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Queue,Destination="
operator|+
name|getDestinationString
argument_list|()
operator|+
literal|",BrokerName=localhost"
argument_list|)
decl_stmt|;
name|echo
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
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
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
name|echo
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
name|echo
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
name|echo
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
name|echo
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
name|echo
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
literal|10
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|messageID
init|=
name|messageIDs
index|[
literal|0
index|]
decl_stmt|;
name|String
name|newDestinationName
init|=
literal|"queue://dummy.test.cheese"
decl_stmt|;
name|echo
argument_list|(
literal|"Attempting to copy: "
operator|+
name|messageID
operator|+
literal|" to destination: "
operator|+
name|newDestinationName
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|copyMessageTo
argument_list|(
name|messageID
argument_list|,
name|newDestinationName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
literal|10
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|messageID
operator|=
name|messageIDs
index|[
literal|1
index|]
expr_stmt|;
name|echo
argument_list|(
literal|"Attempting to remove: "
operator|+
name|messageID
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|removeMessage
argument_list|(
name|messageID
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
literal|9
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|echo
argument_list|(
literal|"Worked!"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertCreateAndDestroyDurableSubscriptions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// lets create a new topic
name|ObjectName
name|brokerName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Broker,BrokerName=localhost"
argument_list|)
decl_stmt|;
name|echo
argument_list|(
literal|"Create QueueView MBean..."
argument_list|)
expr_stmt|;
name|BrokerViewMBean
name|broker
init|=
operator|(
name|BrokerViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|brokerName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|broker
operator|.
name|addTopic
argument_list|(
name|getDestinationString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Durable subscriber count"
argument_list|,
literal|0
argument_list|,
name|broker
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|topicName
init|=
name|getDestinationString
argument_list|()
decl_stmt|;
name|String
name|selector
init|=
literal|null
decl_stmt|;
name|ObjectName
name|name1
init|=
name|broker
operator|.
name|createDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"subscriber1"
argument_list|,
name|topicName
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|broker
operator|.
name|createDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"subscriber2"
argument_list|,
name|topicName
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Durable subscriber count"
argument_list|,
literal|2
argument_list|,
name|broker
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have created an mbean name for the durable subscriber!"
argument_list|,
name|name1
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created durable subscriber with name: "
operator|+
name|name1
argument_list|)
expr_stmt|;
comment|// now lets try destroy it
name|broker
operator|.
name|destroyDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"subscriber1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Durable subscriber count"
argument_list|,
literal|1
argument_list|,
name|broker
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ObjectName
name|assertRegisteredObjectName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|NullPointerException
block|{
name|ObjectName
name|objectName
init|=
operator|new
name|ObjectName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbeanServer
operator|.
name|isRegistered
argument_list|(
name|objectName
argument_list|)
condition|)
block|{
name|echo
argument_list|(
literal|"Bean Registered: "
operator|+
name|objectName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Could not find MBean!: "
operator|+
name|objectName
argument_list|)
expr_stmt|;
block|}
return|return
name|objectName
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|bindAddress
operator|=
literal|"tcp://localhost:61616"
expr_stmt|;
name|useTopic
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|mbeanServer
operator|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getMBeanServer
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
if|if
condition|(
name|waitForKeyPress
condition|)
block|{
comment|// We are running from the command line so let folks browse the
comment|// mbeans...
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Press enter to terminate the program."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"In the meantime you can use your JMX console to view the current MBeans"
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
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
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|useConnection
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
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
name|transacted
argument_list|,
name|authMode
argument_list|)
decl_stmt|;
name|destination
operator|=
name|createDestination
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|Message
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
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"counter"
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|echo
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

