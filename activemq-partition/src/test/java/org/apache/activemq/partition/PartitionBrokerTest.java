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
name|partition
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
name|AutoFailTestSupport
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
name|BrokerPlugin
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
name|partition
operator|.
name|dto
operator|.
name|Partitioning
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
name|partition
operator|.
name|dto
operator|.
name|Target
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
name|net
operator|.
name|URISyntaxException
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
name|concurrent
operator|.
name|TimeUnit
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
comment|/**  * Unit tests for the PartitionBroker plugin.  */
end_comment

begin_class
specifier|public
class|class
name|PartitionBrokerTest
block|{
specifier|protected
name|HashMap
argument_list|<
name|String
argument_list|,
name|BrokerService
argument_list|>
name|brokers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|BrokerService
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|ArrayList
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
name|Partitioning
name|partitioning
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|partitioning
operator|=
operator|new
name|Partitioning
argument_list|()
expr_stmt|;
name|partitioning
operator|.
name|brokers
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Partitioning can only re-direct failover clients since those      * can re-connect and re-establish their state with another broker.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
operator|*
literal|60
operator|*
literal|60
argument_list|)
specifier|public
name|void
name|testNonFailoverClientHasNoPartitionEffect
parameter_list|()
throws|throws
name|Exception
block|{
name|partitioning
operator|.
name|byClientId
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Target
argument_list|>
argument_list|()
expr_stmt|;
name|partitioning
operator|.
name|byClientId
operator|.
name|put
argument_list|(
literal|"client1"
argument_list|,
operator|new
name|Target
argument_list|(
literal|"broker1"
argument_list|)
argument_list|)
expr_stmt|;
name|createBrokerCluster
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|createConnectionToUrl
argument_list|(
name|getConnectURL
argument_list|(
literal|"broker2"
argument_list|)
argument_list|)
decl_stmt|;
name|within
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker2"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"client1"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker2"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
operator|*
literal|60
operator|*
literal|60
argument_list|)
specifier|public
name|void
name|testPartitionByClientId
parameter_list|()
throws|throws
name|Exception
block|{
name|partitioning
operator|.
name|byClientId
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Target
argument_list|>
argument_list|()
expr_stmt|;
name|partitioning
operator|.
name|byClientId
operator|.
name|put
argument_list|(
literal|"client1"
argument_list|,
operator|new
name|Target
argument_list|(
literal|"broker1"
argument_list|)
argument_list|)
expr_stmt|;
name|partitioning
operator|.
name|byClientId
operator|.
name|put
argument_list|(
literal|"client2"
argument_list|,
operator|new
name|Target
argument_list|(
literal|"broker2"
argument_list|)
argument_list|)
expr_stmt|;
name|createBrokerCluster
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|createConnectionTo
argument_list|(
literal|"broker2"
argument_list|)
decl_stmt|;
name|within
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker2"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"client1"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|within
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker2"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
operator|*
literal|60
operator|*
literal|60
argument_list|)
specifier|public
name|void
name|testPartitionByQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|partitioning
operator|.
name|byQueue
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Target
argument_list|>
argument_list|()
expr_stmt|;
name|partitioning
operator|.
name|byQueue
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|Target
argument_list|(
literal|"broker1"
argument_list|)
argument_list|)
expr_stmt|;
name|createBrokerCluster
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Connection
name|connection2
init|=
name|createConnectionTo
argument_list|(
literal|"broker2"
argument_list|)
decl_stmt|;
name|within
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker2"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|MessageConsumer
name|consumer
init|=
name|session2
operator|.
name|createConsumer
argument_list|(
name|session2
operator|.
name|createQueue
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|within
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker2"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Connection
name|connection1
init|=
name|createConnectionTo
argument_list|(
literal|"broker2"
argument_list|)
decl_stmt|;
name|Session
name|session1
init|=
name|connection1
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
name|session1
operator|.
name|createProducer
argument_list|(
name|session1
operator|.
name|createQueue
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|within
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker2"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session1
operator|.
name|createTextMessage
argument_list|(
literal|"#"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|within
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getTransportConnector
argument_list|(
literal|"broker2"
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|static
interface|interface
name|Task
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
specifier|private
name|void
name|within
parameter_list|(
name|int
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|Task
name|task
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|timeMS
init|=
name|unit
operator|.
name|toMillis
argument_list|(
name|time
argument_list|)
decl_stmt|;
name|long
name|deadline
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeMS
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|task
operator|.
name|run
argument_list|()
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|long
name|remaining
init|=
name|deadline
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
if|if
condition|(
name|e
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|e
throw|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|timeMS
operator|/
literal|10
argument_list|,
name|remaining
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|Connection
name|createConnectionTo
parameter_list|(
name|String
name|brokerId
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|JMSException
block|{
return|return
name|createConnectionToUrl
argument_list|(
literal|"failover://("
operator|+
name|getConnectURL
argument_list|(
name|brokerId
argument_list|)
operator|+
literal|")?randomize=false"
argument_list|)
return|;
block|}
specifier|private
name|Connection
name|createConnectionToUrl
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
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
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|protected
name|String
name|getConnectURL
parameter_list|(
name|String
name|broker
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|TransportConnector
name|tcp
init|=
name|getTransportConnector
argument_list|(
name|broker
argument_list|)
decl_stmt|;
return|return
name|tcp
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|TransportConnector
name|getTransportConnector
parameter_list|(
name|String
name|broker
parameter_list|)
block|{
name|BrokerService
name|brokerService
init|=
name|brokers
operator|.
name|get
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerService
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid broker id"
argument_list|)
throw|;
block|}
return|return
name|brokerService
operator|.
name|getTransportConnectorByName
argument_list|(
literal|"tcp"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|createBrokerCluster
parameter_list|(
name|int
name|brokerCount
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|brokerCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|brokerId
init|=
literal|"broker"
operator|+
name|i
decl_stmt|;
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|brokerId
argument_list|)
decl_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
operator|.
name|setName
argument_list|(
literal|"tcp"
argument_list|)
expr_stmt|;
name|addPartitionBrokerPlugin
argument_list|(
name|broker
argument_list|)
expr_stmt|;
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
name|partitioning
operator|.
name|brokers
operator|.
name|put
argument_list|(
name|brokerId
argument_list|,
name|getConnectURL
argument_list|(
name|brokerId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|addPartitionBrokerPlugin
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
block|{
name|PartitionBrokerPlugin
name|plugin
init|=
operator|new
name|PartitionBrokerPlugin
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|setConfig
argument_list|(
name|partitioning
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|plugin
block|}
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|name
parameter_list|)
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
name|setBrokerName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|broker
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Connection
name|connection
range|:
name|connections
control|)
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
name|Throwable
name|e
parameter_list|)
block|{             }
block|}
name|connections
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|BrokerService
name|broker
range|:
name|brokers
operator|.
name|values
argument_list|()
control|)
block|{
try|try
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
catch|catch
parameter_list|(
name|Throwable
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
block|}
end_class

end_unit

