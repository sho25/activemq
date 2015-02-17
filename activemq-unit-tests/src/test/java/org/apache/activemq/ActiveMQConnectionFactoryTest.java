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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
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
name|broker
operator|.
name|BrokerRegistry
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
name|ActiveMQConnectionFactoryTest
extends|extends
name|CombinationTestSupport
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
name|ActiveMQConnectionFactoryTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|public
name|void
name|testUseURIToSetUseClientIDPrefixOnConnectionFactory
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?jms.clientIDPrefix=Cheese&broker.persistent=false"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Cheese"
argument_list|,
name|cf
operator|.
name|getClientIDPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|clientID
init|=
name|connection
operator|.
name|getClientID
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got client ID: "
operator|+
name|clientID
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should start with Cheese! but was: "
operator|+
name|clientID
argument_list|,
name|clientID
operator|.
name|startsWith
argument_list|(
literal|"Cheese"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
literal|"=========== Start test "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Try our best to close any previously opend connection.
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{         }
comment|// Try our best to stop any previously started broker.
try|try
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{         }
name|LOG
operator|.
name|info
argument_list|(
literal|"=========== Finished test "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUseURIToSetOptionsOnConnectionFactory
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?jms.useAsyncSend=true"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cf
operator|.
name|isUseAsyncSend
argument_list|()
argument_list|)
expr_stmt|;
comment|// the broker url have been adjusted.
name|assertEquals
argument_list|(
literal|"vm://localhost"
argument_list|,
name|cf
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
name|cf
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?jms.useAsyncSend=false"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cf
operator|.
name|isUseAsyncSend
argument_list|()
argument_list|)
expr_stmt|;
comment|// the broker url have been adjusted.
name|assertEquals
argument_list|(
literal|"vm://localhost"
argument_list|,
name|cf
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
name|cf
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm:(broker:()/localhost)?jms.useAsyncSend=true"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cf
operator|.
name|isUseAsyncSend
argument_list|()
argument_list|)
expr_stmt|;
comment|// the broker url have been adjusted.
name|assertEquals
argument_list|(
literal|"vm:(broker:()/localhost)"
argument_list|,
name|cf
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
name|cf
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?jms.auditDepth=5000"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5000
argument_list|,
name|cf
operator|.
name|getAuditDepth
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUseURIToConfigureRedeliveryPolicy
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false&broker.useJmx=false&jms.redeliveryPolicy.maximumRedeliveries=2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"connection redeliveries"
argument_list|,
literal|2
argument_list|,
name|cf
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"connection redeliveries"
argument_list|,
literal|2
argument_list|,
name|connection
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
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
name|ActiveMQMessageConsumer
name|consumer
init|=
operator|(
name|ActiveMQMessageConsumer
operator|)
name|session
operator|.
name|createConsumer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
literal|"FOO.BAR"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"consumer redeliveries"
argument_list|,
literal|2
argument_list|,
name|consumer
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateVMConnectionWithEmbdeddBroker
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://myBroker2?broker.persistent=false&broker.useJmx=false"
argument_list|)
decl_stmt|;
comment|// Make sure the broker is not created until the connection is
comment|// instantiated.
name|assertNull
argument_list|(
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
literal|"myBroker2"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
comment|// This should create the connection.
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
comment|// Verify the broker was created.
name|assertNotNull
argument_list|(
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
literal|"myBroker2"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Verify the broker was destroyed.
name|assertNull
argument_list|(
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
literal|"myBroker2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetBrokerName
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false&broker.useJmx=false"
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|brokerName
init|=
name|connection
operator|.
name|getBrokerName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got broker name: "
operator|+
name|brokerName
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"No broker name available!"
argument_list|,
name|brokerName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateTcpConnectionUsingAllocatedPort
parameter_list|()
throws|throws
name|Exception
block|{
name|assertCreateConnection
argument_list|(
literal|"tcp://localhost:0?wireFormat.tcpNoDelayEnabled=true"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateTcpConnectionUsingKnownPort
parameter_list|()
throws|throws
name|Exception
block|{
name|assertCreateConnection
argument_list|(
literal|"tcp://localhost:61610?wireFormat.tcpNoDelayEnabled=true"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateTcpConnectionUsingKnownLocalPort
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
literal|"tcp://localhost:61610?wireFormat.tcpNoDelayEnabled=true"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// This should create the connection.
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61610/localhost:51610"
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionFailsToConnectToVMBrokerThatIsNotRunning
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?create=false"
argument_list|)
decl_stmt|;
try|try
block|{
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected connection failure."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|testFactorySerializable
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clientID
init|=
literal|"TestClientID"
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|bytesOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|objectsOut
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|bytesOut
argument_list|)
decl_stmt|;
name|objectsOut
operator|.
name|writeObject
argument_list|(
name|cf
argument_list|)
expr_stmt|;
name|objectsOut
operator|.
name|flush
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|bytesOut
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bytesIn
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|ObjectInputStream
name|objectsIn
init|=
operator|new
name|ObjectInputStream
argument_list|(
name|bytesIn
argument_list|)
decl_stmt|;
name|cf
operator|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|objectsIn
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|cf
operator|.
name|getClientID
argument_list|()
argument_list|,
name|clientID
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSetExceptionListener
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|connection
operator|.
name|getExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|ExceptionListener
name|exListener
init|=
operator|new
name|ExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|arg0
parameter_list|)
block|{             }
block|}
decl_stmt|;
name|cf
operator|.
name|setExceptionListener
argument_list|(
name|exListener
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|connection
operator|.
name|getExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exListener
argument_list|,
name|connection
operator|.
name|getExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|exListener
argument_list|,
name|connection
operator|.
name|getExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exListener
argument_list|,
name|cf
operator|.
name|getExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSetClientInternalExceptionListener
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|connection
operator|.
name|getClientInternalExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|ClientInternalExceptionListener
name|listener
init|=
operator|new
name|ClientInternalExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{             }
block|}
decl_stmt|;
name|connection
operator|.
name|setClientInternalExceptionListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setClientInternalExceptionListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|connection
operator|.
name|getClientInternalExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|listener
argument_list|,
name|connection
operator|.
name|getClientInternalExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|listener
argument_list|,
name|connection
operator|.
name|getClientInternalExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|listener
argument_list|,
name|cf
operator|.
name|getClientInternalExceptionListener
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|assertCreateConnection
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Start up a broker with a tcp connector.
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
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|URI
name|temp
init|=
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
decl_stmt|;
comment|// URI connectURI = connector.getServer().getConnectURI();
comment|// TODO this sometimes fails when using the actual local host name
name|URI
name|currentURI
init|=
operator|new
name|URI
argument_list|(
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
decl_stmt|;
comment|// sometimes the actual host name doesn't work in this test case
comment|// e.g. on OS X so lets use the original details but just use the actual
comment|// port
name|URI
name|connectURI
init|=
operator|new
name|URI
argument_list|(
name|temp
operator|.
name|getScheme
argument_list|()
argument_list|,
name|temp
operator|.
name|getUserInfo
argument_list|()
argument_list|,
name|temp
operator|.
name|getHost
argument_list|()
argument_list|,
name|currentURI
operator|.
name|getPort
argument_list|()
argument_list|,
name|temp
operator|.
name|getPath
argument_list|()
argument_list|,
name|temp
operator|.
name|getQuery
argument_list|()
argument_list|,
name|temp
operator|.
name|getFragment
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"connection URI is: "
operator|+
name|connectURI
argument_list|)
expr_stmt|;
comment|// This should create the connection.
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectURI
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

