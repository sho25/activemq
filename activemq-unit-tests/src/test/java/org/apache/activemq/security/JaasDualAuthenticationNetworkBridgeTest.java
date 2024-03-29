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
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|java
operator|.
name|util
operator|.
name|List
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
name|network
operator|.
name|NetworkBridge
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
name|network
operator|.
name|NetworkConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xbean
operator|.
name|spring
operator|.
name|context
operator|.
name|ClassPathXmlApplicationContext
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
name|Assert
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

begin_comment
comment|/**  * Unit test for https://issues.apache.org/jira/browse/AMQ-5943.  * Creates a network bridge to a broker that is configured for   * JaasDualAuthenticationPlugin.  * The broker that creates the network bridge does not set a   * username/password on the nc configuration but expects to be   * authenticated via its SSL certificate.  * This test uses these external configuration files from  * src/test/resources/  * - org/apache/activemq/security/JaasDualAuthenticationNetworkBridgeTest.xml  * - login-JaasDualAuthenticationNetworkBridgeTest.config  * - users-JaasDualAuthenticationNetworkBridgeTest.properties  * - groups-JaasDualAuthenticationNetworkBridgeTest.properties  * - ssl-domain-JaasDualAuthenticationNetworkBridgeTest.properties  */
end_comment

begin_class
specifier|public
class|class
name|JaasDualAuthenticationNetworkBridgeTest
block|{
specifier|protected
name|String
name|CONFIG_FILE
init|=
literal|"org/apache/activemq/security/JaasDualAuthenticationNetworkBridge.xml"
decl_stmt|;
specifier|protected
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JaasDualAuthenticationNetworkBridgeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker1
init|=
literal|null
decl_stmt|;
specifier|private
name|BrokerService
name|broker2
init|=
literal|null
decl_stmt|;
comment|/**      * @throws java.lang.Exception      */
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
literal|"Starting up"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
name|URL
name|resource
init|=
name|JaasDualAuthenticationNetworkBridgeTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"login-JaasDualAuthenticationNetworkBridge.config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|resource
operator|.
name|getFile
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Path to login config: "
operator|+
name|path
argument_list|)
expr_stmt|;
try|try
block|{
name|ClassPathXmlApplicationContext
name|context
init|=
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
name|CONFIG_FILE
argument_list|)
decl_stmt|;
name|broker1
operator|=
operator|(
name|BrokerService
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"broker1"
argument_list|)
expr_stmt|;
name|broker2
operator|=
operator|(
name|BrokerService
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"broker2"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|broker2
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker1
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws java.lang.Exception      */
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down"
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker1
operator|!=
literal|null
operator|&&
name|broker1
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker still running, stopping it now."
argument_list|)
expr_stmt|;
name|broker1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker1 not running, nothing to shutdown."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|broker2
operator|!=
literal|null
operator|&&
name|broker2
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker still running, stopping it now."
argument_list|)
expr_stmt|;
name|broker2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker2 not running, nothing to shutdown."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Waits 5 seconds for the network bridge between broker 1 and 2 to be      * established, then checks if the bridge exists, by querying broker1.      *       * @throws Exception is network bridge does not exist between both      * broker instances.      */
annotation|@
name|Test
specifier|public
name|void
name|testNetworkBridgeUsingJaasDualAuthenticationPlugin
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"testNetworkBridgeUsingJaasDualAuthenticationPlugin() called."
argument_list|)
expr_stmt|;
try|try
block|{
comment|// give 5 seconds for broker instances to establish network bridge
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// verify that network bridge is established
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|broker1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NetworkConnector
argument_list|>
name|ncs
init|=
name|broker1
operator|.
name|getNetworkConnectors
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Network Connector not found."
argument_list|,
name|ncs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Network Connector not found."
argument_list|,
name|ncs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc
init|=
operator|(
name|NetworkConnector
operator|)
name|ncs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|NetworkBridge
argument_list|>
name|bridges
init|=
name|nc
operator|.
name|activeBridges
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Network bridge not established to broker 2"
argument_list|,
name|bridges
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Network bridge not established to broker 2"
argument_list|,
name|bridges
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|NetworkBridge
name|nb
range|:
name|bridges
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nb
operator|.
name|getRemoteBrokerId
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Network bridge is correctly established."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

