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
name|jaas
operator|.
name|ldap
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|server
operator|.
name|configuration
operator|.
name|ConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|server
operator|.
name|configuration
operator|.
name|StartupConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|registry
operator|.
name|ServiceRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|registry
operator|.
name|SimpleServiceRegistry
import|;
end_import

begin_comment
comment|/**  * A {@link StartupConfiguration} that starts up ApacheDS with network layer support.  *  * @version $Rev: 233391 $ $Date: 2005-08-18 16:38:47 -0600 (Thu, 18 Aug 2005) $  */
end_comment

begin_class
specifier|public
class|class
name|ServerStartupConfiguration
extends|extends
name|StartupConfiguration
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7138616822614155454L
decl_stmt|;
specifier|private
name|boolean
name|enableNetworking
init|=
literal|true
decl_stmt|;
specifier|private
name|ServiceRegistry
name|minaServiceRegistry
init|=
operator|new
name|SimpleServiceRegistry
argument_list|()
decl_stmt|;
specifier|private
name|int
name|ldapPort
init|=
literal|389
decl_stmt|;
specifier|private
name|int
name|ldapsPort
init|=
literal|636
decl_stmt|;
specifier|private
name|InetAddress
name|host
decl_stmt|;
specifier|private
name|boolean
name|enableKerberos
decl_stmt|;
specifier|protected
name|ServerStartupConfiguration
parameter_list|()
block|{     }
specifier|protected
name|InetAddress
name|getHost
parameter_list|()
block|{
return|return
name|host
return|;
block|}
specifier|protected
name|void
name|setHost
parameter_list|(
name|InetAddress
name|host
parameter_list|)
block|{
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
block|}
comment|/**      * Returns<tt>true</tt> if networking (LDAP, LDAPS, and Kerberos) is enabled.      */
specifier|public
name|boolean
name|isEnableNetworking
parameter_list|()
block|{
return|return
name|enableNetworking
return|;
block|}
comment|/**      * Sets whether to enable networking (LDAP, LDAPS, and Kerberos) or not.      */
specifier|public
name|void
name|setEnableNetworking
parameter_list|(
name|boolean
name|enableNetworking
parameter_list|)
block|{
name|this
operator|.
name|enableNetworking
operator|=
name|enableNetworking
expr_stmt|;
block|}
comment|/**      * Returns<tt>true</tt> if Kerberos support is enabled.      */
specifier|public
name|boolean
name|isEnableKerberos
parameter_list|()
block|{
return|return
name|enableKerberos
return|;
block|}
comment|/**      * Sets whether to enable Kerberos support or not.      */
specifier|protected
name|void
name|setEnableKerberos
parameter_list|(
name|boolean
name|enableKerberos
parameter_list|)
block|{
name|this
operator|.
name|enableKerberos
operator|=
name|enableKerberos
expr_stmt|;
block|}
comment|/**      * Returns LDAP TCP/IP port number to listen to.      */
specifier|public
name|int
name|getLdapPort
parameter_list|()
block|{
return|return
name|ldapPort
return|;
block|}
comment|/**      * Sets LDAP TCP/IP port number to listen to.      */
specifier|protected
name|void
name|setLdapPort
parameter_list|(
name|int
name|ldapPort
parameter_list|)
block|{
name|this
operator|.
name|ldapPort
operator|=
name|ldapPort
expr_stmt|;
block|}
comment|/**      * Returns LDAPS TCP/IP port number to listen to.      */
specifier|public
name|int
name|getLdapsPort
parameter_list|()
block|{
return|return
name|ldapsPort
return|;
block|}
comment|/**      * Sets LDAPS TCP/IP port number to listen to.      */
specifier|protected
name|void
name|setLdapsPort
parameter_list|(
name|int
name|ldapsPort
parameter_list|)
block|{
name|this
operator|.
name|ldapsPort
operator|=
name|ldapsPort
expr_stmt|;
block|}
comment|/**      * Returns<a href="http://directory.apache.org/subprojects/network/">MINA</a>      * {@link ServiceRegistry} that will be used by ApacheDS.      */
specifier|public
name|ServiceRegistry
name|getMinaServiceRegistry
parameter_list|()
block|{
return|return
name|minaServiceRegistry
return|;
block|}
comment|/**      * Sets<a href="http://directory.apache.org/subprojects/network/">MINA</a>      * {@link ServiceRegistry} that will be used by ApacheDS.      */
specifier|protected
name|void
name|setMinaServiceRegistry
parameter_list|(
name|ServiceRegistry
name|minaServiceRegistry
parameter_list|)
block|{
if|if
condition|(
name|minaServiceRegistry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"MinaServiceRegistry cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minaServiceRegistry
operator|=
name|minaServiceRegistry
expr_stmt|;
block|}
block|}
end_class

end_unit

