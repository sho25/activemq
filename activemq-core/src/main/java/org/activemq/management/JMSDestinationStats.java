begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|management
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * A simple interface to allow statistics gathering to be easily switched out  * for performance reasons.  *  * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|JMSDestinationStats
block|{
comment|/**      * On startup sets the pending message count      *      * @param count      */
specifier|public
name|void
name|setPendingMessageCountOnStartup
parameter_list|(
name|long
name|count
parameter_list|)
function_decl|;
comment|/**      * On a message send to this destination, update the producing stats      *      * @param message      */
specifier|public
name|void
name|onMessageSend
parameter_list|(
name|Message
name|message
parameter_list|)
function_decl|;
comment|/**      * On a consume from this destination, updates the consumed states      */
specifier|public
name|void
name|onMessageAck
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

