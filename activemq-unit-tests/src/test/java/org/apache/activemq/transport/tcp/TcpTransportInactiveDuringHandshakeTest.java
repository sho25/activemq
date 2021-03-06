begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|tcp
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
name|util
operator|.
name|DefaultTestAppender
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
name|log4j
operator|.
name|Level
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
name|spi
operator|.
name|LoggingEvent
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
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
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
name|io
operator|.
name|OutputStream
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
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|CertificateException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|TcpTransportInactiveDuringHandshakeTest
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TcpTransportInactiveDuringHandshakeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEYSTORE_TYPE
init|=
literal|"jks"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_KEYSTORE
init|=
literal|"src/test/resources/server.keystore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TRUST_KEYSTORE
init|=
literal|"src/test/resources/client.keystore"
decl_stmt|;
static|static
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|TRUST_KEYSTORE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
name|PASSWORD
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStoreType"
argument_list|,
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
name|SERVER_KEYSTORE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
name|PASSWORD
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStoreType"
argument_list|,
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|DefaultTestAppender
name|appender
decl_stmt|;
name|CountDownLatch
name|inactivityMonitorFired
decl_stmt|;
name|CountDownLatch
name|handShakeComplete
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|inactivityMonitorFired
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|handShakeComplete
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|appender
operator|=
operator|new
name|DefaultTestAppender
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doAppend
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getLevel
argument_list|()
operator|.
name|equals
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
operator|&&
name|event
operator|.
name|getRenderedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"InactivityIOException"
argument_list|)
condition|)
block|{
name|inactivityMonitorFired
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|rootLogger
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|rootLogger
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInactivityMonitorThreadCompletesWhenFiringDuringStart
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"mqtt+nio+ssl://localhost:0?transport.connectAttemptTimeout=1000&transport.closeAsync=false"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|TransportConnector
name|transportConnector
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
name|URI
name|uri
init|=
name|transportConnector
operator|.
name|getPublishableConnectURI
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|blockHandShakeCompletion
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TrustManager
index|[]
name|trustManagers
init|=
operator|new
name|TrustManager
index|[]
block|{
operator|new
name|X509TrustManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|checkClientTrusted
parameter_list|(
name|X509Certificate
index|[]
name|x509Certificates
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|CertificateException
block|{
block|}
function|@Override             public void checkServerTrusted
parameter_list|(
name|X509Certificate
index|[]
name|x509Certificates
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|CertificateException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Check Server Trusted: "
operator|+
name|s
argument_list|,
operator|new
name|Throwable
argument_list|(
literal|"HERE"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|blockHandShakeCompletion
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Check Server Trusted done!"
argument_list|)
expr_stmt|;
block|}
function|@Override             public X509Certificate[] getAcceptedIssuers
parameter_list|()
block|{
return|return
operator|new
name|X509Certificate
index|[
literal|0
index|]
return|;
block|}
function|}};
name|SSLContext
name|sslContext
init|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"TLS"
argument_list|)
decl_stmt|;
name|sslContext
operator|.
name|init
argument_list|(
literal|null
argument_list|,
name|trustManagers
argument_list|,
operator|new
name|SecureRandom
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SSLSocket
name|sslSocket
init|=
operator|(
name|SSLSocket
operator|)
name|sslContext
operator|.
name|getSocketFactory
argument_list|()
operator|.
name|createSocket
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|uri
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|sslSocket
operator|.
name|addHandshakeCompletedListener
argument_list|(
operator|new
name|HandshakeCompletedListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handshakeCompleted
parameter_list|(
name|HandshakeCompletedEvent
name|handshakeCompletedEvent
parameter_list|)
block|{
name|handShakeComplete
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
function|}
block|)
empty_stmt|;
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
operator|.
name|submit
argument_list|(
operator|new
name|Runnable
argument_list|()
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
name|sslSocket
operator|.
name|startHandshake
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Socket connected"
argument_list|,
name|sslSocket
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|oops
parameter_list|)
block|{
name|oops
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
function|}
block|)
empty_stmt|;
name|assertTrue
argument_list|(
literal|"inactivity fired"
argument_list|,
name|inactivityMonitorFired
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
name|assertTrue
argument_list|(
literal|"Found non blocked inactivity monitor thread - done its work"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify no InactivityMonitor Task blocked
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|20
index|]
decl_stmt|;
name|int
name|activeCount
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
operator|.
name|enumerate
argument_list|(
name|threads
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
name|activeCount
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
name|threads
index|[
name|i
index|]
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"T["
operator|+
name|i
operator|+
literal|"]: "
operator|+
name|thread
argument_list|)
expr_stmt|;
if|if
condition|(
name|thread
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"InactivityMonitor"
argument_list|)
operator|&&
name|thread
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|Thread
operator|.
name|State
operator|.
name|TIMED_WAITING
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Found inactivity monitor in timed-wait"
argument_list|)
expr_stmt|;
comment|// good
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
function|}
block|)
argument_list|)
argument_list|;
comment|// allow handshake to complete
name|blockHandShakeCompletion
operator|.
name|countDown
argument_list|()
argument_list|;
name|final
name|OutputStream
name|socketOutPutStream
operator|=
name|sslSocket
operator|.
name|getOutputStream
argument_list|()
argument_list|;
name|assertTrue
argument_list|(
literal|"Handshake complete"
argument_list|,
name|handShakeComplete
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
argument_list|;
comment|// wait for socket to be closed via Inactivity monitor
name|assertTrue
argument_list|(
literal|"socket error"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expecting socket to error from remote close: "
operator|+
name|sslSocket
argument_list|)
expr_stmt|;
try|try
block|{
name|socketOutPutStream
operator|.
name|write
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|socketOutPutStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
function|}
block|)
argument_list|)
argument_list|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Socket at end: "
operator|+
name|sslSocket
argument_list|)
argument_list|;
name|sslSocket
operator|.
name|close
argument_list|()
argument_list|;     }
argument_list|}
end_class

end_unit

