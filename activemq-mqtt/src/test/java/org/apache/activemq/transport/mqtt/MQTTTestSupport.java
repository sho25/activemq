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
name|mqtt
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|ProtectionDomain
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|JMSException
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|X509TrustManager
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
name|transport
operator|.
name|mqtt
operator|.
name|util
operator|.
name|ResourceLoadingSslContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|MQTT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|Tracer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|MQTTFrame
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
name|MQTTTestSupport
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
name|MQTTTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
name|int
name|port
decl_stmt|;
specifier|protected
name|String
name|jmsUri
init|=
literal|"vm://localhost"
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|cf
decl_stmt|;
specifier|protected
name|LinkedList
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|LinkedList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|persistent
decl_stmt|;
specifier|protected
name|String
name|protocolConfig
decl_stmt|;
specifier|protected
name|String
name|protocolScheme
decl_stmt|;
specifier|protected
name|boolean
name|useSSL
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|AT_MOST_ONCE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|AT_LEAST_ONCE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|EXACTLY_ONCE
init|=
literal|2
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
specifier|public
name|File
name|basedir
parameter_list|()
throws|throws
name|IOException
block|{
name|ProtectionDomain
name|protectionDomain
init|=
name|getClass
argument_list|()
operator|.
name|getProtectionDomain
argument_list|()
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|protectionDomain
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"../.."
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
return|;
block|}
specifier|public
name|MQTTTestSupport
parameter_list|()
block|{
name|this
operator|.
name|protocolScheme
operator|=
literal|"mqtt"
expr_stmt|;
name|this
operator|.
name|useSSL
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|MQTTTestSupport
parameter_list|(
name|String
name|connectorScheme
parameter_list|,
name|boolean
name|useSSL
parameter_list|)
block|{
name|this
operator|.
name|protocolScheme
operator|=
name|connectorScheme
expr_stmt|;
name|this
operator|.
name|useSSL
operator|=
name|useSSL
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
operator|.
name|getMethodName
argument_list|()
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|basedir
init|=
name|basedir
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|basedir
operator|+
literal|"/src/test/resources/client.keystore"
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
name|basedir
operator|+
literal|"/src/test/resources/server.keystore"
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
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|startBroker
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
name|stopBroker
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|()
expr_stmt|;
name|applyBrokerPolicies
argument_list|()
expr_stmt|;
name|applyMemoryLimitPolicy
argument_list|()
expr_stmt|;
comment|// Setup SSL context...
name|File
name|keyStore
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|()
argument_list|,
literal|"src/test/resources/server.keystore"
argument_list|)
decl_stmt|;
name|File
name|trustStore
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|()
argument_list|,
literal|"src/test/resources/client.keystore"
argument_list|)
decl_stmt|;
specifier|final
name|ResourceLoadingSslContext
name|sslContext
init|=
operator|new
name|ResourceLoadingSslContext
argument_list|()
decl_stmt|;
name|sslContext
operator|.
name|setKeyStore
argument_list|(
name|keyStore
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
name|trustStore
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
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|addOpenWireConnector
argument_list|()
expr_stmt|;
name|cf
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|jmsUri
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
name|createPlugins
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
name|BrokerPlugin
name|authenticationPlugin
init|=
name|configureAuthentication
argument_list|()
decl_stmt|;
if|if
condition|(
name|authenticationPlugin
operator|!=
literal|null
condition|)
block|{
name|plugins
operator|.
name|add
argument_list|(
name|configureAuthorization
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BrokerPlugin
name|authorizationPlugin
init|=
name|configureAuthorization
argument_list|()
decl_stmt|;
if|if
condition|(
name|authorizationPlugin
operator|!=
literal|null
condition|)
block|{
name|plugins
operator|.
name|add
argument_list|(
name|configureAuthentication
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|port
operator|=
name|brokerService
operator|.
name|getTransportConnectorByName
argument_list|(
literal|"mqtt"
argument_list|)
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|applyMemoryLimitPolicy
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|protected
name|void
name|createBroker
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
name|isPersistent
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
name|setSchedulerSupport
argument_list|(
name|isSchedulerSupportEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPopulateJMSXUserID
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Allows a subclass to add additional broker plugins during the broker startup      * process.  This method should not add Authorization or Authentication plugins      * as those are handled by the configureAuthentication and configureAuthorization      * methods later.      *      * @param plugins      *        The List object to add Plugins for installation into the new Broker.      *      * @throws Exception if an error occurs during the plugin creation process.      */
specifier|protected
name|void
name|createPlugins
parameter_list|(
name|List
argument_list|<
name|BrokerPlugin
argument_list|>
name|plugins
parameter_list|)
throws|throws
name|Exception
block|{
comment|// NOOP
block|}
specifier|protected
name|BrokerPlugin
name|configureAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|protected
name|BrokerPlugin
name|configureAuthorization
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|applyBrokerPolicies
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOOP here
block|}
specifier|protected
name|void
name|addOpenWireConnector
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
decl_stmt|;
name|jmsUri
operator|=
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|addMQTTConnector
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Overrides of this method can add additional configuration options or add multiple
comment|// MQTT transport connectors as needed, the port variable is always supposed to be
comment|// assigned the primary MQTT connector's port.
name|StringBuilder
name|connectorURI
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|connectorURI
operator|.
name|append
argument_list|(
name|getProtocolScheme
argument_list|()
argument_list|)
expr_stmt|;
name|connectorURI
operator|.
name|append
argument_list|(
literal|"://0.0.0.0:"
argument_list|)
operator|.
name|append
argument_list|(
name|port
argument_list|)
expr_stmt|;
if|if
condition|(
name|protocolConfig
operator|!=
literal|null
operator|&&
operator|!
name|protocolConfig
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|connectorURI
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
operator|.
name|append
argument_list|(
name|protocolConfig
argument_list|)
expr_stmt|;
block|}
name|TransportConnector
name|connector
init|=
operator|new
name|TransportConnector
argument_list|()
decl_stmt|;
name|connector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
name|connectorURI
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setName
argument_list|(
literal|"mqtt"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added connector {} to broker"
argument_list|,
name|getProtocolScheme
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stopBroker
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
block|}
specifier|protected
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|name
operator|.
name|getMethodName
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getTopicName
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|name
operator|.
name|getMethodName
argument_list|()
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
name|topicViewMBeanName
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
name|topicViewMBeanName
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
comment|/**      * Initialize an MQTTClientProvider instance.  By default this method uses the port that's      * assigned to be the TCP based port using the base version of addMQTTConnector.  A subclass      * can either change the value of port or override this method to assign the correct port.      *      * @param provider      *        the MQTTClientProvider instance to initialize.      *      * @throws Exception if an error occurs during initialization.      */
specifier|protected
name|void
name|initializeConnection
parameter_list|(
name|MQTTClientProvider
name|provider
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isUseSSL
argument_list|()
condition|)
block|{
name|provider
operator|.
name|connect
argument_list|(
literal|"tcp://localhost:"
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|provider
operator|.
name|setSslContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|provider
operator|.
name|connect
argument_list|(
literal|"ssl://localhost:"
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getProtocolScheme
parameter_list|()
block|{
return|return
name|protocolScheme
return|;
block|}
specifier|public
name|void
name|setProtocolScheme
parameter_list|(
name|String
name|scheme
parameter_list|)
block|{
name|this
operator|.
name|protocolScheme
operator|=
name|scheme
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseSSL
parameter_list|()
block|{
return|return
name|this
operator|.
name|useSSL
return|;
block|}
specifier|public
name|void
name|setUseSSL
parameter_list|(
name|boolean
name|useSSL
parameter_list|)
block|{
name|this
operator|.
name|useSSL
operator|=
name|useSSL
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|persistent
return|;
block|}
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|this
operator|.
name|port
return|;
block|}
specifier|public
name|boolean
name|isSchedulerSupportEnabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
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
specifier|protected
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
name|MQTTClientProvider
name|getMQTTClientProvider
parameter_list|()
block|{
return|return
operator|new
name|FuseMQTTClientProvider
argument_list|()
return|;
block|}
specifier|protected
name|MQTT
name|createMQTTConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createMQTTConnection
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|MQTT
name|createMQTTConnection
parameter_list|(
name|String
name|clientId
parameter_list|,
name|boolean
name|clean
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|isUseSSL
argument_list|()
condition|)
block|{
return|return
name|createMQTTSslConnection
argument_list|(
name|clientId
argument_list|,
name|clean
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createMQTTTcpConnection
argument_list|(
name|clientId
argument_list|,
name|clean
argument_list|)
return|;
block|}
block|}
specifier|private
name|MQTT
name|createMQTTTcpConnection
parameter_list|(
name|String
name|clientId
parameter_list|,
name|boolean
name|clean
parameter_list|)
throws|throws
name|Exception
block|{
name|MQTT
name|mqtt
init|=
operator|new
name|MQTT
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setConnectAttemptsMax
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setReconnectAttemptsMax
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setTracer
argument_list|(
name|createTracer
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
name|mqtt
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
name|mqtt
operator|.
name|setCleanSession
argument_list|(
name|clean
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setHost
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|)
expr_stmt|;
return|return
name|mqtt
return|;
block|}
specifier|private
name|MQTT
name|createMQTTSslConnection
parameter_list|(
name|String
name|clientId
parameter_list|,
name|boolean
name|clean
parameter_list|)
throws|throws
name|Exception
block|{
name|MQTT
name|mqtt
init|=
operator|new
name|MQTT
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setConnectAttemptsMax
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setReconnectAttemptsMax
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setTracer
argument_list|(
name|createTracer
argument_list|()
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setHost
argument_list|(
literal|"ssl://localhost:"
operator|+
name|port
argument_list|)
expr_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
name|mqtt
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
name|mqtt
operator|.
name|setCleanSession
argument_list|(
name|clean
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
name|mqtt
operator|.
name|setSslContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
name|mqtt
return|;
block|}
specifier|protected
name|Tracer
name|createTracer
parameter_list|()
block|{
return|return
operator|new
name|Tracer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onReceive
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client Received:\n"
operator|+
name|frame
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSend
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client Sent:\n"
operator|+
name|frame
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|static
class|class
name|DefaultTrustManager
implements|implements
name|X509TrustManager
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
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|checkServerTrusted
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
block|{         }
annotation|@
name|Override
specifier|public
name|X509Certificate
index|[]
name|getAcceptedIssuers
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
block|}
block|}
end_class

end_unit

