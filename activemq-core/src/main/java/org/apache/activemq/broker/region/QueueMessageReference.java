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
name|broker
operator|.
name|region
package|;
end_package

begin_comment
comment|/**  * Queue specific MessageReference.  *   * @author fateev@amazon.com  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|QueueMessageReference
extends|extends
name|MessageReference
block|{
name|QueueMessageReference
name|NULL_MESSAGE
init|=
operator|new
name|NullMessageReference
argument_list|()
decl_stmt|;
name|boolean
name|isAcked
parameter_list|()
function_decl|;
name|void
name|setAcked
parameter_list|(
name|boolean
name|b
parameter_list|)
function_decl|;
name|void
name|drop
parameter_list|()
function_decl|;
name|boolean
name|isDropped
parameter_list|()
function_decl|;
name|boolean
name|lock
parameter_list|(
name|LockOwner
name|subscription
parameter_list|)
function_decl|;
name|void
name|unlock
parameter_list|()
function_decl|;
name|LockOwner
name|getLockOwner
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

