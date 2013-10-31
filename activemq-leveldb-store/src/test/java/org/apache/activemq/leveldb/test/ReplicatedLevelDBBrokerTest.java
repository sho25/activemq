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
name|leveldb
operator|.
name|test
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
name|leveldb
operator|.
name|replicated
operator|.
name|ElectingLevelDBStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|SynchronousQueue
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|*
import|;
end_import

begin_comment
comment|/**  * Holds broker unit tests of the replicated leveldb store.  */
end_comment

begin_class
specifier|public
class|class
name|ReplicatedLevelDBBrokerTest
extends|extends
name|ZooKeeperTestSupport
block|{
specifier|final
name|SynchronousQueue
argument_list|<
name|BrokerService
argument_list|>
name|masterQueue
init|=
operator|new
name|SynchronousQueue
argument_list|<
name|BrokerService
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|BrokerService
argument_list|>
name|brokers
init|=
operator|new
name|ArrayList
argument_list|<
name|BrokerService
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Tries to replicate the problem reported at:      * https://issues.apache.org/jira/browse/AMQ-4837      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
operator|*
literal|60
operator|*
literal|10
argument_list|)
specifier|public
name|void
name|testAMQ4837viaJMS
parameter_list|()
throws|throws
name|Throwable
block|{
name|testAMQ4837
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tries to replicate the problem reported at:      * https://issues.apache.org/jira/browse/AMQ-4837      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
operator|*
literal|60
operator|*
literal|10
argument_list|)
specifier|public
name|void
name|testAMQ4837viaJMX
parameter_list|()
throws|throws
name|Throwable
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|resetDataDirs
argument_list|()
expr_stmt|;
name|testAMQ4837
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|stopBrokers
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|resetDataDirs
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteDirectory
argument_list|(
literal|"node-1"
argument_list|)
expr_stmt|;
name|deleteDirectory
argument_list|(
literal|"node-2"
argument_list|)
expr_stmt|;
name|deleteDirectory
argument_list|(
literal|"node-3"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|deleteDirectory
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|data_dir
argument_list|()
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|testAMQ4837
parameter_list|(
name|boolean
name|jmx
parameter_list|)
throws|throws
name|Throwable
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"1.	Start 3 activemq nodes."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|startBrokerAsync
argument_list|(
name|createBrokerNode
argument_list|(
literal|"node-1"
argument_list|)
argument_list|)
expr_stmt|;
name|startBrokerAsync
argument_list|(
name|createBrokerNode
argument_list|(
literal|"node-2"
argument_list|)
argument_list|)
expr_stmt|;
name|startBrokerAsync
argument_list|(
name|createBrokerNode
argument_list|(
literal|"node-3"
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerService
name|master
init|=
name|waitForNextMaster
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"2.	Push a message to the master and browse the queue"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|master
argument_list|,
name|pad
argument_list|(
literal|"Hello World #1"
argument_list|,
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|browseMessages
argument_list|(
name|master
argument_list|,
name|jmx
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"3.	Stop master node"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|stop
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|BrokerService
name|prevMaster
init|=
name|master
decl_stmt|;
name|master
operator|=
name|waitForNextMaster
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"4.	Push a message to the new master and browse the queue. Message summary and queue content ok."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|browseMessages
argument_list|(
name|master
argument_list|,
name|jmx
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|master
argument_list|,
name|pad
argument_list|(
literal|"Hello World #2"
argument_list|,
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|browseMessages
argument_list|(
name|master
argument_list|,
name|jmx
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"5.	Restart the stopped node& 6. stop current master"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|prevMaster
operator|=
name|createBrokerNode
argument_list|(
name|prevMaster
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|startBrokerAsync
argument_list|(
name|prevMaster
argument_list|)
expr_stmt|;
name|stop
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|master
operator|=
name|waitForNextMaster
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"7.	Browse the queue on new master"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================================"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|browseMessages
argument_list|(
name|master
argument_list|,
name|jmx
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|private
name|void
name|stop
parameter_list|(
name|BrokerService
name|master
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stopping "
operator|+
name|master
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|master
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|waitForNextMaster
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wait for master to start up..."
argument_list|)
expr_stmt|;
name|BrokerService
name|master
init|=
name|masterQueue
operator|.
name|poll
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Master elected"
argument_list|,
name|master
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|master
operator|.
name|isSlave
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Only one master elected at a time.."
argument_list|,
name|masterQueue
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Master started: "
operator|+
name|master
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|master
return|;
block|}
specifier|private
name|String
name|pad
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|size
parameter_list|)
block|{
while|while
condition|(
name|value
operator|.
name|length
argument_list|()
operator|<
name|size
condition|)
block|{
name|value
operator|+=
literal|" "
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
specifier|private
name|void
name|startBrokerAsync
parameter_list|(
name|BrokerService
name|b
parameter_list|)
block|{
specifier|final
name|BrokerService
name|broker
init|=
name|b
decl_stmt|;
operator|new
name|Thread
argument_list|(
literal|"Starting broker node: "
operator|+
name|b
operator|.
name|getBrokerName
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|masterQueue
operator|.
name|put
argument_list|(
name|broker
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
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connector
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
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
literal|"FOO"
argument_list|)
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
name|body
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|browseMessages
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|boolean
name|jmx
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|jmx
condition|)
block|{
return|return
name|browseMessagesViaJMX
argument_list|(
name|brokerService
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|browseMessagesViaJMS
argument_list|(
name|brokerService
argument_list|)
return|;
block|}
block|}
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|browseMessagesViaJMX
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ObjectName
name|on
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerService
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|",destinationType=Queue,destinationName=FOO"
argument_list|)
decl_stmt|;
name|CompositeData
index|[]
name|browse
init|=
operator|(
name|CompositeData
index|[]
operator|)
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
operator|.
name|invoke
argument_list|(
name|on
argument_list|,
literal|"browse"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|CompositeData
name|cd
range|:
name|browse
control|)
block|{
name|rc
operator|.
name|add
argument_list|(
name|cd
operator|.
name|get
argument_list|(
literal|"Text"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|browseMessagesViaJMS
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connector
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
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
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
literal|"FOO"
argument_list|)
argument_list|)
decl_stmt|;
name|Enumeration
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
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|rc
operator|.
name|add
argument_list|(
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBrokers
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|BrokerService
name|broker
range|:
name|brokers
control|)
block|{
try|try
block|{
name|stop
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
name|brokers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createBrokerNode
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|bs
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|bs
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokers
operator|.
name|add
argument_list|(
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setBrokerName
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setPersistenceAdapter
argument_list|(
name|createStoreNode
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|bs
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
return|return
name|bs
return|;
block|}
specifier|private
name|ElectingLevelDBStore
name|createStoreNode
parameter_list|(
name|String
name|id
parameter_list|)
block|{
comment|// This little hack is in here because we give each of the 3 brokers
comment|// different broker names so they can show up in JMX correctly,
comment|// but the store needs to be configured with the same broker name
comment|// so that they can find each other in ZK properly.
name|ElectingLevelDBStore
name|store
init|=
operator|new
name|ElectingLevelDBStore
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|setBrokerName
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|data_dir
argument_list|()
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|setContainer
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|store
operator|.
name|setReplicas
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|store
operator|.
name|setZkAddress
argument_list|(
literal|"localhost:"
operator|+
name|connector
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|setHostname
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setBind
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
block|}
end_class

end_unit

