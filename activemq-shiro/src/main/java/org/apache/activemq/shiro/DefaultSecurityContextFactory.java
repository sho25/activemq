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
name|env
operator|.
name|Environment
import|;
end_import

begin_comment
comment|/**  * Default {@code SecurityContextFactory} implementation that creates  * {@link org.apache.activemq.shiro.subject.SubjectSecurityContext} instances, allowing the connection's {@code Subject} and the Shiro  * {@link Environment} to be available to downstream security broker filters.  *  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|DefaultSecurityContextFactory
implements|implements
name|SecurityContextFactory
block|{
comment|/**      * Returns a new {@link org.apache.activemq.shiro.subject.SubjectSecurityContext} instance, allowing the connection's {@code Subject} and the Shiro      * {@link Environment} to be available to downstream security broker filters.      *      * @param conn the subject's connection      * @return a new {@link org.apache.activemq.shiro.subject.SubjectSecurityContext} instance, allowing the connection's {@code Subject} and the Shiro      *         {@link Environment} to be available to downstream security broker filters.      */
annotation|@
name|Override
specifier|public
name|SecurityContext
name|createSecurityContext
parameter_list|(
name|SubjectConnectionReference
name|conn
parameter_list|)
block|{
return|return
operator|new
name|SubjectSecurityContext
argument_list|(
name|conn
argument_list|)
return|;
block|}
block|}
end_class

end_unit

