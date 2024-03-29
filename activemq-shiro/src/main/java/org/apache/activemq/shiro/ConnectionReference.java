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
name|shiro
operator|.
name|env
operator|.
name|Environment
import|;
end_import

begin_comment
comment|/**  * A reference (handle) to a client's {@link ConnectionContext} and {@link ConnectionInfo} as well as the Shiro  * {@link Environment}.  *<p/>  * This implementation primarily exists as a<a href="http://sourcemaking.com/refactoring/introduce-parameter-object">  * Parameter Object Design Pattern</a> implementation to eliminate long parameter lists, but provides additional  * benefits, such as immutability and non-null guarantees, and possibility for future data without forcing method  * signature changes.  *  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionReference
block|{
specifier|private
specifier|final
name|ConnectionContext
name|connectionContext
decl_stmt|;
specifier|private
specifier|final
name|ConnectionInfo
name|connectionInfo
decl_stmt|;
specifier|private
specifier|final
name|Environment
name|environment
decl_stmt|;
specifier|public
name|ConnectionReference
parameter_list|(
name|ConnectionContext
name|connCtx
parameter_list|,
name|ConnectionInfo
name|connInfo
parameter_list|,
name|Environment
name|environment
parameter_list|)
block|{
if|if
condition|(
name|connCtx
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ConnectionContext argument cannot be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|connInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ConnectionInfo argument cannot be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|environment
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Environment argument cannot be null."
argument_list|)
throw|;
block|}
name|this
operator|.
name|connectionContext
operator|=
name|connCtx
expr_stmt|;
name|this
operator|.
name|connectionInfo
operator|=
name|connInfo
expr_stmt|;
name|this
operator|.
name|environment
operator|=
name|environment
expr_stmt|;
block|}
specifier|public
name|ConnectionContext
name|getConnectionContext
parameter_list|()
block|{
return|return
name|connectionContext
return|;
block|}
specifier|public
name|ConnectionInfo
name|getConnectionInfo
parameter_list|()
block|{
return|return
name|connectionInfo
return|;
block|}
specifier|public
name|Environment
name|getEnvironment
parameter_list|()
block|{
return|return
name|environment
return|;
block|}
block|}
end_class

end_unit

