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
name|auto
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|ActiveMQTextMessage
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|AutoTransportConfigureTest
block|{
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
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|String
name|url
decl_stmt|;
annotation|@
name|Parameters
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"auto"
block|}
block|,
block|{
literal|"auto+nio"
block|}
block|,
block|{
literal|"auto+ssl"
block|}
block|,
block|{
literal|"auto+nio+ssl"
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|String
name|transportType
decl_stmt|;
specifier|public
name|AutoTransportConfigureTest
parameter_list|(
name|String
name|transportType
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|transportType
operator|=
name|transportType
expr_stmt|;
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
name|this
operator|.
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|this
operator|.
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|createBroker
parameter_list|(
name|String
name|uriConfig
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
literal|false
argument_list|)
expr_stmt|;
name|url
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|uriConfig
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
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
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|JMSException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testUrlConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
name|transportType
operator|+
literal|"://localhost:0?wireFormat.maxFrameSize=10"
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|sendMessage
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|JMSException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testUrlConfigurationOpenWireFail
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
name|transportType
operator|+
literal|"://localhost:0?wireFormat.default.maxFrameSize=10"
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|sendMessage
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUrlConfigurationOpenWireSuccess
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Will work because max frame size only applies to stomp
name|createBroker
argument_list|(
name|transportType
operator|+
literal|"://localhost:0?wireFormat.stomp.maxFrameSize=10"
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|sendMessage
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|JMSException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testUrlConfigurationOpenWireNotAvailable
parameter_list|()
throws|throws
name|Exception
block|{
comment|// only stomp is available so should fail
name|createBroker
argument_list|(
name|transportType
operator|+
literal|"://localhost:0?auto.protocols=stomp"
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|sendMessage
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUrlConfigurationOpenWireAvailable
parameter_list|()
throws|throws
name|Exception
block|{
comment|// only open wire is available
name|createBroker
argument_list|(
name|transportType
operator|+
literal|"://localhost:0?auto.protocols=default"
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|sendMessage
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUrlConfigurationOpenWireAndAmqpAvailable
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
name|transportType
operator|+
literal|"://localhost:0?auto.protocols=default,stomp"
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|sendMessage
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|sendMessage
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
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
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"this is a test"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

