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
name|ConcurrentHashMap
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
name|Executors
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
name|MessageListener
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
name|command
operator|.
name|ActiveMQTopic
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
name|Wait
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
name|Wait
operator|.
name|Condition
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
name|AMQ2183Test
extends|extends
name|AutoFailTestSupport
implements|implements
name|UncaughtExceptionHandler
implements|,
name|MessageListener
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
name|AMQ2183Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|maxSent
init|=
literal|2000
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Thread
argument_list|,
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Thread
argument_list|,
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|BrokerService
name|master
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|BrokerService
name|slave
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|URI
name|masterUrl
decl_stmt|,
name|slaveUrl
decl_stmt|;
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
name|master
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|slave
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|master
operator|.
name|setBrokerName
argument_list|(
literal|"Master"
argument_list|)
expr_stmt|;
name|master
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|master
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|master
operator|.
name|setWaitForSlave
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|master
operator|.
name|start
argument_list|()
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
name|exceptions
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|masterUrl
operator|=
name|master
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|startSlave
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
throws|,
name|URISyntaxException
block|{
name|slave
operator|.
name|setBrokerName
argument_list|(
literal|"Slave"
argument_list|)
expr_stmt|;
name|slave
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|slave
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setMasterConnectorURI
argument_list|(
name|masterUrl
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|slave
operator|.
name|start
argument_list|()
expr_stmt|;
name|slaveUrl
operator|=
name|slave
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|slave
operator|.
name|stop
argument_list|()
expr_stmt|;
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
class|class
name|MessageCounter
implements|implements
name|MessageListener
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
specifier|public
name|void
name|testMasterSlaveBugWithStopStartConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|masterUrl
operator|+
literal|")?randomize=false"
argument_list|)
decl_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|startCommenced
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|startDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// start will be blocked pending slave connection but should resume after slave started
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|startCommenced
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|startDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"connection.start has commenced"
argument_list|,
name|startCommenced
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|startSlave
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"connection.start done"
argument_list|,
name|startDone
operator|.
name|await
argument_list|(
literal|70
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|MessageCounter
name|counterA
init|=
operator|new
name|MessageCounter
argument_list|()
decl_stmt|;
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
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.A.VirtualTopic.T"
argument_list|)
argument_list|)
operator|.
name|setMessageListener
argument_list|(
name|counterA
argument_list|)
expr_stmt|;
specifier|final
name|MessageCounter
name|counterB
init|=
operator|new
name|MessageCounter
argument_list|()
decl_stmt|;
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
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.B.VirtualTopic.T"
argument_list|)
argument_list|)
operator|.
name|setMessageListener
argument_list|(
name|counterB
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
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
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualTopic.T"
argument_list|)
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
name|maxSent
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
literal|"Hi"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Condition
argument_list|()
block|{
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|maxSent
operator|==
name|counterA
operator|.
name|getCount
argument_list|()
operator|&&
name|maxSent
operator|==
name|counterB
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|maxSent
argument_list|,
name|counterA
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|maxSent
argument_list|,
name|counterB
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
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
name|exceptions
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"message received: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

