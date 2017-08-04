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
name|javax
operator|.
name|jms
operator|.
name|InvalidClientIDException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|AppConfigurationEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|CopyOnWriteArrayList
import|;
end_import

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
name|broker
operator|.
name|Broker
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
name|ConnectionContext
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
name|StubBroker
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
name|ConnectionInfo
import|;
end_import

begin_class
specifier|public
class|class
name|JaasAuthenticationBrokerTest
extends|extends
name|TestCase
block|{
name|StubBroker
name|receiveBroker
decl_stmt|;
name|JaasAuthenticationBroker
name|authBroker
decl_stmt|;
name|ConnectionContext
name|connectionContext
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
decl_stmt|;
name|CopyOnWriteArrayList
argument_list|<
name|SecurityContext
argument_list|>
name|visibleSecurityContexts
decl_stmt|;
class|class
name|JaasAuthenticationBrokerTester
extends|extends
name|JaasAuthenticationBroker
block|{
specifier|public
name|JaasAuthenticationBrokerTester
parameter_list|(
name|Broker
name|next
parameter_list|,
name|String
name|jassConfiguration
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|,
name|jassConfiguration
argument_list|)
expr_stmt|;
name|visibleSecurityContexts
operator|=
name|securityContexts
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|receiveBroker
operator|=
operator|new
name|StubBroker
argument_list|()
expr_stmt|;
name|authBroker
operator|=
operator|new
name|JaasAuthenticationBrokerTester
argument_list|(
name|receiveBroker
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|connectionContext
operator|=
operator|new
name|ConnectionContext
argument_list|()
expr_stmt|;
name|connectionInfo
operator|=
operator|new
name|ConnectionInfo
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setConfiguration
parameter_list|(
name|boolean
name|loginShouldSucceed
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configOptions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|configOptions
operator|.
name|put
argument_list|(
name|StubLoginModule
operator|.
name|ALLOW_LOGIN_PROPERTY
argument_list|,
name|loginShouldSucceed
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
expr_stmt|;
name|configOptions
operator|.
name|put
argument_list|(
name|StubLoginModule
operator|.
name|USERS_PROPERTY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|configOptions
operator|.
name|put
argument_list|(
name|StubLoginModule
operator|.
name|GROUPS_PROPERTY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|AppConfigurationEntry
name|configEntry
init|=
operator|new
name|AppConfigurationEntry
argument_list|(
literal|"org.apache.activemq.security.StubLoginModule"
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|configOptions
argument_list|)
decl_stmt|;
name|StubJaasConfiguration
name|jaasConfig
init|=
operator|new
name|StubJaasConfiguration
argument_list|(
name|configEntry
argument_list|)
decl_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|jaasConfig
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddConnectionFailureOnDuplicateClientId
parameter_list|()
throws|throws
name|Exception
block|{
name|setConfiguration
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connectionInfo
operator|.
name|setClientId
argument_list|(
literal|"CliIdX"
argument_list|)
expr_stmt|;
name|authBroker
operator|.
name|addConnection
argument_list|(
name|connectionContext
argument_list|,
name|connectionInfo
argument_list|)
expr_stmt|;
name|ConnectionContext
name|secondContext
init|=
name|connectionContext
operator|.
name|copy
argument_list|()
decl_stmt|;
name|secondContext
operator|.
name|setSecurityContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ConnectionInfo
name|secondInfo
init|=
name|connectionInfo
operator|.
name|copy
argument_list|()
decl_stmt|;
try|try
block|{
name|authBroker
operator|.
name|addConnection
argument_list|(
name|secondContext
argument_list|,
name|secondInfo
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expect duplicate id"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidClientIDException
name|expected
parameter_list|)
block|{         }
name|assertEquals
argument_list|(
literal|"one connection allowed."
argument_list|,
literal|1
argument_list|,
name|receiveBroker
operator|.
name|addConnectionData
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one context ."
argument_list|,
literal|1
argument_list|,
name|visibleSecurityContexts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

