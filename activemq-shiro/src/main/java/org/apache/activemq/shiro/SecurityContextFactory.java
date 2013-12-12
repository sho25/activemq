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
name|shiro
operator|.
name|subject
operator|.
name|Subject
import|;
end_import

begin_comment
comment|/**  * A {@code SecurityContextFactory} returns a {@link SecurityContext} instance that retains a client  * connection's {@link Subject} instance.  *<p/>  * It should be noted that at the time a {@code SecurityContextFactory} is invoked, a {@link Subject} is already  * associated with the client connection.  A {@code SecurityContextFactory} is merely responsible for creating  * a Shiro-specific {@link org.apache.activemq.security.SecurityContext SecurityContext} instance.  *<p/>  * The returned {@code SecurityContext} instance will then be made available to any downstream Broker Filters via  * {@code connectionContext.}{@link org.apache.activemq.broker.ConnectionContext#getSecurityContext() getSecurityContext()}  * to ensure it may be used for Shiro-based security checks.  *  * @see org.apache.activemq.shiro.subject.SubjectSecurityContext  * @since 5.10.0  */
end_comment

begin_interface
specifier|public
interface|interface
name|SecurityContextFactory
block|{
comment|/**      * Creates a new {@link SecurityContext} retaining the client connection's {@link Subject} instance.      *<p/>      * It should be noted that at the time a {@code SecurityContextFactory} is invoked, a {@code Subject} is already      * associated with the client connection.  A {@code SecurityContextFactory} is merely responsible for creating      * a Shiro-specific {@link org.apache.activemq.security.SecurityContext SecurityContext} instance.      *<p/>      * The returned {@code SecurityContext} instance will then be made available to any downstream Broker Filters via      * {@code connectionContext.}{@link org.apache.activemq.broker.ConnectionContext#getSecurityContext() getSecurityContext()}      * to ensure it may be used for Shiro-based security checks.      *      * @param ref the client's connection and subject      * @return a new {@link SecurityContext} retaining the client connection's {@link Subject} instance.      * @see org.apache.activemq.shiro.subject.SubjectSecurityContext      */
name|SecurityContext
name|createSecurityContext
parameter_list|(
name|SubjectConnectionReference
name|ref
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

