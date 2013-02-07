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
name|security
operator|.
name|Principal
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jaas
operator|.
name|GroupPrincipal
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
name|jaas
operator|.
name|UserPrincipal
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
name|tcp
operator|.
name|StubX509Certificate
import|;
end_import

begin_class
specifier|public
class|class
name|JaasCertificateAuthenticationBrokerTest
extends|extends
name|TestCase
block|{
name|StubBroker
name|receiveBroker
decl_stmt|;
name|JaasCertificateAuthenticationBroker
name|authBroker
decl_stmt|;
name|ConnectionContext
name|connectionContext
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
decl_stmt|;
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
name|JaasCertificateAuthenticationBroker
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
name|connectionInfo
operator|.
name|setTransportContext
argument_list|(
operator|new
name|StubX509Certificate
index|[]
block|{}
argument_list|)
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
name|Set
argument_list|<
name|String
argument_list|>
name|userNames
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|groupNames
parameter_list|,
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
name|String
name|userNamesString
decl_stmt|;
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|userNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|userNamesString
operator|=
literal|""
operator|+
operator|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|?
name|iter
operator|.
name|next
argument_list|()
else|:
literal|""
operator|)
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|userNamesString
operator|+=
literal|","
operator|+
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|groupNamesString
init|=
literal|""
decl_stmt|;
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|groupNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|groupNamesString
operator|=
literal|""
operator|+
operator|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|?
name|iter
operator|.
name|next
argument_list|()
else|:
literal|""
operator|)
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|groupNamesString
operator|+=
literal|","
operator|+
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
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
name|userNamesString
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
name|groupNamesString
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
name|testAddConnectionSuccess
parameter_list|()
block|{
name|String
name|dnUserName
init|=
literal|"dnUserName"
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|userNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|userNames
operator|.
name|add
argument_list|(
name|dnUserName
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|groupNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|groupNames
operator|.
name|add
argument_list|(
literal|"testGroup1"
argument_list|)
expr_stmt|;
name|groupNames
operator|.
name|add
argument_list|(
literal|"testGroup2"
argument_list|)
expr_stmt|;
name|groupNames
operator|.
name|add
argument_list|(
literal|"tesetGroup3"
argument_list|)
expr_stmt|;
name|setConfiguration
argument_list|(
name|userNames
argument_list|,
name|groupNames
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|authBroker
operator|.
name|addConnection
argument_list|(
name|connectionContext
argument_list|,
name|connectionInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Call to addConnection failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Number of addConnection calls to underlying Broker must match number of calls made to "
operator|+
literal|"AuthenticationBroker."
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
name|ConnectionContext
name|receivedContext
init|=
name|receiveBroker
operator|.
name|addConnectionData
operator|.
name|getFirst
argument_list|()
operator|.
name|connectionContext
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The SecurityContext's userName must be set to that of the UserPrincipal."
argument_list|,
name|dnUserName
argument_list|,
name|receivedContext
operator|.
name|getSecurityContext
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|receivedPrincipals
init|=
name|receivedContext
operator|.
name|getSecurityContext
argument_list|()
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Principal
argument_list|>
name|iter
init|=
name|receivedPrincipals
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Principal
name|currentPrincipal
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentPrincipal
operator|instanceof
name|UserPrincipal
condition|)
block|{
if|if
condition|(
name|userNames
operator|.
name|remove
argument_list|(
name|currentPrincipal
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// Nothing, we did good.
block|}
else|else
block|{
comment|// Found an unknown userName.
name|fail
argument_list|(
literal|"Unknown UserPrincipal found"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|currentPrincipal
operator|instanceof
name|GroupPrincipal
condition|)
block|{
if|if
condition|(
name|groupNames
operator|.
name|remove
argument_list|(
name|currentPrincipal
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// Nothing, we did good.
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unknown GroupPrincipal found."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected Principal subclass found."
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|userNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Some usernames were not added as UserPrincipals"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|groupNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Some group names were not added as GroupPrincipals"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testAddConnectionFailure
parameter_list|()
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|userNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|groupNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|groupNames
operator|.
name|add
argument_list|(
literal|"testGroup1"
argument_list|)
expr_stmt|;
name|groupNames
operator|.
name|add
argument_list|(
literal|"testGroup2"
argument_list|)
expr_stmt|;
name|groupNames
operator|.
name|add
argument_list|(
literal|"tesetGroup3"
argument_list|)
expr_stmt|;
name|setConfiguration
argument_list|(
name|userNames
argument_list|,
name|groupNames
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|boolean
name|connectFailed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|authBroker
operator|.
name|addConnection
argument_list|(
name|connectionContext
argument_list|,
name|connectionInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|connectFailed
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Failed to connect for unexpected reason: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|connectFailed
condition|)
block|{
name|fail
argument_list|(
literal|"Unauthenticated connection allowed."
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Unauthenticated connection allowed."
argument_list|,
literal|true
argument_list|,
name|receiveBroker
operator|.
name|addConnectionData
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRemoveConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|connectionContext
operator|.
name|setSecurityContext
argument_list|(
operator|new
name|StubSecurityContext
argument_list|()
argument_list|)
expr_stmt|;
name|authBroker
operator|.
name|removeConnection
argument_list|(
name|connectionContext
argument_list|,
name|connectionInfo
argument_list|,
operator|new
name|Throwable
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"removeConnection should clear ConnectionContext."
argument_list|,
literal|null
argument_list|,
name|connectionContext
operator|.
name|getSecurityContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect number of calls to underlying broker were made."
argument_list|,
literal|1
argument_list|,
name|receiveBroker
operator|.
name|removeConnectionData
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

