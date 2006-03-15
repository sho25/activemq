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
name|command
package|;
end_package

begin_comment
comment|/**  * Represents the logical endpoint where commands come from or are sent to.  *   * For connection based transports like TCP / VM then there is a single endpoint  * for all commands. For transports like multicast there could be different  * endpoints being used on the same transport.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|Endpoint
block|{
comment|/**      * Returns the name of the endpoint.      */
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Returns the broker ID for this endpoint, if the endpoint is a broker or      * null      */
specifier|public
name|BrokerId
name|getBrokerId
parameter_list|()
function_decl|;
comment|/**      * Returns the broker information for this endpoint, if the endpoint is a      * broker or null      */
specifier|public
name|BrokerInfo
name|getBrokerInfo
parameter_list|()
function_decl|;
specifier|public
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

