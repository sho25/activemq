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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|ExceptionListener
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
name|net
operator|.
name|SocketFactory
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
name|transport
operator|.
name|TransportFactory
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
name|tcp
operator|.
name|TcpTransportFactory
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
name|URISupport
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
name|DurableConsumerCloseAndReconnectTcpTest
extends|extends
name|DurableConsumerCloseAndReconnectTest
implements|implements
name|ExceptionListener
implements|,
name|TransportListener
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
name|DurableConsumerCloseAndReconnectTcpTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|TransportConnector
name|connector
decl_stmt|;
specifier|private
name|CountDownLatch
name|gotException
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|Exception
name|reconnectException
decl_stmt|;
specifier|private
name|boolean
name|reconnectInExceptionListener
decl_stmt|;
specifier|private
name|boolean
name|reconnectInTransportListener
decl_stmt|;
specifier|public
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
comment|// let the client initiate the inactivity timeout
name|connector
operator|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0?transport.useInactivityMonitor=false"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
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
class|class
name|SlowCloseSocketTcpTransportFactory
extends|extends
name|TcpTransportFactory
block|{
class|class
name|SlowCloseSocketFactory
extends|extends
name|SocketFactory
block|{
class|class
name|SlowCloseSocket
extends|extends
name|Socket
block|{
specifier|public
name|SlowCloseSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SlowCloseSocket
parameter_list|(
name|InetAddress
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SlowCloseSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localHost
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|localHost
argument_list|,
name|localPort
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SlowCloseSocket
parameter_list|(
name|InetAddress
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localAddress
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|address
argument_list|,
name|port
argument_list|,
name|localAddress
argument_list|,
name|localPort
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"delaying close"
argument_list|)
expr_stmt|;
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
return|return
operator|new
name|SlowCloseSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SlowCloseSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localHost
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
return|return
operator|new
name|SlowCloseSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|localHost
argument_list|,
name|localPort
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localAddress
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SlowCloseSocket
argument_list|(
name|address
argument_list|,
name|port
argument_list|,
name|localAddress
argument_list|,
name|localPort
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|SocketFactory
name|createSocketFactory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SlowCloseSocketFactory
argument_list|()
return|;
block|}
block|}
name|TransportFactory
operator|.
name|registerTransportFactory
argument_list|(
literal|"tcp"
argument_list|,
operator|new
name|SlowCloseSocketTcpTransportFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
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
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|URISupport
operator|.
name|removeQuery
argument_list|(
name|connector
operator|.
name|getConnectUri
argument_list|()
argument_list|)
operator|+
literal|"?useKeepAlive=false&wireFormat.maxInactivityDuration=2000"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|testCreateDurableConsumerCloseThenReconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|reconnectInExceptionListener
operator|=
literal|true
expr_stmt|;
name|makeConsumer
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|connection
operator|)
operator|.
name|addTransportListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"inactive connection timedout"
argument_list|,
name|gotException
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Got expected exception on close reconnect overlap: "
operator|+
name|reconnectException
argument_list|,
name|reconnectException
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateDurableConsumerSlowCloseThenReconnectTransportListener
parameter_list|()
throws|throws
name|Exception
block|{
name|reconnectInTransportListener
operator|=
literal|true
expr_stmt|;
name|makeConsumer
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|connection
operator|)
operator|.
name|addTransportListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"inactive connection timedout"
argument_list|,
name|gotException
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"No exception: "
operator|+
name|reconnectException
argument_list|,
name|reconnectException
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception listener exception:"
operator|+
name|exception
argument_list|)
expr_stmt|;
if|if
condition|(
name|reconnectInExceptionListener
condition|)
block|{
try|try
block|{
name|makeConsumer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|reconnectException
operator|=
name|e
expr_stmt|;
block|}
name|gotException
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Transport listener exception:"
operator|+
name|error
argument_list|)
expr_stmt|;
if|if
condition|(
name|reconnectInTransportListener
condition|)
block|{
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|makeConsumer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|reconnectException
operator|=
name|e
expr_stmt|;
block|}
name|gotException
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{}
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{}
block|}
end_class

end_unit

