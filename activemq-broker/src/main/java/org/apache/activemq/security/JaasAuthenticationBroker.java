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
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|Subject
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
name|LoginContext
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
name|JassCredentialCallbackHandler
import|;
end_import

begin_comment
comment|/**  * Logs a user in using JAAS.  *  *  */
end_comment

begin_class
specifier|public
class|class
name|JaasAuthenticationBroker
extends|extends
name|AbstractAuthenticationBroker
block|{
specifier|private
specifier|final
name|String
name|jassConfiguration
decl_stmt|;
specifier|public
name|JaasAuthenticationBroker
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|jassConfiguration
operator|=
name|jassConfiguration
expr_stmt|;
block|}
specifier|static
class|class
name|JaasSecurityContext
extends|extends
name|SecurityContext
block|{
specifier|private
specifier|final
name|Subject
name|subject
decl_stmt|;
specifier|public
name|JaasSecurityContext
parameter_list|(
name|String
name|userName
parameter_list|,
name|Subject
name|subject
parameter_list|)
block|{
name|super
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|()
block|{
return|return
name|subject
operator|.
name|getPrincipals
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|context
operator|.
name|getSecurityContext
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// Set the TCCL since it seems JAAS needs it to find the login module classes.
name|ClassLoader
name|original
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|JaasAuthenticationBroker
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|SecurityContext
name|s
init|=
name|authenticate
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|,
name|info
operator|.
name|getPassword
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|context
operator|.
name|setSecurityContext
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|securityContexts
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|original
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SecurityContext
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|X509Certificate
index|[]
name|certificates
parameter_list|)
throws|throws
name|SecurityException
block|{
name|SecurityContext
name|result
init|=
literal|null
decl_stmt|;
name|JassCredentialCallbackHandler
name|callback
init|=
operator|new
name|JassCredentialCallbackHandler
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
try|try
block|{
name|LoginContext
name|lc
init|=
operator|new
name|LoginContext
argument_list|(
name|jassConfiguration
argument_list|,
name|callback
argument_list|)
decl_stmt|;
name|lc
operator|.
name|login
argument_list|()
expr_stmt|;
name|Subject
name|subject
init|=
name|lc
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|result
operator|=
operator|new
name|JaasSecurityContext
argument_list|(
name|username
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User name ["
operator|+
name|username
operator|+
literal|"] or password is invalid."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

