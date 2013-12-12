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
name|shiro
operator|.
name|authc
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
name|security
operator|.
name|SecurityContext
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
name|shiro
operator|.
name|ConnectionReference
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
name|shiro
operator|.
name|env
operator|.
name|EnvironmentFilter
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
name|shiro
operator|.
name|subject
operator|.
name|ConnectionSubjectResolver
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
name|shiro
operator|.
name|subject
operator|.
name|SubjectConnectionReference
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
name|shiro
operator|.
name|subject
operator|.
name|SubjectSecurityContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|shiro
operator|.
name|authc
operator|.
name|AuthenticationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|shiro
operator|.
name|authc
operator|.
name|AuthenticationToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|shiro
operator|.
name|subject
operator|.
name|Subject
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
comment|/**  * The {@code AuthenticationFilter} enforces if authentication is required before allowing the broker filter chain  * to continue.  *<p/>  * This implementation performs a connection-level authentication assertion:  If the {@link Subject} associated with the  * connection<b>*</b> is not authenticated, and the  * {@link AuthenticationPolicy AuthenticationPolicy} requires the {@code Subject} to be authenticated, it will attempt  * to {@link Subject#login(org.apache.shiro.authc.AuthenticationToken) login} the Subject automatically.  The  * {@link AuthenticationToken} used to login is created by the  * {@link #getAuthenticationTokenFactory() authenticationTokenFactory}, typically by acquiring any credentials  * associated with the connection.  *<p/>  * Once the connection's {@code Subject} is authenticated as necessary, the broker filter chain will continue  * as expected.  *<p/>  *<b>*</b>: The upstream {@link org.apache.activemq.shiro.subject.SubjectFilter} is expected to execute before this one, ensuring a Subject instance  * is already associated with the connection.  *  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|AuthenticationFilter
extends|extends
name|EnvironmentFilter
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AuthenticationFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|AuthenticationPolicy
name|authenticationPolicy
decl_stmt|;
specifier|private
name|AuthenticationTokenFactory
name|authenticationTokenFactory
decl_stmt|;
specifier|public
name|AuthenticationFilter
parameter_list|()
block|{
name|this
operator|.
name|authenticationPolicy
operator|=
operator|new
name|DefaultAuthenticationPolicy
argument_list|()
expr_stmt|;
name|this
operator|.
name|authenticationTokenFactory
operator|=
operator|new
name|DefaultAuthenticationTokenFactory
argument_list|()
expr_stmt|;
block|}
specifier|public
name|AuthenticationPolicy
name|getAuthenticationPolicy
parameter_list|()
block|{
return|return
name|authenticationPolicy
return|;
block|}
specifier|public
name|void
name|setAuthenticationPolicy
parameter_list|(
name|AuthenticationPolicy
name|authenticationPolicy
parameter_list|)
block|{
name|this
operator|.
name|authenticationPolicy
operator|=
name|authenticationPolicy
expr_stmt|;
block|}
specifier|public
name|AuthenticationTokenFactory
name|getAuthenticationTokenFactory
parameter_list|()
block|{
return|return
name|authenticationTokenFactory
return|;
block|}
specifier|public
name|void
name|setAuthenticationTokenFactory
parameter_list|(
name|AuthenticationTokenFactory
name|authenticationTokenFactory
parameter_list|)
block|{
name|this
operator|.
name|authenticationTokenFactory
operator|=
name|authenticationTokenFactory
expr_stmt|;
block|}
specifier|protected
name|Subject
name|getSubject
parameter_list|(
name|ConnectionReference
name|conn
parameter_list|)
block|{
return|return
operator|new
name|ConnectionSubjectResolver
argument_list|(
name|conn
argument_list|)
operator|.
name|getSubject
argument_list|()
return|;
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
name|isEnabled
argument_list|()
condition|)
block|{
comment|//disabled means don't enforce authentication (i.e. allow anonymous access):
name|Subject
name|subject
init|=
name|getSubject
argument_list|(
operator|new
name|ConnectionReference
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|getEnvironment
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|subject
operator|.
name|isAuthenticated
argument_list|()
condition|)
block|{
name|SubjectConnectionReference
name|connection
init|=
operator|new
name|SubjectConnectionReference
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|getEnvironment
argument_list|()
argument_list|,
name|subject
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|authenticationPolicy
operator|.
name|isAuthenticationRequired
argument_list|(
name|connection
argument_list|)
condition|)
block|{
name|AuthenticationToken
name|token
init|=
name|this
operator|.
name|authenticationTokenFactory
operator|.
name|getAuthenticationToken
argument_list|(
name|connection
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Unable to obtain authentication credentials for newly established connection.  "
operator|+
literal|"Authentication is required."
decl_stmt|;
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
comment|//token is not null - login the current subject:
name|subject
operator|.
name|login
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
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
try|try
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
block|}
finally|finally
block|{
name|SecurityContext
name|secCtx
init|=
name|context
operator|.
name|getSecurityContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|secCtx
operator|instanceof
name|SubjectSecurityContext
condition|)
block|{
name|SubjectSecurityContext
name|subjectSecurityContext
init|=
operator|(
name|SubjectSecurityContext
operator|)
name|secCtx
decl_stmt|;
name|Subject
name|subject
init|=
name|subjectSecurityContext
operator|.
name|getSubject
argument_list|()
decl_stmt|;
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|subject
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Unable to cleanly logout connection Subject during connection removal.  This is "
operator|+
literal|"unexpected but not critical: it can be safely ignored because the "
operator|+
literal|"connection will no longer be used."
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|msg
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

