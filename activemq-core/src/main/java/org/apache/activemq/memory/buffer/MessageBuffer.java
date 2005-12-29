begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|memory
operator|.
name|buffer
package|;
end_package

begin_comment
comment|/**  * Represents a collection of MessageQueue instances which are all bound by the  * same memory buffer to fix the amount of RAM used to some uppper bound.  *   * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageBuffer
block|{
specifier|public
name|int
name|getSize
parameter_list|()
function_decl|;
comment|/**      * Creates a new message queue instance      */
specifier|public
name|MessageQueue
name|createMessageQueue
parameter_list|()
function_decl|;
comment|/**      * After a message queue has changed we may need to perform some evictions      *       * @param delta      * @param queueSize      */
specifier|public
name|void
name|onSizeChanged
parameter_list|(
name|MessageQueue
name|queue
parameter_list|,
name|int
name|delta
parameter_list|,
name|int
name|queueSize
parameter_list|)
function_decl|;
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

