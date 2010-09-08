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
name|util
operator|.
name|Vector
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|ActiveMQSession
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
name|CombinationTestSupport
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
name|ActiveMQDestination
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
name|ActiveMQObjectMessage
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

begin_class
specifier|public
class|class
name|ObjectMessageNotSerializableTest
extends|extends
name|CombinationTestSupport
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
name|ObjectMessageNotSerializableTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|AtomicInteger
name|numReceived
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|ObjectMessageNotSerializableTest
operator|.
name|class
argument_list|)
return|;
block|}
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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSendNotSerializeableObjectMessage
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"testQ"
argument_list|)
decl_stmt|;
specifier|final
name|MyObject
name|obj
init|=
operator|new
name|MyObject
argument_list|(
literal|"A message"
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|consumerStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|vmConsumerThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Consumer Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|consumerStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ActiveMQObjectMessage
name|message
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|30000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|MyObject
name|object
init|=
operator|(
name|MyObject
operator|)
name|message
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got message "
operator|+
name|object
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|numReceived
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|vmConsumerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|producingThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Producing Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|destination
argument_list|)
decl_stmt|;
name|ActiveMQObjectMessage
name|message
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|session
operator|.
name|createObjectMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setObject
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|assertTrue
argument_list|(
literal|"consumers started"
argument_list|,
name|consumerStarted
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
name|producingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|vmConsumerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|producingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"writeObject called"
argument_list|,
literal|0
argument_list|,
name|obj
operator|.
name|getWriteObjectCalled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"readObject called"
argument_list|,
literal|0
argument_list|,
name|obj
operator|.
name|getReadObjectCalled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"readObjectNoData called"
argument_list|,
literal|0
argument_list|,
name|obj
operator|.
name|getReadObjectNoDataCalled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got expected messages"
argument_list|,
literal|1
argument_list|,
name|numReceived
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no unexpected exceptions: "
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendNotSerializeableObjectMessageOverTcp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"testTopic"
argument_list|)
decl_stmt|;
specifier|final
name|MyObject
name|obj
init|=
operator|new
name|MyObject
argument_list|(
literal|"A message"
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|consumerStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|Thread
name|vmConsumerThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Consumer Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|consumerStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ActiveMQObjectMessage
name|message
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|30000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|MyObject
name|object
init|=
operator|(
name|MyObject
operator|)
name|message
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got message "
operator|+
name|object
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|numReceived
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|vmConsumerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|tcpConsumerThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Consumer Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
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
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|consumerStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ActiveMQObjectMessage
name|message
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|30000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|MyObject
name|object
init|=
operator|(
name|MyObject
operator|)
name|message
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got message "
operator|+
name|object
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|numReceived
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"readObject called"
argument_list|,
literal|1
argument_list|,
name|object
operator|.
name|getReadObjectCalled
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|tcpConsumerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|notherVmConsumerThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Consumer Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|consumerStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ActiveMQObjectMessage
name|message
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|30000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|MyObject
name|object
init|=
operator|(
name|MyObject
operator|)
name|message
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got message "
operator|+
name|object
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|numReceived
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|notherVmConsumerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|producingThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Producing Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|destination
argument_list|)
decl_stmt|;
name|ActiveMQObjectMessage
name|message
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|session
operator|.
name|createObjectMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setObject
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|assertTrue
argument_list|(
literal|"consumers started"
argument_list|,
name|consumerStarted
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
name|producingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|vmConsumerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|tcpConsumerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|notherVmConsumerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|producingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"writeObject called"
argument_list|,
literal|1
argument_list|,
name|obj
operator|.
name|getWriteObjectCalled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"readObject called"
argument_list|,
literal|0
argument_list|,
name|obj
operator|.
name|getReadObjectCalled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"readObjectNoData called"
argument_list|,
literal|0
argument_list|,
name|obj
operator|.
name|getReadObjectNoDataCalled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got expected messages"
argument_list|,
literal|3
argument_list|,
name|numReceived
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no unexpected exceptions: "
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
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
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
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
return|return
name|broker
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
block|}
end_class

end_unit

