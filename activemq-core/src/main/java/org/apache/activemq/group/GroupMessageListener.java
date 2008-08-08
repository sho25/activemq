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
name|group
package|;
end_package

begin_comment
comment|/**  * A listener for message communication  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|GroupMessageListener
block|{
comment|/**      * Called when a message is delivered to the<CODE>Group</CODE> from another member      * @param sender the member who sent the message      * @param replyId the id to use to respond to a message      * @param message the message object      */
name|void
name|messageDelivered
parameter_list|(
name|Member
name|sender
parameter_list|,
name|String
name|replyId
parameter_list|,
name|Object
name|message
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

