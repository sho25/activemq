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
operator|.
name|security
package|;
end_package

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
name|ActiveMQTopic
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
name|DestinationMap
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
name|Arrays
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
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests that the broker allows/fails access to destinations based on the  * security policy installed on the broker.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SimpleSecurityBrokerSystemTest
extends|extends
name|SecurityTestSupport
block|{
specifier|static
specifier|final
name|GroupPrincipal
name|guests
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"guests"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|GroupPrincipal
name|users
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"users"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|GroupPrincipal
name|admins
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"admins"
argument_list|)
decl_stmt|;
specifier|public
name|BrokerPlugin
name|authorizationPlugin
decl_stmt|;
specifier|public
name|BrokerPlugin
name|authenticationPlugin
decl_stmt|;
specifier|public
name|AuthorizationMap
name|createAuthorizationMap
parameter_list|()
block|{
name|DestinationMap
name|readAccess
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
name|readAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|admins
argument_list|)
expr_stmt|;
name|readAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"USERS.>"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|readAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.>"
argument_list|)
argument_list|,
name|guests
argument_list|)
expr_stmt|;
name|readAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|admins
argument_list|)
expr_stmt|;
name|readAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"USERS.>"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|readAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.>"
argument_list|)
argument_list|,
name|guests
argument_list|)
expr_stmt|;
name|DestinationMap
name|writeAccess
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|admins
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"USERS.>"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.>"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.>"
argument_list|)
argument_list|,
name|guests
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|admins
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"USERS.>"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.>"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.>"
argument_list|)
argument_list|,
name|guests
argument_list|)
expr_stmt|;
name|readAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"ActiveMQ.Advisory.>"
argument_list|)
argument_list|,
name|guests
argument_list|)
expr_stmt|;
name|readAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"ActiveMQ.Advisory.>"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"ActiveMQ.Advisory.>"
argument_list|)
argument_list|,
name|guests
argument_list|)
expr_stmt|;
name|writeAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"ActiveMQ.Advisory.>"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|DestinationMap
name|adminAccess
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
name|adminAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|admins
argument_list|)
expr_stmt|;
name|adminAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|adminAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|guests
argument_list|)
expr_stmt|;
name|adminAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|admins
argument_list|)
expr_stmt|;
name|adminAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|users
argument_list|)
expr_stmt|;
name|adminAccess
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|guests
argument_list|)
expr_stmt|;
return|return
operator|new
name|SimpleAuthorizationMap
argument_list|(
name|writeAccess
argument_list|,
name|readAccess
argument_list|,
name|adminAccess
argument_list|)
return|;
block|}
class|class
name|SimpleAuthenticationFactory
implements|implements
name|BrokerPlugin
block|{
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|HashMap
name|u
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|u
operator|.
name|put
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|u
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|u
operator|.
name|put
argument_list|(
literal|"guest"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|HashMap
name|groups
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|groups
operator|.
name|put
argument_list|(
literal|"system"
argument_list|,
operator|new
name|HashSet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|admins
block|,
name|users
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|groups
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
operator|new
name|HashSet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|users
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|groups
operator|.
name|put
argument_list|(
literal|"guest"
argument_list|,
operator|new
name|HashSet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|guests
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|SimpleAuthenticationBroker
argument_list|(
name|broker
argument_list|,
name|u
argument_list|,
name|groups
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SimpleAuthenticationBroker"
return|;
block|}
block|}
static|static
block|{
name|String
name|path
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|URL
name|resource
init|=
name|SimpleSecurityBrokerSystemTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"login.config"
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
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Path to login config: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|SimpleSecurityBrokerSystemTest
operator|.
name|class
argument_list|)
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
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombos
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"authorizationPlugin"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|AuthorizationPlugin
argument_list|(
name|createAuthorizationMap
argument_list|()
argument_list|)
block|, }
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"authenticationPlugin"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|SimpleAuthenticationFactory
argument_list|()
block|,
operator|new
name|JassAuthenticationPlugin
argument_list|()
block|, }
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|authorizationPlugin
block|,
name|authenticationPlugin
block|}
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|initCombosForTestUserReceiveFails
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"user"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|, }
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestInvalidAuthentication
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"user"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestUserReceiveSucceeds
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"user"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|, }
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestGuestReceiveSucceeds
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"guest"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|, }
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestGuestReceiveFails
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"guest"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|, }
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestUserSendSucceeds
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"user"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|, }
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestUserSendFails
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"user"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST"
argument_list|)
block|, }
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestGuestSendFails
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"guest"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|, }
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestGuestSendSucceeds
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"guest"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"password"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|, }
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

