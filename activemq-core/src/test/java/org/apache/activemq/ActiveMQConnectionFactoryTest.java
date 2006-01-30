begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Connection
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

begin_class
specifier|public
class|class
name|ActiveMQConnectionFactoryTest
extends|extends
name|CombinationTestSupport
block|{
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
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
comment|// Make sure the broker is not created until the connection is instantiated.
name|assertNull
argument_list|(
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
literal|"localhost"
argument_list|)
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
literal|"localhost"
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
literal|"localhost"
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
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
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
name|System
operator|.
name|out
operator|.
name|println
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
name|connection
operator|.
name|close
argument_list|()
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
comment|//URI connectURI = connector.getServer().getConnectURI();
comment|// TODO this sometimes fails when using the actual local host name
name|URI
name|currentURI
init|=
name|connector
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
decl_stmt|;
comment|// sometimes the actual host name doesn't work in this test case
comment|// e.g. on OS X so lets use the original details but just use the actual port
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
name|System
operator|.
name|out
operator|.
name|println
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
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
block|}
end_class

end_unit

