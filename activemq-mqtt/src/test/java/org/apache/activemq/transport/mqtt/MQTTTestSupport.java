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
name|security
operator|.
name|ProtectionDomain
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
name|filter
operator|.
name|DestinationMapEntry
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
name|security
operator|.
name|AuthenticationUser
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
name|security
operator|.
name|AuthorizationEntry
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
name|security
operator|.
name|AuthorizationPlugin
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
name|security
operator|.
name|DefaultAuthorizationMap
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
name|security
operator|.
name|SimpleAuthenticationPlugin
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
name|security
operator|.
name|TempDestinationAuthorizationEntry
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
name|scheduler
operator|.
name|JobSchedulerStoreImpl
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
name|int
name|sslPort
decl_stmt|;
specifier|protected
name|int
name|nioPort
decl_stmt|;
specifier|protected
name|int
name|nioSslPort
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
name|int
name|numberOfMessages
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
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|MQTTTestSupport
name|s
init|=
operator|new
name|MQTTTestSupport
argument_list|()
decl_stmt|;
name|s
operator|.
name|sslPort
operator|=
literal|5675
expr_stmt|;
name|s
operator|.
name|port
operator|=
literal|5676
expr_stmt|;
name|s
operator|.
name|nioPort
operator|=
literal|5677
expr_stmt|;
name|s
operator|.
name|nioSslPort
operator|=
literal|5678
expr_stmt|;
name|s
operator|.
name|startBroker
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100000
argument_list|)
expr_stmt|;
block|}
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
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numberOfMessages
operator|=
literal|1000
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
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPopulateJMSXUserID
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setSchedulerSupport
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JobSchedulerStoreImpl
name|jobStore
init|=
operator|new
name|JobSchedulerStoreImpl
argument_list|()
decl_stmt|;
name|jobStore
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"activemq-data"
argument_list|)
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setJobSchedulerStore
argument_list|(
name|jobStore
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerPlugin
name|configureAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|AuthenticationUser
argument_list|>
name|users
init|=
operator|new
name|ArrayList
argument_list|<
name|AuthenticationUser
argument_list|>
argument_list|()
decl_stmt|;
name|users
operator|.
name|add
argument_list|(
operator|new
name|AuthenticationUser
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|,
literal|"users,admins"
argument_list|)
argument_list|)
expr_stmt|;
name|users
operator|.
name|add
argument_list|(
operator|new
name|AuthenticationUser
argument_list|(
literal|"user"
argument_list|,
literal|"password"
argument_list|,
literal|"users"
argument_list|)
argument_list|)
expr_stmt|;
name|users
operator|.
name|add
argument_list|(
operator|new
name|AuthenticationUser
argument_list|(
literal|"guest"
argument_list|,
literal|"password"
argument_list|,
literal|"guests"
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleAuthenticationPlugin
name|authenticationPlugin
init|=
operator|new
name|SimpleAuthenticationPlugin
argument_list|(
name|users
argument_list|)
decl_stmt|;
return|return
name|authenticationPlugin
return|;
block|}
specifier|protected
name|BrokerPlugin
name|configureAuthorization
parameter_list|()
throws|throws
name|Exception
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|List
argument_list|<
name|DestinationMapEntry
argument_list|>
name|authorizationEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|DestinationMapEntry
argument_list|>
argument_list|()
decl_stmt|;
name|AuthorizationEntry
name|entry
init|=
operator|new
name|AuthorizationEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRead
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setWrite
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdmin
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|authorizationEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|entry
operator|=
operator|new
name|AuthorizationEntry
argument_list|()
expr_stmt|;
name|entry
operator|.
name|setQueue
argument_list|(
literal|"USERS.>"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRead
argument_list|(
literal|"users"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setWrite
argument_list|(
literal|"users"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdmin
argument_list|(
literal|"users"
argument_list|)
expr_stmt|;
name|authorizationEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|entry
operator|=
operator|new
name|AuthorizationEntry
argument_list|()
expr_stmt|;
name|entry
operator|.
name|setQueue
argument_list|(
literal|"GUEST.>"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRead
argument_list|(
literal|"guests"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setWrite
argument_list|(
literal|"guests,users"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdmin
argument_list|(
literal|"guests,users"
argument_list|)
expr_stmt|;
name|authorizationEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|entry
operator|=
operator|new
name|AuthorizationEntry
argument_list|()
expr_stmt|;
name|entry
operator|.
name|setTopic
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRead
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setWrite
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdmin
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|authorizationEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|entry
operator|=
operator|new
name|AuthorizationEntry
argument_list|()
expr_stmt|;
name|entry
operator|.
name|setTopic
argument_list|(
literal|"USERS.>"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRead
argument_list|(
literal|"users"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setWrite
argument_list|(
literal|"users"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdmin
argument_list|(
literal|"users"
argument_list|)
expr_stmt|;
name|authorizationEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|entry
operator|=
operator|new
name|AuthorizationEntry
argument_list|()
expr_stmt|;
name|entry
operator|.
name|setTopic
argument_list|(
literal|"GUEST.>"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRead
argument_list|(
literal|"guests"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setWrite
argument_list|(
literal|"guests,users"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdmin
argument_list|(
literal|"guests,users"
argument_list|)
expr_stmt|;
name|authorizationEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|entry
operator|=
operator|new
name|AuthorizationEntry
argument_list|()
expr_stmt|;
name|entry
operator|.
name|setTopic
argument_list|(
literal|"ActiveMQ.Advisory.>"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRead
argument_list|(
literal|"guests,users"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setWrite
argument_list|(
literal|"guests,users"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdmin
argument_list|(
literal|"guests,users"
argument_list|)
expr_stmt|;
name|authorizationEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|TempDestinationAuthorizationEntry
name|tempEntry
init|=
operator|new
name|TempDestinationAuthorizationEntry
argument_list|()
decl_stmt|;
name|tempEntry
operator|.
name|setRead
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|tempEntry
operator|.
name|setWrite
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|tempEntry
operator|.
name|setAdmin
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|DefaultAuthorizationMap
name|authorizationMap
init|=
operator|new
name|DefaultAuthorizationMap
argument_list|(
name|authorizationEntries
argument_list|)
decl_stmt|;
name|authorizationMap
operator|.
name|setTempDestinationAuthorizationEntry
argument_list|(
name|tempEntry
argument_list|)
expr_stmt|;
name|AuthorizationPlugin
name|authorizationPlugin
init|=
operator|new
name|AuthorizationPlugin
argument_list|(
name|authorizationMap
argument_list|)
decl_stmt|;
return|return
name|authorizationPlugin
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
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|getProtocolScheme
argument_list|()
operator|+
literal|"://0.0.0.0:"
operator|+
name|port
argument_list|)
decl_stmt|;
name|port
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
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
comment|/**      * Initialize an MQTTClientProvider instance.  By default this method uses the port that's      * assigned to be the TCP based port using the base version of addMQTTConnector.  A sbuclass      * can either change the value of port or override this method to assign the correct port.      *      * @param provider      *        the MQTTClientProvider instance to initialize.      *      * @throws Exception if an error occurs during initialization.      */
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
specifier|protected
name|String
name|getProtocolScheme
parameter_list|()
block|{
return|return
literal|"mqtt"
return|;
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
name|FuseMQQTTClientProvider
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
block|}
end_class

end_unit

