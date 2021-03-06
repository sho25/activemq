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
name|nio
package|;
end_package

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
name|util
operator|.
name|ConsumerThread
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
name|ProducerThread
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
name|Ignore
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|assertEquals
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
name|assertNull
import|;
end_import

begin_class
specifier|public
class|class
name|NIOSSLLoadTest
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
name|NIOSSLLoadTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|Session
name|session
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
specifier|public
specifier|static
specifier|final
name|int
name|PRODUCER_COUNT
init|=
literal|10
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|CONSUMER_COUNT
init|=
literal|10
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|1000
decl_stmt|;
specifier|final
name|ConsumerThread
index|[]
name|consumers
init|=
operator|new
name|ConsumerThread
index|[
name|CONSUMER_COUNT
index|]
decl_stmt|;
name|TransportConnector
name|connector
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
literal|"javax.net.ssl.keyStoreType"
argument_list|,
name|KEYSTORE_TYPE
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
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
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
name|setUseJmx
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
literal|"nio+ssl://localhost:0?transport.needClientAuth=true&transport.enabledCipherSuites=TLS_RSA_WITH_AES_256_CBC_SHA256"
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
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"nio+ssl://localhost:"
operator|+
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
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
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|broker
operator|!=
literal|null
condition|)
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
annotation|@
name|Test
specifier|public
name|void
name|testLoad
parameter_list|()
throws|throws
name|Exception
block|{
name|Queue
name|dest
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST"
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
name|PRODUCER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|ProducerThread
name|producer
init|=
operator|new
name|ProducerThread
argument_list|(
name|session
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setMessageCount
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|CONSUMER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|ConsumerThread
name|consumer
init|=
operator|new
name|ConsumerThread
argument_list|(
name|session
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageCount
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|=
name|consumer
expr_stmt|;
block|}
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
return|return
name|getReceived
argument_list|()
operator|==
name|PRODUCER_COUNT
operator|*
name|MESSAGE_COUNT
return|;
block|}
block|}
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PRODUCER_COUNT
operator|*
name|MESSAGE_COUNT
argument_list|,
name|getReceived
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|360000
argument_list|)
annotation|@
name|Ignore
specifier|public
name|void
name|testConnectionHandshakeLoad
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"nio+ssl://localhost:"
operator|+
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|threadNumber
init|=
literal|500
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|threadNumber
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|errors
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
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
name|threadNumber
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"thread "
operator|+
name|i
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conn
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|int
name|sleepTime
init|=
name|rand
operator|.
name|nextInt
argument_list|(
operator|(
literal|3000
operator|-
literal|1000
operator|)
operator|+
literal|1
argument_list|)
operator|+
literal|1000
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" sleeping "
operator|+
name|sleepTime
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
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
name|errors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" iteration "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" finished"
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|latch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"errors "
operator|+
name|errors
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|int
name|getReceived
parameter_list|()
block|{
name|int
name|received
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ConsumerThread
name|consumer
range|:
name|consumers
control|)
block|{
name|received
operator|+=
name|consumer
operator|.
name|getReceived
argument_list|()
expr_stmt|;
block|}
return|return
name|received
return|;
block|}
block|}
end_class

end_unit

