begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * * Copyright 2004 Hiram Chirino * *  Licensed under the Apache License, Version 2.0 (the "License"); *  you may not use this file except in compliance with the License. *  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * *  Unless required by applicable law or agreed to in writing, software *  distributed under the License is distributed on an "AS IS" BASIS, *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *  See the License for the specific language governing permissions and *  limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|AcceptListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|ChannelServer
import|;
end_import

begin_comment
comment|/**  * AsyncChannelServer objects asynchronously accept and create {@see org.apache.activeio.Channel} objects  * and then delivers those objects to a {@see org.apache.activeio.AcceptConsumer}.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|AsyncChannelServer
extends|extends
name|ChannelServer
block|{
comment|/** 	 * Registers an AcceptListener which is notified of accepted channels. 	 *   	 * @param acceptListener 	 */
name|void
name|setAcceptListener
parameter_list|(
name|AcceptListener
name|acceptListener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

