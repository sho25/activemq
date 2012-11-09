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
name|jndi
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NameNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_comment
comment|/**  * Allows users to dynamically create items  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|LazyCreateContext
extends|extends
name|ReadOnlyContext
block|{
specifier|public
name|Object
name|lookup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|NamingException
block|{
try|try
block|{
return|return
name|super
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NameNotFoundException
name|e
parameter_list|)
block|{
name|Object
name|answer
init|=
name|createEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|internalBind
argument_list|(
name|name
argument_list|,
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
specifier|protected
specifier|abstract
name|Object
name|createEntry
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_class

end_unit
