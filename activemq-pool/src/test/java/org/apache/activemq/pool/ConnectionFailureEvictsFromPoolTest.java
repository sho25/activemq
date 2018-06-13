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
name|pool
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
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|ActiveMQConnection
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
name|ActiveMQXAConnectionFactory
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
name|EnhancedConnection
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
name|advisory
operator|.
name|DestinationSource
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
name|jms
operator|.
name|pool
operator|.
name|PooledConnection
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
name|test
operator|.
name|TestSupport
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
name|transport
operator|.
name|TransportListener
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
name|transport
operator|.
name|mock
operator|.
name|MockTransport
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
name|ConnectionFailureEvictsFromPoolTest
extends|extends
name|TestSupport
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
name|ConnectionFailureEvictsFromPoolTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
name|TransportConnector
name|connector
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
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
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connector
operator|=
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
block|}
specifier|public
name|void
name|testEnhancedConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|XaPooledConnectionFactory
name|pooledFactory
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|(
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"mock:"
operator|+
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|+
literal|"?closeAsync=false"
argument_list|)
argument_list|)
decl_stmt|;
name|PooledConnection
name|connection
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|EnhancedConnection
name|enhancedConnection
init|=
operator|(
name|EnhancedConnection
operator|)
name|connection
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|DestinationSource
name|destinationSource
init|=
name|enhancedConnection
operator|.
name|getDestinationSource
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|destinationSource
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEvictionXA
parameter_list|()
throws|throws
name|Exception
block|{
name|XaPooledConnectionFactory
name|pooledFactory
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|(
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"mock:("
operator|+
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|+
literal|"?closeAsync=false)?jms.xaAckMode=1"
argument_list|)
argument_list|)
decl_stmt|;
name|doTestEviction
argument_list|(
name|pooledFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEviction
parameter_list|()
throws|throws
name|Exception
block|{
name|PooledConnectionFactory
name|pooledFactory
init|=
operator|new
name|PooledConnectionFactory
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"mock:"
operator|+
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|+
literal|"?closeAsync=false"
argument_list|)
argument_list|)
decl_stmt|;
name|doTestEviction
argument_list|(
name|pooledFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestEviction
parameter_list|(
name|ConnectionFactory
name|pooledFactory
parameter_list|)
throws|throws
name|Exception
block|{
name|PooledConnection
name|connection
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|ActiveMQConnection
name|amqC
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connection
operator|.
name|getConnection
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotExceptionEvent
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|amqC
operator|.
name|addTransportListener
argument_list|(
operator|new
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{             }
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
comment|// we know connection is dead...
comment|// listeners are fired async
name|gotExceptionEvent
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{             }
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{             }
block|}
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sent one message worked fine"
argument_list|)
expr_stmt|;
name|createConnectionFailure
argument_list|(
name|connection
argument_list|)
expr_stmt|;
try|try
block|{
name|sendMessage
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|fail
argument_list|(
literal|"Expected Error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{         }
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|TestCase
operator|.
name|assertTrue
argument_list|(
literal|"exception event propagated ok"
argument_list|,
name|gotExceptionEvent
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// If we get another connection now it should be a new connection that
comment|// works.
name|LOG
operator|.
name|info
argument_list|(
literal|"expect new connection after failure"
argument_list|)
expr_stmt|;
name|Connection
name|connection2
init|=
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|sendMessage
argument_list|(
name|connection2
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createConnectionFailure
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|c
init|=
call|(
name|ActiveMQConnection
call|)
argument_list|(
operator|(
name|PooledConnection
operator|)
name|connection
argument_list|)
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|MockTransport
name|t
init|=
operator|(
name|MockTransport
operator|)
name|c
operator|.
name|getTransportChannel
argument_list|()
operator|.
name|narrow
argument_list|(
name|MockTransport
operator|.
name|class
argument_list|)
decl_stmt|;
name|t
operator|.
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"forcing exception for "
operator|+
name|getName
argument_list|()
operator|+
literal|" to force pool eviction"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"arranged for failure, chucked exception"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|JMSException
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
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
literal|"Test"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
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
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

