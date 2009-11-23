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
name|broker
operator|.
name|jmx
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|PersistenceAdapter
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
name|amq
operator|.
name|AMQPersistenceAdapter
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
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|memory
operator|.
name|MemoryPersistenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * A specific test of Queue.purge() functionality  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|PurgeTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PurgeTest
operator|.
name|class
argument_list|)
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
specifier|public
name|PersistenceAdapter
name|persistenceAdapter
decl_stmt|;
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
name|TestRunner
operator|.
name|run
argument_list|(
name|PurgeTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|PurgeTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|void
name|testPurge
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Send some messages
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|// Now get the QueueViewMBean and purge
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
name|count
init|=
name|proxy
operator|.
name|getQueueSize
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|count
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|purge
argument_list|()
expr_stmt|;
name|count
operator|=
name|proxy
operator|.
name|getQueueSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|count
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Browse size"
argument_list|,
name|proxy
operator|.
name|browseMessages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Queues have a special case once there are more than a thousand
comment|// dead messages, make sure we hit that.
name|messageCount
operator|+=
literal|1000
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
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|count
operator|=
name|proxy
operator|.
name|getQueueSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|count
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|purge
argument_list|()
expr_stmt|;
name|count
operator|=
name|proxy
operator|.
name|getQueueSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|count
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Browse size"
argument_list|,
name|proxy
operator|.
name|browseMessages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestDelete
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"persistenceAdapter"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|MemoryPersistenceAdapter
argument_list|()
block|,
operator|new
name|AMQPersistenceAdapter
argument_list|()
block|,
operator|new
name|JDBCPersistenceAdapter
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDelete
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Send some messages
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|sendMessages
argument_list|(
name|session
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
comment|// Now get the QueueViewMBean and purge
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
name|queueProxy
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
name|ObjectName
name|brokerViewMBeanName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Broker,BrokerName=localhost"
argument_list|)
decl_stmt|;
name|BrokerViewMBean
name|brokerProxy
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
name|brokerViewMBeanName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|long
name|count
init|=
name|queueProxy
operator|.
name|getQueueSize
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|count
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|brokerProxy
operator|.
name|removeQueue
argument_list|(
name|getDestinationString
argument_list|()
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|session
argument_list|,
name|messageCount
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
name|getDestinationString
argument_list|()
operator|+
literal|",BrokerName=localhost"
argument_list|)
expr_stmt|;
name|queueProxy
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
name|count
operator|=
name|queueProxy
operator|.
name|getQueueSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|count
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|queueProxy
operator|.
name|purge
argument_list|()
expr_stmt|;
comment|// Queues have a special case once there are more than a thousand
comment|// dead messages, make sure we hit that.
name|messageCount
operator|+=
literal|1000
expr_stmt|;
name|sendMessages
argument_list|(
name|session
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|count
operator|=
name|queueProxy
operator|.
name|getQueueSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|count
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|brokerProxy
operator|.
name|removeQueue
argument_list|(
name|getDestinationString
argument_list|()
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|session
argument_list|,
name|messageCount
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
name|getDestinationString
argument_list|()
operator|+
literal|",BrokerName=localhost"
argument_list|)
expr_stmt|;
name|queueProxy
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
name|count
operator|=
name|queueProxy
operator|.
name|getQueueSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|count
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessages
parameter_list|(
name|Session
name|session
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
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
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
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
name|setEnableStatistics
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|answer
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|echo
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the name of the destination used in this test case      */
specifier|protected
name|String
name|getDestinationString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

