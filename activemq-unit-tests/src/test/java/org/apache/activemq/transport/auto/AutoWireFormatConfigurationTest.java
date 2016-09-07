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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|TransportConnection
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
name|openwire
operator|.
name|OpenWireFormat
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
name|AutoWireFormatConfigurationTest
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
name|String
name|uri
decl_stmt|;
specifier|private
specifier|final
name|String
name|protocol
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
comment|//Use the scheme for applying wireformat options or apply to all wireformats if false
specifier|private
specifier|final
name|boolean
name|onlyScheme
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"protocol={0},onlyScheme={1}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
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
block|,
literal|true
block|}
block|,
block|{
literal|"auto+nio"
block|,
literal|true
block|}
block|,
block|{
literal|"auto+nio+ssl"
block|,
literal|true
block|}
block|,
block|{
literal|"auto+ssl"
block|,
literal|true
block|}
block|,
block|{
literal|"auto"
block|,
literal|false
block|}
block|,
block|{
literal|"auto+nio"
block|,
literal|false
block|}
block|,
block|{
literal|"auto+nio+ssl"
block|,
literal|false
block|}
block|,
block|{
literal|"auto+ssl"
block|,
literal|false
block|}
block|}
argument_list|)
return|;
block|}
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
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|String
name|wireFormatSetting
init|=
name|onlyScheme
condition|?
literal|"wireFormat.default.cacheEnabled=false"
else|:
literal|"wireFormat.cacheEnabled=false"
decl_stmt|;
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|protocol
operator|+
literal|"://localhost:0?"
operator|+
name|wireFormatSetting
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setName
argument_list|(
literal|"auto"
argument_list|)
expr_stmt|;
name|uri
operator|=
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|this
operator|.
name|brokerService
operator|=
name|brokerService
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
name|After
specifier|public
name|void
name|after
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
block|}
block|}
comment|/**      * @param isNio      */
specifier|public
name|AutoWireFormatConfigurationTest
parameter_list|(
name|String
name|protocol
parameter_list|,
name|boolean
name|onlyScheme
parameter_list|)
block|{
name|this
operator|.
name|protocol
operator|=
name|protocol
expr_stmt|;
name|this
operator|.
name|onlyScheme
operator|=
name|onlyScheme
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testConnect
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setBrokerURL
argument_list|(
name|uri
argument_list|)
expr_stmt|;
comment|//Create 5 connections to make sure all are properly set
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|factory
operator|.
name|createConnection
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|TransportConnection
name|connection
range|:
name|brokerService
operator|.
name|getTransportConnectorByName
argument_list|(
literal|"auto"
argument_list|)
operator|.
name|getConnections
argument_list|()
control|)
block|{
comment|//Cache should be disabled on the wire format
name|OpenWireFormat
name|wireFormat
init|=
operator|(
name|OpenWireFormat
operator|)
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|getWireFormat
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|wireFormat
operator|.
name|isCacheEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

