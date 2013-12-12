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
name|subject
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
name|DefaultSecurityContextFactory
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
name|SecurityContextFactory
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
name|shiro
operator|.
name|subject
operator|.
name|Subject
import|;
end_import

begin_comment
comment|/**  * The {@code SubjectFilter} ensures a Shiro {@link Subject} representing the client's identity is associated with  * every connection to the ActiveMQ Broker.  The {@code Subject} is made available to downstream broker filters so  * they may perform security checks as necessary.  *<p/>  * This implementation does not perform any security checks/assertions itself.  It is expected that other broker filters  * will be configured after this one and those will perform any security behavior or checks as necessary.  *  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|SubjectFilter
extends|extends
name|EnvironmentFilter
block|{
specifier|private
name|ConnectionSubjectFactory
name|connectionSubjectFactory
decl_stmt|;
specifier|private
name|SecurityContextFactory
name|securityContextFactory
decl_stmt|;
specifier|public
name|SubjectFilter
parameter_list|()
block|{
name|this
operator|.
name|connectionSubjectFactory
operator|=
operator|new
name|DefaultConnectionSubjectFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|securityContextFactory
operator|=
operator|new
name|DefaultSecurityContextFactory
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ConnectionSubjectFactory
name|getConnectionSubjectFactory
parameter_list|()
block|{
return|return
name|connectionSubjectFactory
return|;
block|}
specifier|public
name|void
name|setConnectionSubjectFactory
parameter_list|(
name|ConnectionSubjectFactory
name|connectionSubjectFactory
parameter_list|)
block|{
if|if
condition|(
name|connectionSubjectFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ConnectionSubjectFactory argument cannot be null."
argument_list|)
throw|;
block|}
name|this
operator|.
name|connectionSubjectFactory
operator|=
name|connectionSubjectFactory
expr_stmt|;
block|}
specifier|public
name|SecurityContextFactory
name|getSecurityContextFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|securityContextFactory
return|;
block|}
specifier|public
name|void
name|setSecurityContextFactory
parameter_list|(
name|SecurityContextFactory
name|securityContextFactory
parameter_list|)
block|{
if|if
condition|(
name|securityContextFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SecurityContextFactory argument cannot be null."
argument_list|)
throw|;
block|}
name|this
operator|.
name|securityContextFactory
operator|=
name|securityContextFactory
expr_stmt|;
block|}
specifier|protected
name|Subject
name|createSubject
parameter_list|(
name|ConnectionReference
name|conn
parameter_list|)
block|{
return|return
name|this
operator|.
name|connectionSubjectFactory
operator|.
name|createSubject
argument_list|(
name|conn
argument_list|)
return|;
block|}
specifier|protected
name|SecurityContext
name|createSecurityContext
parameter_list|(
name|SubjectConnectionReference
name|conn
parameter_list|)
block|{
return|return
name|this
operator|.
name|securityContextFactory
operator|.
name|createSecurityContext
argument_list|(
name|conn
argument_list|)
return|;
block|}
comment|/**      * Creates a {@link Subject} instance reflecting the specified Connection.  The {@code Subject} is then stored in      * a {@link SecurityContext} instance which is set as the Connection's      * {@link ConnectionContext#setSecurityContext(org.apache.activemq.security.SecurityContext) securityContext}.      *      * @param context state associated with the client's connection      * @param info    info about the client's connection      * @throws Exception if there is a problem creating a Subject or {@code SecurityContext} instance.      */
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
operator|==
literal|null
condition|)
block|{
name|ConnectionReference
name|conn
init|=
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
decl_stmt|;
name|Subject
name|subject
init|=
name|createSubject
argument_list|(
name|conn
argument_list|)
decl_stmt|;
name|SubjectConnectionReference
name|subjectConn
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
name|secCtx
operator|=
name|createSecurityContext
argument_list|(
name|subjectConn
argument_list|)
expr_stmt|;
name|context
operator|.
name|setSecurityContext
argument_list|(
name|secCtx
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|context
operator|.
name|setSecurityContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
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
name|context
operator|.
name|setSecurityContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

