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
name|shiro
operator|.
name|subject
operator|.
name|Subject
import|;
end_import

begin_comment
comment|/**  * A {@code ConnectionSubjectFactory} creates a {@code Subject} instance that represents the connection client's identity.  *<p/>  * Most implementations will simply use the {@link Subject.Builder Subject.Builder} to create an anonymous  * {@code Subject} instance and let a downstream {@link org.apache.activemq.shiro.authc.AuthenticationFilter} authenticate the {@code Subject} based on  * any credentials associated with the connection.  After authentication, the {@code Subject} will have an identity, and  * this is the expected flow for most connection clients.  *<p/>  * However, if there is some other data associated with the connection that can be inspected to create a  * {@code Subject} instance beyond what the {@link DefaultConnectionSubjectFactory} provides, this interface allows that  * logic to be plugged in as necessary.  *  * @see DefaultConnectionSubjectFactory  * @since 5.10.0  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConnectionSubjectFactory
block|{
comment|/**      * Creates a {@code Subject} instance representing the connection client.  It is common for {@code Subject} instances      * returned from this method to be anonymous until a downstream {@link org.apache.activemq.shiro.authc.AuthenticationFilter} authenticates the      * subject to associate an identity.      *      * @param ref a reference to the client's connection metadata      * @return a {@code Subject} instance representing the connection client.      */
name|Subject
name|createSubject
parameter_list|(
name|ConnectionReference
name|ref
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

