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
name|amqp
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

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
name|ExecutorService
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
name|Destination
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
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
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
name|KeyManager
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
name|SSLContext
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
name|TrustManager
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
name|broker
operator|.
name|jmx
operator|.
name|BrokerViewMBean
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
name|jmx
operator|.
name|ConnectorViewMBean
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
name|jmx
operator|.
name|QueueViewMBean
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
name|jmx
operator|.
name|SubscriptionViewMBean
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
name|jmx
operator|.
name|TopicViewMBean
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
name|spring
operator|.
name|SpringSslContext
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
name|kahadb
operator|.
name|KahaDBStore
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
name|amqp
operator|.
name|protocol
operator|.
name|AmqpConnection
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
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
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
name|AmqpTestSupport
block|{
specifier|public
specifier|static
specifier|final
name|String
name|MESSAGE_NUMBER
init|=
literal|"MessageNumber"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KAHADB_DIRECTORY
init|=
literal|"target/activemq-data/"
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AmqpTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ExecutorService
name|testService
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
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
specifier|protected
name|int
name|numberOfMessages
decl_stmt|;
specifier|protected
name|URI
name|amqpURI
decl_stmt|;
specifier|protected
name|int
name|amqpPort
decl_stmt|;
specifier|protected
name|URI
name|amqpSslURI
decl_stmt|;
specifier|protected
name|int
name|amqpSslPort
decl_stmt|;
specifier|protected
name|URI
name|amqpNioURI
decl_stmt|;
specifier|protected
name|int
name|amqpNioPort
decl_stmt|;
specifier|protected
name|URI
name|amqpNioPlusSslURI
decl_stmt|;
specifier|protected
name|int
name|amqpNioPlusSslPort
decl_stmt|;
specifier|protected
name|URI
name|amqpWsURI
decl_stmt|;
specifier|protected
name|int
name|amqpWsPort
decl_stmt|;
specifier|protected
name|URI
name|amqpWssURI
decl_stmt|;
specifier|protected
name|int
name|amqpWssPort
decl_stmt|;
specifier|protected
name|URI
name|autoURI
decl_stmt|;
specifier|protected
name|int
name|autoPort
decl_stmt|;
specifier|protected
name|URI
name|autoSslURI
decl_stmt|;
specifier|protected
name|int
name|autoSslPort
decl_stmt|;
specifier|protected
name|URI
name|autoNioURI
decl_stmt|;
specifier|protected
name|int
name|autoNioPort
decl_stmt|;
specifier|protected
name|URI
name|autoNioPlusSslURI
decl_stmt|;
specifier|protected
name|int
name|autoNioPlusSslPort
decl_stmt|;
specifier|protected
name|URI
name|openwireURI
decl_stmt|;
specifier|protected
name|int
name|openwirePort
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
name|LOG
operator|.
name|info
argument_list|(
literal|"========== start "
operator|+
name|getTestName
argument_list|()
operator|+
literal|" =========="
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfMessages
operator|=
literal|2000
expr_stmt|;
block|}
specifier|protected
name|void
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
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
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPersistent
argument_list|()
condition|)
block|{
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|KAHADB_DIRECTORY
operator|+
name|getTestName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
block|}
name|brokerService
operator|.
name|setSchedulerSupport
argument_list|(
name|isSchedulerEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
name|isUseJmx
argument_list|()
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|performAdditionalConfiguration
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|SSLContext
name|ctx
init|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"TLS"
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|init
argument_list|(
operator|new
name|KeyManager
index|[
literal|0
index|]
argument_list|,
operator|new
name|TrustManager
index|[]
block|{
operator|new
name|DefaultTrustManager
argument_list|()
block|}
argument_list|,
operator|new
name|SecureRandom
argument_list|()
argument_list|)
expr_stmt|;
name|SSLContext
operator|.
name|setDefault
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
comment|// Setup SSL context...
specifier|final
name|File
name|classesDir
init|=
operator|new
name|File
argument_list|(
name|AmqpConnection
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|keystore
init|=
operator|new
name|File
argument_list|(
name|classesDir
argument_list|,
literal|"../../src/test/resources/keystore"
argument_list|)
decl_stmt|;
specifier|final
name|SpringSslContext
name|sslContext
init|=
operator|new
name|SpringSslContext
argument_list|()
decl_stmt|;
name|sslContext
operator|.
name|setKeyStore
argument_list|(
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setKeyStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setTrustStore
argument_list|(
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setTrustStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setSslContext
argument_list|(
name|sslContext
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStoreType"
argument_list|,
literal|"jks"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStoreType"
argument_list|,
literal|"jks"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|BrokerPlugin
argument_list|>
name|plugins
init|=
operator|new
name|ArrayList
argument_list|<
name|BrokerPlugin
argument_list|>
argument_list|()
decl_stmt|;
name|addAdditionalPlugins
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|plugins
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|BrokerPlugin
index|[]
name|array
init|=
operator|new
name|BrokerPlugin
index|[
name|plugins
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|brokerService
operator|.
name|setPlugins
argument_list|(
name|plugins
operator|.
name|toArray
argument_list|(
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addTranportConnectors
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|performAdditionalConfiguration
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{      }
specifier|protected
name|void
name|addAdditionalPlugins
parameter_list|(
name|List
argument_list|<
name|BrokerPlugin
argument_list|>
name|plugins
parameter_list|)
throws|throws
name|Exception
block|{      }
specifier|protected
name|void
name|addTranportConnectors
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportConnector
name|connector
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isUseOpenWireConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:"
operator|+
name|openwirePort
argument_list|)
expr_stmt|;
name|openwirePort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|openwireURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using openwire port "
operator|+
name|openwirePort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseTcpConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"amqp://0.0.0.0:"
operator|+
name|amqpPort
operator|+
literal|"?transport.transformer="
operator|+
name|getAmqpTransformer
argument_list|()
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|amqpPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|amqpURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using amqp port "
operator|+
name|amqpPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseSslConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"amqp+ssl://0.0.0.0:"
operator|+
name|amqpSslPort
operator|+
literal|"?transport.transformer="
operator|+
name|getAmqpTransformer
argument_list|()
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|amqpSslPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|amqpSslURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using amqp+ssl port "
operator|+
name|amqpSslPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseNioConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"amqp+nio://0.0.0.0:"
operator|+
name|amqpNioPort
operator|+
literal|"?transport.transformer="
operator|+
name|getAmqpTransformer
argument_list|()
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|amqpNioPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|amqpNioURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using amqp+nio port "
operator|+
name|amqpNioPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseNioPlusSslConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"amqp+nio+ssl://0.0.0.0:"
operator|+
name|amqpNioPlusSslPort
operator|+
literal|"?transport.transformer="
operator|+
name|getAmqpTransformer
argument_list|()
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|amqpNioPlusSslPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|amqpNioPlusSslURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using amqp+nio+ssl port "
operator|+
name|amqpNioPlusSslPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseAutoConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"auto://0.0.0.0:"
operator|+
name|autoPort
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|autoPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|autoURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using auto port "
operator|+
name|autoPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseAutoSslConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"auto+ssl://0.0.0.0:"
operator|+
name|autoSslPort
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|autoSslPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|autoSslURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using auto+ssl port "
operator|+
name|autoSslPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseAutoNioConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"auto+nio://0.0.0.0:"
operator|+
name|autoNioPort
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|autoNioPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|autoNioURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using auto+nio port "
operator|+
name|autoNioPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseAutoNioPlusSslConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"auto+nio+ssl://0.0.0.0:"
operator|+
name|autoNioPlusSslPort
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|autoNioPlusSslPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|autoNioPlusSslURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using auto+nio+ssl port "
operator|+
name|autoNioPlusSslPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseWsConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"ws://0.0.0.0:"
operator|+
name|getProxyPort
argument_list|(
name|amqpWsPort
argument_list|)
operator|+
literal|"?transport.transformer="
operator|+
name|getAmqpTransformer
argument_list|()
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|amqpWsPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|amqpWsURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using amqp+ws port "
operator|+
name|amqpWsPort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isUseWssConnector
argument_list|()
condition|)
block|{
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"wss://0.0.0.0:"
operator|+
name|getProxyPort
argument_list|(
name|amqpWssPort
argument_list|)
operator|+
literal|"?transport.transformer="
operator|+
name|getAmqpTransformer
argument_list|()
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
name|amqpWssPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|amqpWssURI
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using amqp+wss port "
operator|+
name|amqpWssPort
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseJmx
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|protected
name|boolean
name|isSchedulerEnabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseOpenWireConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseTcpConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|protected
name|boolean
name|isUseSslConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseNioConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseNioPlusSslConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseAutoConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseAutoSslConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseAutoNioConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseAutoNioPlusSslConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseWsConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|isUseWssConnector
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|String
name|getAmqpTransformer
parameter_list|()
block|{
return|return
literal|"jms"
return|;
block|}
specifier|protected
name|String
name|getAdditionalConfig
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Broker is already created."
argument_list|)
throw|;
block|}
name|createBroker
argument_list|(
literal|true
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
block|}
specifier|public
name|void
name|restartBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|restartBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|restartBroker
parameter_list|(
name|boolean
name|deleteAllOnStartup
parameter_list|)
throws|throws
name|Exception
block|{
name|stopBroker
argument_list|()
expr_stmt|;
name|createBroker
argument_list|(
name|deleteAllOnStartup
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
block|}
specifier|public
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"entering AmqpTestSupport.stopBroker"
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
name|brokerService
operator|=
literal|null
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"exiting AmqpTestSupport.stopBroker"
argument_list|)
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
name|stopBroker
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"========== tearDown "
operator|+
name|getTestName
argument_list|()
operator|+
literal|" =========="
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Connection
name|createJMSConnection
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
operator|!
name|isUseOpenWireConnector
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
argument_list|(
literal|"OpenWire TransportConnector was not configured."
argument_list|)
throw|;
block|}
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|openwireURI
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|int
name|count
parameter_list|,
name|boolean
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|createJMSConnection
argument_list|()
decl_stmt|;
try|try
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
name|Destination
name|destination
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|destination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
name|sendMessages
argument_list|(
name|connection
argument_list|,
name|destination
argument_list|,
name|count
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
specifier|public
name|void
name|sendMessages
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
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
try|try
block|{
name|MessageProducer
name|p
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
literal|1
init|;
name|i
operator|<=
name|count
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"TextMessage: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
name|MESSAGE_NUMBER
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|p
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getTestName
parameter_list|()
block|{
return|return
name|name
operator|.
name|getMethodName
argument_list|()
return|;
block|}
specifier|protected
name|int
name|getProxyPort
parameter_list|(
name|int
name|proxyPort
parameter_list|)
block|{
if|if
condition|(
name|proxyPort
operator|==
literal|0
condition|)
block|{
name|ServerSocket
name|ss
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ss
operator|=
name|ServerSocketFactory
operator|.
name|getDefault
argument_list|()
operator|.
name|createServerSocket
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|proxyPort
operator|=
name|ss
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
return|return
name|proxyPort
return|;
block|}
specifier|protected
name|BrokerViewMBean
name|getProxyToBroker
parameter_list|()
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
block|{
name|ObjectName
name|brokerViewMBean
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost"
argument_list|)
decl_stmt|;
name|BrokerViewMBean
name|proxy
init|=
operator|(
name|BrokerViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|brokerViewMBean
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
specifier|protected
name|ConnectorViewMBean
name|getProxyToConnectionView
parameter_list|(
name|String
name|connectionType
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectName
name|connectorQuery
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,connector=clientConnectors,connectorName="
operator|+
name|connectionType
operator|+
literal|"_//*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|results
init|=
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|queryNames
argument_list|(
name|connectorQuery
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
operator|||
name|results
operator|.
name|isEmpty
argument_list|()
operator|||
name|results
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Unable to find the exact Connector instance."
argument_list|)
throw|;
block|}
name|ConnectorViewMBean
name|proxy
init|=
operator|(
name|ConnectorViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|results
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
name|ConnectorViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
specifier|protected
name|QueueViewMBean
name|getProxyToQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
block|{
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName="
operator|+
name|name
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
specifier|protected
name|SubscriptionViewMBean
name|getProxyToQueueSubscriber
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
throws|,
name|IOException
block|{
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName="
operator|+
name|name
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SubscriptionViewMBean
name|subscription
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectName
name|subscriber
range|:
name|proxy
operator|.
name|getSubscriptions
argument_list|()
control|)
block|{
name|subscription
operator|=
operator|(
name|SubscriptionViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|subscriber
argument_list|,
name|SubscriptionViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|subscription
return|;
block|}
specifier|protected
name|TopicViewMBean
name|getProxyToTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
block|{
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Topic,destinationName="
operator|+
name|name
argument_list|)
decl_stmt|;
name|TopicViewMBean
name|proxy
init|=
operator|(
name|TopicViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|queueViewMBeanName
argument_list|,
name|TopicViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
block|}
end_class

end_unit

