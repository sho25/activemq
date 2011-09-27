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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|jaas
operator|.
name|GroupPrincipal
import|;
end_import

begin_comment
comment|/**  * A simple authentication plugin  *  * @org.apache.xbean.XBean element="simpleAuthenticationPlugin"  *                         description="Provides a simple authentication plugin  *                         configured with a map of user-passwords and a map of  *                         user-groups or a list of authentication users"  *  *  */
end_comment

begin_class
specifier|public
class|class
name|SimpleAuthenticationPlugin
implements|implements
name|BrokerPlugin
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userPasswords
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
name|userGroups
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_ANONYMOUS_USER
init|=
literal|"anonymous"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_ANONYMOUS_GROUP
init|=
literal|"anonymous"
decl_stmt|;
specifier|private
name|String
name|anonymousUser
init|=
name|DEFAULT_ANONYMOUS_USER
decl_stmt|;
specifier|private
name|String
name|anonymousGroup
init|=
name|DEFAULT_ANONYMOUS_GROUP
decl_stmt|;
specifier|private
name|boolean
name|anonymousAccessAllowed
init|=
literal|false
decl_stmt|;
specifier|public
name|SimpleAuthenticationPlugin
parameter_list|()
block|{     }
specifier|public
name|SimpleAuthenticationPlugin
parameter_list|(
name|List
argument_list|<
name|?
argument_list|>
name|users
parameter_list|)
block|{
name|setUsers
argument_list|(
name|users
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|parent
parameter_list|)
block|{
name|SimpleAuthenticationBroker
name|broker
init|=
operator|new
name|SimpleAuthenticationBroker
argument_list|(
name|parent
argument_list|,
name|userPasswords
argument_list|,
name|userGroups
argument_list|)
decl_stmt|;
name|broker
operator|.
name|setAnonymousAccessAllowed
argument_list|(
name|anonymousAccessAllowed
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAnonymousUser
argument_list|(
name|anonymousUser
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAnonymousGroup
argument_list|(
name|anonymousGroup
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
name|getUserGroups
parameter_list|()
block|{
return|return
name|userGroups
return|;
block|}
comment|/**      * Sets individual users for authentication      *      * @org.apache.xbean.ElementType class="org.apache.activemq.security.AuthenticationUser"      */
specifier|public
name|void
name|setUsers
parameter_list|(
name|List
argument_list|<
name|?
argument_list|>
name|users
parameter_list|)
block|{
name|userPasswords
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|userGroups
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|?
argument_list|>
name|it
init|=
name|users
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AuthenticationUser
name|user
init|=
operator|(
name|AuthenticationUser
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|userPasswords
operator|.
name|put
argument_list|(
name|user
operator|.
name|getUsername
argument_list|()
argument_list|,
name|user
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|groups
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|iter
init|=
operator|new
name|StringTokenizer
argument_list|(
name|user
operator|.
name|getGroups
argument_list|()
argument_list|,
literal|","
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|iter
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|groups
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|userGroups
operator|.
name|put
argument_list|(
name|user
operator|.
name|getUsername
argument_list|()
argument_list|,
name|groups
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setAnonymousAccessAllowed
parameter_list|(
name|boolean
name|anonymousAccessAllowed
parameter_list|)
block|{
name|this
operator|.
name|anonymousAccessAllowed
operator|=
name|anonymousAccessAllowed
expr_stmt|;
block|}
specifier|public
name|void
name|setAnonymousUser
parameter_list|(
name|String
name|anonymousUser
parameter_list|)
block|{
name|this
operator|.
name|anonymousUser
operator|=
name|anonymousUser
expr_stmt|;
block|}
specifier|public
name|void
name|setAnonymousGroup
parameter_list|(
name|String
name|anonymousGroup
parameter_list|)
block|{
name|this
operator|.
name|anonymousGroup
operator|=
name|anonymousGroup
expr_stmt|;
block|}
comment|/**      * Sets the groups a user is in. The key is the user name and the value is a      * Set of groups      */
specifier|public
name|void
name|setUserGroups
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
name|userGroups
parameter_list|)
block|{
name|this
operator|.
name|userGroups
operator|=
name|userGroups
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUserPasswords
parameter_list|()
block|{
return|return
name|userPasswords
return|;
block|}
comment|/**      * Sets the map indexed by user name with the value the password      */
specifier|public
name|void
name|setUserPasswords
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userPasswords
parameter_list|)
block|{
name|this
operator|.
name|userPasswords
operator|=
name|userPasswords
expr_stmt|;
block|}
block|}
end_class

end_unit

