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
name|console
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|console
operator|.
name|CommandContext
import|;
end_import

begin_interface
specifier|public
interface|interface
name|Command
block|{
name|void
name|setCommandContext
parameter_list|(
name|CommandContext
name|context
parameter_list|)
function_decl|;
comment|/**      * Execute the specified command      * @param tokens - arguments to the command      * @throws Exception      */
name|void
name|execute
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

