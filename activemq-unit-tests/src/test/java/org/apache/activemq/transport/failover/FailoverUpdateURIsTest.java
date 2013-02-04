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
name|failover
package|;
end_package

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
name|FileOutputStream
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
name|network
operator|.
name|NetworkConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_class
specifier|public
class|class
name|FailoverUpdateURIsTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"test.failoverupdateuris"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FailoverUpdateURIsTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|firstTcpUri
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|String
name|secondTcpUri
init|=
literal|"tcp://localhost:61626"
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|BrokerService
name|bs1
init|=
literal|null
decl_stmt|;
name|BrokerService
name|bs2
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
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
block|}
if|if
condition|(
name|bs1
operator|!=
literal|null
condition|)
block|{
name|bs1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|bs2
operator|!=
literal|null
condition|)
block|{
name|bs2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testUpdateURIsViaFile
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|targetDir
init|=
literal|"target/"
operator|+
name|getName
argument_list|()
decl_stmt|;
operator|new
name|File
argument_list|(
name|targetDir
argument_list|)
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|File
name|updateFile
init|=
operator|new
name|File
argument_list|(
name|targetDir
operator|+
literal|"/updateURIsFile.txt"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|updateFile
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|updateFile
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|updateFile
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|updateFile
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|updateFile
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|firstTcpUri
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|bs1
operator|=
name|createBroker
argument_list|(
literal|"bs1"
argument_list|,
name|firstTcpUri
argument_list|)
expr_stmt|;
name|bs1
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// no failover uri's to start with, must be read from file...
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:()?updateURIsURL=file:///"
operator|+
name|updateFile
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|theQueue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|theQueue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|theQueue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test message"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|bs1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|bs1
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|bs1
operator|=
literal|null
expr_stmt|;
name|bs2
operator|=
name|createBroker
argument_list|(
literal|"bs2"
argument_list|,
name|secondTcpUri
argument_list|)
expr_stmt|;
name|bs2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// add the transport uri for broker number 2
name|out
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|updateFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|","
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|secondTcpUri
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|tcpUri
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
name|setBrokerName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|bs
operator|.
name|addConnector
argument_list|(
name|tcpUri
argument_list|)
expr_stmt|;
return|return
name|bs
return|;
block|}
specifier|public
name|void
name|testAutoUpdateURIs
parameter_list|()
throws|throws
name|Exception
block|{
name|bs1
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs1
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|transportConnector
init|=
name|bs1
operator|.
name|addConnector
argument_list|(
name|firstTcpUri
argument_list|)
decl_stmt|;
name|transportConnector
operator|.
name|setUpdateClusterClients
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs1
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|firstTcpUri
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|theQueue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|theQueue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|theQueue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test message"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|bs2
operator|=
name|createBroker
argument_list|(
literal|"bs2"
argument_list|,
name|secondTcpUri
argument_list|)
expr_stmt|;
name|NetworkConnector
name|networkConnector
init|=
name|bs2
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:("
operator|+
name|firstTcpUri
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|networkConnector
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bs2
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"started brokerService 2"
argument_list|)
expr_stmt|;
name|bs2
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stopping brokerService 1"
argument_list|)
expr_stmt|;
name|bs1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|bs1
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|bs1
operator|=
literal|null
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

