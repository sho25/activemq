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
name|transport
operator|.
name|discovery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|DiscoveryEvent
import|;
end_import

begin_comment
comment|/**  * An agent used to discover other instances of a service.   *   * We typically use a discovery agent to auto-discover JMS clients and JMS brokers on a network  *  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|DiscoveryAgent
extends|extends
name|Service
block|{
comment|/**      * Sets the discovery listener      * @param listener      */
specifier|public
name|void
name|setDiscoveryListener
parameter_list|(
name|DiscoveryListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * register a service      * @param name      * @param details      * @throws JMSException      */
name|void
name|registerService
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * A process actively using a service may see it go down before the DiscoveryAgent notices the      * service's failure.  That process can use this method to notify the DiscoveryAgent of the failure      * so that other listeners of this DiscoveryAgent can also be made aware of the failure.      */
name|void
name|serviceFailed
parameter_list|(
name|DiscoveryEvent
name|event
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|String
name|getGroup
parameter_list|()
function_decl|;
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
function_decl|;
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

