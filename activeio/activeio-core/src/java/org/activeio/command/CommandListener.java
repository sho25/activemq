begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2005 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|command
package|;
end_package

begin_comment
comment|/**  * A listener of command objects  *  * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|CommandListener
block|{
comment|/**      * Called when a command is received      *      */
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
function_decl|;
comment|/**      * Called when an error occurs trying to      * read a new command.      */
name|void
name|onError
parameter_list|(
name|Exception
name|e
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

