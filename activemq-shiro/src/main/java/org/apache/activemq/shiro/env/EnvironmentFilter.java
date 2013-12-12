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
name|env
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
name|SecurityFilter
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
comment|/**  * An abstract {@code BrokerFilter} that makes the Shiro {@link Environment} available to subclasses.  *  * @since 5.10.0  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|EnvironmentFilter
extends|extends
name|SecurityFilter
block|{
specifier|private
name|Environment
name|environment
decl_stmt|;
specifier|public
name|EnvironmentFilter
parameter_list|()
block|{     }
specifier|public
name|Environment
name|getEnvironment
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|environment
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Environment has not yet been set.  This should be done before this broker filter is used."
decl_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
return|return
name|environment
return|;
block|}
specifier|public
name|void
name|setEnvironment
parameter_list|(
name|Environment
name|environment
parameter_list|)
block|{
name|this
operator|.
name|environment
operator|=
name|environment
expr_stmt|;
block|}
block|}
end_class

end_unit

