begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BrokerFilter
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
name|JaasCertificateCallbackHandler
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
name|security
operator|.
name|JaasAuthenticationBroker
operator|.
name|JaasSecurityContext
import|;
end_import

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
name|Iterator
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
name|callback
operator|.
name|CallbackHandler
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

begin_comment
comment|/**  * A JAAS Authentication Broker that uses SSL Certificates.  *   * This class will provide the JAAS framework with a JaasCertificateCallbackHandler that will grant JAAS access to  *      incoming connections' SSL certificate chains.   * NOTE: There is a chance that the incoming connection does not have a valid certificate (has null).  *   * @author sepandm@gmail.com (Sepand)  */
end_comment

begin_class
specifier|public
class|class
name|JaasCertificateAuthenticationBroker
extends|extends
name|BrokerFilter
block|{
specifier|private
specifier|final
name|String
name|jaasConfiguration
decl_stmt|;
comment|/**      * Simple constructor. Leaves everything to superclass.      *       * @param next The Broker that does the actual work for this Filter.      * @param jassConfiguration The JAAS domain configuration name (refere to JAAS documentation).      */
specifier|public
name|JaasCertificateAuthenticationBroker
parameter_list|(
name|Broker
name|next
parameter_list|,
name|String
name|jaasConfiguration
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|jaasConfiguration
operator|=
name|jaasConfiguration
expr_stmt|;
block|}
comment|/**      * Overridden to allow for authentication based on client certificates.      *       * Connections being added will be authenticated based on their certificate chain and the JAAS module specified      *      through the JAAS framework.      * NOTE: The security context's username will be set to the first UserPrincipal created by the login module.      *       * @param context The context for the incoming Connection.      * @param info The ConnectionInfo Command representing the incoming connection.      */
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
if|if
condition|(
operator|!
operator|(
name|info
operator|.
name|getTransportContext
argument_list|()
operator|instanceof
name|X509Certificate
index|[]
operator|)
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"Unable to authenticate transport without SSL certificate."
argument_list|)
throw|;
block|}
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
comment|// Do the login.
try|try
block|{
name|CallbackHandler
name|callback
init|=
operator|new
name|JaasCertificateCallbackHandler
argument_list|(
operator|(
name|X509Certificate
index|[]
operator|)
name|info
operator|.
name|getTransportContext
argument_list|()
argument_list|)
decl_stmt|;
name|LoginContext
name|lc
init|=
operator|new
name|LoginContext
argument_list|(
name|jaasConfiguration
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
name|String
name|dnName
init|=
literal|""
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|subject
operator|.
name|getPrincipals
argument_list|()
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
name|nextPrincipal
init|=
operator|(
name|Principal
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextPrincipal
operator|instanceof
name|UserPrincipal
condition|)
block|{
name|dnName
operator|=
operator|(
operator|(
name|UserPrincipal
operator|)
name|nextPrincipal
operator|)
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|SecurityContext
name|s
init|=
operator|new
name|JaasSecurityContext
argument_list|(
name|dnName
argument_list|,
name|subject
argument_list|)
decl_stmt|;
name|context
operator|.
name|setSecurityContext
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User name or password is invalid."
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
comment|/**      * Overriding removeConnection to make sure the security context is cleaned.      */
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|removeConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|context
operator|.
name|setSecurityContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

